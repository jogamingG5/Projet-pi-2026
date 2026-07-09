@echo off
REM Repopulate MongoDB with correct event data

setlocal enabledelayedexpansion

set "backendUrl=http://localhost:8081/streetleague/api"

echo Creating events...

REM Event 1
curl -X POST "%backendUrl%/events" ^
  -H "Content-Type: application/json" ^
  -d "{\"nomEvenement\":\"Championnat National Football 2026\",\"description\":\"Championnat de premiere division\",\"dateDebut\":\"2026-06-01\",\"dateFin\":\"2026-08-31\",\"type\":\"LEAGUE\",\"sportId\":\"FOOTBALL\",\"teamsIds\":[]}"

REM Event 2
curl -X POST "%backendUrl%/events" ^
  -H "Content-Type: application/json" ^
  -d "{\"nomEvenement\":\"Coupe de Tunisie 2026\",\"description\":\"Coupe nationale de football\",\"dateDebut\":\"2026-07-01\",\"dateFin\":\"2026-09-30\",\"type\":\"TOURNAMENT\",\"sportId\":\"FOOTBALL\",\"teamsIds\":[]}"

REM Event 3
curl -X POST "%backendUrl%/events" ^
  -H "Content-Type: application/json" ^
  -d "{\"nomEvenement\":\"Ligue Volley Pro 2026\",\"description\":\"Championnat national de volley\",\"dateDebut\":\"2026-06-15\",\"dateFin\":\"2026-09-15\",\"type\":\"LEAGUE\",\"sportId\":\"VOLLEYBALL\",\"teamsIds\":[]}"

REM Event 4
curl -X POST "%backendUrl%/events" ^
  -H "Content-Type: application/json" ^
  -d "{\"nomEvenement\":\"Tournoi Basket Elite\",\"description\":\"Tournoi international de basketball\",\"dateDebut\":\"2026-07-10\",\"dateFin\":\"2026-08-20\",\"type\":\"TOURNAMENT\",\"sportId\":\"BASKETBALL\",\"teamsIds\":[]}"

REM Event 5
curl -X POST "%backendUrl%/events" ^
  -H "Content-Type: application/json" ^
  -d "{\"nomEvenement\":\"Open Tennis Tunis 2026\",\"description\":\"Tournoi de tennis professionnel\",\"dateDebut\":\"2026-08-01\",\"dateFin\":\"2026-08-15\",\"type\":\"FRIENDLY\",\"sportId\":\"TENNIS\",\"teamsIds\":[]}"

echo Done!
