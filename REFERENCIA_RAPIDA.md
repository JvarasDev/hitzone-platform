# ⚡ REFERENCIA RÁPIDA - HITBOXKING

## Cheat Sheet para Desarrolladores

---

## 🚀 START AQUÍ (5 minutos)

### Comandos Esenciales

```bash
# PASO 1: Infraestructura
cd fullstack_project
cp .env.example .env
docker-compose up -d

# PASO 2: Config Server (Terminal 1)
cd config-server && mvn spring-boot:run

# PASO 3: Eureka Server (Terminal 2)
cd eureka-server && mvn spring-boot:run

# PASO 4: Microservicios (Terminales 3-5)
cd fullstack && mvn spring-boot:run
cd ms-agents && mvn spring-boot:run
cd ms-weapons && mvn spring-boot:run

# PASO 5: API Gateway (Terminal 6)
cd api-gateway && mvn spring-boot:run

# VERIFICAR TODO ESTÁ UP
curl http://localhost:8080/actuator/health
```

### URLs Importantes

| Servicio         | URL                   |
| ---------------- | --------------------- |
| API Gateway      | http://localhost:8080 |
| MS-Auth          | http://localhost:8081 |
| MS-Weapons       | http://localhost:8082 |
| MS-Agents        | http://localhost:8083 |
| Config Server    | http://localhost:8888 |
| Eureka Dashboard | http://localhost:8761 |
| PostgreSQL       | localhost:5432        |

---

## 🔐 AUTENTICACIÓN RÁPIDA

### Registrarse

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!",
    "firstName": "Juan",
    "lastName": "Pérez"
  }'
```

### Login y obtener Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!"
  }' | jq '.token'

# Guardar token
export TOKEN="eyJhbGciOi..."
```

### Usar Token en Requests

```bash
# Todas las peticiones necesitan el token
curl http://localhost:8080/api/agents \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎮 ENDPOINTS RÁPIDOS

### Agentes

```bash
# Listar
curl http://localhost:8080/api/agents \
  -H "Authorization: Bearer $TOKEN" | jq

# Uno específico
curl http://localhost:8080/api/agents/1 \
  -H "Authorization: Bearer $TOKEN" | jq

# Habilidades
curl http://localhost:8080/api/agents/1/abilities \
  -H "Authorization: Bearer $TOKEN" | jq

# Crear
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Phoenix",
    "role": "CONTROLLER",
    "abilities": [...]
  }' | jq
```

### Armas

```bash
# Listar todas
curl http://localhost:8080/api/weapons \
  -H "Authorization: Bearer $TOKEN" | jq

# Por categoría
curl "http://localhost:8080/api/weapons?category=RIFLE" \
  -H "Authorization: Bearer $TOKEN" | jq

# Rango de precio
curl "http://localhost:8080/api/weapons/price-range?minPrice=1000&maxPrice=3000" \
  -H "Authorization: Bearer $TOKEN" | jq

# Crear
curl -X POST http://localhost:8080/api/weapons \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Vandal",
    "category": "RIFLE",
    "damage": 39,
    "fireRate": 9.75,
    "magazineSize": 30,
    "price": 2700
  }' | jq
```

---

## 📊 ESTRUCTURA DE MICROSERVICIO

### Plantilla para nuevo servicio

```
ms-nuevo/
├── src/main/java/cl/hitzone/ms_nuevo/
│   ├── MsNuevoApplication.java
│   ├── controller/NuevoController.java
│   ├── service/NuevoService.java
│   ├── repository/NuevoRepository.java
│   ├── model/Nuevo.java
│   ├── dto/NuevoRequestDTO.java
│   ├── dto/NuevoResponseDTO.java
│   ├── mapper/NuevoMapper.java
│   └── exception/GlobalExceptionHandler.java
├── src/main/resources/
│   └── application.yml (IGNORADO - viene de Config Server)
├── pom.xml
└── src/test/java/...

# En Config Server agregar:
config/ms-nuevo.yml
```

---

## 🔧 CONFIGURACIÓN ESENCIAL

### pom.xml (Dependencias mínimas)

```xml
<parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.6</version>
</parent>

<properties>
    <java.version>17</java.version>
    <spring-cloud.version>2025.1.1</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### application.yml (Lo básico)

```yaml
spring:
  application:
    name: ms-nuevo

  datasource:
    url: jdbc:postgresql://localhost:5432/db_hitbox_nuevo
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: create-drop
    database: POSTGRESQL

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

server:
  port: 8084
```

---

## 💾 ENTIDADES RÁPIDAS

### Entity Básica

```java
@Entity
@Table(name = "recursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### DTO Básico

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecursoResponseDTO {
    private Long id;
    private String nombre;
    private LocalDateTime createdAt;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecursoRequestDTO {
    @NotBlank
    private String nombre;
}
```

### Controller Básico

```java
@RestController
@RequestMapping("/api/recursos")
public class RecursoController {
    @Autowired
    private RecursoService service;

    @GetMapping
    public ResponseEntity<List<RecursoResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<RecursoResponseDTO> create(
        @Valid @RequestBody RecursoRequestDTO request) {
        return ResponseEntity.status(201).body(service.create(request));
    }
}
```

---

## 🐛 DEBUGGING RÁPIDO

### Ver qué está pasando

```bash
# Logs en tiempo real
mvn spring-boot:run | grep -i error

# Ver si el servicio está registrado en Eureka
curl http://localhost:8761/eureka/apps/ms-agents | jq

# Verificar base de datos
psql -h localhost -U hitbox_admin -d db_hitbox_stats_agents -c "SELECT * FROM agents;"

# Ver eventos de Kafka
docker exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic usuario.autenticado \
  --from-beginning

# Verificar configuración cargada
curl http://localhost:8888/ms-agents/default | jq
```

### Problemas comunes

| Problema              | Comando                                  |
| --------------------- | ---------------------------------------- |
| Puerto en uso         | `lsof -i :8080`                          |
| Postgres no levanta   | `docker logs postgres`                   |
| No registra en Eureka | `curl http://localhost:8761/eureka/apps` |
| JWT inválido          | Hacer nuevo login                        |
| Kafka offline         | `docker restart kafka zookeeper`         |

---

## 📝 VALIDACIONES (Bean Validation)

### Anotaciones Frecuentes

```java
@NotBlank              // No nulo, no vacío
@NotNull               // No nulo
@Email                 // Formato email
@Min(value)            // Mínimo
@Max(value)            // Máximo
@DecimalMin/Max        // Para decimales
@Length(min, max)      // Longitud
@Pattern(regex)        // Regex
@Future/@Past          // Fechas futuras/pasadas
@Positive/@Negative    // Números
@Size(min, max)        // Tamaño colecciones
@Valid                 // Validar objeto anidado
```

### Ejemplo Completo

```java
@Data
public class AgentRequestDTO {
    @NotBlank(message = "El nombre es requerido")
    @Length(min = 2, max = 100, message = "Debe estar entre 2 y 100 caracteres")
    private String name;

    @NotNull(message = "El rol es requerido")
    private AgentRole role;

    @Size(min = 1, max = 4, message = "Debe tener entre 1 y 4 habilidades")
    @Valid  // Validar cada elemento de la lista
    private List<AbilityRequestDTO> abilities;
}
```

---

## 🔄 EXCEPCIONES GLOBALES

### Pattern recomendado

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, message, LocalDateTime.now()));
    }
}

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
```

---

## 📤 PUBLICAR EVENTOS KAFKA

### Pattern básico

```java
@Service
public class EventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAgentCreated(Agent agent) {
        AgentCreatedEvent event = AgentCreatedEvent.builder()
            .agentId(agent.getId())
            .agentName(agent.getName())
            .timestamp(LocalDateTime.now())
            .build();

        kafkaTemplate.send("agent.created", String.valueOf(agent.getId()), event);
    }
}

@Component
public class EventListener {
    @KafkaListener(topics = "agent.created", groupId = "stats-group")
    public void onAgentCreated(AgentCreatedEvent event) {
        // Procesar evento
        System.out.println("Agente creado: " + event.getAgentName());
    }
}
```

---

## 🔐 JWT RÁPIDO

### Generar token

```java
@Component
public class JwtProvider {
    @Value("${app.jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        return Jwts.builder()
            .subject(String.valueOf(user.getId()))
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    public String extractUserId(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
```

---

## 🧪 TESTING RÁPIDO

### Unit Test

```java
@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private AgentRepository repository;

    @InjectMocks
    private AgentService service;

    @Test
    void shouldReturnAgentWhenIdExists() {
        // Arrange
        Agent expected = Agent.builder().id(1L).name("Phoenix").build();
        when(repository.findById(1L)).thenReturn(Optional.of(expected));

        // Act
        Optional<Agent> result = service.getById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Phoenix", result.get().getName());
    }
}
```

### Integration Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAgentsWhenCallingGetAll() throws Exception {
        mockMvc.perform(get("/api/agents")
                .header("Authorization", "Bearer " + JWT_TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
}
```

---

## 📦 GIT WORKFLOW

### Feature Branch

```bash
# Crear rama
git checkout -b feature/nueva-funcionalidad

# Trabajar
git add src/
git commit -m "feat(ms-agents): agregar filtro por rol"

# Push
git push origin feature/nueva-funcionalidad

# En GitHub: crear Pull Request
# Esperar review → Merge
```

### Commit Conventions

```
feat(ms-name):      Nueva funcionalidad
fix(ms-name):       Bug fix
docs(ms-name):      Documentación
test(ms-name):      Tests
refactor(ms-name):  Refactorización
perf(ms-name):      Performance
ci(ms-name):        CI/CD
```

---

## 💡 PATRONES RECOMENDADOS

### Repository Pattern

```java
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByName(String name);
    List<Agent> findByRole(AgentRole role);
}
```

### Service Pattern

```java
public interface AgentService {
    Agent create(Agent agent);
    Optional<Agent> getById(Long id);
    List<Agent> getAll();
}

@Service
public class AgentServiceImpl implements AgentService {
    // Implementación
}
```

### Mapper Pattern

```java
@Component
public class AgentMapper {
    public AgentResponseDTO toDTO(Agent entity) { ... }
    public Agent toEntity(AgentRequestDTO dto) { ... }
}
```

---

## ⚡ PERFORMANCE TIPS

### Caché

```java
@Cacheable("agents")
public List<Agent> getAll() {
    return repository.findAll();
}

@CacheEvict(value = "agents", allEntries = true)
public Agent create(Agent agent) {
    return repository.save(agent);
}
```

### N+1 Query Prevention

```java
// ❌ MAL
agents.forEach(a -> abilities = getAbilities(a.getId()));

// ✅ BIEN
@Query("SELECT a FROM Agent a LEFT JOIN FETCH a.abilities")
List<Agent> findAllWithAbilities();
```

### Pagination

```java
@GetMapping
public ResponseEntity<Page<AgentResponseDTO>> getAll(
    @RequestParam int page,
    @RequestParam int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
    Page<Agent> agents = service.getAll(pageable);

    return ResponseEntity.ok(agents.map(agentMapper::toDTO));
}
```

---

## 🚨 CHECKLIST ANTES DE COMMIT

- [ ] Código compila (`mvn clean compile`)
- [ ] Tests pasan (`mvn test`)
- [ ] No hay warnings
- [ ] Código está formateado (IntelliJ: Ctrl+Alt+L)
- [ ] Logs removidos
- [ ] Sin credenciales hardcodeadas
- [ ] Commit message descriptivo
- [ ] Branch actualizada con main

---

## 📚 REFERENCIAS

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Cloud Docs](https://spring.io/projects/spring-cloud)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Apache Kafka](https://kafka.apache.org/)
- [Docker Docs](https://docs.docker.com/)

---

**Guarda este documento. Consulta cuando necesites respuestas rápidas.**
