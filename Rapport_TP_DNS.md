# Rapport — Installation et Configuration d'un serveur DNS BIND sous Ubuntu
### Services et Administration des Réseaux — 2026-2027

**Étudiant :** Adem
**Environnement :** Ubuntu Desktop, VMware Workstation 26, interface `ens33`
**Topologie :**
| Rôle | IP | Adresse MAC |
|---|---|---|
| Serveur DNS Maître (BIND9, instance `named`) | `192.168.1.100` | `00:0c:29:5e:f6:45` |
| Alias IP (esclave simulé sur la même VM) | `192.168.1.102` | — |
| Domaine géré | `esprit1.com` | — |

---

## Introduction

Un DNS (Domain Name Service) permet la résolution des noms de domaine en adresses IP. Ce TP consiste à installer et configurer Bind 9 pour gérer la zone `esprit1.com` ainsi que sa zone inverse, à intégrer le DNS avec un serveur web Apache, puis à déployer un serveur DNS **secondaire (esclave)** sur la même machine via une **seconde instance BIND9** écoutant sur une **adresse alias (`192.168.1.102`)**, afin d'assurer la redondance (transfert de zone **AXFR**) et un test de bascule (failover).

---

# Partie 1 — Serveur DNS Maître

## Étape 1 : Mises à jour
```bash
sudo apt update
sudo apt upgrade -y
```

## Étape 2 : Désactivation du pare-feu
```bash
sudo ufw disable
sudo systemctl stop ufw
sudo systemctl disable ufw
```

## Étape 3 : Installation de BIND9
```bash
sudo apt install -y bind9 bind9utils bind9-doc dnsutils
```
**Preuve (service actif) :**
```
● named.service - BIND Domain Name Server
     Loaded: loaded (/usr/lib/systemd/system/named.service; enabled; preset: enabled)
     Active: active (running)
```

## Étape 4 : `/etc/bind/named.conf.options`
```conf
acl "lan" {
    192.168.1.0/24;
};

options {
    directory "/var/cache/bind";

    recursion yes;                 # requêtes récursives autorisées
    allow-query { localhost; lan; };

    forwarders {
        8.8.8.8;
        8.8.4.4;
    };

    listen-on port 53 { 127.0.0.1; 192.168.1.100; };
    listen-on-v6 { none; };
};
```
**Vérification :** `sudo named-checkconf /etc/bind/named.conf.options` → aucune sortie (syntaxe valide).

## Étape 5 : `/etc/bind/named.conf.local`
```conf
zone "esprit1.com" {
    type master;
    file "/etc/bind/zones/esprit1.com";
    allow-transfer { 192.168.1.102; };
};

zone "1.168.192.in-addr.arpa" {
    type master;
    file "/etc/bind/zones/esprit1.com.rev";
    allow-transfer { 192.168.1.102; };
};
```

## Étape 7 : Zone directe — `/etc/bind/zones/esprit1.com`
```bind
$TTL    604800
@       IN      SOA     ns1.esprit1.com. admin.esprit1.com. (
                        2026092101      ; Serial
                        604800          ; Refresh
                          86400         ; Retry
                        2419200         ; Expire
                         604800 )       ; Negative Cache TTL
;
@       IN      NS      ns1.esprit1.com.
@       IN      A       192.168.1.100
ns1     IN      A       192.168.1.100
www     IN      A       192.168.1.100
```

## Étape 8 : Zone inverse — `/etc/bind/zones/esprit1.com.rev`
```bind
$TTL    604800
@       IN      SOA     ns1.esprit1.com. admin.esprit1.com. (
                        2026092101      ; Serial
                        604800          ; Refresh
                          86400         ; Retry
                        2419200         ; Expire
                         604800 )       ; Negative Cache TTL
;
@       IN      NS      ns1.esprit1.com.
100     IN      PTR     ns1.esprit1.com.
```

## Étape 9 : Vérification de la syntaxe des zones
```
$ sudo named-checkzone esprit1.com /etc/bind/zones/esprit1.com
zone esprit1.com/IN: loaded serial 2026092101
OK

$ sudo named-checkzone 1.168.192.in-addr.arpa /etc/bind/zones/esprit1.com.rev
zone 1.168.192.in-addr.arpa/IN: loaded serial 2026092101
OK

$ sudo named-checkconf && echo "CONF OK"
CONF OK
```

## Étape 11 : Tests de résolution
```
$ dig www.esprit1.com @192.168.1.100 +noall +answer +comments
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 14709
www.esprit1.com.	604800	IN	A	192.168.1.100

$ dig -x 192.168.1.100 @192.168.1.100 +noall +answer +comments
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 14116
100.1.168.192.in-addr.arpa. 604800 IN	PTR	ns1.esprit1.com.
```
✅ Résolution **directe** (A → `192.168.1.100`) et **inverse** (PTR → `ns1.esprit1.com`) fonctionnelles.

---

# Partie 1 (suite) — Configuration du client

## Étape 12 : `/etc/systemd/resolved.conf`
```ini
[Resolve]
DNS=192.168.1.100
Domains=esprit1.com
```
## Étape 12 : `/etc/netplan/01-network-manager-all.yaml` (IP statique)
> ⚠️ Au premier démarrage, le fichier Netplan pré-installé référençait une **ancienne adresse MAC** (`00:0c:29:f4:46:47`) ≠ carte réelle (`00:0c:29:5e:f6:45`) → erreur *« Cannot find unique matching interface for ens33 »*. Résolution : suppression des fichiers Netplan obsolètes et création d'un fichier unique.

```yaml
network:
  version: 2
  renderer: NetworkManager
  ethernets:
    ens33:
      dhcp4: no
      addresses:
        - 192.168.1.100/24
      nameservers:
        address:
          - 192.168.1.100
        search:
          - esprit1.com
```
**Preuve :**
```
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> ... state UP ...
    inet 192.168.1.100/24 brd 192.168.1.255 scope global noprefixroute ens33
```

## Étape 12 : `/etc/hosts` + test navigateur
- Suppression de toute ligne `www.esprit1.com` dans `/etc/hosts` afin que la résolution passe **réellement par le DNS**.
- **Preuve navigateur :** ouverture de `http://esprit1.com` → page Apache **« Bienvenue sur esprit1.com »** affichée.

**Preuve `nslookup` :**
```
Server:		127.0.0.53
Address:	127.0.0.53#53

Non-authoritative answer:
Name:	esprit1.com
Address: 192.168.1.100
```

---

# Partie 2 — Serveur DNS Secondaire (esclave)

> Principe : une seule VM, deux adresses : le maître écoute sur `192.168.1.100`, une **seconde instance BIND9 indépendante** écoute sur l'alias `192.168.1.102` et copie la zone via **AXFR**.

## Étape 13 : Alias IP `192.168.1.102` (Netplan)
```yaml
      addresses:
        - 192.168.1.100/24
        - 192.168.1.102/24
```
**Preuve :**
```
2: ens33: ...
    inet 192.168.1.100/24 brd 192.168.1.255 scope global noprefixroute ens33
    inet 192.168.1.102/24 brd 192.168.1.255 scope global secondary noprefixroute ens33
```

## Étape 14 : Transfert de zone autorisé sur le maître
Directive `allow-transfer { 192.168.1.102; };` ajoutée dans les **deux** zones de `named.conf.local`, puis `sudo systemctl restart named`.

## Étape 15 : Instance esclave dédiée

### Arborescence
```bash
sudo mkdir -p /etc/bind-slave
sudo mkdir -p /var/cache/bind-slave
sudo chown bind:bind /var/cache/bind-slave
```

### `/etc/bind-slave/named.conf`
```conf
options {
    directory "/var/cache/bind-slave";

    listen-on port 53 { 192.168.1.102; };   # ⚠️ uniquement l'alias !
    listen-on-v6 { none; };

    allow-query { any; };
    recursion no;

    transfer-source 192.168.1.102;          # force l'adresse source AXFR
    pid-file "/run/named/named-slave.pid";
};

controls { };                                # désactivation de rndc (conflit maître/esclave)

zone "esprit1.com" {
    type slave;
    file "/var/cache/bind-slave/esprit1.com.slave";
    masters { 192.168.1.100; };
};

zone "1.168.192.in-addr.arpa" {
    type slave;
    file "/var/cache/bind-slave/esprit1.com.rev.slave";
    masters { 192.168.1.100; };
};
```

### AppArmor
> Problème rencontré : `open: /etc/bind-slave/named.conf: permission denied`. La règle par défaut confinait `named` à `/etc/bind/**` et `/var/cache/bind/**`.
> ⚠️ Point d'attention : les règles ajoutées en fin de fichier (après la fermeture `}`) ne sont **pas** prises en compte → erreur de parsing. Les règles ont été placées dans le fichier d'override officiel **`/etc/apparmor.d/local/usr.sbin.named`** (inclus par le profil via `#include <local/usr.sbin.named>`), puis profil rechargé :

```apparmor
  /etc/bind-slave/ r,
  /etc/bind-slave/** rk,
  /var/cache/bind-slave/ rw,
  /var/cache/bind-slave/** rwk,
  /run/named/** rwk,
```
```bash
sudo apparmor_parser -r /etc/apparmor.d/usr.sbin.named
```

### Vérification du transfert de zone (AXFR)
**Preuve :**
```
$ sudo ls -l /var/cache/bind-slave/
-rw-r--r-- 1 bind bind 292 Oct 12 01:32 esprit1.com.slave
-rw-r--r-- 1 bind bind 256 Oct 12 01:32 esprit1.com.rev.slave
-rw-r--r-- 1 bind bind 937 Oct 12 01:33 managed-keys.bind.jnl

$ dig www.esprit1.com @192.168.1.102 +noall +answer +comments
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 17269
www.esprit1.com.	604800	IN	A	192.168.1.100
```
✅ Les fichiers de zone esclaves existent → le transfert AXFR a bien eu lieu ; l'esclave répond aux requêtes.

## Étape 17 : Service systemd permanent `named-slave.service`
```ini
[Unit]
Description=BIND DNS Slave Server
Documentation=man:named(8)
After=network.target

[Service]
Type=forking
ExecStart=/usr/sbin/named -u bind -c /etc/bind-slave/named.conf
PIDFile=/run/named/named-slave.pid
Restart=on-failure

[Install]
WantedBy=multi-user.target
```
**Preuve (service actif et activé) :**
```
Created symlink '/etc/systemd/system/multi-user.target.wants/named-slave.service' → '/etc/systemd/system/named-slave.service'.
● named-slave.service - BIND DNS Slave Server
     Loaded: loaded (/etc/systemd/system/named-slave.service; enabled; preset: enabled)
     Active: active (running) ...
    Process: 29494 ExecStart=/usr/sbin/named -u bind -c /etc/bind-slave/named.conf (code=exited, status=0/SUCCESS)
```

## Étape 16 : Client configuré avec les deux serveurs
```ini
# /etc/systemd/resolved.conf
[Resolve]
DNS=192.168.1.100 192.168.1.102
Domains=esprit1.com
```
```yaml
# /etc/netplan/01-network-manager-all.yaml (extrait)
      nameservers:
        addresses:
          - 192.168.1.100
          - 192.168.1.102
        search:
          - esprit1.com
```
**Preuve attendue par le TP (obtenue) :**
```
Global
       DNS Servers: 192.168.1.100 192.168.1.102
        DNS Domain: esprit1.com
 Current DNS Server: 192.168.1.100

Link 2 (ens33)
    Current Scopes: DNS
       DNS Servers: 192.168.1.100 192.168.1.102
        DNS Domain: esprit1.com
     Default Route: yes
```
✅ La configuration figure bien dans les sections **Global** et **Link** (interface concernée), avec `Current DNS Server: 192.168.1.100`.

---

# Partie 3 — Test de bascule (failover) Maître → Esclave

## Étape 18 : Les deux serveurs répondent
```
$ dig www.esprit1.com @192.168.1.100 +short
192.168.1.100
$ dig www.esprit1.com @192.168.1.102 +short
192.168.1.100
```

## Étape 19 : Simulation de la panne du maître
```
$ sudo systemctl stop named
○ named.service - BIND Domain Name Server
     Active: inactive (dead)
```

## Étape 20 : Résolution côté client pendant la panne
```
$ resolvectl flush-caches
$ nslookup www.esprit1.com
Server:		127.0.0.53
Address:	127.0.0.53#53

Non-authoritative answer:
Name:	www.esprit1.com
Address: 192.168.1.100
```
✅ **La résolution continue de fonctionner** : le client tente `192.168.1.100` (indisponible) puis bascule automatiquement sur l'esclave `192.168.1.102` → bonne adresse renvoyée. La redondance est opérationnelle car l'esclave avait reçu une copie de la zone via AXFR avant la panne.

## Étape 21 : Restauration du maître
```
$ sudo systemctl start named
$ dig www.esprit1.com @192.168.1.100 +short
192.168.1.100
```
✅ Le maître est de nouveau fonctionnel après redémarrage.

---

## Difficultés rencontrées et solutions
1. **Fichier Netplan hérité avec ancienne adresse MAC** → impossible d'affecter `ens33` (*« Cannot find unique matching interface »*). Remplacé par un fichier Netplan unique propre.
2. **AppArmor trop restrictif** pour les chemins `/etc/bind-slave` et `/var/cache/bind-slave` (*permission denied*). Règles ajoutées dans `/etc/apparmor.d/local/usr.sbin.named` + rechargement du profil.
3. **Conflit d'écoute port 53** : l'esclave ne doit écouter **que** sur `192.168.1.102` (pas de `127.0.0.1`), sinon *« address already in use »* avec le maître.
4. **Adresse source du transfert AXFR** : `transfer-source 192.168.1.102` obligatoire pour que le maître accepte la requête d'un esclave partageant la même interface.
5. **Désactivation de `controls { };` (`rndc`) côté esclave** pour éviter que l'arrêt du maître coupe aussi l'esclave.

## Conclusion
Le serveur DNS BIND9 maître gère la zone `esprit1.com` (directe et inverse) et s'intègre au serveur web Apache (`http://esprit1.com`). La seconde instance BIND9, écoutant sur l'alias `192.168.1.102`, récupère la zone via AXFR et devient une **copie redondante** du maître. Le test de bascule démontre que, lors d'une panne du maître, les clients continuent de résoudre les noms sans intervention manuelle. La redondance DNS est donc correctement mise en œuvre.

---

*Rapport généré à partir des manipulations réellement réalisées sur la VM Ubuntu (BIND 9.20.24, kernel 7.0.0).*