# 🏗️ Architecture Backend - Modules Créés

## Diagramme des composants

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          COUCHE PRÉSENTATION (REST API)                     │
├─────────────────────────────────────────────────────────────────────────────┤
│
│  FeuillesDeMatchController    StatistiquesController    ClassementController
│  ├─ POST   /                  ├─ POST   /               ├─ POST   /
│  ├─ GET    /                  ├─ GET    /               ├─ GET    /
│  ├─ GET    /{id}              ├─ GET    /{id}           ├─ GET    /{id}
│  ├─ GET    /match/{matchId}   ├─ GET    /team/{teamId}  ├─ GET    /event/{eventId}
│  ├─ PUT    /{id}              ├─ PUT    /{id}           ├─ POST   /generate
│  ├─ DELETE /{id}              ├─ DELETE /{id}           ├─ GET    /{id}/team/{teamId}/position
│  ├─ GET    /{id}/team/{teamId}/score      ├─ PUT    /{id}/victoire   └─ ... (8 endpoints)
│  ├─ GET    /{id}/team/{teamId}/yellow-cards ├─ PUT    /{id}/defaite
│  └─ GET    /{id}/team/{teamId}/red-cards    └─ PUT    /{id}/nul
│
├─────────────────────────────────────────────────────────────────────────────┤
│                          COUCHE MÉTIER (Services)                           │
├─────────────────────────────────────────────────────────────────────────────┤
│
│  FeuillesDeMatchService      StatistiquesService        ClassementService
│  ├─ createFeuillesDeMatch()  ├─ createStatistiques()    ├─ createClassement()
│  ├─ getAllFeuillesDeMatch()  ├─ getAllStatistiques()    ├─ getAllClassements()
│  ├─ getFeuillesDeMatchById() ├─ getStatistiquesById()   ├─ getClassementById()
│  ├─ updateFeuillesDeMatch()  ├─ updateStatistiques()    ├─ updateClassement()
│  ├─ deleteFeuillesDeMatch()  ├─ deleteStatistiques()    ├─ deleteClassement()
│  ├─ getTeamScoreInMatch()    ├─ addVictoire()           ├─ generateClassementFromStatistiques()
│  ├─ getTeamYellowCardsCount()├─ addDefaite()            ├─ getTeamRankingPosition()
│  └─ getTeamRedCardsCount()   └─ addNul()                └─ ...
│
├─────────────────────────────────────────────────────────────────────────────┤
│                    COUCHE ACCÈS AUX DONNÉES (Repositories)                  │
├─────────────────────────────────────────────────────────────────────────────┤
│
│  FeuillesDeMatchRepository   StatistiquesRepository     ClassementRepository
│  ├─ findByMatchId()          ├─ findByTeamIdAndSportId()├─ findByEventId()
│  ├─ findAll()                ├─ findByTeamId()          ├─ findBySportId()
│  └─ ...                       ├─ findBySportId()        ├─ findByEventIdAndSportId()
│                               └─ findAll()              └─ findBySportIdOrderByUpdatedAtDesc()
│
├─────────────────────────────────────────────────────────────────────────────┤
│                          COUCHE MODÈLES (Entities)                          │
├─────────────────────────────────────────────────────────────────────────────┤
│
│  FeuillesDeMatch              Statistiques               Classement
│  ├─ @Document(feuillesDeMatch) ├─ @Document(statistiques)├─ @Document(classement)
│  ├─ id: String                ├─ id: String             ├─ id: String
│  ├─ matchId: String           ├─ teamId: String         ├─ eventId: String
│  ├─ recap: List<RecapEquipe>  ├─ sportId: String        ├─ sportId: String
│  │  ├─ teamId                 ├─ nbMatchsJoues: int      ├─ classements: List<ClassementEntry>
│  │  ├─ score: int             ├─ nbVictoires: int       │  ├─ teamId, teamName
│  │  ├─ yellowCards: List      ├─ nbDefaites: int        │  ├─ rang, matchsJoues
│  │  └─ redCards: List         ├─ nbNuls: int            │  ├─ victoires, defaites, nuls
│  ├─ createdAt, updatedAt      ├─ nbButsMarques: int      │  ├─ pointsTotal
│  └─                            ├─ nbButsEncaisses: int   │  ├─ butsMarques, butsEncaisses
│                                ├─ createdAt, updatedAt   │  ├─ differenceButsGoal
│                                └─ computed: points, taux  │  └─ tauxVictoire
│                                                           ├─ createdAt, updatedAt
│                                                           └─
│
├─────────────────────────────────────────────────────────────────────────────┤
│                          BASE DE DONNÉES (MongoDB)                          │
├─────────────────────────────────────────────────────────────────────────────┤
│
│  Collections:
│  ├─ db.feuillesDeMatch    (documents de feuilles de match)
│  ├─ db.statistiques       (documents de statistiques d'équipes)
│  └─ db.classement         (documents de classements)
│
└─────────────────────────────────────────────────────────────────────────────┘
```

## Flux de données

### Workflow après un match
```
1. Match complété
   ↓
2. FeuillesDeMatch créée/mise à jour
   ├─ Enregistrement des scores
   ├─ Cartons jaunes/rouges
   └─ Détails par équipe
   ↓
3. Statistiques mises à jour
   ├─ Appel service.addVictoire() ou addDefaite() ou addNul()
   ├─ Calcul des points totaux
   └─ Mise à jour des statistiques de l'équipe
   ↓
4. Classement régénéré
   ├─ Appel classementService.generateClassementFromStatistiques()
   ├─ Tri automatique des équipes
   ├─ Attribution des rangs
   └─ Mise à jour du classement
```

## Intégrations avec modules existants

```
                              ┌─────────────┐
                              │   EVENT     │
                              └────────┬────┘
                                       │
                                   eventId
                                       ↓
  ┌──────────────────┐      ┌─────────────────┐
  │   MATCH          │      │    CLASSEMENT   │
  ├──────────────────┤      ├─────────────────┤
  │ id               │◄─────┤ id              │
  │ team1Id          │      │ eventId         │
  │ team2Id          │  ─┐  │ sportId         │
  │ scoreTeam1       │   │  │ classements[...]│
  │ scoreTeam2       │   │  └─────────────────┘
  │ sportId          │   │
  │ ...              │   └─► matchId (via FeuillesDeMatch)
  └──────────────────┘

  ┌──────────────────┐      ┌─────────────────┐      ┌─────────────────┐
  │   TEAM           │      │  STATISTIQUES   │      │ FEUILLESDEMATCH │
  ├──────────────────┤      ├─────────────────┤      ├─────────────────┤
  │ id               │◄─────┤ teamId          │      │ id              │
  │ name             │  ─┐  │ sportId         │  ─┬─►│ matchId         │
  │ ...              │   │  │ nbMatchsJoues   │   │  │ recap[...]      │
  └──────────────────┘   │  │ nbVictoires     │   │  └─────────────────┘
                         │  │ nbDefaites      │   │
                         │  │ nbNuls          │   └──► team1Id, team2Id
                         │  │ nbButsMarques   │       (in recap)
                         │  │ nbButsEncaisses │
                         │  │ ...             │
                         └─►│                 │
                            └─────────────────┘
```

## Validation du flux de création

```
USER REQUEST (HTTP)
    ↓
[Validation] DTOs (Jakarta annotations)
    ↓
[Controller] Receives request
    ↓
[Service] Business logic processing
    ↓
[Repository] Persist to MongoDB
    ↓
[Response] Return created entity (HTTP 201/200)
    ↓
SWAGGER DOCUMENTATION (automatic)
```

## Points clés de l'architecture

✅ **Séparation des responsabilités**
- Controllers : gestion HTTP
- Services : logique métier
- Repositories : persistance
- Models : structure données

✅ **Réutilisabilité**
- Services utilisés par Controllers
- DTOs pour validation
- Computed properties dans Models

✅ **Scalabilité**
- Repositories Spring Data (requêtes optimisées)
- Services métier centralisés
- Collections MongoDB indexées

✅ **Maintenabilité**
- Annotation @Service, @Repository, @RestController
- Swagger/OpenAPI documentation
- Validation déclarative
- Gestion centralisée des erreurs

---

**Architecture complète et prête pour l'intégration! ✨**
