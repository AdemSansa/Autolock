# AutoLoc API - Notes de l'Atelier 5 (Spring MVC & SpringDoc)

Application : `autoloc-api` - Spring Boot 4.1.1, Java 21, MySQL.
Préfixe commun défini une seule fois dans `application.properties` : `server.servlet.context-path=/api`.
Documentation interactive : `http://localhost:8080/api/swagger-ui.html` - description brute : `http://localhost:8080/api/v3/api-docs`.

## A. Endpoints

Toutes les routes sont préfixées par `/api` (context-path). Les contrôleurs n'écrivent donc pas `/api` dans `@RequestMapping`.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| AgenceController | GET /agences | IAgenceServices.findAll() | 200 | - |
| AgenceController | GET /agences/{id} | IAgenceServices.findById(id) | 200 | 404 |
| AgenceController | POST /agences | IAgenceServices.create(agence) | 201 | 400 |
| AgenceController | PUT /agences/{id} | IAgenceServices.update(agence) | 200 | 404, 400 |
| AgenceController | DELETE /agences/{id} | IAgenceServices.deleteById(id) | 204 | 404 |
| EmployeController | GET /employes | IEmployeServices.findAll() | 200 | - |
| EmployeController | GET /employes/{id} | IEmployeServices.findById(id) | 200 | 404 |
| EmployeController | POST /employes | IEmployeServices.create(employe) | 201 | 400 |
| EmployeController | PUT /employes/{id} | IEmployeServices.update(employe) | 200 | 404, 400 |
| EmployeController | DELETE /employes/{id} | IEmployeServices.deleteById(id) | 204 | 404 |
| VehiculeController | GET /vehicules | IVehiculeServices.findAll() | 200 | - |
| VehiculeController | GET /vehicules/{id} | IVehiculeServices.findById(id) | 200 | 404 |
| VehiculeController | POST /vehicules | IVehiculeServices.create(vehicule) | 201 | 400 |
| VehiculeController | PUT /vehicules/{id} | IVehiculeServices.update(vehicule) | 200 | 404, 400 |
| VehiculeController | DELETE /vehicules/{id} | IVehiculeServices.deleteById(id) | 204 | 404 |
| EquipementController | GET /equipements | IEquipementServices.findAll() | 200 | - |
| EquipementController | GET /equipements/{id} | IEquipementServices.findById(id) | 200 | 404 |
| EquipementController | POST /equipements | IEquipementServices.create(equipement) | 201 | 400 |
| EquipementController | PUT /equipements/{id} | IEquipementServices.update(equipement) | 200 | 404, 400 |
| EquipementController | DELETE /equipements/{id} | IEquipementServices.deleteById(id) | 204 | 404 |
| ClientController | GET /clients | IClientServices.findAll() | 200 | - |
| ClientController | GET /clients/{id} | IClientServices.findById(id) | 200 | 404 |
| ClientController | POST /clients | IClientServices.create(client) | 201 | 400 |
| ClientController | PUT /clients/{id} | IClientServices.update(client) | 200 | 404, 400 |
| ClientController | DELETE /clients/{id} | IClientServices.deleteById(id) | 204 | 404 |
| ReservationController | GET /reservations | IReservationServices.findAll() | 200 | - |
| ReservationController | GET /reservations/{id} | IReservationServices.findById(id) | 200 | 404 |
| ReservationController | POST /reservations | IReservationServices.create(reservation) | 201 | 400 |
| ReservationController | PUT /reservations/{id} | IReservationServices.update(reservation) | 200 | 404, 400 |
| ReservationController | DELETE /reservations/{id} | IReservationServices.deleteById(id) | 204 | 404 |
| ContratController | GET /contrats | IContratServices.findAll() | 200 | - |
| ContratController | GET /contrats/{id} | IContratServices.findById(id) | 200 | 404 |
| ContratController | POST /contrats | IContratServices.create(contrat) | 201 | 400 |
| ContratController | PUT /contrats/{id} | IContratServices.update(contrat) | 200 | 404, 400 |
| ContratController | DELETE /contrats/{id} | IContratServices.deleteById(id) | 204 | 404 |
| PaiementController | GET /paiements | IPaiementServices.findAll() | 200 | - |
| PaiementController | GET /paiements/{id} | IPaiementServices.findById(id) | 200 | 404 |
| MaintenanceController | GET /maintenances | IMaintenanceServices.findAll() | 200 | - |
| MaintenanceController | GET /maintenances/{id} | IMaintenanceServices.findById(id) | 200 | 404 |
| MaintenanceController | POST /maintenances | IMaintenanceServices.create(maintenance) | 201 | 400 |
| MaintenanceController | PUT /maintenances/{id} | IMaintenanceServices.update(maintenance) | 200 | 404, 400 |
| MaintenanceController | DELETE /maintenances/{id} | IMaintenanceServices.deleteById(id) | 204 | 404 |

`PaiementController` est volontairement en lecture seule : il n'expose que les deux GET. Un `POST /api/paiements` renvoie **405 Method Not Allowed**.

## B. Annotations Jackson

| Association | Annotation retenue | Justification |
|---|---|---|
| Contrat -> Paiement (`Contrat.paiements`) | `@JsonManagedReference("contrat-paiements")` | Composition : le contrat porte ses paiements (cascade persist/remove). On veut voir les paiements dans le JSON du contrat. |
| Paiement -> Contrat (`Paiement.contrat`) | `@JsonBackReference("contrat-paiements")` | Côté inverse de la composition : on coupe la boucle. La référence est écrite lors de la désérialisation (le POST renseigne bien le contrat), mais ignorée à la lecture. |
| Agence.vehicules / Agence.employes | @JsonIgnore | L'enfant (Vehicule, Employe) est créé seul en désignant son parent par `{"agence":{"idAgence":1}}`. Exposer la liste côté Agence serait inutile et risquerait des cycles Agence -> Vehicule -> Agence. |
| Vehicule.reservations / maintenances / Equipement.vehicules / Client.reservations | @JsonIgnore | Listes inverses de relations `@ManyToOne`/`@ManyToMany` : non nécessaires dans la réponse et sources de cycles. |
| Reservation.contrat | @JsonIgnore | Relation inverse `@OneToOne` non nulle : évite le cycle Reservation -> Contrat -> Reservation et le proxy LAZY non sérialisable. |

### Différence entre @JsonBackReference et @JsonIgnore

- **@JsonBackReference** : utilisée pour les associations symétriques (paire avec `@JsonManagedReference`). Le côté annoté est **sérialisé** en objet (via le managed reference) mais **ignoré lors de la (dé)sérialisation JSON** ; il ne casse pas la lisibilité métier puisqu'une des deux extrémités reste présente. Elle garantit qu'à la lecture seule la liste (managed) apparaît.
- **@JsonIgnore** : la propriété est **totalement** ignorée dans les deux sens (lecture et écriture). On l'utilise quand l'association n'a aucune raison d'apparaître dans le JSON (liste inverse, ou bien relation que l'on ne veut jamais accepter depuis le client).

En résumé : `@JsonManagedReference`/`@JsonBackReference` forment un couple « liste visible + référence masquée » pour casser une boucle tout en conservant une extrémité lisible ; `@JsonIgnore` masque complètement un attribut.

## B'. Cascade Contrat -> Paiement

`Contrat.paiements` porte `cascade = CascadeType.ALL, orphanRemoval = true` : un `POST /api/contrats` avec un tableau `paiements` crée le contrat **et** ses paiements (cascade PERSIST), et un `DELETE /api/contrats/{id}` supprime aussi ses paiements (cascade REMOVE). `PaiementController` reste en lecture seule : les paiements ne se créent pas seuls.

## C. Postman et Swagger

- **Postman** est le client de **test** : il envoie des requêtes HTTP réelles et permet de vérifier le statut et le corps (onglet Scripts > After response : `pm.response.to.have.status(201)`), et de rejouer une collection. Exemple concret de l'atelier : le dossier « Contrats » rejoue le CRUD complet (200 / 201 / 404 / 204) et le scénario de **cascade** (un contrat créé avec ses deux paiements, visibles ensuite dans `GET /paiements` sans création séparée).
- **Swagger UI** (généré par SpringDoc) est la **documentation vivante** : elle est reconstruite à partir des contrôleurs au démarrage, on y explore les endpoints, on lit les schémas déduits des entités, et on exécute une requête (« Try it out »). Exemple : `GET /api/contrats/{id}` y affiche les réponses 200 et 404 documentées par `@ApiResponse`.

Postman teste/vérifie ; Swagger documente/explore : les deux sont complémentaires.

## D. Anomalies SonarQube

| Règle | Explication | Correction |
|---|---|---|
| `java:S6813` - Field injection (`@Autowired` sur attribut) | L'injection par attribut masque les dépendances et empêche l'immuabilité / rend le test unitaire plus difficile. | Injection par constructeur ; ici via `@RequiredArgsConstructor` de Lombok sur un champ `private final`. |
| `java:S2386` - Exposer les entités JPA directement | Les entités sont renvoyées telles quelles par les contrôleurs : couplage API/base, champs internes exposés, boucles de sérialisation possibles. | Palliatif immédiat avec les annotations Jackson ; la solution propre (DTO) arrive au chapitre 10. |
| `java:S112` - Levée d'exception générique | Les services levaient `RuntimeException` au lieu d'une exception métier. | Remplacé par `ResourceNotFoundException`, traduite en 404 par `GlobalExceptionHandler`. |
| Import inutilisé (S1128) | Imports laissés après refactor. | Suppression des imports non utilisés (IDE / SonarQube for IDE). |

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode à appeler ?**
   Le `DispatcherServlet` reçoit toutes les requêtes HTTP. Il interroge les `HandlerMapping` (basés sur `@RequestMapping`/`@GetMapping`/...) pour trouver, selon le verbe et l'URL, la méthode de contrôleur correspondante, puis la délègue au `HandlerAdapter` qui l'invoque et sérialise le retour en JSON.

2. **Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?**
   Pour respecter la séparation en couches : le contrôleur ne fait que traduire HTTP <-> objet métier. Dépendre de l'interface (inversion de dépendance) découple le web de l'implémentation, facilite les tests (mocks) et interdit au contrôleur d'accéder directement à la base. `...ServiceImpl` est un détail d'implémentation qui peut changer.

3. **Pourquoi un POST renvoie 201 et un DELETE 204 ? Pourquoi une ressource absente donne 404 et non 500 ?**
   Un `POST` crée une ressource : `201 Created` (avec l'entité créée et son identifiant). Un `DELETE` réussit sans contenu à renvoyer : `204 No Content`. Une ressource absente est un cas **attendu** côté client, pas une erreur serveur : `ResourceNotFoundException` est traduite en `404 Not Found` par le `GlobalExceptionHandler`. Un `500` signifierait un bug serveur inattendu.

4. **Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST /api/paiements ?**
   Parce qu'un paiement n'existe que rattaché à un contrat (relation `@ManyToOne` obligatoire) : c'est le contrat qui porte la cascade. On ne crée donc pas de paiement orphelin via l'API. Un `POST /api/paiements` renvoie `405 Method Not Allowed` (aucun handler n'est mappé pour ce verbe sur cette route).

5. **Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?**
   Risques : couplage du contrat d'API au modèle de base, fuite de champs internes (clés, champs techniques), boucles de sérialisation (`StackOverflowError`) et proxies Hibernate LAZY non sérialisables. `Contrat.paiements` est une **composition** : les paiements font partie du contrat, on veut les voir et les créer avec lui -> `@JsonManagedReference` (couplé à `@JsonBackReference` côté Paiement). `Agence.vehicules` est une simple liste inverse : le véhicule est créé **seul** en désignant son agence par `{"agence":{"idAgence":1}}`, la liste n'ajoute rien et créerait un cycle -> `@JsonIgnore`.

## Rappel des choix techniques

- Un seul préfixe `/api` dans `application.properties` (`server.servlet.context-path=/api`).
- Contrôleurs minces, injection par constructeur via `@RequiredArgsConstructor`, dépendance aux seules interfaces `I...Services`.
- `GlobalExceptionHandler` (package `web.controller`) : `ResourceNotFoundException` -> 404, `IllegalArgumentException` -> 400, corps JSON `{statut, message, horodatage}`.
- `OpenApiConfig` (package `web.controller`) : titre « AutoLoc API », description, version 1.0.0.
- Annotations Swagger : `@Tag` (classe), `@Operation`, `@ApiResponses`/`@ApiResponse`, `@Parameter`.
- Sérialisation des entités : `spring.jpa.open-in-view=true` (le contrôleur renvoie les entités telles quelles) et palliatif `spring.jackson.serialization.fail-on-empty-beans=false` (le JSON contient `"hibernateLazyInitializer": {}`). Ces palliatifs disparaîtront avec les DTO.
