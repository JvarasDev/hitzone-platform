# Cambios de Seguridad — HitZone Platform

## Problema que se resolvió

El `api-gateway` y `ms-auth` tenían **responsabilidades de autenticación duplicadas y conflictivas**: el gateway validaba el JWT localmente pero la `ConfigSecurity` de `ms-auth` bloqueaba con `anyRequest().authenticated()` el endpoint `/api/auth/validate` que el gateway necesita consultar. Además, el filtro JWT del gateway retornaba respuestas 401 **sin body JSON** (respuesta vacía), lo que confundía al frontend. Las rutas de la whitelist también estaban mal definidas, causando que llamadas legítimas fueran rechazadas.

---

## Arquitectura de seguridad resultante

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         FLUJO DE AUTENTICACIÓN                          │
└─────────────────────────────────────────────────────────────────────────┘

Cliente (navegador / Postman)
        │
        ▼
┌───────────────┐
│  API GATEWAY  │  puerto 8080  ← ÚNICO punto de entrada
│  :8080        │
│               │
│  JwtAuthentic │  ← Valida JWT localmente con la misma clave que ms-auth
│  ationFilter  │
│               │
│  ¿Ruta        │
│  pública?     │
│  /api/auth/   │──YES──►  ms-auth (sin validar token)
│  login|regist │
│               │
│  ¿Token       │
│  válido?      │──NO───►  401 {"status":401,"error":"Unauthorized",
│               │              "message":"Token inválido o ausente"}
│               │
│  Token OK     │──YES──►  Propaga Authorization header al microservicio
└───────────────┘
        │
        ├──────────────────────────────────────────────┐
        │                                              │
        ▼                                              ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   ms-auth    │  │  ms-weapons  │  │  ms-agents   │  │  ms-maps     │
│   :8081      │  │   (negocio)  │  │   (negocio)  │  │  (negocio)   │
│              │  │              │  │              │  │              │
│  /login      │  │ SIN spring-  │  │ SIN spring-  │  │ SIN spring-  │
│  /register   │  │ security     │  │ security     │  │ security     │
│  /validate   │  │              │  │              │  │              │
│  ← públicos  │  │ Confía en el │  │ Confía en el │  │ Confía en el │
│              │  │ gateway      │  │ gateway      │  │ gateway      │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘

        ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
        │  ms-matches  │  │   ms-news    │  │ms-persistence│  │   ms-rank    │
        │  (negocio)   │  │  (negocio)   │  │  (negocio)   │  │  (negocio)   │
        │              │  │              │  │              │  │              │
        │ SIN spring-  │  │ SIN spring-  │  │ SIN spring-  │  │ SIN spring-  │
        │ security     │  │ security     │  │ security     │  │ security     │
        └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
```

---

## Cambios realizados por servicio

### api-gateway

- **Archivos modificados:**
  - `src/main/java/cl/hitzone/api_gateway/filter/JwtAuthenticationFilter.java`
  - `src/main/resources/application.yml`
- **Archivos creados:** ninguno

**Qué hace cada cambio:**

| Archivo | Cambio | Por qué |
|---------|--------|---------|
| `JwtAuthenticationFilter.java` | Whitelist corregida a `/api/auth/login`, `/api/auth/register`, `/api/auth/validate` | Antes usaba paths incompletos que causaban 401 en rutas públicas |
| `JwtAuthenticationFilter.java` | `onError()` ahora retorna JSON `{"status":401,"error":"Unauthorized","message":"..."}` | Antes retornaba 401 con body vacío; el frontend JS no podía parsear la respuesta |
| `JwtAuthenticationFilter.java` | Propaga el header `Authorization` completo + `X-Auth-Username` al microservicio destino | Los microservicios pueden leer el username si lo necesitan sin re-validar el token |
| `JwtAuthenticationFilter.java` | Logs `[GW]` para debug en cada decisión de filtrado | Facilita diagnóstico en desarrollo |
| `application.yml` | Nueva ruta `ms-auth-v1-route` con `RewritePath` `/api/v1/auth/` → `/api/auth/` | Permite que el frontend llame `/api/v1/auth/login` aunque el microservicio lo expone en `/api/auth/login` |

---

### ms-auth (módulo `fullstack`)

- **Archivos modificados:**
  - `src/main/java/cl/hitzone/fullstack/config/security/ConfigSecurity.java`
  - `src/main/java/cl/hitzone/fullstack/controller/AuthController.java`
- **Archivos creados:** ninguno
- **Archivos eliminados:** ninguno

**Qué hace cada cambio:**

| Archivo | Cambio | Por qué |
|---------|--------|---------|
| `ConfigSecurity.java` | Se reemplaza `.requestMatchers("/api/auth/**").permitAll()` por `.requestMatchers("/api/auth/login").permitAll()` + `.requestMatchers("/api/auth/register").permitAll()` + `.requestMatchers("/api/auth/validate").permitAll()` | La versión anterior con wildcard `/**` era correcta pero ambigua; ahora cada ruta pública es explícita. **Lo crítico**: `/api/auth/validate` debe ser público para que el gateway (sin credenciales propias) pueda llamarlo. |
| `AuthController.java` | Se agrega `GET /api/auth/validate` | El endpoint no existía. El gateway lo necesita para delegar la validación. Retorna `{"valid": true/false, "username": "..."}`. |

---

### ms-weapons / ms-agents / ms-maps / ms-persistence / ms-ranks / ms-news / ms-matches

- **Archivos modificados:** **ninguno**
- **Archivos eliminados:** **ninguno**
- **Archivos creados:** **ninguno**

> ✅ **Ninguno de estos servicios tiene `spring-boot-starter-security` en su `pom.xml`.**
> Por lo tanto, Spring Security no está activo en ellos y no hay ningún filtro JWT que eliminar.
> El gateway los protege completamente. No se requiere ningún cambio.

---

## Rutas públicas (sin token)

| Método | Ruta (en el gateway) | Ruta real en el microservicio | Servicio destino |
|--------|---------------------|-------------------------------|-----------------|
| `POST` | `/api/auth/login` | `/api/auth/login` | ms-auth |
| `POST` | `/api/auth/register` | `/api/auth/register` | ms-auth |
| `POST` | `/api/v1/auth/login` | `/api/auth/login` (rewrite) | ms-auth |
| `POST` | `/api/v1/auth/register` | `/api/auth/register` (rewrite) | ms-auth |
| `GET` | `/api/auth/validate` | `/api/auth/validate` | ms-auth (uso interno) |

---

## Rutas protegidas (requieren Bearer token)

| Método | Ruta en el gateway | Servicio destino | Descripción |
|--------|--------------------|-----------------|-------------|
| `GET/POST/PUT/DELETE` | `/api/weapons/**` | ms-weapons | CRUD de armas |
| `GET/POST/PUT/DELETE` | `/api/agents/**` | ms-agents | CRUD de agentes |
| `GET/POST/PUT/DELETE` | `/api/maps/**` | ms-maps | CRUD de mapas |
| `GET/POST/PUT/DELETE` | `/api/matches/**` | ms-matches | CRUD de partidas |
| `GET/POST/PUT/DELETE` | `/api/v1/news/**` | ms-news | CRUD de noticias |
| `GET/POST` | `/api/v1/audit/**` | ms-persistence | Auditoría |
| `GET/POST` | `/api/v1/rank/**` | ms-rank | Rankings |
| `GET/POST/PUT/DELETE` | `/api/users/**` | ms-auth | Gestión de usuarios |

---

## Cómo probar que funciona (Postman paso a paso)

### Paso 1 — Registrar un usuario (sin token)
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "jugador1",
  "email": "jugador1@hitzone.cl",
  "password": "Test1234!"
}
```
**Esperado:** `201 Created` — "Usuario 'jugador1' registrado correctamente."

---

### Paso 2 — Login y obtener el token
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "jugador1",
  "password": "Test1234!"
}
```
**Esperado:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "jugador1"
}
```
> Copia el valor de `"token"` para los siguientes pasos.

---

### Paso 3 — Acceder a ruta protegida SIN token (debe fallar)
```
GET http://localhost:8080/api/weapons
```
**Esperado:** `401 Unauthorized`
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o ausente"
}
```

---

### Paso 4 — Acceder a ruta protegida CON token válido (debe funcionar)
```
GET http://localhost:8080/api/weapons
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
**Esperado:** `200 OK` con el listado de armas.

---

### Paso 5 — Acceder con token expirado o manipulado (debe fallar)
```
GET http://localhost:8080/api/weapons
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.TOKEN_MODIFICADO_A_MANO
```
**Esperado:** `401 Unauthorized`
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o ausente"
}
```

---

### Paso 6 — Validar un token directamente (endpoint interno)
```
GET http://localhost:8080/api/auth/validate
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
**Esperado:** `200 OK`
```json
{
  "valid": true,
  "username": "jugador1"
}
```

---

## Por qué este diseño

### Centralización de la autenticación en el gateway

El patrón **"Authentication at the Edge"** es la práctica estándar en arquitecturas de microservicios:

1. **Un único punto de validación**: Si la clave JWT cambia o se añade una nueva regla de seguridad (p.ej. blacklist de tokens), solo hay que modificar el gateway — no 7 microservicios.

2. **Microservicios más simples y cohesivos**: Cada microservicio hace exactamente lo que su nombre indica (gestionar armas, mapas, etc.). No necesitan conocer nada de autenticación.

3. **Sin duplicación de secretos**: Aunque actualmente el gateway y ms-auth comparten el mismo `jwt.secret` (lo que permite validación local en el gateway), esta arquitectura también soporta fácilmente migrar a validación remota via `/api/auth/validate` si se necesita en el futuro.

4. **Superficie de ataque reducida**: Los microservicios de negocio no tienen puertos expuestos directamente al exterior. Solo el gateway (`:8080`) es accesible desde fuera de la red de servicios.

5. **Consistencia de respuestas de error**: Todos los 401 vienen del mismo sitio (el gateway), con el mismo formato JSON, facilitando el manejo de errores en el frontend.

### Por qué el gateway valida localmente y no delega siempre a /api/auth/validate

La validación **local** en el gateway (con la misma clave HMAC-SHA) es más eficiente: evita un round-trip de red adicional por cada request. El endpoint `/api/auth/validate` existe como utilidad y como puente para sistemas externos, pero el flujo principal de autenticación no lo usa — el gateway verifica la firma JWT directamente.

> ⚠️ **Requisito crítico de sincronización**: El valor de `jwt.secret` en `api-gateway/application.yml` y en `fullstack/application.yml` (ms-auth) **deben ser idénticos**. Si se cambia en uno, hay que cambiarlo en el otro. Actualmente ambos usan:
> ```
> jwt.secret: hitzone-super-secret-key-2026-must-be-at-least-32-chars
> ```
