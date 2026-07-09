# Script with CORRECT field names for backend
$API_BASE = "http://localhost:8081/streetleague/api"

Write-Host "=== Creation des evenements CORRECTS ===" -ForegroundColor Yellow

$events = @(
    @{
        nomEvenement = "Championnat National Football 2026"
        description = "Championnat de premiere division de football"
        dateDebut = "2026-04-01"
        dateFin = "2026-06-30"
        type = "LEAGUE"
        sportId = "Football"
        expectedMatches = 8
    },
    @{
        nomEvenement = "Coupe de Tunisie 2026"
        description = "Tournoi principal de coupe de Tunisie"
        dateDebut = "2026-05-15"
        dateFin = "2026-06-15"
        type = "TOURNAMENT"
        sportId = "Football"
        expectedMatches = 4
    },
    @{
        nomEvenement = "Ligue Volley Pro 2026"
        description = "Championnat de volleyball professionnel"
        dateDebut = "2026-04-10"
        dateFin = "2026-07-20"
        type = "LEAGUE"
        sportId = "Volleyball"
        expectedMatches = 2
    },
    @{
        nomEvenement = "Tournoi Basket Elite"
        description = "Tournoi basketball elite d'Afrique du Nord"
        dateDebut = "2026-05-20"
        dateFin = "2026-05-30"
        type = "TOURNAMENT"
        sportId = "Basketball"
        expectedMatches = 2
    },
    @{
        nomEvenement = "Open Tennis Tunis 2026"
        description = "Open international de tennis"
        dateDebut = "2026-04-20"
        dateFin = "2026-04-27"
        type = "FRIENDLY"
        sportId = "Tennis"
        expectedMatches = 2
    }
)

$eventIds = @()
foreach ($event in $events) {
    try {
        $response = Invoke-WebRequest -Uri "$API_BASE/events" -Method POST -ContentType "application/json" -Body (ConvertTo-Json $event) -UseBasicParsing
        $eventData = $response.Content | ConvertFrom-Json
        $eventIds += $eventData.id
        Write-Host "[OK] $($event.nomEvenement)" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERREUR] $($event.nomEvenement)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Creation des matchs avec scores varies ===" -ForegroundColor Yellow

$matchData = @(
    @{ team1 = "EST Tunis"; team2 = "Club Africain"; score1 = 2; score2 = 1 },
    @{ team1 = "Esperance"; team2 = "Stade Tunisien"; score1 = 0; score2 = 3 },
    @{ team1 = "Monastir FC"; team2 = "Sfaxien"; score1 = 1; score2 = 1 },
    @{ team1 = "Etoile du Sahel"; team2 = "Olympique Skhira"; score1 = 4; score2 = 0 },
    @{ team1 = "EST Tunis"; team2 = "Monastir FC"; score1 = 2; score2 = 2 },
    @{ team1 = "Esperance"; team2 = "Sfaxien"; score1 = 3; score2 = 1 },
    @{ team1 = "Club Africain"; team2 = "Etoile du Sahel"; score1 = 1; score2 = 2 },
    @{ team1 = "Stade Tunisien"; team2 = "Olympique Skhira"; score1 = 5; score2 = 0 },
    @{ team1 = "Volley Tunis"; team2 = "Volley Sfax"; score1 = 3; score2 = 0 },
    @{ team1 = "Volley Sousse"; team2 = "Volley Tunis"; score1 = 1; score2 = 2 },
    @{ team1 = "Basket Elite Tunis"; team2 = "Basket Sfax"; score1 = 85; score2 = 72 },
    @{ team1 = "Basket Sousse"; team2 = "Basket Gafsa"; score1 = 78; score2 = 81 },
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
        Write-Host "[OK] $($match.team1) $($match.score1)-$($match.score2) $($match.team2)" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERREUR]" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========== RECAP ==========" -ForegroundColor Cyan
Write-Host "Evenements: $($eventIds.Count)" -ForegroundColor Yellow
Write-Host "Matchs: $createdCount" -ForegroundColor Yellow
Write-Host ""
Write-Host "Equipes: EST Tunis, Esperance, Club Africain, Stade Tunisien, etc" -ForegroundColor White
Write-Host "Scores: 2-1, 0-3, 1-1, 4-0, 5-0, 85-72, 78-81, etc" -ForegroundColor White
Write-Host "Evenements: LEAGUE, TOURNAMENT, FRIENDLY" -ForegroundColor White
Write-Host ""
Write-Host "Succes!" -ForegroundColor Green
