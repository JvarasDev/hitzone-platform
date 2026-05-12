# 🏛️ Documentación Oficial de Arquitectura: Proyecto Hitboxking

> [!NOTE]
> Este documento técnico es la guía definitiva para entender, desplegar y escalar la arquitectura orientada a microservicios del proyecto **Hitboxking**. Está diseñado para que cualquier nuevo desarrollador, estudiante o profesor pueda entender exactamente el propósito de cada pieza y cómo se interconectan.

---

## 1. 🏗️ Visión General de la Arquitectura

Hitboxking ha dejado atrás el modelo tradicional de "Monolito" (donde todo el código vive en un solo proyecto gigante) para adoptar un modelo avanzado de **Microservicios distribuidos**. 

**¿Por qué tomamos esta decisión técnica?**
- **Escalabilidad quirúrgica:** Si la sección de "armas" recibe muchísimo tráfico, podemos clonar y destinar más memoria solo a ese microservicio sin afectar al sistema de "login".
- **Aislamiento a fallos:** Si un microservicio (por ejemplo, el de estadísticas) sufre un error crítico y se cae, la plataforma principal de autenticación y los demás servicios siguen funcionando intactos.
- **Acoplamiento nulo:** Cada equipo de desarrollo puede trabajar en su propio microservicio sin generar conflictos en el código de los demás.

---

## 2. 🐳 Capa de Infraestructura Base (Docker)

En lugar de obligar a cada desarrollador a instalar bases de datos y herramientas de mensajería manualmente en su computadora, automatizamos todo el proceso utilizando contenedores de Docker orquestados por el archivo `docker-compose.yml`.

### 2.1. PostgreSQL (Motor de Base de Datos Relacional)
- **Versión de Imagen:** `postgres:15-alpine` (Versión ultra ligera para desarrollo ágil).
- **Puerto expuesto:** `5432`
- **Volumen Persistente:** Utilizamos un volumen llamado `postgres_data`. Esto garantiza que, aunque apagues el contenedor de Docker o reinicies tu PC, los registros de la base de datos jamás se perderán.
- **Auto-inicialización Mágica:** Al levantar el contenedor por primerísima vez, Docker localiza y ejecuta internamente el script `docker/init-db.sql`. Este script es vital porque crea **5 bases de datos independientes** para cumplir con la regla de oro de los microservicios ("Base de datos por servicio"):
  1. `db_hitbox_auth` (Almacena Usuarios, Roles, y mapeo de permisos RBAC).
  2. `db_hitbox_main` (Lógica central del juego y la plataforma).
  3. `db_hitbox_stats_maps` (Estadísticas puras sobre el rendimiento en mapas).
  4. `db_hitbox_stats_weapons` (Inventario y estadísticas del armamento).
  5. `db_hitbox_stats_agents` (Datos y progresión de los agentes).
- **Seguridad (Archivos `.env`):** Las credenciales de acceso NUNCA se suben al código fuente (repositorio Git). Docker extrae la información sensible del archivo `.env` local. El archivo `.env.example` se incluye en el repositorio simplemente como una plantilla segura para que los nuevos miembros sepan qué variables deben configurar.

### 2.2. Ecosistema Kafka (Event-Driven Architecture)
Para que los microservicios se comuniquen de forma rapidísima sin tener que esperar la respuesta de otros (comunicación asíncrona), implementamos mensajería basada en eventos.
- **Zookeeper (Puerto 2181):** Es el "Gerente Coordinador". Apache Kafka no puede operar sin él. Se encarga de saber qué nodos de Kafka están activos y gestiona la metadata de todo el clúster.
- **Kafka (Puerto 9092):** Es el "Bróker o Cartero de mensajes". Por ejemplo: Si el microservicio de Auth registra a un usuario nuevo, no llama directamente al servicio de Estadísticas para avisarle (eso generaría cuellos de botella). En su lugar, Auth simplemente "grita" un evento a Kafka (`"NUEVO_USUARIO_REGISTRADO"`), y Kafka se encarga de entregarle ese mensaje a cualquier microservicio que esté interesado en escucharlo.

---

## 3. 🧩 Capa Core de Spring Cloud (La Espina Dorsal)

Estos tres microservicios no contienen lógica del juego. Son la infraestructura de enrutamiento y administración de Spring Boot. Ningún microservicio de negocio puede funcionar si estos tres no están activos.

### 3.1. Config Server (`config-server`)
* **Puerto reservado:** `8888`
* **Perfil activo:** `native`
* **¿Para qué sirve?:** Centraliza toda la configuración del sistema. En lugar de que cada microservicio guarde un archivo `application.yml` lleno de contraseñas, URLs y secretos de JWT esparcidos y repetidos por todos lados, el Config Server es el "Banco Central" de configuraciones. Cuando un microservicio como `ms-weapons` arranca, lo primero que hace es golpear la puerta del Config Server y preguntarle: *"Oye, soy ms-weapons, devuélveme mis credenciales y configuración"*.

### 3.2. Eureka Server (`eureka-server`)
* **Puerto reservado:** `8761`
* **Tecnología:** Netflix OSS Discovery Service
* **¿Para qué sirve?:** Es el "Directorio Telefónico" o Registro de Servicios. En producción, las IPs de los microservicios cambian constantemente y se clonan. Con Eureka, los microservicios no necesitan memorizar las IPs de sus compañeros.
* **Flujo:** Cuando el servicio `ms-auth` arranca, llama a Eureka y le dice: *"Hola, estoy vivo, soy MS-AUTH y vivo en la dirección localhost:8081"*. Si más tarde el Gateway necesita conectarse con Auth, simplemente le pregunta a Eureka: *"Dame la dirección de alguien que se llame MS-AUTH"*, y Eureka hace el emparejamiento.

### 3.3. API Gateway (`api-gateway`)
* **Puerto reservado:** `8080`
* **¿Para qué sirve?:** Es la ÚNICA puerta de entrada autorizada para el mundo exterior (Frontend en React/Angular, o Aplicaciones Móviles). Ningún cliente web puede ni debe conectarse directamente a un microservicio interno por seguridad.
* **Ventajas Críticas:**
  - **Enrutamiento Inteligente:** Lee la URL solicitada (ej. `GET /api/auth/login`) y utiliza la información de Eureka (`lb://ms-auth`) para redirigir el tráfico por debajo de la mesa sin que el usuario se dé cuenta.
  - **Seguridad Centralizada:** Aquí se configura CORS globalmente. También sirve para interceptar tokens JWT antes de dejar pasar la petición hacia adentro.
  - **Balanceo de Carga (Load Balancing):** Si el servidor se congestiona y levantamos 3 copias idénticas del servicio `ms-auth`, el Gateway (gracias al prefijo `lb://`) reparte las peticiones equitativamente (round-robin) para no saturar a una sola copia.

---

## 4. 💼 Capa de Negocio (Microservicios de Aplicación)

### 4.1. MS-Auth (`fullstack` folder)
* **Puerto:** `8081`
* **Conexión a BD:** Se conecta directamente a la base de datos `db_hitbox_auth` ubicada en nuestro contenedor Docker PostgreSQL.
* **Misión Principal:** Absorber toda la responsabilidad de seguridad, autenticación y Control de Acceso Basado en Roles (RBAC).
* **Funcionalidades Clave implementadas:**
  - **Seguridad Spring Security:** Filtros de protección de rutas y `UserDetailsService` conectado a JPA.
  - **Cifrado Fuerte:** Cifrado y validación de contraseñas de usuarios utilizando algoritmos asimétricos (BCryptPasswordEncoder).
  - **Tokenización:** Generación, firma digital secreta y validación de JSON Web Tokens (JWT) válidos por 24 horas para sesiones sin estado (Stateless).

---

## 5. 🚀 Manual de Operaciones: Secuencia Estricta de Arranque

> [!WARNING]
> Si eres un desarrollador clonando este proyecto por primera vez, **DEBES seguir este orden EXACTO** para levantar el entorno. Saltarse un paso o alterar el orden provocará errores de cascada, fallos de inyección de beans en Spring, y errores `ConnectionRefused`.

### Fase 1: Levantando la Infraestructura Base (Consola)
1. Clona este repositorio en tu máquina.
2. Localiza el archivo `.env.example`, hazle una copia, renómbrala a `.env`, y rellena el usuario y contraseña que desees para la base de datos.
3. Abre una terminal (CMD, PowerShell, Bash) en la raíz del proyecto (`fullstack_project`).
4. Ejecuta el comando para orquestar y levantar los contenedores en segundo plano:
   ```bash
   docker-compose up -d
   ```
5. **Comprobación:** Ejecuta `docker ps`. Debes asegurar que los contenedores `postgres`, `kafka` y `zookeeper` tengan un estado `Up`. Espera unos 10 segundos a que la base de datos termine de auto-crear los esquemas.

### Fase 2: El Cerebro del Sistema (IDE - IntelliJ/Eclipse)
6. **Config Server:** Abre el proyecto `config-server` y ejecútalo. 
   - *Verificación:* Revisa la consola de Spring Boot. Debe decir `Started ConfigServerApplication...` y debe estar escuchando silenciosamente en el puerto **8888**.
7. **Eureka Server:** Abre el proyecto `eureka-server` y ejecútalo.
   - *Verificación:* Abre tu navegador web e ingresa a `http://localhost:8761`. Deberías ver el dashboard administrativo rojo de Spring Eureka (actualmente la sección de *Instances currently registered* estará vacía).

### Fase 3: Puente al Mundo Exterior
8. **API Gateway:** Abre el proyecto `api-gateway` y ejecútalo.
   - *Verificación:* En los logs de la consola notarás que descubre automáticamente a Eureka. Si vuelves a refrescar la página de `localhost:8761`, verás que `API-GATEWAY` acaba de registrar su estado como `UP`.

### Fase 4: Servicios de Lógica de Negocio
9. **MS-Auth (`fullstack`):** Abre tu proyecto principal y ejecútalo.
   - *Verificación:*
     1. HikariCP (El pool de conexiones) anunciará en la consola que logró conectarse con éxito a la base de datos de Docker `db_hitbox_auth` (Puerto 5432).
     2. Spring Security cargará tu `DataSeeder` y `SecurityFilterChain`.
     3. Segundos después de iniciar en el puerto **8081**, si refrescas el dashboard de Eureka (`localhost:8761`), verás aparecer a `MS-AUTH` listado y listo para recibir peticiones a través del Gateway.

🎉 **¡Tu entorno de Microservicios está 100% operativo!** Cualquier petición que envíes desde tu Frontend (Postman, React, etc.) a `http://localhost:8080/api/auth/login` navegará por toda esta arquitectura hasta responderte de manera exitosa.
