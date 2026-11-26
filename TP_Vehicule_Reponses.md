# TP Véhicule — DTO, MapStruct, Keywords, jointures et validation

## Réponses aux questions

### Question 1 — Pourquoi `VehiculeResponseDTO` contient `id` et `statut`, mais pas `VehiculeRequestDTO` ?

- **`id`** : il est généré par la base (`@GeneratedValue`). Le client ne doit pas le choisir à la création ; il le reçoit dans la réponse pour pouvoir ensuite consulter, modifier ou supprimer le véhicule. À la modification, l'id est passé dans l'URL (`PUT /vehicules/{id}`), pas dans le corps.
- **`statut`** : c'est un état géré par l'application, pas une donnée saisie. Un véhicule est créé `DISPONIBLE` (`@Mapping(target = "statut", constant = "DISPONIBLE")`), puis son statut évolue selon les règles métier (passage en `MAINTENANCE` lors d'une maintenance, `LOUE` lors d'une location). Si le client pouvait l'envoyer, il pourrait contourner ces règles.
- Le DTO de requête ne contient donc que ce que le client a le droit de saisir ; le DTO de réponse contient ce qu'il a besoin de lire.

### Question 2 — Pourquoi `toDto` doit-il s'exécuter dans la méthode `@Transactional` du service et pas dans le contrôleur ?

- `agence` est en `FetchType.LAZY` et `equipements` est une collection paresseuse. `toDto` lit `agence.nom` et parcourt `equipements` : ces accès déclenchent des requêtes SQL, qui exigent une session Hibernate ouverte.
- Dans le service, la transaction est ouverte : le chargement fonctionne. Dans le contrôleur, la transaction est terminée : on obtient une `LazyInitializationException` (ou, avec `spring.jpa.open-in-view=true`, des requêtes lancées hors transaction pendant la sérialisation, ce qui est une mauvaise pratique).
- C'est aussi le respect de l'architecture en couches : le service renvoie des DTO, le contrôleur ne manipule jamais d'entité.
- Avec J7 (`findByIdAvecEquipements`, `LEFT JOIN FETCH`), `findById` charge le véhicule, son agence et ses équipements en **une seule requête** au lieu d'une requête par association (problème N+1).

Requête générée par J7 (logs) :

```sql
select ... from vehicule v1_0
left join agence a1_0 on a1_0.id_agence = v1_0.agence_id
left join vehicule_equipement e1_0 on v1_0.id_vehicule = e1_0.vehicule_id
left join equipement e1_1 on e1_1.id_equipement = e1_0.equipement_id
where v1_0.id_vehicule = ?
```

### Question 3 — Pourquoi placer les contraintes de validation sur le DTO de requête plutôt que sur l'entité `Vehicule` ?

- Le DTO représente **ce que le client envoie** : on valide l'entrée à la frontière de l'API, **avant** d'atteindre le service, et on renvoie un 400 lisible champ par champ.
- Les règles d'entrée peuvent varier selon le cas d'usage (création, modification, création avec équipements) alors que l'entité est unique. Par exemple `agenceId` est obligatoire dans la requête, alors que l'entité contient un objet `Agence`, pas un id.
- Sur l'entité, la validation ne se déclencherait qu'à la persistance (trop tard, avec une `ConstraintViolationException` en 500 si non gérée) et mélangerait règles de saisie et modèle de persistance.
- L'entité garde ses contraintes de base de données (`nullable`, `unique`, `length`) ; le DTO porte les règles de saisie (`@NotBlank`, `@Pattern`, `@Positive`, ...).

## Endpoints ajoutés (préfixe `/api/vehicules`)

| Étape | Méthode | Endpoint |
|---|---|---|
| 4 | `GET` | `/` (résumé), `/{id}` (détaillé, J7) |
| 4-5 | `POST` | `/` → 201 (validation `@Valid`, 409 si immatriculation déjà utilisée) |
| K1 | `GET` | `/recherche/marque/{marque}` |
| K2 | `GET` | `/recherche/categorie/{categorie}/statut/{statut}` |
| K3 | `GET` | `/recherche/tarif/{min}/{max}` |
| K4 | `GET` | `/recherche/modele/{motCle}` |
| K5 | `GET` | `/recherche/categories/{categories}` (ex. `CITADINE,SUV`) |
| K6 | `GET` | `/recherche/existe/{immatriculation}` |
| K7 | `GET` | `/recherche/nombre/statut/{statut}` |
| K8 | `GET` | `/recherche/moins-chers` |
| K9 | `GET` | `/recherche/agence/{agenceId}` |
| K10 | `GET` | `/recherche/sans-agence` |
| J1 | `GET` | `/ville/{ville}` |
| J2 | `GET` | `/equipement/{libelle}` |
| J3 | `GET` | `/equipements/min/{n}` |
| J4 | `GET` | `/client/{clientId}` |
| J5 | `GET` | `/jamais-reserves` |
| J6 | `GET` | `/maintenance/{date}` (AAAA-MM-JJ) |
| J7 | `GET` | `/{id}` (utilisée par `findById`) |
| J8 | `GET` | `/stats/par-agence` |
