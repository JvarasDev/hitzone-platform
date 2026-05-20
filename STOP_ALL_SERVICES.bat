@echo off
setlocal enabledelayedexpansion

echo.
echo =====================================================
echo   HITZONE - Stop All Services
echo =====================================================
echo.

REM Kill all java processes associated with the microservices
echo [*] Stopping all services...
echo.

REM Kill Maven processes (which start the Java applications)
echo [*] Terminating all Java/Maven processes...
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM javaw.exe >nul 2>&1
taskkill /F /IM mvn.cmd >nul 2>&1

echo.
echo [OK] All services have been stopped.
echo.
echo Available services:
echo - Config Server (port 8888)
echo - Eureka Server (port 8761)
echo - API Gateway (port 8080)
echo - MS-Agents (port 8083)
echo - MS-Weapons (port 8082)
echo - MS-Maps (port 8084)
echo - MS-Persistence (port 8085)
echo.
echo To start services again, run:
echo - START_ALL_SERVICES.bat
echo   (or)
echo - START_ALL_SERVICES_OPTIMIZED.bat
echo.

pause
