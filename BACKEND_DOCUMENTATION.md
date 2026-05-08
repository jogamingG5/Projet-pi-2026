# Documentation Backend - Modules Statistiques, Classement et Feuilles de Match

## Vue d'ensemble

Cette documentation décrit l'implémentation des trois modules backend pour gérer les statistiques des équipes, les classements et les feuilles de match du projet PI 2026.

## Architecture générale

L'architecture suit le pattern MVC avec les couches suivantes :
- **Models** : Entités JPA/MongoDB définissant la structure des données
- **Repositories** : Interfaces Spring Data pour l'accès à la base de données
- **Services** : Logique métier et traitement des données
- **Controllers** : Endpoints REST et gestion des requêtes HTTP
- **DTOs** : Objets de transfert de données pour les requêtes/réponses

---

## 1. Module : Feuilles de Match

### Description
Gère les feuilles de match avec les résultats et statistiques de chaque équipe (scores, cartons jaunes/rouges).

### Fichiers créés

#### Model : `FeuillesDeMatch.java`
```
Champs :
- id : String (identifiant unique)
- matchId : String (référence au match)
- recap : List<RecapEquipe> (détails de chaque équipe)
  - teamId : String
  - score : int
  - playerbookedYellowCards : List<String> (IDs des joueurs)
  - playerbookedRedCards : List<String> (IDs des joueurs)
- createdAt, updatedAt : LocalDateTime
```

#### Repository : `FeuillesDeMatchRepository.java`
```
Méthodes :
- findByMatchId(matchId) : Optional<FeuillesDeMatch>
- findAll() : List<FeuillesDeMatch>
```

#### Service : `FeuillesDeMatchService.java`
```
Méthodes principales :
- createFeuillesDeMatch(request) : crée une feuille de match
- getAllFeuillesDeMatch() : récupère toutes les feuilles
- getFeuillesDeMatchById(id) : récupère par ID
- getFeuillesDeMatchByMatchId(matchId) : récupère par ID du match
- updateFeuillesDeMatch(id, request) : met à jour
- deleteFeuillesDeMatch(id) : supprime
- getTeamScoreInMatch(feuillesDeMatchId, teamId) : récupère le score
- getTeamYellowCardsCount(feuillesDeMatchId, teamId) : compte cartons jaunes
- getTeamRedCardsCount(feuillesDeMatchId, teamId) : compte cartons rouges
```

#### Controller : `FeuillesDeMatchController.java`
```
Endpoints REST :
POST   /api/feuillesDeMatch                    - Créer une feuille de match
GET    /api/feuillesDeMatch                    - Récupérer toutes les feuilles
GET    /api/feuillesDeMatch/{id}               - Récupérer par ID
GET    /api/feuillesDeMatch/match/{matchId}    - Récupérer par Match ID
PUT    /api/feuillesDeMatch/{id}               - Mettre à jour
DELETE /api/feuillesDeMatch/{id}               - Supprimer
GET    /api/feuillesDeMatch/{id}/team/{teamId}/score        - Score d'une équipe
GET    /api/feuillesDeMatch/{id}/team/{teamId}/yellow-cards - Cartons jaunes
GET    /api/feuillesDeMatch/{id}/team/{teamId}/red-cards    - Cartons rouges
```

#### DTO : `FeuillesDeMatchRequest.java`
```
Champs :
- matchId : String (obligatoire)
- recap : List<RecapEquipeDTO> (obligatoire)
```

---

## 2. Module : Statistiques

### Description
Gère les statistiques des équipes (matchs joués, victoires, défaites, nuls, buts marqués/encaissés) par sport.

### Fichiers créés

#### Model : `Statistiques.java`
```
Champs :
- id : String (identifiant unique)
- teamId : String (référence à l'équipe)
- sportId : String (référence au sport)
- nbMatchsJoues : int
- nbVictoires : int
- nbDefaites : int
- nbNuls : int
- nbButsMarques : int
- nbButsEncaisses : int
- createdAt, updatedAt : LocalDateTime

Propriétés calculées :
- getNbPointsTotal() : int (victoires*3 + nuls)
- getDifferenceButsGoal() : double
- getTauxVictoire() : double (en %)
- getMoyenneButsMarques() : double
- getMoyenneButsEncaisses() : double
```

#### Repository : `StatistiquesRepository.java`
```
Méthodes :
- findByTeamIdAndSportId(teamId, sportId) : Optional<Statistiques>
- findByTeamId(teamId) : List<Statistiques>
- findBySportId(sportId) : List<Statistiques>
- findAll() : List<Statistiques>
```

#### Service : `StatistiquesService.java`
```
Méthodes principales :
- createStatistiques(request) : crée des statistiques
- getAllStatistiques() : récupère toutes
- getStatistiquesById(id) : récupère par ID
- getStatistiquesByTeamAndSport(teamId, sportId) : spécifique
- getStatistiquesByTeam(teamId) : toutes pour une équipe
- getStatistiquesBySport(sportId) : toutes pour un sport
- updateStatistiques(id, request) : met à jour
- deleteStatistiques(id) : supprime
- addVictoire(id, buts, butsEncaisses) : ajoute une victoire
- addDefaite(id, buts, butsEncaisses) : ajoute une défaite
- addNul(id, buts) : ajoute un nul
```

#### Controller : `StatistiquesController.java`
```
Endpoints REST :
POST   /api/statistiques                              - Créer
GET    /api/statistiques                              - Récupérer toutes
GET    /api/statistiques/{id}                         - Récupérer par ID
GET    /api/statistiques/team/{teamId}/sport/{sportId} - Spécifique
GET    /api/statistiques/team/{teamId}                - Par équipe
GET    /api/statistiques/sport/{sportId}              - Par sport
PUT    /api/statistiques/{id}                         - Mettre à jour
DELETE /api/statistiques/{id}                         - Supprimer
PUT    /api/statistiques/{id}/victoire?buts=X&butsEncaisses=Y - Ajouter victoire
PUT    /api/statistiques/{id}/defaite?buts=X&butsEncaisses=Y  - Ajouter défaite
PUT    /api/statistiques/{id}/nul?buts=X                      - Ajouter nul
```

#### DTO : `StatistiquesRequest.java`
```
Champs :
- teamId : String (obligatoire)
- sportId : String (obligatoire)
- nbMatchsJoues : int
- nbVictoires : int
- nbDefaites : int
- nbNuls : int
- nbButsMarques : int
- nbButsEncaisses : int
```

---

## 3. Module : Classement

### Description
Gère le classement des équipes basé sur les statistiques et performances dans les matchs.

### Fichiers créés

#### Model : `Classement.java`
```
Champs :
- id : String (identifiant unique)
- eventId : String (référence à l'événement)
- sportId : String (référence au sport)
- classements : List<ClassementEntry> (liste des équipes classées)
  - teamId : String
  - teamName : String
  - rang : int
  - matchsJoues : int
  - victoires, defaites, nuls : int
  - pointsTotal : int
  - butsMarques, butsEncaisses : int
  - differenceButsGoal : int
  - tauxVictoire : double
- createdAt, updatedAt : LocalDateTime

Comparaison auto (ClassementEntry implement Comparable) :
1. Points totaux (ordre décroissant)
2. Différence de buts (ordre décroissant)
3. Buts marqués (ordre décroissant)
```

#### Repository : `ClassementRepository.java`
```
Méthodes :
- findByEventId(eventId) : Optional<Classement>
- findBySportId(sportId) : Optional<Classement>
- findByEventIdAndSportId(eventId, sportId) : Optional<Classement>
- findBySportIdOrderByUpdatedAtDesc(sportId) : List<Classement>
- findAll() : List<Classement>
```

#### Service : `ClassementService.java`
```
Méthodes principales :
- createClassement(request) : crée un classement
- getAllClassements() : récupère tous
- getClassementById(id) : récupère par ID
- getClassementByEventId(eventId) : récupère par événement
- getClassementBySportId(sportId) : récupère par sport
- getClassementByEventIdAndSportId(eventId, sportId) : spécifique
- getClassementsBySportIdOrdered(sportId) : tous ordonnés par sport
- updateClassement(id, request) : met à jour
- deleteClassement(id) : supprime
- generateClassementFromStatistiques(eventId, sportId, teamIds) : génère basé sur stats
- getTeamRankingPosition(classementId, teamId) : position d'une équipe
```

#### Controller : `ClassementController.java`
```
Endpoints REST :
POST   /api/classement                                    - Créer
GET    /api/classement                                    - Récupérer tous
GET    /api/classement/{id}                               - Récupérer par ID
GET    /api/classement/event/{eventId}                    - Par événement
GET    /api/classement/sport/{sportId}                    - Par sport
GET    /api/classement/event/{eventId}/sport/{sportId}    - Spécifique
GET    /api/classement/sport/{sportId}/ordered            - Par sport ordonné
PUT    /api/classement/{id}                               - Mettre à jour
DELETE /api/classement/{id}                               - Supprimer
POST   /api/classement/generate?eventId=X&sportId=Y&teamIds=A,B,C - Générer classement
GET    /api/classement/{classementId}/team/{teamId}/position - Position équipe
```

#### DTO : `ClassementRequest.java`
```
Champs :
- eventId : String (obligatoire)
- sportId : String (obligatoire)
- classements : List<ClassementEntryDTO> (obligatoire)
```

---

## 4. Intégration avec d'autres modules

### Dépendances
- ✅ **Match** : FeuillesDeMatch référence les Match via `matchId`
- ✅ **Event** : Classement référence les Event via `eventId`
- ✅ **Statistics** : Classement génère les rangs basés sur les Statistiques

### Points d'intégration recommandés

#### Après completion d'un Match
1. Créer/récupérer une `FeuillesDeMatch`
2. Récupérer les `Statistiques` de chaque équipe
3. Mettre à jour les statistiques (addVictoire/addDefaite/addNul)
4. Régénérer le `Classement` avec `generateClassementFromStatistiques`

#### Exemple de workflow
```
1. Match créé et complété
2. FeuillesDeMatch enregistrée avec les scores et cartons
3. Statistiques des deux équipes mises à jour
4. Classement de l'événement/sport régénéré automatiquement
```

---

## 5. Validation et Sécurité

### Validations implémentées
- ✅ Tous les champs obligatoires sont marqués `@NotBlank` ou `@NotNull`
- ✅ Scores : `@Min(0)` pour éviter les valeurs négatives
- ✅ Les DTOs incluent les annotations Swagger/OpenAPI
- ✅ Les Services lèvent `MatchNotFoundException` pour les erreurs

### Annotations utilisées
- `@Valid` : validation au niveau du contrôleur
- `@JsonProperty` : mapping personnalisé pour JSON
- `@Document` : MongoDB collection mapping
- `@Repository` : injection Spring
- `@Service` : injection Spring

---

## 6. Base de données - Collections MongoDB

### Collections créées
```javascript
// Collection : feuillesDeMatch
db.feuillesDeMatch.insertOne({
  _id: ObjectId(),
  matchId: "...",
  recap: [
    {
      teamId: "...",
      score: 2,
      playerbookedYellowCards: ["player1", "player2"],
      playerbookedRedCards: ["player3"]
    }
  ],
  createdAt: new Date(),
  updatedAt: new Date()
})

// Collection : statistiques
db.statistiques.insertOne({
  _id: ObjectId(),
  teamId: "...",
  sportId: "...",
  nbMatchsJoues: 10,
  nbVictoires: 7,
  nbDefaites: 2,
  nbNuls: 1,
  nbButsMarques: 25,
  nbButsEncaisses: 12,
  createdAt: new Date(),
  updatedAt: new Date()
})

// Collection : classement
db.classement.insertOne({
  _id: ObjectId(),
  eventId: "...",
  sportId: "...",
  classements: [
    {
      teamId: "...",
      teamName: "Team A",
      rang: 1,
      matchsJoues: 10,
      victoires: 7,
      defaites: 2,
      nuls: 1,
      pointsTotal: 22,
      butsMarques: 25,
      butsEncaisses: 12,
      differenceButsGoal: 13,
      tauxVictoire: 70.0
    }
  ],
  createdAt: new Date(),
  updatedAt: new Date()
})
```

---

## 7. Tests API recommandés

### Feuilles de Match
```bash
# Créer une feuille de match
curl -X POST http://localhost:8080/api/feuillesDeMatch \
  -H "Content-Type: application/json" \
  -d '{
    "matchId": "match123",
    "recap": [
      {"teamId": "team1", "score": 2, "playerbookedYellowCards": [], "playerbookedRedCards": []},
      {"teamId": "team2", "score": 1, "playerbookedYellowCards": ["player1"], "playerbookedRedCards": []}
    ]
  }'

# Récupérer tous les feuillesDeMatch
curl http://localhost:8080/api/feuillesDeMatch

# Score d'une équipe
curl http://localhost:8080/api/feuillesDeMatch/feuilleId/team/team1/score
```

### Statistiques
```bash
# Créer des statistiques
curl -X POST http://localhost:8080/api/statistiques \
  -H "Content-Type: application/json" \
  -d '{"teamId": "team1", "sportId": "football"}'

# Ajouter une victoire
curl -X PUT "http://localhost:8080/api/statistiques/statsId/victoire?buts=2&butsEncaisses=1"

# Récupérer stats par équipe et sport
curl http://localhost:8080/api/statistiques/team/team1/sport/football
```

### Classement
```bash
# Générer un classement
curl -X POST "http://localhost:8080/api/classement/generate?eventId=event1&sportId=football&teamIds=team1,team2,team3"

# Récupérer classement par événement
curl http://localhost:8080/api/classement/event/event1

# Position d'une équipe
curl http://localhost:8080/api/classement/classementId/team/team1/position
```

---

## 8. Prochaines étapes

### Améliorations futures suggérées
1. Ajouter des événements pour notifier les changements de classement
2. Implémenter le cache pour les classements fréquemment accédés
3. Ajouter des statistiques par joueur
4. Générer automatiquement les classements après chaque match
5. Ajouter des filtres avancés (par date, par périodes)
6. Implémenter un historique des changements de position

---

## Résumé des fichiers

| Type | Nombre | Fichiers |
|------|--------|----------|
| Models | 3 | FeuillesDeMatch.java, Statistiques.java, Classement.java |
| Repositories | 3 | FeuillesDeMatchRepository.java, StatistiquesRepository.java, ClassementRepository.java |
| Services | 3 | FeuillesDeMatchService.java, StatistiquesService.java, ClassementService.java |
| Controllers | 3 | FeuillesDeMatchController.java, StatistiquesController.java, ClassementController.java |
| DTOs | 3 | FeuillesDeMatchRequest.java, StatistiquesRequest.java, ClassementRequest.java |
| **Total** | **15** | **fichiers Java créés** |

---

**Date de création** : 25 avril 2026
**Version** : 1.0
**Status** : Prêt pour intégration et tests
