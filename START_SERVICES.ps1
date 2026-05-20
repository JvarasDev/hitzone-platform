param([switch]$Verbose)

# =====================================================
# HITZONE - Single Terminal Microservices Startup
# PowerShell Script - All services in one terminal
# =====================================================

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$ErrorActionPreference = "Continue"

# Define service paths
$services = @(
    @{ name = "Config-Server"; path = "$projectRoot\config-server"; port = 8888 },
    @{ name = "Eureka-Server"; path = "$projectRoot\eureka-server"; port = 8761 },
    @{ name = "API-Gateway"; path = "$projectRoot\api-gateway"; port = 8080 },
    @{ name = "MS-Auth"; path = "$projectRoot\fullstack"; port = 8081 },
    @{ name = "MS-Agents"; path = "$projectRoot\ms-agents"; port = 8083 },
    @{ name = "MS-Weapons"; path = "$projectRoot\ms-weapons"; port = 8082 },
    @{ name = "MS-Maps"; path = "$projectRoot\ms-maps"; port = 8084 },
    @{ name = "MS-Persistence"; path = "$projectRoot\ms-persistence"; port = 8085 }
)

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "   HITZONE - Iniciando Todos los Servicios" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# Start all services in background jobs
$jobs = @()

foreach ($service in $services) {
    Write-Host "Iniciando $($service.name) (puerto $($service.port))..."

    $scriptBlock = {
        param($serviceName, $servicePath, $port)

        Set-Location $servicePath

        if ($Verbose) {
            & .\mvnw.cmd spring-boot:run 2>&1
        } else {
            # Capturar stderr y stdout, mostrar solo errores
            & .\mvnw.cmd spring-boot:run 2>&1 | Where-Object {
                $_ -match 'ERROR|Exception|FATAL|failed' -and $_ -notmatch 'DEBUG|INFO|WARN'
            } | ForEach-Object {
                Write-Host "[$serviceName] $_" -ForegroundColor Red
            }
        }
    }

    $job = Start-Job -ScriptBlock $scriptBlock -ArgumentList $service.name, $service.path, $service.port -Name $service.name
    $jobs += $job

    Start-Sleep -Seconds 1
}

Write-Host ""
Write-Host "Esperando que los servicios se inicialicen..." -ForegroundColor Yellow
Write-Host ""

Start-Sleep -Seconds 20

Clear-Host

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Green
Write-Host "   HITZONE - TODOS LOS SERVICIOS INICIADOS" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
Write-Host ""
Write-Host "PUERTOS DISPONIBLES:" -ForegroundColor Cyan
Write-Host ""
Write-Host "   Infraestructura:"
Write-Host "   - Config Server ......... http://localhost:8888"
Write-Host "   - Eureka Dashboard ...... http://localhost:8761"
Write-Host "   - API Gateway ........... http://localhost:8080"
Write-Host ""
Write-Host "   Microservicios:"
Write-Host "   - MS-Auth ............... http://localhost:8081"
Write-Host "   - MS-Agents ............. http://localhost:8083"
Write-Host "   - MS-Weapons ............ http://localhost:8082"
Write-Host "   - MS-Maps ............... http://localhost:8084"
Write-Host "   - MS-Persistence ........ http://localhost:8085"
Write-Host ""
Write-Host "=====================================================" -ForegroundColor Green
Write-Host "   ACCESO AL FRONTEND" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
Write-Host ""
Write-Host "   LOGIN: " -ForegroundColor Yellow -NoNewline
Write-Host "http://localhost:8080/login.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "=====================================================" -ForegroundColor Green
Write-Host ""
Write-Host "VERIFICACION:" -ForegroundColor Cyan
Write-Host "   1. Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "   2. Frontend Login: http://localhost:8080/login.html" -ForegroundColor White
Write-Host ""
Write-Host "EN ESTA TERMINAL SOLO APARECEN ERRORES." -ForegroundColor Yellow
Write-Host "Si no ves mensajes rojo, los servicios estan corriendo bien." -ForegroundColor Yellow
Write-Host ""
Write-Host "SERVICIOS EN EJECUCION:" -ForegroundColor Cyan
$jobs | ForEach-Object { Write-Host "   - $($_.Name): $($_.State)" -ForegroundColor Green }
Write-Host ""
Write-Host "Para detener, presiona Ctrl+C" -ForegroundColor Yellow
Write-Host ""

# Keep script running and monitor jobs for errors
while ($true) {
    foreach ($job in $jobs) {
        if ($job.State -eq "Failed" -or $job.State -eq "Completed") {
            $output = Receive-Job -Job $job 2>&1
            if ($output) {
                Write-Host "[$($job.Name)] Error - Job ended with status: $($job.State)" -ForegroundColor Red
                $output | Where-Object { $_ -match 'ERROR|Exception|FATAL' } | ForEach-Object {
                    Write-Host "$_" -ForegroundColor Red
                }
            }
        }
    }

    Start-Sleep -Seconds 5
}
