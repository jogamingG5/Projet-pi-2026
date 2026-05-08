# Frontend - Modules Statistiques et Classement

## 📋 Structure du Projet

Le frontend intègre maintenant les trois nouveaux modules pour gérer les statistiques, le classement et les feuilles de match :

```
front/streetleague/src/app/
├── models/
│   └── statistiques.model.ts          # Interfaces TypeScript pour les 3 modules
├── services/
│   ├── statistiques.service.ts        # Services HTTP pour les 3 modules
│   ├── match.service.ts               # (Existant)
│   └── event.service.ts               # (Existant)
├── pages/
│   ├── statistiques-detail/
│   │   ├── statistiques.component.ts
│   │   ├── statistiques.component.html
│   │   └── statistiques.component.css
│   ├── classement-detail/
│   │   ├── classement.component.ts
│   │   ├── classement.component.html
│   │   └── classement.component.css
│   ├── feuillesdematch-detail/
│   │   ├── feuillesdematch.component.ts
│   │   ├── feuillesdematch.component.html
│   │   └── feuillesdematch.component.css
│   └── [autres pages existantes]
├── app.routes.ts                      # Routes mises à jour
└── app.html                            # Navigation mise à jour
```

## 🎯 Modules Implémentés

### 1. **Statistiques (📊 Statistiques)**
Route: `/statistiques`

**Fonctionnalités:**
- Affichage de toutes les statistiques des équipes
- Recherche par équipe
- Recherche par sport
- Recherche combinée (équipe + sport)
- Statistiques détaillées:
  - Matchs joués
  - Points totaux
  - Différence de buts
  - Taux de victoire
  - Résultats (V-D-N)
  - Buts marqués/encaissés
  - Moyennes par match
  - Performance graphique

**Données affichées:**
```typescript
interface Statistiques {
  id: string;
  teamId: string;
  sportId: string;
  nbMatchsJoues: number;
  nbVictoires: number;
  nbDefaites: number;
  nbNuls: number;
  nbButsMarques: number;
  nbButsEncaisses: number;
  createdAt: Date;
  updatedAt: Date;
}
```

**Calculs automatiques:**
- Points totaux = (victoires × 3) + nuls
- Différence buts = buts marqués - buts encaissés
- Taux victoire = (victoires / matchs joués) × 100
- Moyennes = totaux / matchs joués

---

### 2. **Classement (🏆 Classement)**
Route: `/classement`

**Fonctionnalités:**
- Affichage du classement complet par événement et sport
- Tri automatique par:
  1. Points totaux (décroissant)
  2. Différence de buts (décroissant)
  3. Buts marqués (décroissant)
- Recherche par événement + sport
- Médailles (🥇 🥈 🥉) pour les 3 premiers
- Barre de progression pour taux de victoire
- État disciplinaire des équipes

**Données affichées:**
```typescript
interface ClassementEntry {
  rang: number;
  teamId: string;
  teamName: string;
  matchsJoues: number;
  victoires: number;
  defaites: number;
  nuls: number;
  pointsTotal: number;
  butsMarques: number;
  butsEncaisses: number;
  differenceButsGoal: number;
  tauxVictoire: number;
}

interface Classement {
  id: string;
  eventId: string;
  sportId: string;
  classements: ClassementEntry[];
  createdAt: Date;
  updatedAt: Date;
}
```

**Statistiques de synthèse:**
- Total matchs joués
- Meilleure attaque
- Meilleure défense
- Total buts

---

### 3. **Feuilles de Match (📋 Feuilles)**
Route: `/feuillesdematch`

**Fonctionnalités:**
- Affichage de toutes les feuilles de match
- Recherche par ID de match
- Détails complets du résultat
- Cartes jaunes/rouges par équipe
- État disciplinaire (bon standing / attention / expulsion)
- Export en PDF (fonction prévue)
- Suppression de feuilles

**Données affichées:**
```typescript
interface RecapEquipe {
  teamId: string;
  score: number;
  nbCartesJaunes: number;
  nbCartesRouges: number;
}

interface FeuillesDeMatch {
  id: string;
  matchId: string;
  dateMatch: Date;
  recapEquipes: RecapEquipe[];
  note?: string;
  createdAt: Date;
  updatedAt: Date;
}
```

---

## 🔌 Services HTTP

### **FeuillesDeMatchService**
```typescript
getAll()                                    // Récupère toutes les feuilles
getById(id: string)                         // Récupère une feuille par ID
getByMatchId(matchId: string)               // Récupère une feuille par match ID
create(request)                             // Crée une nouvelle feuille
update(id: string, request)                 // Met à jour une feuille
delete(id: string)                          // Supprime une feuille
getTeamScore(feuillesDeMatchId, teamId)     // Score d'une équipe
getTeamYellowCards(feuillesDeMatchId, teamId)
getTeamRedCards(feuillesDeMatchId, teamId)
```

### **StatistiquesService**
```typescript
getAll()                                    // Toutes les statistiques
getById(id: string)
getByTeamAndSport(teamId, sportId)          // Recherche combinée
getByTeam(teamId)                           // Par équipe
getBySport(sportId)                         // Par sport
create(request)
update(id: string, request)
delete(id: string)
addVictoire(id, buts, butsEncaisses)        // Mise à jour match gagné
addDefaite(id, buts, butsEncaisses)         // Mise à jour match perdu
addNul(id, buts)                            // Mise à jour match nul
```

### **ClassementService**
```typescript
getAll()                                    // Tous les classements
getById(id: string)
getByEventId(eventId: string)               // Par événement
getBySportId(sportId: string)               // Par sport
getByEventIdAndSportId(eventId, sportId)    // Combiné
getBySportIdOrdered(sportId: string)        // Trié automatiquement
create(request)
update(id: string, request)
delete(id: string)
generate(eventId, sportId, teamIds)         // Génère depuis statistiques
getTeamPosition(classementId, teamId)       // Position d'une équipe
```

---

## 🎨 Design et Styles

- **Couleurs:**
  - Bleu: Actions et highlights (#3B82F6)
  - Vert: Victoires (#10B981)
  - Rouge: Défaites (#EF4444)
  - Jaune: Cartes jaunes (#FBBF24)
  - Gris: Nuls et informations neutres

- **Composants visuels:**
  - Cartes avec gradients
  - Barres de progression animées
  - Ombres et transitions fluides
  - Icônes emojis pour meilleure UX
  - Mise en page responsive

---

## 🚀 Navigation

### Barre de navigation mise à jour:
```
[Street League] | Matches | Events | 📊 Statistiques | 🏆 Classement | 📋 Feuilles
```

Chaque lien navigue vers la page correspondante avec soulignement actif.

---

## 📝 Utilisation

### Accéder aux Statistiques:
1. Cliquez sur "📊 Statistiques" dans la barre de navigation
2. Vous pouvez:
   - Voir toutes les statistiques des équipes
   - Filtrer par équipe
   - Filtrer par sport
   - Filtrer par équipe ET sport

### Accéder au Classement:
1. Cliquez sur "🏆 Classement" dans la barre de navigation
2. Vous pouvez:
   - Voir tous les classements disponibles
   - Rechercher un classement par événement + sport
   - Observer le tri automatique (points, différence buts, buts marqués)
   - Voir les médailles et la barre de progression

### Accéder aux Feuilles de Match:
1. Cliquez sur "📋 Feuilles" dans la barre de navigation
2. Vous pouvez:
   - Voir toutes les feuilles de match
   - Rechercher par ID de match
   - Voir les résultats détaillés
   - Voir les cartes (jaunes/rouges)
   - Supprimer une feuille

---

## 🔄 Flux d'Intégration Backend

### Architecture client-serveur:

```
Frontend (Angular)
    ↓
HTTP Services (statistiques.service.ts)
    ↓
REST API (http://localhost:8080/api)
    ↓
Backend (Spring Boot)
    ↓
MongoDB (Collections: feuillesDeMatch, statistiques, classement)
```

---

## ✅ Configuration Requise

1. **Backend en cours d'exécution:**
   ```bash
   cd back
   mvn spring-boot:run
   ```
   - API disponible sur `http://localhost:8080/api`

2. **Frontend en cours d'exécution:**
   ```bash
   cd front/streetleague
   npm start  # ou ng serve
   ```
   - Frontend disponible sur `http://localhost:4200`

3. **MongoDB en cours d'exécution:**
   - Collections créées: `feuillesDeMatch`, `statistiques`, `classement`

---

## 🐛 Dépannage

### Les données ne s'affichent pas?
1. Vérifiez que le backend est en cours d'exécution sur `http://localhost:8080`
2. Vérifiez que MongoDB est connecté
3. Vérifiez la console du navigateur (F12) pour les erreurs

### Les routes ne fonctionnent pas?
1. Vérifiez que `app.routes.ts` est à jour
2. Vérifiez que les composants sont importés correctement
3. Rechargez la page

### Les styles ne s'appliquent pas?
1. Vérifiez que Tailwind CSS 4.1.12 est installé
2. Vérifiez les fichiers `.css` des composants
3. Nettoyez le cache du navigateur

---

## 📚 Ressources

- [Modèles TypeScript](./src/app/models/statistiques.model.ts)
- [Services HTTP](./src/app/services/statistiques.service.ts)
- [Routing Configuration](./src/app/app.routes.ts)
- [Backend API Documentation](../../IMPLEMENTATION_GUIDE.md)

---

## ✨ Améliorations Futures

- [ ] Graphiques de statistiques (Chart.js/Plotly)
- [ ] Export en PDF/CSV
- [ ] Comparaison d'équipes
- [ ] Historique de matchs
- [ ] Filtres avancés
- [ ] Notifications de résultats en temps réel
- [ ] Calendrier des matchs
- [ ] Animations de matchs

---

**Créé:** 2024
**Projet:** Street League Management System
**Version:** 2.0 (Avec modules Statistiques et Classement)
