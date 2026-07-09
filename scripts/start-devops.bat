@echo off
REM Windows batch script to start DevOps stack

echo.
echo 🚀 Starting DevOps stack...
echo.

docker-compose -f docker-compose.yml up -d

echo.
echo ⏳ Waiting for services to start...
timeout /t 10

echo.
echo ✅ Services started:
echo    - Jenkins: http://localhost:8080
echo    - SonarQube: http://localhost:9000 (admin/admin)
echo    - Grafana: http://localhost:3000 (admin/admin)
echo    - Prometheus: http://localhost:9090
echo    - MongoDB: mongodb://admin:admin123@localhost:27017
echo.

docker-compose logs -f jenkins
