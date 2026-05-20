@echo off
setlocal enabledelayedexpansion

REM Color codes for console output
set "GREEN=[92m"
set "BLUE=[94m"
set "YELLOW=[93m"
set "RESET=[0m"

echo.
echo =====================================================
echo   HITZONE - Fullstack Microservices Startup
echo =====================================================
echo.
echo [*] Starting services in the correct order...
echo.

REM Change to the project root directory
cd /d "%~dp0"

REM Define the paths to each service
set "CONFIG_SERVER=%cd%\config-server"
set "EUREKA_SERVER=%cd%\eureka-server"
set "API_GATEWAY=%cd%\api-gateway"
set "MS_AGENTS=%cd%\ms-agents"
set "MS_WEAPONS=%cd%\ms-weapons"
set "MS_MAPS=%cd%\ms-maps"
set "MS_PERSISTENCE=%cd%\ms-persistence"

REM Step 1: Start Config Server
echo [1/6] Starting Config Server (port 8888)...
start "Config Server - 8888" cmd /k "cd /d %CONFIG_SERVER% && mvn spring-boot:run"
timeout /t 3 /nobreak

REM Step 2: Start Eureka Server
echo [2/6] Starting Eureka Server (port 8761)...
start "Eureka Server - 8761" cmd /k "cd /d %EUREKA_SERVER% && mvn spring-boot:run"
timeout /t 3 /nobreak

REM Step 3: Start API Gateway
echo [3/6] Starting API Gateway (port 8080)...
start "API Gateway - 8080" cmd /k "cd /d %API_GATEWAY% && mvn spring-boot:run"
timeout /t 3 /nobreak

REM Step 4: Start Microservices
echo [4/6] Starting MS-Agents (port 8083)...
start "MS-Agents - 8083" cmd /k "cd /d %MS_AGENTS% && mvn spring-boot:run"
timeout /t 2 /nobreak

echo [5/6] Starting MS-Weapons (port 8082)...
start "MS-Weapons - 8082" cmd /k "cd /d %MS_WEAPONS% && mvn spring-boot:run"
timeout /t 2 /nobreak

echo [6/6] Starting MS-Maps (port 8084)...
start "MS-Maps - 8084" cmd /k "cd /d %MS_MAPS% && mvn spring-boot:run"
timeout /t 2 /nobreak

echo [7/6] Starting MS-Persistence (port 8085)...
start "MS-Persistence - 8085" cmd /k "cd /d %MS_PERSISTENCE% && mvn spring-boot:run"
timeout /t 3 /nobreak

REM Wait for all services to be ready
echo.
echo =====================================================
echo   WAITING FOR SERVICES TO START
echo =====================================================
echo.
echo Please wait while services are initializing...
echo (This usually takes 30-60 seconds)
echo.

timeout /t 15 /nobreak

REM Display connection information
cls
echo.
echo =====================================================
echo   SERVICES STARTED SUCCESSFULLY!
echo =====================================================
echo.
echo   SERVICES STATUS:
echo   ================
echo.
echo   [*] Config Server ......... http://localhost:8888
echo   [*] Eureka Server ......... http://localhost:8761
echo   [*] API Gateway ........... http://localhost:8080
echo   [*] MS-Agents ............. http://localhost:8083
echo   [*] MS-Weapons ............ http://localhost:8082
echo   [*] MS-Maps ............... http://localhost:8084
echo   [*] MS-Persistence ........ http://localhost:8085
echo.
echo =====================================================
echo   FRONTEND CONNECTION
echo =====================================================
echo.
echo   API Base URL: http://localhost:8080
echo.
echo   To start the frontend:
echo   1. Open the 'frontend' folder in VS Code
echo   2. Open any HTML file in a live server
echo   3. The app will connect to the API Gateway at:
echo      http://localhost:8080
echo.
echo   Or simply open one of these files in your browser:
echo   - frontend/dashboard.html
echo   - frontend/login.html
echo   - frontend/register.html
echo.
echo =====================================================
echo   QUICK LINKS
echo =====================================================
echo.
echo   Eureka Dashboard: http://localhost:8761
echo   API Gateway: http://localhost:8080
echo.
echo   When services are ready, they will appear in Eureka
echo =====================================================
echo.
echo All services are running! Press Ctrl+C in any terminal
echo to stop a specific service.
echo.
pause
