# 🚀 Guide de Démarrage Rapide - Frontend Modules

## ⏱️ Temps Estimé: 5-10 minutes

---

## 📋 Pré-requis

- ✅ Backend en cours d'exécution sur `http://localhost:8080`
- ✅ MongoDB connecté
- ✅ Node.js 20+ installé
- ✅ Frontend stocké dans `front/streetleague/`

---

## 🚀 Démarrage du Frontend

### Étape 1: Ouvrir Terminal
```bash
cd front/streetleague
```

### Étape 2: Installer les Dépendances (si première fois)
```bash
npm install
```

### Étape 3: Démarrer le Serveur de Développement
```bash
npm start
```

Ou:
```bash
ng serve
```

### Étape 4: Accéder à l'Application
Ouvrez votre navigateur:
```
http://localhost:4200
```

---

## 🎯 Tester les Nouveaux Modules

### Via la Barre de Navigation

La barre de navigation contient maintenant:
```
[Street League] | Matches | Events | 📊 Statistiques | 🏆 Classement | 📋 Feuilles
```

### 1️⃣ Tester Statistiques

1. Cliquez sur **"📊 Statistiques"**
2. Vous verrez trois options de filtrage:
   - **Tous** - Affiche toutes les statistiques
   - **Par Équipe** - Entrez un ID d'équipe
   - **Par Sport** - Entrez un ID de sport

3. Exemple de test:
   ```
   Filtre: "Par Équipe"
   ID Équipe: "team_1" (ou un ID existant)
   Cliquez "Rechercher"
   ```

4. Vous verrez:
   - Cartes de statistiques principales
   - Grille détaillée (Résultats, Buts, Performance)
   - Barres de progression
   - Métadonnées (créé, modifié)

### 2️⃣ Tester Classement

1. Cliquez sur **"🏆 Classement"**
2. Vous verrez:
   - Liste des classements disponibles
   - Formulaire de recherche

3. Exemple de test:
   ```
   ID Événement: "event_1"
   ID Sport: "football"
   Cliquez "Rechercher"
   ```

4. Vous verrez:
   - Tableau complet du classement
   - Médailles (🥇 🥈 🥉) pour les 3 premiers
   - Codes couleur pour les positions
   - Barres de progression pour taux de victoire
   - Synthèse statistique

### 3️⃣ Tester Feuilles de Match

1. Cliquez sur **"📋 Feuilles"**
2. Vous verrez:
   - Liste des feuilles de match disponibles
   - Formulaire de recherche

3. Exemple de test:
   ```
   ID Match: "match_1"
   Cliquez "Chercher"
   ```

4. Vous verrez:
   - Résultat final en grand
   - Cartes d'équipes avec détails
   - Cartes jaunes et rouges
   - État disciplinaire
   - Boutons d'action (Exporter PDF, Supprimer)

---

## 🎨 Fonctionnalités Principales

### Statistiques
```
✅ Affichage des matchs joués
✅ Points totaux calculés
✅ Différence de buts
✅ Taux de victoire en %
✅ Résultats (V-D-N)
✅ Buts marqués/encaissés
✅ Moyennes par match
✅ Barre de progression visuelle
```

### Classement
```
✅ Tri automatique (Points → Diff Buts → Buts Marqués)
✅ Médailles pour les 3 premiers
✅ Codes couleur par rang
✅ Affichage V-D-N en couleurs
✅ Cartes colorées
✅ Barre de progression pour victoires
✅ Synthèse statistique (Total M, Meilleure attaque, etc.)
```

### Feuilles de Match
```
✅ Résultat final affichée
✅ Scores des deux équipes
✅ Cartes jaunes par équipe
✅ Cartes rouges par équipe
✅ État disciplinaire (Bon standing / Attention / Expulsion)
✅ Dates et métadonnées
✅ Actions disponibles
```

---

## 📊 Données de Test

Si vous n'avez pas de données, vous pouvez utiliser:

### Créer des Données via Postman

#### 1. Créer une Statistique
```
POST http://localhost:8080/api/statistiques
Content-Type: application/json

{
  "teamId": "team_1",
  "sportId": "football",
  "nbMatchsJoues": 10,
  "nbVictoires": 7,
  "nbDefaites": 2,
  "nbNuls": 1,
  "nbButsMarques": 25,
  "nbButsEncaisses": 10
}
```

#### 2. Créer un Classement
```
POST http://localhost:8080/api/classement/generate?eventId=event_1&sportId=football&teamIds=team_1,team_2,team_3
```

#### 3. Créer une Feuille de Match
```
POST http://localhost:8080/api/feuillesDeMatch
Content-Type: application/json

{
  "matchId": "match_1",
  "dateMatch": "2024-01-15T10:00:00",
  "recapEquipes": [
    {
      "teamId": "team_1",
      "score": 3,
      "nbCartesJaunes": 2,
      "nbCartesRouges": 0
    },
    {
      "teamId": "team_2",
      "score": 1,
      "nbCartesJaunes": 1,
      "nbCartesRouges": 1
    }
  ]
}
```

---

## 🔍 Dépannage Rapide

### Les pages ne chargent pas?

**1. Vérifiez le backend:**
```bash
curl http://localhost:8080/api/statistiques
```

Si vous obtenez une erreur 404 ou une connexion refusée:
- ✗ Backend n'est pas en cours d'exécution
- ✓ Démarrez le backend: `cd back && mvn spring-boot:run`

**2. Vérifiez la console du navigateur (F12):**
- Cherchez les erreurs rouges
- Cherchez les erreurs CORS
- Cherchez les erreurs 404 sur les requêtes API

**3. Videz le cache:**
```bash
# Puis recharger la page
Ctrl+Shift+R (Windows/Linux)
ou
Cmd+Shift+R (Mac)
```

### Les données ne s'affichent pas?

**1. Vérifiez que MongoDB contient des données:**
```bash
# MongoDB CLI
show collections
db.statistiques.find()
db.classement.find()
db.feuillesDeMatch.find()
```

**2. Vérifiez l'URL d'API:**
- Ouvrez l'Inspecteur Web (F12 → Network)
- Vérifiez les requêtes GET
- L'URL doit être: `http://localhost:8080/api/...`

### Les styles ne s'appliquent pas?

**1. Vérifiez Tailwind CSS:**
```bash
# Vérifiez que Tailwind est compilé
npm run build
```

**2. Rechargez la page:**
```
Ctrl+F5 (force refresh)
```

---

## 💡 Conseils d'Utilisation

### Recherche par Plusieurs Critères
```
📊 Statistiques:
   Mode "Tous": Entrez une équipe ET un sport pour résultats spécifiques
   Mode "Par Équipe": Affiche toutes les saisons/sports d'une équipe
   Mode "Par Sport": Affiche tous les classements d'un sport
```

### Comprendre les Calculs
```
Points Totaux = (Victoires × 3) + Nuls
Différence Buts = Buts Marqués - Buts Encaissés
Taux Victoire = (Victoires ÷ Matchs Joués) × 100
Moyenne Buts = Total Buts ÷ Matchs Joués
```

### Interprétation des Codes Couleur
```
🟢 Vert      = Bon (Victoires, Buts pour)
🔴 Rouge     = Mauvais (Défaites, Buts contre)
🟡 Jaune     = Neutre/Avertissement (Nuls, Cartes jaunes)
🟥 Cartes    = Expulsion (Cartes rouges)
```

---

## 📱 Responsive Design

Les pages s'adaptent automatiquement:

```
💻 Desktop    → 3 colonnes de cartes
📱 Tablet     → 2 colonnes de cartes
📱 Mobile     → 1 colonne de cartes
```

Testez en:
1. Ouvrant F12
2. Appuyant sur Ctrl+Shift+M (Toggle device toolbar)
3. Sélectionnant différentes tailles d'écran

---

## 🔗 URLs de Référence

```
Frontend:           http://localhost:4200
├── Matches:        /matches
├── Events:         /events
├── Statistiques:   /statistiques    ⭐ NEW
├── Classement:     /classement      ⭐ NEW
└── Feuilles:       /feuillesdematch ⭐ NEW

Backend API:        http://localhost:8080/api
├── /feuillesDeMatch
├── /statistiques
└── /classement

Swagger UI:         http://localhost:8080/swagger-ui.html
MongoDB:            localhost:27017
```

---

## 📚 Documentation Complète

Pour plus de détails:
- [FRONTEND_MODULES_GUIDE.md](./FRONTEND_MODULES_GUIDE.md) - Guide complet
- [FRONTEND_CHECKLIST.md](./FRONTEND_CHECKLIST.md) - Checklist détaillée
- [PROJECT_OVERVIEW.md](./PROJECT_OVERVIEW.md) - Vue d'ensemble complète

---

## ✅ Checklist de Vérification

Après le démarrage, vérifiez:

- [ ] Frontend accessible sur http://localhost:4200
- [ ] Barre de navigation contient 6 liens
- [ ] Lien "📊 Statistiques" cliquable
- [ ] Lien "🏆 Classement" cliquable
- [ ] Lien "📋 Feuilles" cliquable
- [ ] Réseau backend accessible (F12 → Network)
- [ ] Pas d'erreurs CORS
- [ ] Pas d'erreurs 404
- [ ] Données affichées correctement (avec données dans BD)
- [ ] Recherche fonctionne
- [ ] Responsive design OK

---

## 🎯 Prochaines Étapes

### Immédiat (Test)
1. ✅ Démarrer frontend
2. ✅ Naviguer vers les 3 nouveaux modules
3. ✅ Tester les recherches

### Court Terme (Amélioration)
1. 📊 Ajouter des graphiques
2. 📥 Implémenter export PDF
3. 🧪 Ajouter des tests

### Moyen Terme (Production)
1. 🚀 Déployer sur serveur
2. 🔒 Ajouter authentification
3. 📱 Optimiser pour mobile

---

## 🆘 Besoin d'Aide?

### 1. Vérifier les Logs
```bash
# Terminal 1 (Frontend)
npm start
# Cherchez les erreurs de compilation

# Terminal 2 (Backend)
mvn spring-boot:run
# Cherchez les erreurs de connexion
```

### 2. Vérifier la Console du Navigateur
```
F12 → Console
Cherchez les erreurs rouges
Vérifiez les requêtes réseau (F12 → Network)
```

### 3. Vérifier les Données
```bash
# MongoDB
mongo
use street_league_db
show collections
db.statistiques.count()
db.classement.count()
db.feuillesDeMatch.count()
```

---

## 🎉 Bon Développement!

Vous êtes maintenant prêt à tester les nouveaux modules frontend!

**Durée totale:** ~5-10 minutes
**Prochaine phase:** Exploration et amélioration

---

**Créé:** 2024
**Version:** 2.0
**Statut:** ✅ Prêt à l'emploi
