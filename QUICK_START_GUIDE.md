# 🚀 Guide de Démarrage Rapide - Backend Modules

## 1️⃣ Compilation et démarrage

### Build du projet
```bash
cd back/
mvn clean install
```

### Lancer l'application
```bash
mvn spring-boot:run
```

### Accéder à Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## 2️⃣ Exemples d'utilisation rapide

### A) Créer une Feuille de Match

```bash
curl -X POST http://localhost:8080/api/feuillesDeMatch \
  -H "Content-Type: application/json" \
  -d '{
    "matchId": "match123",
    "recap": [
      {
        "teamId": "team1",
        "score": 2,
        "playerbookedYellowCards": ["joueur1"],
        "playerbookedRedCards": []
      },
      {
        "teamId": "team2",
        "score": 1,
        "playerbookedYellowCards": [],
        "playerbookedRedCards": ["joueur2"]
      }
    ]
  }'
```

**Réponse:**
```json
{
  "id": "65b8a1c2d4e5f6g7h8i9j0k1",
  "matchId": "match123",
  "recap": [...],
  "createdAt": "2026-04-25T10:30:00",
  "updatedAt": "2026-04-25T10:30:00"
}
```

---

### B) Créer des Statistiques

```bash
curl -X POST http://localhost:8080/api/statistiques \
  -H "Content-Type: application/json" \
  -d '{
    "teamId": "team1",
    "sportId": "football"
  }'
```

**Réponse:**
```json
{
  "id": "65b8a1c2d4e5f6g7h8i9j0k2",
  "teamId": "team1",
  "sportId": "football",
  "nbMatchsJoues": 0,
  "nbVictoires": 0,
  "nbDefaites": 0,
  "nbNuls": 0,
  "nbButsMarques": 0,
  "nbButsEncaisses": 0,
  "createdAt": "2026-04-25T10:30:00"
}
```

---

### C) Ajouter une Victoire

```bash
curl -X PUT "http://localhost:8080/api/statistiques/65b8a1c2d4e5f6g7h8i9j0k2/victoire?buts=2&butsEncaisses=1"
```

**Réponse:**
```json
{
  "id": "65b8a1c2d4e5f6g7h8i9j0k2",
  "teamId": "team1",
  "sportId": "football",
  "nbMatchsJoues": 1,
  "nbVictoires": 1,
  "nbDefaites": 0,
  "nbNuls": 0,
  "nbButsMarques": 2,
  "nbButsEncaisses": 1,
  "createdAt": "2026-04-25T10:30:00"
}
```

---

### D) Générer un Classement

```bash
curl -X POST "http://localhost:8080/api/classement/generate?eventId=event1&sportId=football&teamIds=team1,team2,team3"
```

**Réponse:**
```json
{
  "id": "65b8a1c2d4e5f6g7h8i9j0k3",
  "eventId": "event1",
  "sportId": "football",
  "classements": [
    {
      "teamId": "team1",
      "teamName": "team1",
      "rang": 1,
      "matchsJoues": 1,
      "victoires": 1,
      "defaites": 0,
      "nuls": 0,
      "pointsTotal": 3,
      "butsMarques": 2,
      "butsEncaisses": 1,
      "differenceButsGoal": 1,
      "tauxVictoire": 100.0
    }
  ],
  "createdAt": "2026-04-25T10:30:00"
}
```

---

### E) Récupérer un Classement

```bash
# Par événement
curl http://localhost:8080/api/classement/event/event1

# Par sport
curl http://localhost:8080/api/classement/sport/football

# Spécifique
curl http://localhost:8080/api/classement/event/event1/sport/football

# Position d'une équipe
curl http://localhost:8080/api/classement/65b8a1c2d4e5f6g7h8i9j0k3/team/team1/position
```

---

## 3️⃣ Workflow complet d'intégration

### Scénario: Gestion d'un match terminé

```typescript
// 1. Match complété
// Notification de MatchService

// 2. Créer FeuillesDeMatch
POST /api/feuillesDeMatch {
  matchId: match.id,
  recap: [
    { teamId: team1.id, score: 2, yellowCards: [], redCards: [] },
    { teamId: team2.id, score: 1, yellowCards: ["player1"], redCards: [] }
  ]
}

// 3. Récupérer/créer Statistiques des deux équipes
GET /api/statistiques/team/{team1.id}/sport/{match.sportId}
GET /api/statistiques/team/{team2.id}/sport/{match.sportId}

// 4. Mettre à jour les statistiques
// Team 1 a gagné 2-1
PUT /api/statistiques/{stats1.id}/victoire?buts=2&butsEncaisses=1

// Team 2 a perdu 1-2
PUT /api/statistiques/{stats2.id}/defaite?buts=1&butsEncaisses=2

// 5. Récupérer toutes les équipes du sport/événement
GET /api/statistiques/sport/{sport.id}

// 6. Générer/régénérer le classement
POST /api/classement/generate?eventId={event.id}&sportId={sport.id}&teamIds=team1,team2,team3,...

// 7. Récupérer le classement mis à jour
GET /api/classement/event/{event.id}/sport/{sport.id}
```

---

## 4️⃣ Intégration avec MatchController

### Modifier MatchController.java

```java
// Dans la méthode updateMatch(), après completion

@PutMapping("/{id}")
public ResponseEntity<Match> updateMatch(@PathVariable String id, @RequestBody MatchRequest request) {
    Match updatedMatch = matchService.updateMatch(id, request);
    
    // Si le match est maintenant COMPLETED
    if (updatedMatch.getStatus() == Match.MatchStatus.COMPLETED) {
        // 1. Créer/mettre à jour FeuillesDeMatch
        FeuillesDeMatchRequest feuillesRequest = new FeuillesDeMatchRequest(
            updatedMatch.getId(),
            Arrays.asList(
                new FeuillesDeMatchRequest.RecapEquipeDTO(
                    updatedMatch.getTeam1Id(),
                    updatedMatch.getScoreTeam1(),
                    new ArrayList<>(),
                    new ArrayList<>()
                ),
                new FeuillesDeMatchRequest.RecapEquipeDTO(
                    updatedMatch.getTeam2Id(),
                    updatedMatch.getScoreTeam2(),
                    new ArrayList<>(),
                    new ArrayList<>()
                )
            )
        );
        feuillesDeMatchService.createFeuillesDeMatch(feuillesRequest);
        
        // 2. Mettre à jour Statistiques
        updateTeamStatistics(updatedMatch);
        
        // 3. Régénérer Classement
        regenerateClassement(updatedMatch);
    }
    
    return ResponseEntity.ok(updatedMatch);
}

private void updateTeamStatistics(Match match) {
    Optional<Statistiques> stats1 = statistiquesService.getStatistiquesByTeamAndSport(
        match.getTeam1Id(), match.getSportId()
    );
    Optional<Statistiques> stats2 = statistiquesService.getStatistiquesByTeamAndSport(
        match.getTeam2Id(), match.getSportId()
    );
    
    // Team 1
    if (stats1.isPresent()) {
        if (match.getScoreTeam1() > match.getScoreTeam2()) {
            statistiquesService.addVictoire(stats1.get().getId(), 
                match.getScoreTeam1(), match.getScoreTeam2());
        } else if (match.getScoreTeam1() < match.getScoreTeam2()) {
            statistiquesService.addDefaite(stats1.get().getId(), 
                match.getScoreTeam1(), match.getScoreTeam2());
        } else {
            statistiquesService.addNul(stats1.get().getId(), match.getScoreTeam1());
        }
    }
    
    // Team 2 (inverse)
    if (stats2.isPresent()) {
        if (match.getScoreTeam2() > match.getScoreTeam1()) {
            statistiquesService.addVictoire(stats2.get().getId(), 
                match.getScoreTeam2(), match.getScoreTeam1());
        } else if (match.getScoreTeam2() < match.getScoreTeam1()) {
            statistiquesService.addDefaite(stats2.get().getId(), 
                match.getScoreTeam2(), match.getScoreTeam1());
        } else {
            statistiquesService.addNul(stats2.get().getId(), match.getScoreTeam2());
        }
    }
}

private void regenerateClassement(Match match) {
    List<String> teamIds = // récupérer toutes les équipes de l'événement
    classementService.generateClassementFromStatistiques(
        match.getEventId(), 
        match.getSportId(), 
        teamIds
    );
}
```

---

## 5️⃣ Tester les endpoints dans Postman

### 1. Créer une collection
- File → New → Collection

### 2. Ajouter des requêtes

**Request 1: Create FeuillesDeMatch**
```
POST http://localhost:8080/api/feuillesDeMatch
Headers: Content-Type: application/json
Body: (voir section 2A)
```

**Request 2: Create Statistiques**
```
POST http://localhost:8080/api/statistiques
Headers: Content-Type: application/json
Body: (voir section 2B)
```

**Request 3: Add Victoire**
```
PUT http://localhost:8080/api/statistiques/{{statsId}}/victoire?buts=2&butsEncaisses=1
```

**Request 4: Generate Classement**
```
POST http://localhost:8080/api/classement/generate?eventId=event1&sportId=football&teamIds=team1,team2,team3
```

**Request 5: Get Classement**
```
GET http://localhost:8080/api/classement/event/event1/sport/football
```

---

## 6️⃣ Vérifier la compilation

```bash
# Vérifier les erreurs
mvn compile

# Voir les warnings
mvn clean install -DskipTests

# Lancer les tests (si existants)
mvn test
```

---

## 7️⃣ Dépannage courant

### Erreur: "Cannot find symbol"
```
❌ Problem: Import manquant
✅ Solution: Exécuter mvn clean install
```

### Erreur: "No matching constructor found"
```
❌ Problem: Constructeur mal appelé
✅ Solution: Vérifier les constructeurs dans les models
```

### Erreur: "MongoRepository not found"
```
❌ Problem: Spring Data MongoDB non trouvé
✅ Solution: Vérifier les dépendances dans pom.xml
```

### Erreur 404: Endpoint not found
```
❌ Problem: Mapping incorrect
✅ Solution: Vérifier @RequestMapping et les paths
```

---

## 8️⃣ Performance et optimisation

### Ajouter des index MongoDB

```javascript
// Dans MongoDB shell
db.statistiques.createIndex({ "teamId": 1, "sportId": 1 })
db.classement.createIndex({ "eventId": 1, "sportId": 1 })
db.feuillesDeMatch.createIndex({ "matchId": 1 })
```

### Utiliser les projections

```java
// Dans le repository
@Query(fields = "{ 'classements': 1 }")
Optional<Classement> findByEventIdProjected(String eventId);
```

---

## 9️⃣ Déploiement

### Build JAR
```bash
mvn clean package -DskipTests
java -jar back/target/project-pi-0.0.1-SNAPSHOT.jar
```

### Docker (optionnel)
```dockerfile
FROM openjdk:17
COPY back/target/project-pi.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

---

## 🔟 Support et documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Fichiers docs**: BACKEND_DOCUMENTATION.md
- **Architecture**: ARCHITECTURE_DIAGRAM.md
- **Checklist**: BACKEND_MODULES_CHECKLIST.md

---

**✨ Vous êtes prêt pour démarrer !**
