# 🛠️ GUÍA DE OPERACIONES Y TROUBLESHOOTING

## PROYECTO HITBOXKING

---

## 📋 TABLA DE CONTENIDOS

1. [Instalación Paso a Paso](#instalación-paso-a-paso)
2. [Arranque Secuencial](#arranque-secuencial)
3. [Verificación de Salud](#verificación-de-salud)
4. [Troubleshooting Común](#troubleshooting-común)
5. [Logs y Debugging](#logs-y-debugging)
6. [Testing Manual](#testing-manual)
7. [Parada y Limpieza](#parada-y-limpieza)
8. [Mejores Prácticas de Desarrollo](#mejores-prácticas-de-desarrollo)

---

## 🚀 INSTALACIÓN PASO A PASO

### Requisitos Previos

```bash
# Verificar Docker instalado
docker --version
# Output esperado: Docker version 25.0.3 (o más reciente)

# Verificar Docker Compose
docker-compose --version
# Output esperado: Docker Compose version 2.24.0 (o más reciente)

# Verificar Git
git --version
# Output esperado: git version 2.40.1 (o más reciente)

# Verificar Java 17
java -version
# Output esperado: openjdk 17.0.x
```

### Paso 1: Clonar Repositorio

```bash
# Crear carpeta de proyectos
mkdir ~/Projects
cd ~/Projects

# Clonar repositorio
git clone https://github.com/tuusuario/hitboxking.git
cd hitboxking/fullstack_project

# Verificar estructura
ls -la
# Output:
# api-gateway/
# config-server/
# eureka-server/
# frontend/
# ms-agents/
# ms-weapons/
# fullstack/ (MS-Auth)
# docker-compose.yml
# .env.example
```

### Paso 2: Configuración de Ambiente

```bash
# Crear archivo .env
cp .env.example .env

# Editar .env
# Windows: notepad .env
# macOS/Linux: nano .env

# Contenido necesario:
POSTGRES_USER=hitbox_admin
POSTGRES_PASSWORD=SecurePass123!Hitbox
POSTGRES_DB=postgres
```

**⚠️ IMPORTANTE:** El archivo `.env` NUNCA debe subirse a Git (ya está en `.gitignore`).

### Paso 3: Levantar Infraestructura Docker

```bash
# Desde la carpeta raíz (fullstack_project)
docker-compose up -d

# Verificar contenedores
docker ps

# Output esperado:
# CONTAINER ID  IMAGE               PORTS           STATUS
# abc123...     postgres:15-alpine  5432->5432      Up 10 seconds
# def456...     cp-zookeeper        2181->2181      Up 8 seconds
# ghi789...     cp-kafka            9092->9092      Up 5 seconds
```

**⏳ Esperar 15 segundos** a que PostgreSQL termine de inicializar completamente.

### Paso 4: Verificar Bases de Datos

```bash
# Conectarse a PostgreSQL
psql -h localhost -U hitbox_admin -d postgres

# Dentro de PostgreSQL:
\l
# Output esperado:
# db_hitbox_auth           | hitbox_admin | UTF8 | ...
# db_hitbox_main           | hitbox_admin | UTF8 | ...
# db_hitbox_stats_agents   | hitbox_admin | UTF8 | ...
# db_hitbox_stats_weapons  | hitbox_admin | UTF8 | ...

# Salir
\q
```

### Paso 5: Abrir Proyectos en IDE

```bash
# En IntelliJ IDEA:
# File → Open → fullstack_project → OK

# El IDE debe detectar automáticamente:
# - 7 módulos Maven (cada carpeta con pom.xml)
# - Sincronizar automáticamente

# Esperar a que Maven descargue todas las dependencias
# (Puede tomar 3-5 minutos la primera vez)
```

---

## 🔄 ARRANQUE SECUENCIAL

**⚠️ ORDEN CRÍTICO - RESPETAR EXACTAMENTE ESTE ORDEN**

### Terminal 1: Config Server

```bash
# Desde la carpeta fullstack_project
cd config-server

# Limpiar y compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Esperado:
# 2026-05-01 10:30:45.123  INFO ... ConfigServerApplication : Started ConfigServerApplication in 5.234s
# 2026-05-01 10:30:45.234  INFO ... o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8888
```

**✅ Verificación:**

```bash
# En otra terminal:
curl http://localhost:8888/ms-auth/default
# Debe retornar JSON con configuración de ms-auth
```

---

### Terminal 2: Eureka Server

```bash
# Nueva terminal
cd eureka-server

mvn spring-boot:run

# Esperado:
# 2026-05-01 10:31:45.123  INFO ... EurekaServerApplication : Started EurekaServerApplication in 6.123s
# 2026-05-01 10:31:45.234  INFO ... o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8761
```

**✅ Verificación:**

```bash
# Abrir en navegador
open http://localhost:8761
# Debe mostrar dashboard rojo de Eureka
# Sección "Instances currently registered" debe estar vacía (normal por ahora)
```

---

### Terminal 3: MS-Auth (Nombre Eureka: "fullstack")

```bash
# Nueva terminal
cd fullstack

mvn clean compile spring-boot:run

# Esperado:
# 2026-05-01 10:32:45.123  INFO ... FullstackApplication : Started FullstackApplication in 7.456s
# 2026-05-01 10:32:46.234  INFO ... EurekaServiceRegistry : Registering application FULLSTACK with eureka with initial status UP
# 2026-05-01 10:32:46.345  INFO ... DiscoveryClient : InstanceInfoManager updated instanceStatus to UP
```

**✅ Verificación:**

```bash
# Revisar Eureka en navegador (http://localhost:8761)
# Debe mostrar:
# Instances currently registered with Eureka
# FULLSTACK - 1 instance available
```

---

### Terminal 4: MS-Agents

```bash
# Nueva terminal
cd ms-agents

mvn clean compile spring-boot:run

# Esperado:
# 2026-05-01 10:33:45.123  INFO ... MsAgentsApplication : Started MsAgentsApplication in 5.890s
# 2026-05-01 10:33:46.234  INFO ... EurekaServiceRegistry : Registering application MS-AGENTS with eureka with initial status UP
```

**✅ Verificación:**

```bash
curl http://localhost:8083/actuator/health
# Output: {"status":"UP"}
```

---

### Terminal 5: MS-Weapons

```bash
# Nueva terminal
cd ms-weapons

mvn clean compile spring-boot:run

# Esperado:
# 2026-05-01 10:34:45.123  INFO ... MsWeaponsApplication : Started MsWeaponsApplication in 5.678s
# 2026-05-01 10:34:46.234  INFO ... EurekaServiceRegistry : Registering application MS-WEAPONS with eureka with initial status UP
```

---

### Terminal 6: API Gateway

```bash
# Nueva terminal
cd api-gateway

mvn clean compile spring-boot:run

# Esperado:
# 2026-05-01 10:35:45.123  INFO ... GatewayApplication : Started GatewayApplication in 4.234s
# 2026-05-01 10:35:46.234  INFO ... EurekaServiceRegistry : Registering application API-GATEWAY with eureka with initial status UP
```

**✅ Verificación:**

```bash
curl http://localhost:8080/actuator/health
# Output: {"status":"UP"}

# Verify all services registered
curl http://localhost:8761/eureka/apps
# Must show all 6 services
```

---

## ✅ VERIFICACIÓN DE SALUD

### Health Check Endpoints

```bash
# Config Server
curl http://localhost:8888/actuator/health
# {"status":"UP"}

# Eureka Server
curl http://localhost:8761/eureka/
# Abre dashboard web

# MS-Auth
curl http://localhost:8081/actuator/health
# {"status":"UP"}

# MS-Agents
curl http://localhost:8083/actuator/health
# {"status":"UP"}

# MS-Weapons
curl http://localhost:8082/actuator/health
# {"status":"UP"}

# API Gateway
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

### Verificación Completa

```bash
# Script de verificación (crear archivo: verify.sh)
#!/bin/bash

echo "🔍 Verificando Hitboxking Services..."

services=(
  "http://localhost:8888/actuator/health,Config Server (8888)"
  "http://localhost:8761/actuator/health,Eureka Server (8761)"
  "http://localhost:8081/actuator/health,MS-Auth (8081)"
  "http://localhost:8083/actuator/health,MS-Agents (8083)"
  "http://localhost:8082/actuator/health,MS-Weapons (8082)"
  "http://localhost:8080/actuator/health,API Gateway (8080)"
)

for service in "${services[@]}"; do
  URL="${service%%,*}"
  NAME="${service##*,}"

  response=$(curl -s "$URL")

  if echo "$response" | grep -q "UP"; then
    echo "✅ $NAME - OK"
  else
    echo "❌ $NAME - FAILED"
    echo "   Response: $response"
  fi
done

echo ""
echo "📊 Eureka Registered Services:"
curl -s http://localhost:8761/eureka/apps | grep -o '"name":"[^"]*"' | sort | uniq
```

---

## 🐛 TROUBLESHOOTING COMÚN

### Problema 1: Puerto ya en uso

```bash
# Error:
# Address already in use: bind
# Failed to create a new socket: false

# Solución 1: Matar proceso en puerto
# Windows (PowerShell):
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process

# macOS/Linux:
lsof -i :8080  # Encontrar PID
kill -9 <PID>

# Solución 2: Cambiar puerto
# En application.yml del servicio:
server:
  port: 8081  # Cambiar a puerto libre

# Solución 3: Ver qué está usando el puerto
netstat -tuln | grep 8080
```

### Problema 2: PostgreSQL no inicia

```bash
# Error:
# postgres_1  | FATAL: could not create shared memory segment

# Causa: Docker no tiene recursos suficientes

# Solución 1: Asignar más memoria a Docker
# Settings → Resources → Memory: 4GB o más

# Solución 2: Recrear volumen
docker-compose down -v
docker-compose up -d

# Solución 3: Ver logs
docker logs postgres
```

### Problema 3: Microservicio no se registra en Eureka

```bash
# Error:
# No instances available for service: ms-agents
# (En API Gateway logs)

# Solución 1: Verificar Config Server
curl http://localhost:8888/ms-agents/default
# Debe retornar JSON válido

# Solución 2: Revisar logs del microservicio
# En terminal del ms-agents:
# Buscar: "DiscoveryClient", "Registering application"

# Solución 3: Verificar configuración eureka en application.yml
# Debe tener:
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

# Solución 4: Reiniciar en orden
# 1. Eureka
# 2. Luego el microservicio
```

### Problema 4: JWT expirado o inválido

```bash
# Error:
# HTTP 401 Unauthorized
# "Invalid or expired token"

# Solución 1: Hacer nuevo login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Guardar token retornado

# Solución 2: Verificar secret key
# En config-server/config/ms-auth.yml:
app:
  jwt:
    secret: "256-bit-key-string"  # Debe ser consistente

# Solución 3: Token expirado naturalmente
# Duración default: 24 horas
# En config-server/config/ms-auth.yml:
app:
  jwt:
    expiration: 86400000  # ms = 24 horas
```

### Problema 5: Kafka no conecta

```bash
# Error:
# Could not resolve a suitable username for execution
# Not authorized to access topic

# Solución 1: Reiniciar Kafka y Zookeeper
docker-compose restart zookeeper kafka

# Solución 2: Verificar conexión
docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092

# Solución 3: Ver topics disponibles
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Solución 4: Crear topic manualmente si falta
docker exec kafka kafka-topics --create \
  --topic usuario.autenticado \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

### Problema 6: Base de datos no crea tablas

```bash
# Error:
# Table "users" doesn't exist
# Relation "agents" does not exist

# Causa: JPA Hibernate no ejecutó DDL

# Solución 1: Verificar en application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Debe tener este valor

# Solución 2: Recrear contenedores
docker-compose down -v
docker-compose up -d
# Esperar 15 segundos a que Postgres inicialice

# Solución 3: Ejecutar SQL manualmente
docker exec postgres psql -U hitbox_admin -d db_hitbox_auth \
  -f /docker-entrypoint-initdb.d/init-db.sql
```

### Problema 7: CORS error en frontend

```bash
# Error:
# Access to XMLHttpRequest from origin 'http://localhost:3000'
# has been blocked by CORS policy

# Causa: API Gateway no permite origen frontend

# Solución: Configurar CORS en Gateway
# En api-gateway/src/main/resources/application.yml

spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins:
              - "http://localhost:3000"
              - "http://localhost:8080"
            allowed-methods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowed-headers: "*"
            allow-credentials: true
            max-age: 3600
```

---

## 📊 LOGS Y DEBUGGING

### Ver logs en tiempo real

```bash
# Tail logs de un servicio específico
# (Asumiendo terminal con mvn spring-boot:run ejecutándose)
# Los logs aparecen automáticamente en la terminal

# Para logs con grep
mvn spring-boot:run | grep -i "error\|warn\|exception"

# Para logs de Kafka
docker logs kafka | tail -20

# Para logs de PostgreSQL
docker logs postgres | tail -20
```

### Niveles de log

```bash
# En application.yml de cada servicio:

logging:
  level:
    root: INFO                               # Global
    cl.hitzone: DEBUG                        # Nuestro package
    org.springframework: DEBUG                # Spring framework
    org.springframework.cloud: DEBUG          # Spring Cloud
    org.springframework.security: DEBUG       # Security
    org.hibernate: TRACE                     # SQL queries

  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

  file:
    name: logs/application.log               # Guardar en archivo
```

### Debugging avanzado

```bash
# Habilitar debug mode en Maven
mvn -X spring-boot:run

# Attach debugger desde IntelliJ:
# 1. Run → Edit Configurations
# 2. + → Remote JVM Debug
# 3. Host: localhost, Port: 5005
# 4. Ejecutar servicio con debug:
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# 5. Run → Debug "Remote Process"
```

---

## 🧪 TESTING MANUAL

### Test 1: Registro de Usuario

```bash
# Crear usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "SecurePass123!",
    "firstName": "Juan",
    "lastName": "Pérez"
  }'

# Respuesta esperada:
# HTTP 201 Created
# {
#   "userId": 1,
#   "email": "newuser@example.com",
#   "message": "Usuario registrado exitosamente"
# }
```

### Test 2: Login y obtener JWT

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "SecurePass123!"
  }'

# Respuesta esperada:
# HTTP 200 OK
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJuZXd1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MzI5MDAwfQ.Q2CYZ5lW8u_R3GFZQVZ...",
#   "expiresIn": 86400,
#   "userId": 1
# }

# GUARDAR EL TOKEN en variable
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Test 3: Get Agents (Autenticado)

```bash
# Obtener agentes (requiere JWT)
curl http://localhost:8080/api/agents \
  -H "Authorization: Bearer $TOKEN"

# Respuesta esperada:
# HTTP 200 OK
# [
#   {
#     "id": 1,
#     "name": "Jett",
#     "role": "DUELIST",
#     "description": "Agente rápida de Corea",
#     "abilities": [
#       {"id": 1, "name": "Updraft", "type": "Q"},
#       {"id": 2, "name": "Tailwind", "type": "E"},
#       {"id": 3, "name": "Cloudburst", "type": "C"},
#       {"id": 4, "name": "Blade Storm", "type": "ULTIMATE"}
#     ]
#   },
#   ...
# ]
```

### Test 4: Get Weapons

```bash
# Obtener armas
curl http://localhost:8080/api/weapons \
  -H "Authorization: Bearer $TOKEN"

# Filtrar por categoría
curl "http://localhost:8080/api/weapons?category=RIFLE" \
  -H "Authorization: Bearer $TOKEN"

# Rango de precio
curl "http://localhost:8080/api/weapons/price-range?minPrice=1000&maxPrice=3000" \
  -H "Authorization: Bearer $TOKEN"
```

### Test 5: Error Handling

```bash
# Intentar login con credenciales inválidas
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"wrong@example.com","password":"wrong"}'

# Respuesta esperada:
# HTTP 401 Unauthorized
# {
#   "status": 401,
#   "message": "Credenciales inválidas",
#   "timestamp": "2026-05-01T10:30:45"
# }

# Intentar acceder sin token
curl http://localhost:8080/api/agents

# Respuesta esperada:
# HTTP 401 Unauthorized
```

### Script de Testing Completo

```bash
#!/bin/bash
# test-all.sh

API="http://localhost:8080"

echo "=== Test 1: Register ==="
curl -X POST $API/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"TestPass123!"}'

echo -e "\n\n=== Test 2: Login ==="
response=$(curl -s -X POST $API/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"TestPass123!"}')

TOKEN=$(echo $response | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token: $TOKEN"

echo -e "\n\n=== Test 3: Get Agents ==="
curl -s $API/api/agents \
  -H "Authorization: Bearer $TOKEN" | jq '.'

echo -e "\n\n=== Test 4: Get Weapons ==="
curl -s $API/api/weapons \
  -H "Authorization: Bearer $TOKEN" | jq '.'

echo -e "\n\n=== All tests completed ==="
```

---

## 🛑 PARADA Y LIMPIEZA

### Parada ordenada

```bash
# 1. Detener todos los microservicios
# En cada terminal donde está corriendo mvn spring-boot:run:
# Presionar: Ctrl+C

# 2. Detener Docker
docker-compose down

# Verificar que están detenidos
docker ps
# Output: (lista vacía)

# Verificar que contenedores existen pero están stopped
docker ps -a
```

### Limpieza completa

```bash
# ⚠️ CUIDADO: Esto ELIMINA todos los datos de la base de datos

# Parar contenedores y eliminar volúmenes
docker-compose down -v

# Esperar unos segundos
sleep 5

# Verificar
docker volume ls  # No debe mostrar postgres_data

# Limpiar imágenes (opcional)
docker image prune -a
```

### Reinicio completo desde cero

```bash
# 1. Eliminar todo
docker-compose down -v

# 2. Limpiar archivos compilados
find . -name target -type d -exec rm -rf {} + 2>/dev/null
find . -name .m2 -type d -exec rm -rf {} + 2>/dev/null

# 3. Limpiar Maven cache (opcional)
rm -rf ~/.m2/repository/cl/hitzone

# 4. Comenzar de nuevo
docker-compose up -d
# (esperar 15 segundos)
# Terminal 1: config-server
# Terminal 2: eureka-server
# ... etc
```

---

## 📚 MEJORES PRÁCTICAS DE DESARROLLO

### 1. Control de Versiones

```bash
# Crear rama para feature
git checkout -b feature/agregar-estadisticas-avanzadas

# Trabajar en la rama
# ... editar archivos ...

# Commit regular
git add src/main/java/cl/hitzone/ms_agents/service/StatsService.java
git commit -m "feat(ms-agents): agregar servicio de estadísticas avanzadas"

# Push a origen
git push origin feature/agregar-estadisticas-avanzadas

# Crear Pull Request en GitHub
# Esperar review
# Merge cuando sea aprobado

# Volver a main
git checkout main
git pull origin main
```

### 2. Formato de Commits (Conventional Commits)

```
feat(ms-agents): agregar endpoint para obtener estadísticas por agente
  - Implementar AgentStatsController
  - Crear AgentStatsService
  - Agregar tests unitarios

fix(ms-weapons): corregir cálculo de daño crítico
  - El multiplicador de crítico estaba duplicado
  - Actualizar tests para refleja ajuste

docs(README): actualizar instrucciones de instalación

test(ms-auth): agregar tests para validación JWT

refactor(api-gateway): simplificar filtros globales
  - Usar GatewayFilterFactory en lugar de GlobalFilter
  - Mejorar performance 15%
```

### 3. Organización de Branches

```
main (producción)
  └─ release/1.0.0 (release branch)
  └─ develop (staging)
      └─ feature/autenticacion-2fa
      └─ feature/estadisticas-avanzadas
      └─ bugfix/jwt-expiration
```

### 4. Código Limpio

```java
// ❌ MAL: Nombres poco claros
public void proc(List l) {
    for (int i = 0; i < l.size(); i++) {
        System.out.println(l.get(i));
    }
}

// ✅ BIEN: Nombres descriptivos
public void printAgentNames(List<Agent> agents) {
    agents.forEach(agent -> System.out.println(agent.getName()));
}

// ❌ MAL: Método muy largo
public void complexBusinessLogic() {
    // 500 líneas de código
}

// ✅ BIEN: Métodos pequeños y focalizados
private boolean validateAgentRole(Agent agent) { ... }
private void persistAgentToDatabase(Agent agent) { ... }
private void publishAgentCreatedEvent(Agent agent) { ... }
```

### 5. Testing

```java
// Estructura AAA: Arrange, Act, Assert

@Test
void testCreateAgent() {
    // Arrange (Preparar)
    AgentRequestDTO request = new AgentRequestDTO();
    request.setName("Phoenix");
    request.setRole(AgentRole.CONTROLLER);

    // Act (Actuar)
    AgentResponseDTO result = agentService.createAgent(request);

    // Assert (Verificar)
    assertNotNull(result);
    assertEquals("Phoenix", result.getName());
    assertTrue(result.getId() > 0);
}

// Usar descriptivos
@Test
void shouldThrowExceptionWhenCreatingDuplicateAgent() { ... }

@Test
void shouldReturnAgentWithAbilitiesWhenFetching() { ... }
```

### 6. Documentación

```java
/**
 * Crear nuevo agente en el sistema.
 *
 * @param request DTO con datos del agente (nombre, rol, habilidades)
 * @return AgentResponseDTO con datos del agente creado incluyendo ID
 * @throws DuplicateResourceException si ya existe agente con ese nombre
 * @throws ValidationException si los datos no pasan validaciones
 *
 * Ejemplo:
 * POST /api/agents
 * Content-Type: application/json
 * {
 *   "name": "Phoenix",
 *   "role": "CONTROLLER",
 *   "abilities": [...]
 * }
 *
 * Response:
 * HTTP 201 Created
 * { "id": 1, "name": "Phoenix", ... }
 */
@PostMapping
public ResponseEntity<AgentResponseDTO> createAgent(
    @Valid @RequestBody AgentRequestDTO request) {
    return ...;
}
```

### 7. Seguridad

```java
// ❌ MAL: Contraseña en texto plano
user.setPassword("SecurePass123!");

// ✅ BIEN: Contraseña hasheada
String hashedPassword = bCryptPasswordEncoder.encode("SecurePass123!");
user.setPassword(hashedPassword);

// ❌ MAL: SQL Injection
String query = "SELECT * FROM users WHERE email = '" + email + "'";

// ✅ BIEN: Parametrizado
@Query("SELECT u FROM User u WHERE u.email = :email")
Optional<User> findByEmail(@Param("email") String email);

// ❌ MAL: Exponer IPs internas
new URL("http://192.168.1.100:8081/api/agents")

// ✅ BIEN: Usar Eureka
@FeignClient("ms-agents")
public interface AgentServiceClient { ... }
```

### 8. Performance

```java
// ❌ MAL: N+1 query problem
List<Agent> agents = agentRepository.findAll();
agents.forEach(agent -> {
    List<Ability> abilities = abilityRepository.findByAgentId(agent.getId());
    // 1 + N queries totales
});

// ✅ BIEN: Eager loading
@OneToMany(fetch = FetchType.EAGER)
List<Ability> abilities;

// ✅ MEJOR: Explícita con @Query
@Query("SELECT a FROM Agent a LEFT JOIN FETCH a.abilities")
List<Agent> findAllWithAbilities();

// Caché
@Cacheable("agents")
public List<Agent> getAllAgents() {
    return agentRepository.findAll();
}
```

---

**Fin de la Guía de Operaciones**
