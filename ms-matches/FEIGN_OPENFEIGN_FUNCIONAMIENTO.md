# ms-matches — Estado Actual y Funcionamiento

> **Estado**: ✅ Completamente funcional. Tests: `1 run, 0 failures, 0 errors`.

---

## Arquitectura del Microservicio

```
ms-matches (puerto 8087)
├── MsMatchesApplication.java          → @SpringBootApplication + @EnableFeignClients
├── controller/
│   └── MatchController.java           → REST endpoints /api/matches/**
├── service/
│   ├── MatchService.java              → Interfaz de contrato
│   └── impl/MatchServiceImpl.java     → Lógica de negocio
├── client/
│   ├── RankClient.java                → FeignClient → ms-rank (con fallback)
│   └── RankClientFallback.java        → Respuesta UNRANKED si ms-rank no está disponible
├── dto/
│   ├── MatchRequestDTO.java
│   ├── MatchResponseDTO.java
│   ├── MatchPlayerDTO.java
│   ├── KdaSummaryDTO.java
│   └── PlayerRankResponseDTO.java     → DTO local que mapea la respuesta de ms-rank
├── model/
│   ├── Match.java
│   └── MatchPlayer.java
├── repository/
│   ├── MatchRepository.java
│   └── MatchPlayerRepository.java
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ApiError.java
    ├── ResourceNotFoundException.java
    └── DuplicateResourceException.java
```

---

## Cómo funciona el cliente OpenFeign con ms-rank

### RankClient.java

```java
@FeignClient(name = "ms-rank", path = "/api/v1/rank", fallback = RankClientFallback.class)
public interface RankClient {
    @GetMapping("/{username}")
    PlayerRankResponseDTO getPlayerRank(
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String token
    );
}
```

**Flujo normal (ms-rank disponible):**
1. `ms-matches` llama a `rankClient.getPlayerRank(username, token)`.
2. Feign resuelve la dirección de `ms-rank` consultando **Eureka** (service discovery, no URL hardcodeada).
3. Feign hace HTTP `GET http://ms-rank/api/v1/rank/{username}` con el header `Authorization`.
4. La respuesta JSON se deserializa automáticamente en `PlayerRankResponseDTO`.

**Flujo de falla (ms-rank caído):**
1. Feign detecta la excepción (timeout, connection refused, etc.).
2. El Circuit Breaker activado por `spring.cloud.openfeign.circuitbreaker.enabled: true` intercepta.
3. Se invoca automáticamente `RankClientFallback.getPlayerRank()`.
4. El cliente recibe un DTO con `rankName: "UNRANKED"` y todos los números en `0` — sin lanzar excepción.

### RankClientFallback.java

```java
@Component
@Slf4j
public class RankClientFallback implements RankClient {
    @Override
    public PlayerRankResponseDTO getPlayerRank(String username, String token) {
        log.warn("ms-rank no está disponible. Retornando rank por defecto para usuario: {}", username);
        PlayerRankResponseDTO fallback = new PlayerRankResponseDTO();
        fallback.setUsername(username);
        fallback.setRankName("UNRANKED");
        fallback.setRankNumber(0);
        fallback.setRrPoints(0);
        fallback.setWins(0);
        fallback.setLosses(0);
        return fallback;
    }
}
```

---

## Configuración activa (application.yml)

```yaml
server:
  port: 8087

spring:
  application:
    name: ms-matches
  config:
    import: "optional:configserver:"        # Lee del Config Server (port 8888), falla opcional
  cloud:
    openfeign:
      circuitbreaker:
        enabled: true                        # Activa el fallback en Feign
  datasource:
    url: jdbc:postgresql://localhost:5432/db_hitzone_matches
    username: postgres
    password: ${DB_PASSWORD:HitzonePassword123!}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate                     # Flyway gestiona el esquema, JPA solo valida
    show-sql: true
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

**Propiedad clave:** `spring.cloud.openfeign.circuitbreaker.enabled: true` es lo que conecta el mecanismo de fallback de Feign con Spring Cloud CircuitBreaker. Sin esta línea, la anotación `fallback = RankClientFallback.class` en `@FeignClient` no tiene efecto.

---

## Endpoints REST expuestos

| Método | Ruta | Descripción |
|:------:|:-----|:------------|
| `GET` | `/api/matches` | Lista todas las partidas ordenadas por fecha |
| `GET` | `/api/matches/{id}` | Busca partida por ID interno (PK) |
| `GET` | `/api/matches/match/{matchId}` | Busca por ID del servidor de juego |
| `GET` | `/api/matches/result/{result}` | Filtra por resultado: WIN, LOSS, DRAW |
| `GET` | `/api/matches/mode/{gameMode}` | Filtra por modo: COMPETITIVE, UNRATED, etc. |
| `GET` | `/api/matches/map/{mapName}` | Filtra por mapa: Ascent, Bind, etc. |
| `GET` | `/api/matches/player/{username}/history` | Historial de partidas de un jugador |
| `GET` | `/api/matches/player/{username}/kda` | Resumen KDA acumulado del jugador |
| `POST` | `/api/matches` | Crea partida con lista de jugadores (transacción atómica) |
| `DELETE` | `/api/matches/{id}` | Elimina partida (cascade a MatchPlayer) |

---

## Manejo de errores estándar (GlobalExceptionHandler)

Todos los errores se devuelven en el formato `ApiError`:

```json
{
  "timestamp": "2026-05-21T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Partida no encontrada con ID: 99",
  "path": "/api/matches/99"
}
```

| Excepción | Código HTTP | Situación |
|:----------|:-----------:|:----------|
| `ResourceNotFoundException` | 404 | Partida o jugador no encontrado |
| `DuplicateResourceException` | 409 | Ya existe una partida con ese `matchId` |
| `MethodArgumentNotValidException` | 400 | Campos requeridos faltantes o inválidos en el body |
| `Exception` (genérica) | 500 | Error inesperado del servidor |

---

## Integración con el ecosistema HitZone

```
Config Server (8888)
        │
        │ entrega configuración centralizada
        ▼
ms-matches (8087) ──── Eureka (8761) ──── ms-rank (8088)
        │                                       │
        │  GET /api/v1/rank/{username}           │
        │◄──────────────────────────────────────┤
        │                                       │
        │  [Si ms-rank caído]                   │
        │  RankClientFallback → UNRANKED         │
```

- **Config Server**: `ms-matches` carga configuración adicional desde el servidor centralizado al inicio (la clave `optional:` evita que falle si no está disponible).
- **Eureka**: `ms-matches` se registra en el servidor de descubrimiento y usa el nombre lógico `ms-rank` para resolver la dirección real del servicio, sin URLs hardcodeadas.
- **OpenFeign + CircuitBreaker**: La comunicación con `ms-rank` es resiliente — si el servicio cae, el fallback entra en acción automáticamente.

---

## Verificación rápida

```bash
# 1. Verificar que compila y tests pasan
cd ms-matches && mvnw test

# 2. Con todos los servicios arriba, verificar ranking de un jugador
# (ms-rank disponible → retorna rank real)
curl http://localhost:8087/api/matches/player/JohnDoe/history

# 3. Con ms-rank detenido, verificar fallback
# → logs deben mostrar: "ms-rank no está disponible. Retornando rank por defecto..."
```
