# Plan de Implementación: Microservicio MS-Weapons (Según Jira)

Este plan detalla los pasos para construir el microservicio `ms-weapon`, basándose estrictamente en los requerimientos y criterios de aceptación definidos en tu tablero de Jira, e incluyendo el diseño de base de datos normalizado.

## User Review Required

> [!IMPORTANT]
> Revisa este plan con el paquete confirmado (`cl.hitzone.ms_weapons`). 
> **Confírmame si ya generaste el proyecto en Spring Initializr y lo abriste en tu IDE para que empiece a codificar.**

---

## 📊 Diseño de Base de Datos (Tercera Forma Normal - 3NF)

Para asegurar que nuestra base de datos cumpla con la **Tercera Forma Normal (3NF)**, el diseño debe garantizar que:
1.  **1NF:** Todos los atributos son atómicos.
2.  **2NF:** No hay dependencias parciales.
3.  **3NF:** No hay dependencias transitivas.

### Esquema Relacional (`db_hitbox_stats_weapons`)

El requerimiento de Jira propone una estructura que **ya cumple orgánicamente con 3NF** al usar un tipo ENUM estricto.

**Tabla: `weapons`**
| Columna | Tipo de Dato (PostgreSQL) | Restricciones | Dependencia |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Clave Primaria |
| `name` | `VARCHAR(100)` | `NOT NULL`, `UNIQUE` | Depende de `id` |
| `category` | `VARCHAR(50)` (Enum) | `NOT NULL`, `CHECK(category IN (...))` | Depende de `id` |
| `damage` | `DECIMAL(10,2)` | `NOT NULL`, `>= 0` | Depende de `id` |
| `fire_rate` | `DECIMAL(10,2)` | `NOT NULL`, `>= 0` | Depende de `id` |
| `magazine_size` | `INTEGER` | `NOT NULL`, `>= 0` | Depende de `id` |
| `price` | `INTEGER` | `NOT NULL`, `>= 0` | Depende de `id` |
| `description` | `TEXT` | `NULLABLE` | Depende de `id` |
| `created_at` | `TIMESTAMP` | `NOT NULL`, `DEFAULT NOW()`| Depende de `id` |
| `updated_at` | `TIMESTAMP` | `NOT NULL`, `DEFAULT NOW()`| Depende de `id` |

*Nota:* Como Jira estipula que `category` es simplemente un ENUM de valores estáticos, almacenarlo en la tabla `weapons` es válido y respeta la 3NF.

---

## Paso 1: Generación en Spring Initializr (Acción Manual Requerida)

Ve a [start.spring.io](https://start.spring.io/) y configura el proyecto:
- **Group:** `cl.hitzone`
- **Artifact:** `ms-weapons`
- **Package name:** `cl.hitzone.ms_weapons`
- **Dependencies:** Spring Web, Spring Data JPA, PostgreSQL Driver, Eureka Discovery Client, Config Client, Validation, Lombok.

---

## Proposed Changes (Ejecución de Código)

Una vez que tengas el proyecto abierto, crearé toda esta estructura automáticamente en el paquete `cl.hitzone.ms_weapons`:

### 1. Configuración Principal
#### [NEW] `src/main/resources/application.yml`
- Puerto: `8082`, Base de datos: `db_hitbox_stats_weapons`, Eureka Name: `MS-WEAPON`

### 2. Entidades y Enums
#### [NEW] `model/WeaponCategory.java` (Enum)
`RIFLE`, `PISTOL`, `SMG`, `SNIPER`, `SHOTGUN`, `HEAVY`
#### [NEW] `model/Weapon.java` (Entity)

### 3. Data Inicial
#### [NEW] `src/main/resources/data.sql`
Scripts `INSERT` con 10 armas reales de Valorant.

### 4. Capa de Datos (Repository)
#### [NEW] `repository/WeaponRepository.java`
- `findByCategory()`, `findByName()`, `@Query` para `findByPriceRange()`, `existsByName()`, `existsByNameAndIdNot()`

### 5. DTOs y Validaciones
#### [NEW] `dto/WeaponRequestDTO.java` y `dto/WeaponResponseDTO.java`

### 6. Excepciones Globales
#### [NEW] `exception/ResourceNotFoundException.java`
#### [NEW] `exception/DuplicateResourceException.java`
#### [NEW] `exception/GlobalExceptionHandler.java`

### 7. Capa Lógica (Service)
#### [NEW] `service/WeaponService.java` (Interfaz)
#### [NEW] `service/impl/WeaponServiceImpl.java` (Implementación)

### 8. Capa REST (Controller)
#### [NEW] `controller/WeaponController.java`

---

## Verification Plan
1. **Verificación de Inserción Automática:** `data.sql` debe insertar 10 armas.
2. **Validación de Errores (404/400/409):** Peticiones simuladas para asegurar los códigos HTTP correctos.
3. **Registro Eureka:** Verificar dashboard en `http://localhost:8761`.
