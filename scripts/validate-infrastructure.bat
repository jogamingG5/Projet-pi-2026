@echo off
REM Windows validation script for DevOps infrastructure

setlocal enabledelayedexpansion
chcp 65001 > nul

echo.
echo 🔍 Streetleague DevOps Infrastructure Validation
echo ================================================
echo.

set SUCCESS=0
set WARNINGS=0
set ERRORS=0

REM Check infrastructure files
echo 📁 Checking Infrastructure Files...
if exist "back\Dockerfile" (
    echo ✓ back\Dockerfile exists
    set /a SUCCESS+=1
) else (
    echo ✗ back\Dockerfile MISSING
    set /a ERRORS+=1
)

if exist "front\Dockerfile" (
    echo ✓ front\Dockerfile exists
    set /a SUCCESS+=1
) else (
    echo ✗ front\Dockerfile MISSING
    set /a ERRORS+=1
)

if exist "front\nginx.conf" (
    echo ✓ front\nginx.conf exists
    set /a SUCCESS+=1
) else (
    echo ✗ front\nginx.conf MISSING
    set /a ERRORS+=1
)

if exist "k8s\deployment.yaml" (
    echo ✓ k8s\deployment.yaml exists
    set /a SUCCESS+=1
) else (
    echo ✗ k8s\deployment.yaml MISSING
    set /a ERRORS+=1
)

if exist "k8s\config.yaml" (
    echo ✓ k8s\config.yaml exists
    set /a SUCCESS+=1
) else (
    echo ✗ k8s\config.yaml MISSING
    set /a ERRORS+=1
)

if exist "Jenkinsfile" (
    echo ✓ Jenkinsfile exists
    set /a SUCCESS+=1
) else (
    echo ✗ Jenkinsfile MISSING
    set /a ERRORS+=1
)

if exist "docker-compose.yml" (
    echo ✓ docker-compose.yml exists
    set /a SUCCESS+=1
) else (
    echo ✗ docker-compose.yml MISSING
    set /a ERRORS+=1
)

if exist "prometheus.yml" (
    echo ✓ prometheus.yml exists
    set /a SUCCESS+=1
) else (
    echo ✗ prometheus.yml MISSING
    set /a ERRORS+=1
)

if exist "DEPLOYMENT.md" (
    echo ✓ DEPLOYMENT.md exists
    set /a SUCCESS+=1
) else (
    echo ✗ DEPLOYMENT.md MISSING
    set /a ERRORS+=1
)

echo.
echo 🛠️  Checking Required Tools...

REM Check Java
java -version >nul 2>&1
if !errorlevel! equ 0 (
    for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VERSION=%%i
    echo ✓ Java installed: !JAVA_VERSION!
    set /a SUCCESS+=1
) else (
    echo ✗ Java NOT found
    set /a ERRORS+=1
)

REM Check Maven
mvn -version >nul 2>&1
if !errorlevel! equ 0 (
    echo ✓ Maven installed
    set /a SUCCESS+=1
) else (
    echo ⚠ Maven NOT installed
    set /a WARNINGS+=1
)

REM Check Node.js
node --version >nul 2>&1
if !errorlevel! equ 0 (
    for /f "tokens=*" %%i in ('node --version') do set NODE_VERSION=%%i
    echo ✓ Node.js installed: !NODE_VERSION!
    set /a SUCCESS+=1
) else (
    echo ⚠ Node.js NOT installed
    set /a WARNINGS+=1
)

REM Check npm
npm --version >nul 2>&1
if !errorlevel! equ 0 (
    for /f "tokens=*" %%i in ('npm --version') do set NPM_VERSION=%%i
    echo ✓ npm installed: !NPM_VERSION!
    set /a SUCCESS+=1
) else (
    echo ⚠ npm NOT installed
    set /a WARNINGS+=1
)

REM Check Docker
docker --version >nul 2>&1
if !errorlevel! equ 0 (
    for /f "tokens=*" %%i in ('docker --version') do set DOCKER_VERSION=%%i
    echo ✓ Docker installed: !DOCKER_VERSION!
    set /a SUCCESS+=1
) else (
    echo ⚠ Docker NOT installed (see: DOCKER_INSTALLATION_WINDOWS.md)
    set /a WARNINGS+=1
)

REM Check kubectl
kubectl version --client >nul 2>&1
if !errorlevel! equ 0 (
    echo ✓ kubectl installed
    set /a SUCCESS+=1
) else (
    echo ⚠ kubectl NOT installed (required for K8s)
    set /a WARNINGS+=1
)

REM Check Git
git --version >nul 2>&1
if !errorlevel! equ 0 (
    echo ✓ Git installed
    set /a SUCCESS+=1
) else (
    echo ✗ Git NOT installed
    set /a ERRORS+=1
)

echo.
echo ================================================
echo 📊 Summary
echo   ✓ Success: !SUCCESS!
echo   ⚠ Warnings: !WARNINGS!
echo   ✗ Errors: !ERRORS!
echo.

if !ERRORS! equ 0 (
    echo ✅ Infrastructure Ready!
    echo.
    echo 🚀 Next Steps:
    if !WARNINGS! gtr 0 (
        echo   1. Install Docker Desktop (see DOCKER_INSTALLATION_WINDOWS.md)
        echo   2. Run: .\scripts\start-devops.bat
    ) else (
        echo   1. Run: .\scripts\start-devops.bat
    )
    echo   2. Wait for services (2-3 minutes)
    echo   3. Access Jenkins at http://localhost:8080
    echo   4. Configure credentials
    echo   5. Create pipeline job
    pause
    exit /b 0
) else (
    echo ❌ Fix errors before proceeding
    pause
    exit /b 1
)
