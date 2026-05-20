@echo off
REM =====================================================
REM  HITZONE - Single Terminal Microservices Startup
REM  Wrapper para ejecutar PowerShell script
REM =====================================================

cd /d "%~dp0"

REM Check if PowerShell script exists
if not exist "START_SERVICES.ps1" (
    echo Error: START_SERVICES.ps1 no encontrado
    pause
    exit /b 1
)

REM Run PowerShell with the script
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "START_SERVICES.ps1"
pause
