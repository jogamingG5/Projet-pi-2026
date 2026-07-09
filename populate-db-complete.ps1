# Script to populate MongoDB with complete test data
# Clear existing data first
$API_BASE = "http://localhost:8081/streetleague/api"

Write-Host "=== Nettoyage des anciennes donnees ===" -ForegroundColor Yellow

# Delete all existing data
try {
    $matchIds = @((Invoke-WebRequest -Uri "$API_BASE/matchs" -UseBasicParsing).Content | ConvertFrom-Json | Select-Object -ExpandProperty id -ErrorAction SilentlyContinue)
    foreach ($id in $matchIds) {
        Invoke-WebRequest -Uri "$API_BASE/matchs/$id" -Method DELETE -UseBasicParsing -ErrorAction SilentlyContinue | Out-Null
    }
    Write-Host "[NETTOYAGE] $($matchIds.Count) matchs supprimes" -ForegroundColor Cyan
}
catch { Write-Host "[INFO] Pas de matchs a supprimer" }

try {
    $eventIds = @((Invoke-WebRequest -Uri "$API_BASE/events" -UseBasicParsing).Content | ConvertFrom-Json | Select-Object -ExpandProperty id -ErrorAction SilentlyContinue)
    foreach ($id in $eventIds) {
        Invoke-WebRequest -Uri "$API_BASE/events/$id" -Method DELETE -UseBasicParsing -ErrorAction SilentlyContinue | Out-Null
    }
    Write-Host "[NETTOYAGE] $($eventIds.Count) evenements supprimes" -ForegroundColor Cyan
}
catch { Write-Host "[INFO] Pas d'evenements a supprimer" }

Write-Host ""
Write-Host "=== Creation des equipes ===" -ForegroundColor Yellow

# Create teams
$teams = @(
    @{ name = "EST Tunis"; city = "Tunis"; sport = "Football" },
    @{ name = "Club Africain"; city = "Tunis"; sport = "Football" },
    @{ name = "Espérance"; city = "Tunis"; sport = "Football" },
    @{ name = "Stade Tunisien"; city = "Tunis"; sport = "Football" },
    @{ name = "Monastir FC"; city = "Monastir"; sport = "Football" },
    @{ name = "Sfaxien"; city = "Sfax"; sport = "Football" },
    @{ name = "Etoile du Sahel"; city = "Sousse"; sport = "Football" },
    @{ name = "Olympique Skhira"; city = "Skhira"; sport = "Football" },
    @{ name = "Volley Tunis"; city = "Tunis"; sport = "Volleyball" },
    @{ name = "Basket Elite"; city = "Tunis"; sport = "Basketball" },
    @{ name = "Tennis Club Tunis"; city = "Tunis"; sport = "Tennis" },
    @{ name = "Badminton Aces"; city = "Ariana"; sport = "Badminton" }
)

$teamIds = @()
foreach ($team in $teams) {
    try {
        $response = Invoke-WebRequest -Uri "$API_BASE/teams" -Method POST -ContentType "application/json" -Body (ConvertTo-Json $team) -UseBasicParsing
        $teamData = $response.Content | ConvertFrom-Json
        $teamIds += $teamData.id
        Write-Host "[OK] Equipe creee: $($team.name)" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERREUR] Erreur creation equipe $($team.name)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Creation des evenements ===" -ForegroundColor Yellow

$events = @(
    @{
        name = "Championnat National Football 2026"
        type = "LEAGUE"
        location = "Tunis"
        startDate = "2026-04-01"
        endDate = "2026-06-30"
        sportIds = @("Football")
        teamIds = @($teamIds[0], $teamIds[1], $teamIds[2], $teamIds[3], $teamIds[4], $teamIds[5], $teamIds[6], $teamIds[7])
        description = "Championnat de premiere division"
    },
    @{
        name = "Coupe de Tunisie 2026"
        type = "TOURNAMENT"
        location = "Rades"
        startDate = "2026-05-15"
        endDate = "2026-06-15"
        sportIds = @("Football")
        teamIds = @($teamIds[0], $teamIds[1], $teamIds[2], $teamIds[3])
        description = "Tournoi principal de coupe"
    },
    @{
        name = "Ligue Volley Pro 2026"
        type = "LEAGUE"
        location = "Tunis"
        startDate = "2026-04-10"
        endDate = "2026-07-20"
        sportIds = @("Volleyball")
        teamIds = @($teamIds[8])
        description = "Championnat de volleyball professionnel"
    },
    @{
        name = "Tournoi Basket Elite"
        type = "TOURNAMENT"
        location = "Sfax"
        startDate = "2026-05-20"
        endDate = "2026-05-30"
        sportIds = @("Basketball")
        teamIds = @($teamIds[9])
        description = "Tournoi basketball elite"
    },
    @{
        name = "Open Tennis Tunis 2026"
        type = "FRIENDLY"
        location = "Tunis"
        startDate = "2026-04-20"
        endDate = "2026-04-27"
        sportIds = @("Tennis")
        teamIds = @($teamIds[10])
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
        Write-Host "[ERREUR] Erreur creation evenement $($event.name)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Creation des matchs avec scores varies ===" -ForegroundColor Yellow

$matchData = @(
    @{ home = $teamIds[0]; away = $teamIds[1]; homeScore = 2; awayScore = 1; date = "2026-04-05T15:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[1]; away = $teamIds[2]; homeScore = 0; awayScore = 3; date = "2026-04-05T17:30:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[3]; away = $teamIds[4]; homeScore = 1; awayScore = 1; date = "2026-04-06T14:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[5]; away = $teamIds[6]; homeScore = 4; awayScore = 0; date = "2026-04-06T16:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[0]; away = $teamIds[3]; homeScore = 2; awayScore = 2; date = "2026-04-10T15:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[2]; away = $teamIds[4]; homeScore = 3; awayScore = 1; date = "2026-04-10T17:30:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[1]; away = $teamIds[5]; homeScore = 1; awayScore = 2; date = "2026-04-12T14:00:00"; status = "COMPLETED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[6]; away = $teamIds[7]; homeScore = 0; awayScore = 1; date = "2026-04-12T16:00:00"; status = "SCHEDULED"; eventId = $eventIds[0]; type = "LEAGUE" },
    @{ home = $teamIds[0]; away = $teamIds[2]; homeScore = 1; awayScore = 0; date = "2026-05-15T18:00:00"; status = "COMPLETED"; eventId = $eventIds[1]; type = "TOURNAMENT" },
    @{ home = $teamIds[3]; away = $teamIds[1]; homeScore = 2; awayScore = 1; date = "2026-05-15T20:00:00"; status = "SCHEDULED"; eventId = $eventIds[1]; type = "TOURNAMENT" },
    @{ home = $teamIds[8]; away = $null; homeScore = 0; awayScore = 0; date = "2026-04-15T19:00:00"; status = "SCHEDULED"; eventId = $eventIds[2]; type = "LEAGUE" },
    @{ home = $teamIds[9]; away = $null; homeScore = 0; awayScore = 0; date = "2026-05-22T16:00:00"; status = "SCHEDULED"; eventId = $eventIds[3]; type = "TOURNAMENT" }
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
        Write-Host "[OK] Match cree: Score $($match.homeScore)-$($match.awayScore)" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERREUR] Erreur creation match" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== RESUME ===" -ForegroundColor Cyan
Write-Host "Equipes crees: $($teamIds.Count)" -ForegroundColor White
Write-Host "Evenements crees: $($eventIds.Count)" -ForegroundColor White
Write-Host "Matchs crees: $createdCount" -ForegroundColor White
Write-Host ""
Write-Host "Remplissage complete avec succes!" -ForegroundColor Green
