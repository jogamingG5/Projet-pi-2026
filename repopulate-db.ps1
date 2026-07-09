# Repopulate MongoDB with correct event data

$backendUrl = "http://localhost:8081/streetleague/api"

# Delete all events first
Write-Host "Clearing existing events..."
$events = Invoke-RestMethod -Uri "$backendUrl/events" -Method GET
if ($events.value) {
    $events.value | ForEach-Object {
        try {
            Invoke-RestMethod -Uri "$backendUrl/events/$($_.id)" -Method DELETE
            Write-Host "Deleted event: $_"
        } catch {
            Write-Host "Could not delete: $_"
        }
    }
}

Start-Sleep -Milliseconds 500

# Create new events with correct field names
$eventsData = @(
    @{
        nomEvenement = "Championnat National Football 2026"
        description = "Championnat de premiere division"
        dateDebut = "2026-06-01"
        dateFin = "2026-08-31"
        type = "LEAGUE"
        sportId = "FOOTBALL"
        teamsIds = @()
    },
    @{
        nomEvenement = "Coupe de Tunisie 2026"
        description = "Coupe nationale de football"
        dateDebut = "2026-07-01"
        dateFin = "2026-09-30"
        type = "TOURNAMENT"
        sportId = "FOOTBALL"
        teamsIds = @()
    },
    @{
        nomEvenement = "Ligue Volley Pro 2026"
        description = "Championnat national de volley"
        dateDebut = "2026-06-15"
        dateFin = "2026-09-15"
        type = "LEAGUE"
        sportId = "VOLLEYBALL"
        teamsIds = @()
    },
    @{
        nomEvenement = "Tournoi Basket Elite"
        description = "Tournoi international de basketball"
        dateDebut = "2026-07-10"
        dateFin = "2026-08-20"
        type = "TOURNAMENT"
        sportId = "BASKETBALL"
        teamsIds = @()
    },
    @{
        nomEvenement = "Open Tennis Tunis 2026"
        description = "Tournoi de tennis professionnel"
        dateDebut = "2026-08-01"
        dateFin = "2026-08-15"
        type = "FRIENDLY"
        sportId = "TENNIS"
        teamsIds = @()
    }
)

Write-Host "Creating events..."
$eventsData | ForEach-Object {
    try {
        $response = Invoke-RestMethod -Uri "$backendUrl/events" -Method POST -ContentType "application/json" -Body ($_ | ConvertTo-Json)
        Write-Host "✓ Created: $($_.nomEvenement)" -ForegroundColor Green
    } catch {
        Write-Host "✗ Error creating event: $($_.nomEvenement)" -ForegroundColor Red
        Write-Host $_.Exception.Message
    }
}

Write-Host "✓ Done!" -ForegroundColor Green
