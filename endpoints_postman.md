# Hitzone API Endpoints - Postman Testing Guide

Esta guía contiene todos los endpoints expuestos a través del **API Gateway** (por defecto en el puerto `8080`) listos para ser testeados en Postman u Obsidian.

Todos los endpoints deben ser llamados apuntando a `http://localhost:8080` (ej: `http://localhost:8080/api/auth/login`).

---

## 🔐 1. Autenticación y Usuarios (`ms-auth` / `fullstack`)
**Base Path:** `/api/auth` (o `/api/v1/auth`)

- `POST` `/api/auth/register` - Registrar un nuevo usuario.
  ```json
  {
    "username": "johndoe",
    "password": "Password123!",
    "email": "john.doe@example.com"
  }
  ```
- `POST` `/api/auth/login` - Iniciar sesión (Devuelve token JWT).
  ```json
  {
    "username": "johndoe",
    "password": "Password123!"
  }
  ```
- `GET` `/api/auth/validate` - Validar un token JWT (Requiere Header: `Authorization: Bearer <token>`).
- `GET` `/api/auth/stats/users/count` - Obtener el total de usuarios.
- `GET` `/api/auth/stats/users/by-role` - Obtener estadísticas de usuarios por rol.
- `GET` `/api/auth/users/search?username={username}` - Buscar usuarios por nombre.

---

## 🕵️ 2. Agentes (`ms-agents`)
**Base Path:** `/api/agents`

**Valores Permitidos (Enums):**
- **Roles válidos (`role`):** `DUELIST`, `CONTROLLER`, `SENTINEL`, `INITIATOR`
- **Tipos de habilidad (`type`):** `Q`, `E`, `C`, `ULTIMATE`

- `GET` `/api/agents` - Listar todos los agentes.
- `GET` `/api/agents/{id}` - Obtener un agente por ID.
- `GET` `/api/agents/{id}/abilities` - Obtener las habilidades de un agente.
- `GET` `/api/agents/role/{role}` - Listar agentes por su rol (Usar uno de los roles válidos).
- `GET` `/api/agents/stats/by-role` - Estadísticas de cantidad de agentes por rol.
- `POST` `/api/agents` - Crear un nuevo agente.
  ```json
  {
    "name": "Jett",
    "role": "DUELIST",
    "description": "Agente ágil y evasiva de Corea del Sur.",
    "imageUrl": "https://example.com/jett.png",
    "abilities": [
      {
        "name": "Cloudburst",
        "type": "C",
        "description": "Lanza un proyectil que se expande formando una nube que bloquea la visión."
      },
      {
        "name": "Tailwind",
        "type": "E",
        "description": "Se impulsa rápidamente en la dirección en la que se mueve."
      }
    ]
  }
  ```
- `PUT` `/api/agents/{id}` - Actualizar un agente. (Usa el mismo formato JSON que POST)
- `DELETE` `/api/agents/{id}` - Eliminar un agente.

---

## 🔫 3. Armas (`ms-weapons`)
**Base Path:** `/api/weapons`

- `GET` `/api/weapons` - Listar todas las armas.
- `GET` `/api/weapons/{id}` - Obtener arma por ID.
- `GET` `/api/weapons/category/{cat}` - Filtrar armas por categoría (ej: RIFLE, PISTOL).
- `GET` `/api/weapons/price-range?min={min}&max={max}` - Filtrar armas por rango de precio.
- `GET` `/api/weapons/cheapest` - Obtener las armas más baratas.
- `GET` `/api/weapons/stats/by-category` - Estadísticas de armas por categoría.
- `POST` `/api/weapons` - Crear una nueva arma.
  ```json
  {
    "name": "Vandal",
    "category": "RIFLE",
    "damage": 40.0,
    "fireRate": 9.75,
    "magazineSize": 25,
    "price": 2900,
    "description": "Rifle de asalto preciso capaz de eliminar de un tiro a la cabeza a cualquier distancia."
  }
  ```
- `PUT` `/api/weapons/{id}` - Actualizar un arma. (Usa el mismo formato JSON que POST)
- `DELETE` `/api/weapons/{id}` - Eliminar un arma.

---

## 🗺️ 4. Mapas (`ms-maps`)
**Base Path:** `/api/maps`

- `GET` `/api/maps` - Listar todos los mapas.
- `GET` `/api/maps/{id}` - Obtener un mapa por ID.
- `GET` `/api/maps/tipo/{tipo}` - Filtrar por tipo.
- `GET` `/api/maps/analizar/{id}` - Analizar mapa.
- `GET` `/api/maps/filter` - Filtros adicionales de mapas.
- `GET` `/api/maps/stats/by-difficulty` - Estadísticas de mapas por dificultad.
- `POST` `/api/maps` - Crear un nuevo mapa.
  ```json
  {
    "name": "Ascent",
    "type": "COMPETITIVE",
    "difficulty": "MEDIUM",
    "description": "Mapa situado en Italia con un área central abierta y puertas de metal que se pueden cerrar.",
    "totalSite": 2
  }
  ```
- `PUT` `/api/maps/{id}` - Actualizar un mapa. (Usa el mismo formato JSON que POST)
- `DELETE` `/api/maps/{id}` - Eliminar un mapa.

---

## 📰 5. Noticias (`ms-news`)
**Base Path:** `/api/v1/news`

- `GET` `/api/v1/news` - Listar todas las noticias.
- `GET` `/api/v1/news/{id}` - Obtener noticia por ID.
- `GET` `/api/v1/news/category/{cat}` - Filtrar por categoría.
- `GET` `/api/v1/news/author/{author}` - Filtrar por autor.
- `GET` `/api/v1/news/count` - Cantidad total de noticias.
- `POST` `/api/v1/news` - Crear noticia.
  ```json
  {
    "title": "Notas de la versión 8.11",
    "summary": "Balance de agentes y ajustes en el mapa Haven.",
    "content": "Contenido completo detallando los nerfs de Reyna y Raze...",
    "category": "PATCH_NOTES",
    "author": "Riot Games",
    "imageUrl": "https://example.com/patch811.png",
    "published": true
  }
  ```
- `PUT` `/api/v1/news/{id}` - Actualizar noticia. (Usa el mismo formato JSON que POST)
- `DELETE` `/api/v1/news/{id}` - Eliminar noticia.

---

## 🎮 6. Partidas (`ms-matches`)
**Base Path:** `/api/matches`

- `GET` `/api/matches` - Listar todas las partidas.
- `GET` `/api/matches/{id}` - Obtener partida por ID interno.
- `GET` `/api/matches/match/{matchId}` - Obtener partida por el ID alfanumérico.
- `GET` `/api/matches/result/{result}` - Filtrar por resultado (ej: WIN, LOSS).
- `GET` `/api/matches/mode/{gameMode}` - Filtrar por modo de juego (ej: COMPETITIVE).
- `GET` `/api/matches/map/{mapName}` - Filtrar partidas por mapa.
- `GET` `/api/matches/player/{username}/history` - Historial de partidas de un jugador.
- `GET` `/api/matches/player/{username}/kda` - Estadísticas de KDA del jugador.
- `POST` `/api/matches` - Registrar nueva partida.
  ```json
  {
    "matchId": "MATCH-987654321",
    "mapName": "Ascent",
    "gameMode": "COMPETITIVE",
    "result": "WIN",
    "scoreTeam": 13,
    "scoreEnemy": 11,
    "durationS": 2450,
    "players": [
      {
        "username": "johndoe",
        "agentName": "Jett",
        "kills": 25,
        "deaths": 12,
        "assists": 4,
        "rrChange": 24
      }
    ]
  }
  ```
- `DELETE` `/api/matches/{id}` - Eliminar partida.

---

## 🏆 7. Rangos (`ms-rank`)
**Base Path:** `/api/v1/rank` (También accesible vía `/api/v1/ranks`)

- `GET` `/api/v1/rank` - Listar todos los rangos de jugadores.
- `GET` `/api/v1/rank/{username}` - Obtener rango de un jugador específico.
- `GET` `/api/v1/rank/top/{count}` - Obtener el Top N jugadores.
- `GET` `/api/v1/rank/distribution` - Distribución estadística de los rangos.
- `GET` `/api/v1/rank/search` - Buscar rangos.
- `POST` `/api/v1/rank` - Crear registro de rango inicial.
  ```json
  {
    "username": "johndoe",
    "rrPoints": 1500,
    "wins": 45,
    "losses": 30
  }
  ```
- `PUT` `/api/v1/rank/{username}` - Actualizar rango de un jugador. (Usa el mismo formato JSON que POST)
- `DELETE` `/api/v1/rank/{username}` - Eliminar el rango de un jugador.

---

## 📋 8. Auditoría/Persistencia (`ms-persistence`)
**Base Path:** `/api/v1/audit`

- `GET` `/api/v1/audit` - Listar todos los logs de auditoría.
- `GET` `/api/v1/audit/service/{name}` - Filtrar logs por microservicio.
- `GET` `/api/v1/audit/user/{username}` - Filtrar logs por nombre de usuario.
- `GET` `/api/v1/audit/stats/by-action` - Conteo de logs agrupados por acción.
- `POST` `/api/v1/audit` - Registrar un evento de auditoría manualmente.
  ```json
  {
    "serviceName": "ms-auth",
    "action": "USER_LOGIN",
    "entityType": "User",
    "entityId": 1234,
    "username": "johndoe",
    "details": "El usuario inició sesión desde la IP 192.168.1.1",
    "status": "SUCCESS"
  }
  ```
