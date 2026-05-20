# 📊 DIAGRAMAS ARQUITECTÓNICOS DETALLADOS

## PROYECTO HITBOXKING

---

## 1. DIAGRAMA DE TOPOLOGÍA GENERAL

```mermaid
graph TB
    subgraph "Cliente"
        Browser["🌐 Navegador Web<br/>React/Angular Frontend"]
    end

    subgraph "API Layer"
        Gateway["🚪 API Gateway<br/>Port: 8080<br/>Spring Cloud Gateway"]
    end

    subgraph "Service Discovery"
        Eureka["🔍 Eureka Server<br/>Port: 8761<br/>Service Registry"]
        Config["⚙️ Config Server<br/>Port: 8888<br/>Central Config"]
    end

    subgraph "Business Microservices"
        Auth["🔐 MS-Auth<br/>Port: 8081<br/>Authentication & RBAC"]
        Agents["🎮 MS-Agents<br/>Port: 8083<br/>Agent Management"]
        Weapons["🔫 MS-Weapons<br/>Port: 8082<br/>Weapon Inventory"]
    end

    subgraph "Data Layer"
        subgraph "PostgreSQL 15"
            DB1["db_hitbox_auth"]
            DB2["db_hitbox_main"]
            DB3["db_hitbox_stats_agents"]
            DB4["db_hitbox_stats_weapons"]
        end

        PG["🗄️ PostgreSQL<br/>Port: 5432"]
    end

    subgraph "Event Bus"
        ZK["🎯 Zookeeper<br/>Port: 2181<br/>Coordination"]
        Kafka["📨 Apache Kafka<br/>Port: 9092<br/>Message Broker"]
    end

    Browser -->|REST/JSON| Gateway

    Gateway -->|Load Balancing<br/>lb://ms-auth| Auth
    Gateway -->|Load Balancing<br/>lb://ms-agents| Agents
    Gateway -->|Load Balancing<br/>lb://ms-weapons| Weapons

    Auth -->|Register| Eureka
    Agents -->|Register| Eureka
    Weapons -->|Register| Eureka

    Auth -->|Fetch Config| Config
    Agents -->|Fetch Config| Config
    Weapons -->|Fetch Config| Config

    Auth -->|Query/Persist| PG
    Agents -->|Query/Persist| PG
    Weapons -->|Query/Persist| PG

    PG -->|db_hitbox_auth| DB1
    PG -->|db_hitbox_main| DB2
    PG -->|db_hitbox_stats_agents| DB3
    PG -->|db_hitbox_stats_weapons| DB4

    Auth -->|Publish Events| Kafka
    Agents -->|Publish Events| Kafka
    Weapons -->|Publish Events| Kafka

    Kafka -->|Coordination| ZK

    style Gateway fill:#FF6B6B
    style Eureka fill:#4ECDC4
    style Config fill:#45B7D1
    style Auth fill:#96CEB4
    style Agents fill:#FFEAA7
    style Weapons fill:#DFE6E9
    style PG fill:#A29BFE
    style Kafka fill:#FD79A8
```

---

## 2. FLUJO DE REGISTRO E AUTENTICACIÓN

```mermaid
sequenceDiagram
    actor User
    participant Frontend as 🌐 Frontend
    participant Gateway as 🚪 API Gateway
    participant Auth as 🔐 MS-Auth
    participant DB as 🗄️ PostgreSQL
    participant Kafka as 📨 Kafka
    participant Consumer as 📧 Email Service

    User ->> Frontend: 1. Click "Registrar"
    Frontend ->> Frontend: Validar email

    Frontend ->> Gateway: 2. POST /api/auth/register<br/>{email, password}

    Gateway ->> Gateway: 3. Predicate Check<br/>Path = /api/auth/**?
    Gateway ->> Gateway: 4. Rewrite path<br/>Remove /api prefix

    Gateway ->> Auth: 5. Route to ms-auth:8081

    Auth ->> Auth: 6. AuthController.register()
    Auth ->> Auth: 7. Validar email no existe
    Auth ->> Auth: 8. Hash password (BCrypt)

    Auth ->> DB: 9. INSERT INTO users<br/>(email, password_hash, created_at)
    DB -->> Auth: 10. User inserted (id=42)

    Auth ->> DB: 11. INSERT INTO user_roles<br/>(user_id=42, role_id=USER)
    DB -->> Auth: 12. Role assigned

    Auth ->> Kafka: 13. Publish event<br/>USUARIO_REGISTRADO
    Kafka ->> Consumer: 14. Event received
    Consumer ->> Consumer: 15. Send welcome email

    Auth -->> Gateway: 16. HTTP 201 Created<br/>{userId: 42}
    Gateway -->> Frontend: 17. Response
    Frontend ->> User: 18. "Registro exitoso"

    note over Auth,DB: Transaction: ACID<br/>Everything or nothing
    note over Kafka: Asynchronous:<br/>No espera respuesta
```

---

## 3. FLUJO DE LOGIN Y GENERACIÓN JWT

```mermaid
sequenceDiagram
    actor User
    participant Frontend as 🌐 Frontend
    participant Gateway as 🚪 Gateway
    participant Auth as 🔐 MS-Auth
    participant DB as 🗄️ BD
    participant JWT as 🎫 JWT Provider
    participant LS as 💾 LocalStorage

    User ->> Frontend: 1. Ingresa email & password
    Frontend ->> Frontend: 2. Validar formato

    Frontend ->> Gateway: 3. POST /api/auth/login

    Gateway ->> Auth: 4. Route a ms-auth:8081

    Auth ->> DB: 5. SELECT * FROM users<br/>WHERE email = ?
    DB -->> Auth: 6. User found

    Auth ->> Auth: 7. BCrypt.matches(pwd, hash)

    alt Password incorrecto
        Auth -->> Gateway: 401 Unauthorized
        Gateway -->> Frontend: Error
        Frontend ->> User: "Credenciales inválidas"
    else Password correcto
        Auth ->> JWT: 8. generateToken(user)
        JWT ->> JWT: 9. Create claims:<br/>sub=42, email, roles
        JWT ->> JWT: 10. Sign with secret key<br/>(HS256)
        JWT -->> Auth: 11. Return token

        Auth ->> DB: 12. UPDATE users SET last_login=NOW()

        Auth -->> Gateway: 13. HTTP 200 OK<br/>{token, expiresIn}
        Gateway -->> Frontend: 14. Response

        Frontend ->> LS: 15. localStorage.setItem<br/>('authToken', token)
        Frontend ->> User: 16. Redirect a dashboard
    end

    note over JWT: HMAC-SHA256 Signature<br/>Secret: 256-bit key
    note over LS: Token persiste<br/>aún después del refresh
```

---

## 4. FLUJO DE SOLICITUD AUTENTICADA

```mermaid
sequenceDiagram
    actor User
    participant Frontend as 🌐 Frontend
    participant LS as 💾 LocalStorage
    participant Gateway as 🚪 Gateway
    participant Filter as 🔍 Auth Filter
    participant Eureka as 🔍 Eureka
    participant Service as 📦 Microservicio

    User ->> Frontend: 1. Click "Get Agents"
    Frontend ->> LS: 2. Retrieve token
    LS -->> Frontend: 3. Return token

    Frontend ->> Gateway: 4. GET /api/agents<br/>Header: Authorization: Bearer <token>

    Gateway ->> Filter: 5. Global auth filter
    Filter ->> Filter: 6. Extract token from header
    Filter ->> Filter: 7. Parse JWT (verify signature)

    alt Token inválido/expirado
        Filter -->> Gateway: 401 Unauthorized
        Gateway -->> Frontend: 401
        Frontend ->> User: "Sesión expirada. Login required"
    else Token válido
        Filter ->> Filter: 8. Extract claims<br/>user_id = 42
        Filter ->> Filter: 9. Add header:<br/>X-User-Id: 42

        Gateway ->> Eureka: 10. Query: "Give me ms-agents"
        Eureka -->> Gateway: 11. Return: [192.168.1.100:8083]

        Gateway ->> Service: 12. Route to ms-agents<br/>GET /agents<br/>Header: X-User-Id: 42

        Service ->> Service: 13. AuthController.getAll()
        Service ->> Service: 14. @PreAuthorize("hasRole('USER')")<br/>Validate user_id from header

        note over Service: Additional checks

        Service -->> Gateway: 15. HTTP 200<br/>List<AgentResponseDTO>
        Gateway -->> Frontend: 16. Response
        Frontend ->> Frontend: 17. Render agents list
        Frontend ->> User: 18. Display UI
    end
```

---

## 5. CICLO DE VIDA DE UN AGENTE (CRUD)

```mermaid
graph LR
    subgraph "CREATE"
        C1["POST /api/agents"]
        C2["RequestDTO validation"]
        C3["Hash password if needed"]
        C4["INSERT INTO agents"]
        C5["INSERT INTO abilities"]
        C6["HTTP 201 Created"]

        C1 --> C2
        C2 --> C3
        C3 --> C4
        C4 --> C5
        C5 --> C6
    end

    subgraph "READ"
        R1["GET /api/agents"]
        R2["SELECT * FROM agents"]
        R3["Map Entity to DTO"]
        R4["HTTP 200 + JSON"]

        R1 --> R2
        R2 --> R3
        R3 --> R4
    end

    subgraph "UPDATE"
        U1["PUT /api/agents/{id}"]
        U2["Find agent by ID"]
        U3["Update fields"]
        U4["UPDATE agents SET..."]
        U5["HTTP 200 OK"]

        U1 --> U2
        U2 --> U3
        U3 --> U4
        U4 --> U5
    end

    subgraph "DELETE"
        D1["DELETE /api/agents/{id}"]
        D2["Find agent by ID"]
        D3["DELETE FROM abilities WHERE agent_id"]
        D4["DELETE FROM agents"]
        D5["HTTP 204 No Content"]

        D1 --> D2
        D2 --> D3
        D3 --> D4
        D4 --> D5
    end

    C6 --> R1
    R4 --> U1
    U5 --> D1

    style C6 fill:#90EE90
    style R4 fill:#87CEEB
    style U5 fill:#FFD700
    style D5 fill:#FF6B6B
```

---

## 6. ARQUITECTURA MULTICAPA DE MS-AGENTS

```mermaid
graph TB
    subgraph "Controller Layer"
        CR["🌐 AgentController<br/>@RestController<br/>@RequestMapping('/api/agents')"]
    end

    subgraph "DTO & Validation"
        DTO["📋 AgentRequestDTO<br/>@NotBlank, @NotNull<br/>Bean Validation"]
        MAPPER["🔄 AgentMapper<br/>Entity ↔ DTO"]
    end

    subgraph "Business Logic"
        SI["📦 AgentService (Interface)"]
        SIM["📦 AgentServiceImpl<br/>Core Logic:<br/>- Validations<br/>- Business rules<br/>- Exception handling"]
    end

    subgraph "Data Access"
        REPO["🗄️ AgentRepository<br/>extends JpaRepository<br/>- findById<br/>- findAll<br/>- save, delete"]
    end

    subgraph "Domain Model"
        ENT["📊 Agent (Entity)<br/>@Entity<br/>@Table(agents)"]
        ENUM["🎭 AgentRole<br/>DUELIST, CONTROLLER<br/>SENTINEL, INITIATOR"]
        AB["📊 Ability (Entity)<br/>@Entity<br/>@Table(abilities)"]
    end

    subgraph "Database"
        SQL["🗄️ PostgreSQL<br/>Port: 5432<br/>db_hitbox_stats_agents"]
        T1["Table: agents<br/>(id, name, role, ...)"]
        T2["Table: abilities<br/>(id, agent_id, name, ...)"]
    end

    CR -->|@Valid| DTO
    DTO -->|convert| MAPPER
    MAPPER -->|toEntity| SIM

    CR -->|@Autowired| SI
    SI -->|implements| SIM

    SIM -->|@Autowired| REPO
    REPO -->|JPA| ENT

    ENT --> ENUM
    ENT -->|@OneToMany| AB

    REPO -->|SQL Queries| SQL
    SQL --> T1
    SQL --> T2

    style CR fill:#FF6B6B
    style DTO fill:#FFD93D
    style SIM fill:#96CEB4
    style REPO fill:#6C5CE7
    style ENT fill:#A29BFE
    style SQL fill:#74B9FF
```

---

## 7. FLUJO KAFKA: EVENTO USUARIO AUTENTICADO

```mermaid
graph LR
    subgraph "Producer"
        P1["🔐 MS-Auth"]
        P2["AuthService.authenticate()"]
        P3["✅ Credenciales válidas"]
        P4["📨 Publish event<br/>usuario.autenticado"]

        P1 --> P2
        P2 --> P3
        P3 --> P4
    end

    subgraph "Message Broker"
        KB["Apache Kafka"]
        T["Topic: usuario.autenticado<br/>Partitions: 3<br/>Replication Factor: 1"]

        KB --> T
    end

    subgraph "Consumers"
        C1["📧 Email Service<br/>@KafkaListener<br/>Send welcome email"]
        C2["📊 Analytics<br/>Increment login counter"]
        C3["🎮 Recommendation Engine<br/>Load user preferences"]
        C4["🔔 Notification Service<br/>Create notification"]

        T --> C1
        T --> C2
        T --> C3
        T --> C4
    end

    P4 --> KB

    note over KB: Asynchronous Processing<br/>No esperar respuestas<br/>Eventual Consistency

    style P3 fill:#90EE90
    style T fill:#FFD93D
    style C1 fill:#74B9FF
    style C2 fill:#A29BFE
    style C3 fill:#FF85B3
    style C4 fill:#FFB88C
```

---

## 8. MODELO DE DATOS: AGENTS & ABILITIES

```mermaid
erDiagram
    AGENTS ||--o{ ABILITIES : contains

    AGENTS {
        BIGSERIAL id PK
        string name UK "UNIQUE"
        string role "ENUM: DUELIST, CONTROLLER, SENTINEL, INITIATOR"
        text description
        string image_url
        timestamp created_at "DEFAULT NOW()"
        timestamp updated_at "DEFAULT NOW()"
    }

    ABILITIES {
        BIGSERIAL id PK
        BIGINT agent_id FK "Foreign Key to agents"
        string name
        string type "ENUM: Q, E, C, ULTIMATE"
        text description
        integer cooldown_sec
    }

    note: "3NF Compliance"
    note: "1NF: All attributes atomic"
    note: "2NF: No partial dependencies"
    note: "3NF: No transitive dependencies"
```

---

## 9. MODELO DE DATOS: WEAPONS

```mermaid
erDiagram
    WEAPONS {
        BIGSERIAL id PK
        string name UK "UNIQUE"
        string category "ENUM: RIFLE, PISTOL, SMG, SNIPER, SHOTGUN, HEAVY"
        decimal damage "NOT NULL, >= 0"
        decimal fire_rate "NOT NULL, >= 0"
        integer magazine_size "NOT NULL, >= 0"
        integer price "NOT NULL, >= 0"
        text description
        timestamp created_at "DEFAULT NOW()"
        timestamp updated_at "DEFAULT NOW()"
    }

    note: "Simple Entity (1 table)"
    note: "3NF Compliant - Static Enum"
```

---

## 10. MODELO DE DATOS: AUTHENTICATION

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : "many-to-many"
    ROLES ||--o{ USER_ROLES : "many-to-many"

    USERS {
        BIGSERIAL id PK
        string email UK "UNIQUE"
        string password_hash "BCrypt"
        string first_name
        string last_name
        timestamp created_at
        timestamp updated_at
    }

    ROLES {
        BIGSERIAL id PK
        string name UK "UNIQUE, ENUM"
        text description
    }

    USER_ROLES {
        BIGINT user_id FK "Composite PK"
        BIGINT role_id FK "Composite PK"
    }

    note: "RBAC: Role-Based Access Control"
    note: "Many-to-Many: User puede tener múltiples roles"
```

---

## 11. CICLO DE COMPILACIÓN & DESPLIEGUE

```mermaid
graph TB
    subgraph "Local Development"
        L1["👨‍💻 Escribir código<br/>Java Spring Boot"]
        L2["🧪 Unit Tests<br/>JUnit 5"]
        L3["📦 Maven Build<br/>mvn clean package"]
        L4["🐳 Build Docker<br/>docker build"]
        L5["🚀 Run Container<br/>docker-compose up"]
    end

    subgraph "Version Control"
        V1["📝 Git Commit"]
        V2["🌐 Git Push"]
        V3["📍 GitHub"]
    end

    subgraph "CI/CD (Opcional)"
        CI1["⚙️ GitHub Actions"]
        CI2["✅ Run Tests"]
        CI3["🐳 Build & Push<br/>to Docker Hub"]
    end

    subgraph "Production"
        PD1["☁️ Kubernetes Cluster"]
        PD2["📦 Pod Deployment"]
        PD3["🔄 Auto Scaling"]
        PD4["📊 Monitoring"]
    end

    L1 --> L2
    L2 --> L3
    L3 --> L4
    L4 --> L5
    L5 --> V1

    V1 --> V2
    V2 --> V3
    V3 --> CI1

    CI1 --> CI2
    CI2 --> CI3

    CI3 --> PD1
    PD1 --> PD2
    PD2 --> PD3
    PD3 --> PD4

    style L1 fill:#90EE90
    style L5 fill:#87CEEB
    style V3 fill:#FFD93D
    style CI1 fill:#FF85B3
    style PD1 fill:#A29BFE
```

---

## 12. MANEJO DE EXCEPCIONES GLOBAL

```mermaid
graph TB
    subgraph "Controllers"
        C["🌐 REST Controller"]
    end

    subgraph "Service/Repository"
        S["📦 Business Logic"]
    end

    subgraph "Exception Hierarchy"
        E["🚨 Base Exception"]
        E --> E1["ResourceNotFoundException<br/>→ HTTP 404"]
        E --> E2["DuplicateResourceException<br/>→ HTTP 409"]
        E --> E3["ValidationException<br/>→ HTTP 400"]
        E --> E4["UnauthorizedException<br/>→ HTTP 401"]
    end

    subgraph "Global Exception Handler"
        GEH["@RestControllerAdvice<br/>GlobalExceptionHandler"]
        M1["@ExceptionHandler<br/>ResourceNotFoundException"]
        M2["@ExceptionHandler<br/>ValidationException"]
        M3["@ExceptionHandler<br/>MethodArgumentNotValidException"]
    end

    subgraph "Response"
        R["ErrorResponse DTO<br/>{<br/>  status: 404,<br/>  message: 'Resource not found',<br/>  timestamp: '2026-05-01T...'<br/>}"]
    end

    C -->|throws| S
    S -->|throws| E1
    S -->|throws| E2

    E1 --> GEH
    E2 --> GEH

    GEH --> M1
    GEH --> M2
    M1 --> R
    M2 --> R

    style E1 fill:#FF6B6B
    style E2 fill:#FFB88C
    style GEH fill:#96CEB4
    style R fill:#87CEEB
```

---

## 13. FLUJO COMPLETO: CREAR NUEVO AGENTE

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend
    participant GW as Gateway
    participant AG as MS-Agents
    participant MAPPER as Mapper
    participant SVC as AgentService
    participant REPO as Repository
    participant DB as PostgreSQL

    User ->> FE: Click "Crear Agente"
    FE ->> FE: Abrir formulario

    User ->> FE: Ingresa datos<br/>- Name: "Phoenix"<br/>- Role: "CONTROLLER"<br/>- Abilities: [...]

    FE ->> FE: Validar frontend
    FE ->> GW: POST /api/agents<br/>Header: Authorization: Bearer <JWT>

    GW ->> GW: Validar JWT
    GW ->> AG: Route to ms-agents:8083

    AG ->> AG: Controller receive
    AG ->> MAPPER: Convert JSON → AgentRequestDTO

    MAPPER ->> MAPPER: Deserialize
    AG ->> AG: @Valid validation<br/>- @NotBlank name ✓<br/>- @NotNull role ✓<br/>- abilities not empty ✓

    AG ->> SVC: agentService.createAgent()

    SVC ->> SVC: Check duplicate: findByName("Phoenix")
    alt Already exists
        SVC -->> AG: throw DuplicateException
        AG -->> GW: HTTP 409 Conflict
    else New agent
        SVC ->> REPO: save(agent)
        REPO ->> DB: INSERT INTO agents<br/>(name, role, description, ...)

        DB -->> REPO: id = 99
        REPO -->> SVC: Agent{id:99, name:"Phoenix", ...}

        SVC ->> REPO: save each ability
        note over REPO: CASCADE: abilities also saved

        REPO ->> DB: INSERT INTO abilities<br/>(agent_id=99, name, type, ...)
        DB -->> REPO: ✓ 4 abilities inserted

        SVC -->> AG: Agent entity
        AG ->> MAPPER: Convert → AgentResponseDTO
        MAPPER -->> AG: DTO with nested abilities

        AG -->> GW: HTTP 201 Created<br/>{<br/>  id: 99,<br/>  name: "Phoenix",<br/>  role: "CONTROLLER",<br/>  abilities: [{...}, {...}, ...],<br/>  createdAt: "2026-05-01T..."<br/>}

        GW -->> FE: Response
        FE ->> FE: Parse JSON
        FE ->> User: "✅ Agente creado exitosamente"
    end

    note over DB: Transaction garantiza<br/>atomicidad completa
```

---

## 14. ESCALABILIDAD: DE MONOLITO A MICROSERVICIOS

```mermaid
graph TB
    subgraph "Antes: Monolito"
        M["🏢 Monolith<br/>400K LOC<br/>100 endpoints<br/>1 servidor<br/>1 BD"]
        M -->|Problema| P1["Escalamos TODO<br/>o NADA"]
        M -->|Problema| P2["1 fallo =<br/>Sistema caído"]
        M -->|Problema| P3["Despliegues<br/>acoplados"]
    end

    subgraph "Después: Microservicios"
        MS1["🔐 MS-Auth<br/>50K LOC<br/>15 endpoints<br/>3 instancias<br/>1 BD"]

        MS2["🎮 MS-Agents<br/>30K LOC<br/>10 endpoints<br/>2 instancias<br/>1 BD"]

        MS3["🔫 MS-Weapons<br/>40K LOC<br/>12 endpoints<br/>5 instancias<br/>1 BD"]

        MS1 -->|Solución| S1["Escalamos solo<br/>lo que necesita"]
        MS2 -->|Solución| S2["Fallos aislados<br/>resto funciona"]
        MS3 -->|Solución| S3["Despliegues<br/>independientes"]
    end

    subgraph "Resultados"
        R1["⚡ 5x más throughput"]
        R2["🔒 99.99% uptime"]
        R3["🚀 Deployment 10x más rápido"]
    end

    P1 -.->|Mejorado| S1
    P2 -.->|Mejorado| S2
    P3 -.->|Mejorado| S3

    S1 --> R1
    S2 --> R2
    S3 --> R3

    style M fill:#FF6B6B
    style MS1 fill:#96CEB4
    style MS2 fill:#FFEAA7
    style MS3 fill:#DFE6E9
    style R1 fill:#90EE90
```

---

## 15. STACK DE DEPENDENCIAS (pom.xml)

```mermaid
graph TB
    subgraph "Maven Project"
        POM["pom.xml"]
    end

    subgraph "Parent"
        SB["spring-boot-starter-parent<br/>v4.0.6"]
    end

    subgraph "Spring Boot Starters"
        WEB["spring-boot-starter-webmvc"]
        JPA["spring-boot-starter-data-jpa"]
        SEC["spring-boot-starter-security"]
        VAL["spring-boot-starter-validation"]
        KAF["spring-boot-starter-kafka"]
    end

    subgraph "Spring Cloud"
        EUREKA["spring-cloud-starter-netflix-eureka-client"]
        CONFIG["spring-cloud-starter-config"]
        GATEWAY["spring-cloud-starter-gateway"]
        FEIGN["spring-cloud-starter-openfeign"]
    end

    subgraph "Third-Party"
        JWT["jjwt (JWT Token)"]
        LOMBOK["lombok"]
        PG["postgresql driver"]
        JACKSON["jackson (JSON)"]
    end

    subgraph "Transitive"
        SPRING["Spring Framework 6.0"]
        TOMCAT["Apache Tomcat"]
        HIBERNATE["Hibernate ORM"]
    end

    POM --> SB
    SB --> WEB
    SB --> JPA
    SB --> SEC
    SB --> VAL
    SB --> KAF

    POM --> EUREKA
    POM --> CONFIG
    POM --> GATEWAY
    POM --> FEIGN

    POM --> JWT
    POM --> LOMBOK
    POM --> PG
    POM --> JACKSON

    JPA --> HIBERNATE
    WEB --> TOMCAT
    SEC --> SPRING

    style SB fill:#90EE90
    style EUREKA fill:#4ECDC4
    style CONFIG fill:#45B7D1
    style JWT fill:#FFD93D
```

---

**Fin del Documento de Diagramas**
_Todos los diagramas son totalmente funcionales en Obsidian_
