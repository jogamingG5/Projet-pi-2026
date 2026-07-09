@echo off
REM Delete all old events and keep only the new ones

setlocal enabledelayedexpansion

set "backendUrl=http://localhost:8081/streetleague/api"

echo Fetching all events...

REM Get events and delete the ones with null nomEvenement
for /f "delims=" %%A in ('powershell -Command "((Invoke-WebRequest -Uri '%backendUrl%/events' -UseBasicParsing).Content | ConvertFrom-Json) | Where-Object { $_.nomEvenement -eq $null } | Select-Object -ExpandProperty id"') do (
    echo Deleting event: %%A
    curl -X DELETE "%backendUrl%/events/%%A"
)

echo Done!
