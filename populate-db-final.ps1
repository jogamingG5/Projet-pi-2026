# Script to populate MongoDB with complete test data (without teams endpoint)
$API_BASE = "http://localhost:8081/streetleague/api"

Write-Host "=== Nettoyage des anciennes donnees ===" -ForegroundColor Yellow

# Delete all existing data
try {
    $matchIds = @((Invoke-WebRequest -Uri "$API_BASE/matchs" -UseBasicParsing).Content | ConvertFrom-Json | Select-Object -ExpandProperty id -ErrorAction SilentlyContinue)
    foreach ($id in $matchIds) {
        Invoke-WebRequest -Uri "$API_BASE/matchs/$id" -Method DELETE -UseBasicParsing -ErrorAction SilentlyContinue | Out-Null
    }
    Write-Host "[OK] $($matchIds.Count) matchs supprimes" -ForegroundColor Green
}
catch { }

try {
    $eventIds = @((Invoke-WebRequest -Uri "$API_BASE/events" -UseBasicParsing).Content | ConvertFrom-Json | Select-Object -ExpandProperty id -ErrorAction SilentlyContinue)
    foreach ($id in $eventIds) {
        Invoke-WebRequest -Uri "$API_BASE/events/$id" -Method DELETE -UseBasicParsing -ErrorAction SilentlyContinue | Out-Null
    }
    Write-Host "[OK] $($eventIds.Count) evenements supprimes" -ForegroundColor Green
}
catch { }

Write-Host ""
Write-Host "=== Creation des evenements ===" -ForegroundColor Yellow

$events = @(
    @{
        name = "Championnat National Football 2026"
        type = "LEAGUE"
        location = "Stade Rades, Tunis"
        startDate = "2026-04-01"
        endDate = "2026-06-30"
        sportIds = @("Football")
        description = "Championnat de premiere division de football"
    },
    @{
        name = "Coupe de Tunisie 2026"
        type = "TOURNAMENT"
        location = "Stade Olympique Rades"
        startDate = "2026-05-15"
        endDate = "2026-06-15"
        sportIds = @("Football")
        description = "Tournoi principal de coupe de Tunisie"
    },
    @{
        name = "Ligue Volley Pro 2026"
        type = "LEAGUE"
        location = "Salle Omnisports Tunis"
        startDate = "2026-04-10"
        endDate = "2026-07-20"
        sportIds = @("Volleyball")
        description = "Championnat de volleyball professionnel"
    },
    @{
        name = "Tournoi Basket Elite"
        type = "TOURNAMENT"
        location = "Salle El Faouz Sfax"
        startDate = "2026-05-20"
        endDate = "2026-05-30"
        sportIds = @("Basketball")
        description = "Tournoi basketball elite d'Afrique du Nord"
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
        Write-Host "[OK] Evenement cree: $($event.name)" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERREUR] Erreur creation evenement" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Creation des matchs avec scores varies ===" -ForegroundColor Yellow

$matchData = @(
    # Championnat Football - Semaine 1
    @{ home = "EST Tunis"; away = "Club Africain"; homeScore = 2; awayScore = 1; date = "2026-04-05T15:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = "Esperance"; away = "Stade Tunisien"; homeScore = 0; awayScore = 3; date = "2026-04-05T17:30:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = "Monastir FC"; away = "Sfaxien"; homeScore = 1; awayScore = 1; date = "2026-04-06T14:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = "Etoile du Sahel"; away = "Olympique Skhira"; homeScore = 4; awayScore = 0; date = "2026-04-06T16:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    
    # Championnat Football - Semaine 2
    @{ home = "EST Tunis"; away = "Monastir FC"; homeScore = 2; awayScore = 2; date = "2026-04-10T15:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = "Esperance"; away = "Sfaxien"; homeScore = 3; awayScore = 1; date = "2026-04-10T17:30:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = "Club Africain"; away = "Etoile du Sahel"; homeScore = 1; awayScore = 2; date = "2026-04-12T14:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = "Stade Tunisien"; away = "Olympique Skhira"; homeScore = 5; awayScore = 0; date = "2026-04-12T16:00:00"; status = "SCHEDULED"; eventId = $eventIds[0]; type = "LEAGUE" },
    
    # Coupe de Tunisie
    @{ home = "EST Tunis"; away = "Esperance"; homeScore = 1; awayScore = 0; date = "2026-05-15T18:00:00"; status = "COMPLETED"; eventId = $eventIds[1]; type = "TOURNAMENT" },
    @{ home = "Stade Tunisien"; away = "Club Africain"; homeScore = 2; awayScore = 1; date = "2026-05-15T20:00:00"; status = "SCHEDULED"; eventId = $eventIds[1]; type = "TOURNAMENT" },
    
    # Volleyball
    @{ home = "Volley Tunis"; away = "Volley Sfax"; homeScore = 3; awayScore = 0; date = "2026-04-15T19:00:00"; status = "COMPLETED"; eventId = $eventIds[2]; type = "LEAGUE" },
    @{ home = "Volley Sousse"; away = "Volley Tunis"; homeScore = 1; awayScore = 2; date = "2026-04-20T19:00:00"; status = "SCHEDULED"; eventId = $eventIds[2]; type = "LEAGUE" },
    
    # Basketball Tournament
    @{ home = "Basket Elite Tunis"; away = "Basket Sfax"; homeScore = 85; awayScore = 72; date = "2026-05-22T16:00:00"; status = "COMPLETED"; eventId = $eventIds[3]; type = "TOURNAMENT" },
    @{ home = "Basket Sousse"; away = "Basket Gafsa"; homeScore = 78; awayScore = 81; date = "2026-05-22T18:00:00"; status = "COMPLETED"; eventId = $eventIds[3]; type = "TOURNAMENT" },
    
    # Tennis
    @{ home = "Ons Jabeur"; away = "Elise Mertens"; homeScore = 2; awayScore = 0; date = "2026-04-20T10:00:00"; status = "COMPLETED"; eventId = $eventIds[4]; type = "FRIENDLY" },
    @{ home = "Genie Bouchard"; away = "Emma Raducanu"; homeScore = 1; awayScore = 2; date = "2026-04-21T10:00:00"; status = "COMPLETED"; eventId = $eventIds[4]; type = "FRIENDLY" }
)

$createdCount = 0
foreach ($match in $matchData) {
    try {
        $matchBody = @{
            homeTeamId = $match.home
            awayTeamId = $match.away
            homeTeamScore = $match.homeScore
            awayTeamScore = $match.awayScore
            matchDate = $match.date
            status = $match.status
            type = $match.type
            eventId = $match.eventId
        }
        
        $response = Invoke-WebRequest -Uri "$API_BASE/matchs" -Method POST -ContentType "application/json" -Body (ConvertTo-Json $matchBody) -UseBasicParsing
        $createdCount++
        Write-Host "[OK] Match cree: $($match.home) $($match.homeScore)-$($match.awayScore) $($match.away)" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERREUR] Erreur creation match $($match.home) vs $($match.away)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========== RESUME ==========" -ForegroundColor Cyan
Write-Host "Evenements crees: $($eventIds.Count)" -ForegroundColor Yellow
Write-Host "Matchs crees: $createdCount" -ForegroundColor Yellow
Write-Host ""
Write-Host "Donnees disponibles:" -ForegroundColor White
Write-Host "- 5 Evenements varies (LEAGUE, TOURNAMENT, FRIENDLY)" -ForegroundColor White
Write-Host "- 16 Matchs avec scores differents (0-0, 1-2, 3-1, 4-0, 5-0, 85-72, etc)" -ForegroundColor White
Write-Host "- Equipes: EST, Esperance, Club Africain, Monastir, Sfaxien, etc" -ForegroundColor White
Write-Host ""
Write-Host "Remplissage termine avec succes!" -ForegroundColor Green
