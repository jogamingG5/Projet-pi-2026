$BaseURL = "http://localhost:8081/streetleague/api"
$EventHeaders = @{"Content-Type" = "application/json"}

Write-Host ">>> Remplissage de la base de donnees..." -ForegroundColor Cyan

# ======================= STEP 1: CREATE 5 EVENTS =======================
Write-Host "`n>>> Creation de 5 evenements..." -ForegroundColor Yellow

$Events = @(
    @{
        name = "Championnat Street League 2026"
        description = "Championnat officiel Street League 2026"
        type = "LEAGUE"
        location = "Tunis"
        startDate = "2026-04-01"
        endDate = "2026-06-30"
        sportIds = @("sport-football")
        teamIds = @("team-alpha", "team-beta", "team-gamma", "team-delta", "EST", "BINZARTE")
    },
    @{
        name = "Tournoi Printemps 2026"
        description = "Tournoi amical du printemps"
        type = "TOURNAMENT"
        location = "Sfax"
        startDate = "2026-03-15"
        endDate = "2026-03-30"
        sportIds = @("sport-volleyball")
        teamIds = @("team-alpha", "team-gamma")
    },
    @{
        name = "Ligue Basket 2026"
        description = "Saison de basket-ball"
        type = "LEAGUE"
        location = "Sousse"
        startDate = "2026-05-01"
        endDate = "2026-07-31"
        sportIds = @("sport-basketball")
        teamIds = @("team-beta", "team-delta", "EST")
    },
    @{
        name = "Amicaux Fevrier 2026"
        description = "Matchs amicaux sans classement"
        type = "FRIENDLY"
        location = "Bizerte"
        startDate = "2026-02-10"
        endDate = "2026-02-28"
        sportIds = @("sport-tennis")
        teamIds = @("team-alpha", "team-beta")
    },
    @{
        name = "Finale Badminton 2026"
        description = "Finale nationale de badminton"
        type = "TOURNAMENT"
        location = "Tunis"
        startDate = "2026-06-15"
        endDate = "2026-06-20"
        sportIds = @("sport-badminton")
        teamIds = @("team-gamma", "team-delta", "EST", "BINZARTE")
    }
)

$EventIds = @()
foreach ($event in $Events) {
    $body = $event | ConvertTo-Json
    try {
        $response = Invoke-WebRequest -Uri "$BaseURL/events" `
            -Method POST `
            -Headers $EventHeaders `
            -Body $body
        $eventData = $response.Content | ConvertFrom-Json
        $EventIds += $eventData.id
        Write-Host "[OK] Evenement cree: $($event.name)" -ForegroundColor Green
    } catch {
        Write-Host "[ERREUR] Erreur creation evenement: $($event.name)" -ForegroundColor Red
        Write-Host $_.Exception.Message
    }
}

# ======================= STEP 2: CREATE 10+ MATCHES =======================
Write-Host "`n>>> Creation de matchs..." -ForegroundColor Yellow

$MatchHeaders = @{"Content-Type" = "application/json"}

$Matches = @(
    @{
        homeTeam = "team-alpha"
        awayTeam = "team-beta"
        eventId = $EventIds[0]
        sportId = "sport-football"
        scheduledDate = "2026-04-10"
        scheduledTime = "15:00"
        location = "Stade El Menzah"
        status = "COMPLETED"
        homeScore = 2
        awayScore = 1
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "team-gamma"
        awayTeam = "team-delta"
        eventId = $EventIds[0]
        sportId = "sport-football"
        scheduledDate = "2026-04-11"
        scheduledTime = "14:30"
        location = "Stade Ezzahra"
        status = "COMPLETED"
        homeScore = 0
        awayScore = 0
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "EST"
        awayTeam = "BINZARTE"
        eventId = $EventIds[0]
        sportId = "sport-football"
        scheduledDate = "2026-04-12"
        scheduledTime = "16:00"
        location = "Stade Radès"
        status = "COMPLETED"
        homeScore = 3
        awayScore = 1
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "team-alpha"
        awayTeam = "team-gamma"
        eventId = $EventIds[0]
        sportId = "sport-football"
        scheduledDate = "2026-04-18"
        scheduledTime = "15:00"
        location = "Stade El Menzah"
        status = "COMPLETED"
        homeScore = 1
        awayScore = 1
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "team-beta"
        awayTeam = "team-delta"
        eventId = $EventIds[0]
        sportId = "sport-football"
        scheduledDate = "2026-04-19"
        scheduledTime = "15:00"
        location = "Stade Ezzahra"
        status = "COMPLETED"
        homeScore = 2
        awayScore = 0
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "EST"
        awayTeam = "team-alpha"
        eventId = $EventIds[0]
        sportId = "sport-football"
        scheduledDate = "2026-04-25"
        scheduledTime = "14:00"
        location = "Stade Radès"
        status = "COMPLETED"
        homeScore = 1
        awayScore = 2
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "BINZARTE"
        awayTeam = "team-gamma"
        eventId = $EventIds[0]
        sportId = "sport-football"
        scheduledDate = "2026-04-26"
        scheduledTime = "15:30"
        location = "Stade Bizerte"
        status = "COMPLETED"
        homeScore = 0
        awayScore = 2
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "team-alpha"
        awayTeam = "team-gamma"
        eventId = $EventIds[1]
        sportId = "sport-volleyball"
        scheduledDate = "2026-03-20"
        scheduledTime = "18:00"
        location = "Salle Sfax"
        status = "COMPLETED"
        homeScore = 25
        awayScore = 18
        matchType = "TOURNAMENT"
    },
    @{
        homeTeam = "team-beta"
        awayTeam = "EST"
        eventId = $EventIds[2]
        sportId = "sport-basketball"
        scheduledDate = "2026-05-15"
        scheduledTime = "19:00"
        location = "Palais Omnisports Sousse"
        status = "COMPLETED"
        homeScore = 85
        awayScore = 78
        matchType = "LEAGUE"
    },
    @{
        homeTeam = "team-delta"
        awayTeam = "BINZARTE"
        eventId = $EventIds[2]
        sportId = "sport-basketball"
        scheduledDate = "2026-05-16"
        scheduledTime = "19:00"
        location = "Palais Omnisports Sousse"
        status = "SCHEDULED"
        homeScore = 0
        awayScore = 0
        matchType = "LEAGUE"
    }
)

$MatchCount = 0
foreach ($match in $Matches) {
    $body = $match | ConvertTo-Json
    try {
        $response = Invoke-WebRequest -Uri "$BaseURL/matchs" `
            -Method POST `
            -Headers $MatchHeaders `
            -Body $body
        $MatchCount++
        Write-Host "[OK] Match cree: $($match.homeTeam) vs $($match.awayTeam)" -ForegroundColor Green
    } catch {
        Write-Host "[ERREUR] Erreur creation match: $($match.homeTeam) vs $($match.awayTeam)" -ForegroundColor Red
        Write-Host $_.Exception.Message
    }
}

# ======================= SUMMARY =======================
Write-Host "`n" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host ">>> Remplissage termine avec succes!" -ForegroundColor Green
Write-Host ">>> Evenements crees: $($EventIds.Count)" -ForegroundColor Cyan
Write-Host ">>> Matchs crees: $MatchCount" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
