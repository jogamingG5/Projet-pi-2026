# Script to populate MongoDB with complete and valid test data
$API_BASE = "http://localhost:8081/streetleague/api"

Write-Host "=== Creation des donnees complete ===" -ForegroundColor Yellow

# Créer les événements d'abord
$events = @(
    @{
        name = "Championnat National Football 2026"
        type = "LEAGUE"
        location = "Stade Rades, Tunis"
        startDate = "2026-04-01"
        endDate = "2026-06-30"
        sportIds = @("Football")
        description = "Championnat de premiere division"
    },
    @{
        name = "Coupe de Tunisie 2026"
        type = "TOURNAMENT"
        location = "Stade Olympique Rades"
        startDate = "2026-05-15"
        endDate = "2026-06-15"
        sportIds = @("Football")
        description = "Tournoi principal de coupe"
    },
    @{
        name = "Ligue Volley Pro 2026"
        type = "LEAGUE"
        location = "Salle Omnisports Tunis"
        startDate = "2026-04-10"
        endDate = "2026-07-20"
        sportIds = @("Volleyball")
        description = "Championnat de volleyball"
    },
    @{
        name = "Tournoi Basket Elite"
        type = "TOURNAMENT"
        location = "Salle El Faouz Sfax"
        startDate = "2026-05-20"
        endDate = "2026-05-30"
        sportIds = @("Basketball")
        description = "Tournoi basketball elite"
    },
    @{
        name = "Open Tennis Tunis 2026"
        type = "FRIENDLY"
        location = "Tennis Club Tunis"
        startDate = "2026-04-20"
        endDate = "2026-04-27"
        sportIds = @("Tennis")
        description = "Open international de tennis"
    }
)

$eventIds = @()
foreach ($event in $events) {
    try {
        $response = Invoke-WebRequest -Uri "$API_BASE/events" -Method POST -ContentType "application/json" -Body (ConvertTo-Json $event) -UseBasicParsing
        $eventData = $response.Content | ConvertFrom-Json
        $eventIds += $eventData.id
        Write-Host "[OK] Evenement: $($event.name)" -ForegroundColor Green
    }
    catch { Write-Host "[SKIP] Evenement echoue" -ForegroundColor Yellow }
}

Write-Host ""
Write-Host "=== Creation des matchs (15 matchs avec scores varies) ===" -ForegroundColor Yellow

# Utiliser des IDs valides génériques pour les équipes, terrains, arbitres
# Le backend semble accepter n'importe quel ID string
$matchData = @(
    # Football matches - varied scores
    @{ team1 = "EST Tunis"; team2 = "Club Africain"; score1 = 2; score2 = 1 },
    @{ team1 = "Esperance"; team2 = "Stade Tunisien"; score1 = 0; score2 = 3 },
    @{ team1 = "Monastir FC"; team2 = "Sfaxien"; score1 = 1; score2 = 1 },
    @{ team1 = "Etoile du Sahel"; team2 = "Olympique Skhira"; score1 = 4; score2 = 0 },
    @{ team1 = "EST Tunis"; team2 = "Monastir FC"; score1 = 2; score2 = 2 },
    @{ team1 = "Esperance"; team2 = "Sfaxien"; score1 = 3; score2 = 1 },
    @{ team1 = "Club Africain"; team2 = "Etoile du Sahel"; score1 = 1; score2 = 2 },
    @{ team1 = "Stade Tunisien"; team2 = "Olympique Skhira"; score1 = 5; score2 = 0 },
    # Volleyball
    @{ team1 = "Volley Tunis"; team2 = "Volley Sfax"; score1 = 3; score2 = 0 },
    @{ team1 = "Volley Sousse"; team2 = "Volley Tunis"; score1 = 1; score2 = 2 },
    # Basketball
    @{ team1 = "Basket Elite Tunis"; team2 = "Basket Sfax"; score1 = 85; score2 = 72 },
    @{ team1 = "Basket Sousse"; team2 = "Basket Gafsa"; score1 = 78; score2 = 81 },
    # Tennis
    @{ team1 = "Ons Jabeur"; team2 = "Elise Mertens"; score1 = 2; score2 = 0 },
    @{ team1 = "Genie Bouchard"; team2 = "Emma Raducanu"; score1 = 1; score2 = 2 },
    @{ team1 = "Novak Djokovic"; team2 = "Dominic Thiem"; score1 = 3; score2 = 1 }
)

$createdCount = 0
foreach ($match in $matchData) {
    try {
        $matchBody = @{
            team1Id = $match.team1
            team2Id = $match.team2
            scoreTeam1 = $match.score1
            scoreTeam2 = $match.score2
            terrainId = "terrain-default"
            dateDebut = "2026-05-10"
            heure = "15:30"
            sportId = "football"
            arbitreId = "arbitre-default"
            eventId = if($eventIds.Count -gt 0) { $eventIds[0] } else { $null }
            status = "COMPLETED"
            type = "LEAGUE"
        }
        
        $response = Invoke-WebRequest -Uri "$API_BASE/matchs" -Method POST -ContentType "application/json" -Body (ConvertTo-Json $matchBody) -UseBasicParsing
        $createdCount++
        Write-Host "[OK] Match: $($match.team1) $($match.score1)-$($match.score2) $($match.team2)" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERREUR] $($match.team1) vs $($match.team2)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========== RESUME FINAL ==========" -ForegroundColor Cyan
Write-Host "Evenements: $($eventIds.Count)" -ForegroundColor Yellow
Write-Host "Matchs: $createdCount" -ForegroundColor Yellow
Write-Host ""
Write-Host "DONNEES POPULEES!" -ForegroundColor Green
