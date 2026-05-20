@echo off
setlocal enabledelayedexpansion

REM =====================================================
REM  HITZONE - Fullstack Microservices Startup Script
REM  Optimized Version using Maven Wrapper
REM =====================================================

echo.
echo =====================================================
echo   HITZONE - Fullstack Microservices Startup
echo   Using Maven Wrapper for compatibility
echo =====================================================
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

echo [*] Starting services in order...
echo.

REM Step 1: Config Server
echo [1/7] Starting Config Server (port 8888)...
start "Config Server - 8888" cmd /k "cd /d %CONFIG_SERVER% && mvnw.cmd spring-boot:run"
timeout /t 4 /nobreak

REM Step 2: Eureka Server
echo [2/7] Starting Eureka Server (port 8761)...
start "Eureka Server - 8761" cmd /k "cd /d %EUREKA_SERVER% && mvnw.cmd spring-boot:run"
timeout /t 4 /nobreak

REM Step 3: API Gateway
echo [3/7] Starting API Gateway (port 8080)...
start "API Gateway - 8080" cmd /k "cd /d %API_GATEWAY% && mvnw.cmd spring-boot:run"
timeout /t 4 /nobreak

REM Step 4: Microservices
echo [4/7] Starting MS-Agents (port 8083)...
start "MS-Agents - 8083" cmd /k "cd /d %MS_AGENTS% && mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

echo [5/7] Starting MS-Weapons (port 8082)...
start "MS-Weapons - 8082" cmd /k "cd /d %MS_WEAPONS% && mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

echo [6/7] Starting MS-Maps (port 8084)...
start "MS-Maps - 8084" cmd /k "cd /d %MS_MAPS% && mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

echo [7/7] Starting MS-Persistence (port 8085)...
start "MS-Persistence - 8085" cmd /k "cd /d %MS_PERSISTENCE% && mvnw.cmd spring-boot:run"

echo.
echo =====================================================
echo   SERVICES INITIALIZATION IN PROGRESS
echo =====================================================
echo.
echo [*] Waiting 20 seconds for services to be ready...
echo.

timeout /t 20 /nobreak

cls

echo.
echo =====================================================
echo   ALL SERVICES STARTED!
echo =====================================================
echo.
echo AVAILABLE ENDPOINTS:
echo.
echo   Core Infrastructure:
echo   - Config Server ......... http://localhost:8888
echo   - Eureka Discovery ...... http://localhost:8761
echo   - API Gateway ........... http://localhost:8080
echo.
echo   Microservices:
echo   - MS-Agents ............. http://localhost:8083
echo   - MS-Weapons ............ http://localhost:8082
echo   - MS-Maps ............... http://localhost:8084
echo   - MS-Persistence ........ http://localhost:8085
echo.
echo =====================================================
echo   FRONTEND CONFIGURATION
echo =====================================================
echo.
echo   API Base URL: http://localhost:8080
echo   (This is the API Gateway - all requests go through here)
echo.
echo   To access the frontend:
echo.
echo   OPTION 1: Using Live Server (Recommended)
echo   - Right-click on any HTML file in the frontend folder
echo   - Select "Open with Live Server"
echo   - The app will connect to http://localhost:8080
echo.
echo   OPTION 2: Direct Browser Access
echo   - Open frontend/dashboard.html in your browser
echo   - Or any other HTML file (login.html, register.html, etc.)
echo.
echo =====================================================
echo   SERVICE VERIFICATION
echo =====================================================
echo.
echo   1. Check Eureka Dashboard:
echo      http://localhost:8761
echo      (Wait for all instances to show "UP")
echo.
echo   2. Test API Gateway:
echo      http://localhost:8080 (Should show gateway info)
echo.
echo   3. Access Frontend:
echo      frontend/dashboard.html via Live Server
echo.
echo =====================================================
echo.
echo [INFO] All services are running in separate terminals.
echo [INFO] Each terminal shows service logs for debugging.
echo [INFO] To stop a service, close its terminal or press Ctrl+C.
echo.
echo [!] Note: If you see connection errors, wait a bit longer
echo [!] for services to fully initialize. This can take 1-2 minutes
echo [!] depending on your system.
echo.

pause

REM Keep main window open
