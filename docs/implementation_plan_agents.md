# 🚀 Developer Blueprint: Microservicio MS-Agents

> [!NOTE]
> Este manual está diseñado para el desarrollador humano encargado de codificar `ms-agents`. Contiene la arquitectura relacional (3NF), el orden lógico de creación de paquetes para evitar errores de compilación, y los requisitos técnicos de Jira.

---

## 1. 📊 Modelado de Datos (Tercera Forma Normal - 3NF)

El sistema de Agentes y Habilidades requiere una relación **One-to-Many (1:N)**. Para garantizar la Tercera Forma Normal (3NF), el diseño separa conceptualmente la información en dos tablas.

**¿Por qué este diseño cumple la 3NF?**
1. **1NF:** Todos los atributos son atómicos. No guardamos la lista de 4 habilidades como un texto gigante separado por comas dentro del Agente.
2. **2NF:** En la tabla `abilities`, toda la información (nombre, tipo, descripción) depende de su propia clave primaria (`id`), no parcialmente de otra cosa.
3. **3NF:** No hay dependencias transitivas. El nombre de la habilidad depende de su `id`. Para conectar la habilidad a su Agente dueño, usamos una Clave Foránea (`agent_id`), lo cual es el estándar relacional puro.

### Tabla `agents` (Lado "One")
| Columna | Tipo PostgreSQL | Dependencia / Regla |
| :--- | :--- | :--- |
| `id` | `BIGSERIAL` (PK) | Clave Primaria |
| `name` | `VARCHAR(100)` | Depende de `id` (Debe ser Único) |
| `role` | `VARCHAR(50)` | Enum estricto (DUELIST, CONTROLLER, SENTINEL, INITIATOR) |
| `description`| `TEXT` | Depende de `id` |
| `image_url` | `VARCHAR(255)` | Depende de `id` |

### Tabla `abilities` (Lado "Many")
| Columna | Tipo PostgreSQL | Dependencia / Regla |
| :--- | :--- | :--- |
| `id` | `BIGSERIAL` (PK) | Clave Primaria |
| `agent_id` | `BIGINT` (FK) | **Clave Foránea** -> Relación con `agents(id)` |
| `name` | `VARCHAR(100)` | Depende de `id` |
| `type` | `VARCHAR(20)` | Enum estricto (Q, E, C, ULTIMATE) |
| `description`| `TEXT` | Depende de `id` |

---

## 2. ⚙️ Setup Inicial en Spring Initializr

1. Ve a `start.spring.io`.
2. **Metadata:** Maven, Java 17, Spring Boot, Group: `cl.hitzone`, Artifact: `ms-agents`, Package: `cl.hitzone.ms_agents`.
3. **Dependencias OBLIGATORIAS:** `Spring Web`, `Spring Data JPA`, `PostgreSQL Driver`, `Eureka Discovery Client`, `Config Client`, `Validation`, `Lombok`.
4. Descarga, extrae junto a los demás microservicios y abre en IntelliJ.

---

## 3. 📂 Orden Lógico de Desarrollo (Paso a Paso)

El desarrollador **DEBE** seguir este orden exacto de creación de carpetas y archivos para que todo ensamble perfectamente:

### Paso 1: Configuración Global (`src/main/resources/`)
- Borra `application.properties` y crea `application.yml`.
- Configura: puerto `8083`, nombre `ms-agents`, BD local PostgreSQL a `db_hitbox_stats_agents` (creada en tu Docker) y la zona de Eureka `http://localhost:8761/eureka/`.
- **Importante:** Como usarás `Config Client`, recuerda agregar la propiedad `spring.config.import: optional:configserver:http://localhost:8888` en tu `.yml` para conectarte a tu servidor centralizado.

### Paso 2: Dominio y Enums (Paquete: `model`)
1. Crea Enum `AgentRole.java`.
2. Crea Enum `AbilityType.java`.
3. Crea Entity `Agent.java`. 
   - La magia de JPA aquí es: `@OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private List<Ability> abilities;`
4. Crea Entity `Ability.java`.
   - La conexión obligatoria: `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "agent_id") private Agent agent;`

### Paso 3: Data Transfer Objects (Paquete: `dto`)
*Misión Crítica:* Si devuelves la entidad `Agent` directamente al Frontend, causarás un bucle infinito de JSON (Agente trae Habilidades -> Habilidad llama al Agente -> Infinito).
1. Crea `AbilityDTO.java` (Solo id, name, type, description).
2. Crea `AgentResponseDTO.java` (Debe tener los datos del agente + una `List<AbilityDTO> abilities` anidada).
3. Crea `AgentRequestDTO.java` (Aplica validaciones como `@NotBlank` y `@NotNull` para los POST).

### Paso 4: Capa de Acceso a Datos (Paquete: `repository`)
1. Crea interfaz `AgentRepository` extendiendo `JpaRepository`.
2. Crea interfaz `AbilityRepository` extendiendo `JpaRepository`.

### Paso 5: Excepciones Globales (Paquete: `exception`)
1. Crea `ResourceNotFoundException.java`.
2. Crea `GlobalExceptionHandler.java` anotado con `@RestControllerAdvice`.
3. Mapea la excepción NotFound a un retorno HTTP 404 (Requisito de Criterio de Aceptación).

### Paso 6: Lógica de Negocio (Paquete: `service`)
1. Crea la interfaz `AgentService`.
2. En la subcarpeta `impl/`, crea la clase `AgentServiceImpl.java`.
   - Aquí transformarás manualmente los Entities a DTOs.
   - Si `findById` no encuentra el ID, debes disparar un `throw new ResourceNotFoundException()`.

### Paso 7: Controladores REST (Paquete: `controller`)
1. Crea `AgentController.java` anotado con `@RestController` en la ruta `/api/agents`.
2. Define los mapeos clásicos: `GET`, `POST`, `PUT`, `DELETE`.
3. Crea el endpoint específico de Jira: `@GetMapping("/{id}/abilities")` que devuelva solo el Array de habilidades.

### Paso 8: Siembra de Datos Automática (`data.sql`)
En `src/main/resources/`, crea un archivo `data.sql`. Las tablas se relacionan en SQL mediante `agent_id`.

```sql
-- Ejemplo (Debes insertar 8 agentes reales y 32 habilidades en total):
INSERT INTO agents (id, name, role, description, image_url) VALUES (1, 'Jett', 'DUELIST', 'Agente rápida de Corea.', 'url');
INSERT INTO abilities (name, type, description, agent_id) VALUES ('Updraft', 'Q', 'Impulso vertical', 1);
INSERT INTO abilities (name, type, description, agent_id) VALUES ('Tailwind', 'E', 'Deslizamiento', 1);
INSERT INTO abilities (name, type, description, agent_id) VALUES ('Cloudburst', 'C', 'Humo', 1);
INSERT INTO abilities (name, type, description, agent_id) VALUES ('Blade Storm', 'ULTIMATE', 'Cuchillos voladores', 1);
```
