# 📚 ÍNDICE MAESTRO - ARQUITECTURA HITBOXKING

## Acceso rápido a toda la documentación

---

## 🎯 INICIO RÁPIDO

**Para empezar a entender el proyecto en 30 minutos:**

1. Lee: [RESUMEN EJECUTIVO](#resumen-ejecutivo)
2. Visualiza: [Diagrama de Topología General](DIAGRAMAS_ARQUITECTONICOS.md#1-diagrama-de-topología-general)
3. Ejecuta: [Instalación Paso a Paso](GUIA_OPERACIONES_TROUBLESHOOTING.md#-instalación-paso-a-paso)
4. Testea: [Testing Manual](GUIA_OPERACIONES_TROUBLESHOOTING.md#-testing-manual)

---

## 📑 ESTRUCTURA DE DOCUMENTACIÓN

Este proyecto incluye **3 documentos principales optimizados para Obsidian**:

### 📄 1. INFORME_ARQUITECTONICO_EJECUTIVO.md (20,000+ palabras)

**Contenido completo y técnico**

- ✅ Visión arquitectónica completa
- ✅ Decisiones técnicas explicadas
- ✅ Stack de dependencias completo
- ✅ Flujos transaccionales detallados
- ✅ Integración de microservicios
- ✅ Patrones implementados

**Para:** Ingenieros, Arquitectos, Estudiantes que quieren ENTENDER el proyecto

---

### 📊 2. DIAGRAMAS_ARQUITECTONICOS.md (15 diagramas Mermaid)

**Visualización gráfica de toda la arquitectura**

Incluye:

- Topología general del sistema
- Flujo de registro e autenticación
- Flujo de login y JWT
- Solicitudes autenticadas
- CRUD de agentes
- Arquitectura multicapa
- Eventos Kafka
- Modelos de datos
- Ciclo de compilación
- Manejo de excepciones
- Escalabilidad
- Stack de dependencias

**Para:** Entender visualmente cómo funciona todo

---

### 🛠️ 3. GUIA_OPERACIONES_TROUBLESHOOTING.md (Práctico)

**Cómo operar, mantener y debuggear el proyecto**

Incluye:

- Instalación paso a paso
- Arranque secuencial correcto
- Verificación de salud
- Troubleshooting de 7 problemas comunes
- Debugging con logs
- Testing manual de endpoints
- Parada y limpieza
- Mejores prácticas de desarrollo

**Para:** Operación diaria del proyecto

---

## 🏗️ RESUMEN EJECUTIVO

### ¿Qué es Hitboxking?

**Plataforma competitiva de videojuegos** con:

- 🎮 Gestión de agentes (personajes)
- 🔫 Inventario de armas
- 🏆 Rankings y estadísticas
- 🔐 Autenticación segura
- 📊 Análisis de rendimiento

### Arquitectura: MICROSERVICIOS

```
🌐 Frontend (HTML/CSS/JS)
        ↓
🚪 API Gateway (8080)
     ↙ ↓ ↘
🔐 MS-Auth  🎮 MS-Agents  🔫 MS-Weapons
(8081)      (8083)        (8082)
   ↓          ↓              ↓
🗄️ PostgreSQL (4 bases de datos)
📨 Kafka (Event Bus)
```

### Por qué Microservicios vs Monolito

| Aspecto                 | Monolito                | Microservicios    |
| ----------------------- | ----------------------- | ----------------- |
| Escalabilidad           | Todo o nada             | Quirúrgica        |
| Resiliencia             | 1 fallo = Sistema caído | Fallos aislados   |
| Despliegues             | Acoplados               | Independientes    |
| Velocidad de desarrollo | Lenta                   | Rápida            |
| Complejidad             | Baja                    | Media (manejable) |

---

## 🔍 NAVEGACIÓN POR TEMA

### Si quieres entender...

#### **Autenticación & Seguridad**

- [JWT y Tokenización →](INFORME_ARQUITECTONICO_EJECUTIVO.md#generador-jwt)
- [Roles y Permisos (RBAC) →](INFORME_ARQUITECTONICO_EJECUTIVO.md#1-ms-auth-microservicio-de-autenticación)
- [Flujo de Login →](DIAGRAMAS_ARQUITECTONICOS.md#3-flujo-de-login-y-generación-jwt)
- [Validación de JWT →](DIAGRAMAS_ARQUITECTONICOS.md#4-flujo-de-solicitud-autenticada)

#### **Microservicios**

- [MS-Auth (Autenticación) →](INFORME_ARQUITECTONICO_EJECUTIVO.md#1-ms-auth-microservicio-de-autenticación)
- [MS-Agents (Agentes) →](INFORME_ARQUITECTONICO_EJECUTIVO.md#2-ms-agents-microservicio-de-agentes)
- [MS-Weapons (Armas) →](INFORME_ARQUITECTONICO_EJECUTIVO.md#3-ms-weapons-microservicio-de-armas)
- [API Gateway (Enrutamiento) →](INFORME_ARQUITECTONICO_EJECUTIVO.md#3-api-gateway-puerto-8080)

#### **Infraestructura**

- [Docker & Compose →](INFORME_ARQUITECTONICO_EJECUTIVO.md#-capa-de-infraestructura)
- [PostgreSQL & Bases de Datos →](INFORME_ARQUITECTONICO_EJECUTIVO.md#2-base-de-datos-relacional-normalizada-postgresql)
- [Kafka & Event Bus →](INFORME_ARQUITECTONICO_EJECUTIVO.md#3-event-driven-con-kafka--zookeeper)
- [Eureka & Service Discovery →](INFORME_ARQUITECTONICO_EJECUTIVO.md#2-eureka-server-puerto-8761)

#### **Patrones & Diseño**

- [Patrón Síncrono (REST) →](INFORME_ARQUITECTONICO_EJECUTIVO.md#patrón-1-comunicación-sincrónica-resthttp)
- [Patrón Asincrónico (Kafka) →](INFORME_ARQUITECTONICO_EJECUTIVO.md#patrón-2-comunicación-asincrónica-eventoskafka)
- [Arquitectura Multicapa →](DIAGRAMAS_ARQUITECTONICOS.md#6-arquitectura-multicapa-de-ms-agents)
- [Manejo de Excepciones →](DIAGRAMAS_ARQUITECTONICOS.md#12-manejo-de-excepciones-global)

#### **Base de Datos**

- [Modelo de Agents & Abilities →](DIAGRAMAS_ARQUITECTONICOS.md#8-modelo-de-datos-agents--abilities)
- [Modelo de Weapons →](DIAGRAMAS_ARQUITECTONICOS.md#9-modelo-de-datos-weapons)
- [Modelo de Authentication →](DIAGRAMAS_ARQUITECTONICOS.md#10-modelo-de-datos-authentication)
- [Normalización 3NF →](INFORME_ARQUITECTONICO_EJECUTIVO.md#2-base-de-datos-relacional-normalizada-postgresql)

#### **Operación & Deployment**

- [Instalación Inicial →](GUIA_OPERACIONES_TROUBLESHOOTING.md#-instalación-paso-a-paso)
- [Arranque Secuencial →](GUIA_OPERACIONES_TROUBLESHOOTING.md#-arranque-secuencial)
- [Troubleshooting →](GUIA_OPERACIONES_TROUBLESHOOTING.md#-troubleshooting-común)
- [Testing Manual →](GUIA_OPERACIONES_TROUBLESHOOTING.md#-testing-manual)
- [CI/CD →](DIAGRAMAS_ARQUITECTONICOS.md#11-ciclo-de-compilación--despliegue)

#### **Performance & Escalabilidad**

- [Escalabilidad Horizontal →](DIAGRAMAS_ARQUITECTONICOS.md#14-escalabilidad-de-monolito-a-microservicios)
- [Circuit Breakers →](INFORME_ARQUITECTONICO_EJECUTIVO.md#2-resiliencia--circuit-breakers)
- [Caché & Optimización →](GUIA_OPERACIONES_TROUBLESHOOTING.md#8-performance)

---

## 🎓 CASOS DE USO COMUNES

### Caso 1: "Quiero entender cómo funciona el login"

**Lectura recomendada (20 min):**

1. [Decisión Técnica: JWT →](INFORME_ARQUITECTONICO_EJECUTIVO.md#4-eureka-para-service-discovery)
2. [Diagrama: Flujo de Login →](DIAGRAMAS_ARQUITECTONICOS.md#3-flujo-de-login-y-generación-jwt)
3. [Código: GeneradorJWT →](INFORME_ARQUITECTONICO_EJECUTIVO.md#generador-jwt)
4. [Test Manual: Login →](GUIA_OPERACIONES_TROUBLESHOOTING.md#test-2-login-y-obtener-jwt)

### Caso 2: "Cómo agregar un nuevo microservicio"

**Lectura recomendada (1 hora):**

1. [Estructura de Microservicio →](INFORME_ARQUITECTONICO_EJECUTIVO.md#estructura-general-de-cada-microservicio)
2. [Spring Cloud Setup →](INFORME_ARQUITECTONICO_EJECUTIVO.md#-capa-core-spring-cloud)
3. [Ejemplos: MS-Agents →](INFORME_ARQUITECTONICO_EJECUTIVO.md#2-ms-agents-microservicio-de-agentes)
4. [Registrarse en Eureka →](INFORME_ARQUITECTONICO_EJECUTIVO.md#2-eureka-server-puerto-8761)
5. [Troubleshooting →](GUIA_OPERACIONES_TROUBLESHOOTING.md#problema-3-microservicio-no-se-registra-en-eureka)

### Caso 3: "El sistema está lento. ¿Qué hago?"

**Lectura recomendada (30 min):**

1. [Performance Best Practices →](GUIA_OPERACIONES_TROUBLESHOOTING.md#8-performance)
2. [Caché Strategy →](INFORME_ARQUITECTONICO_EJECUTIVO.md#4-validación-de-datos-3-capas)
3. [Monitoreo & Observabilidad →](INFORME_ARQUITECTONICO_EJECUTIVO.md#3-monitoreo--observabilidad)
4. [Logs →](GUIA_OPERACIONES_TROUBLESHOOTING.md#📊-logs-y-debugging)

### Caso 4: "¿Cómo escalo el sistema?"

**Lectura recomendada (45 min):**

1. [Escalabilidad Horizontal →](DIAGRAMAS_ARQUITECTONICOS.md#14-escalabilidad-de-monolito-a-microservicios)
2. [Docker Compose Scaling →](GUIA_OPERACIONES_TROUBLESHOOTING.md#terminal-3-ms-auth-nombre-eureka-fullstack)
3. [Kubernetes Deployment →](INFORME_ARQUITECTONICO_EJECUTIVO.md#1-escalabilidad-horizontal)
4. [Load Balancing →](INFORME_ARQUITECTONICO_EJECUTIVO.md#3-api-gateway-puerto-8080)

---

## 📊 ESTADÍSTICAS DEL PROYECTO

| Métrica                    | Valor                              |
| -------------------------- | ---------------------------------- |
| **Microservicios**         | 3 + 3 de infraestructura = 6 total |
| **Bases de Datos**         | 4 independientes (PostgreSQL)      |
| **Puertos**                | 8080-8083, 8761, 8888              |
| **Lenguajes Backend**      | Java 17 + Spring Boot 4.0.6        |
| **Lenguaje Frontend**      | HTML5 + CSS3 + JavaScript Vanilla  |
| **Contenedores Docker**    | 3 (PostgreSQL, Kafka, Zookeeper)   |
| **Patrones Implementados** | 8+                                 |
| **Endpoints REST**         | 30+                                |

---

## 🔗 REFERENCIAS RÁPIDAS

### Stack Tecnológico

- **Backend:** Spring Boot 4.0.6, Spring Cloud 2025.1.1, Java 17
- **Base Datos:** PostgreSQL 15, JPA/Hibernate
- **Messaging:** Apache Kafka 7.5.0, Zookeeper
- **Discovery:** Netflix Eureka
- **Gateway:** Spring Cloud Gateway
- **Config:** Spring Cloud Config Server
- **Frontend:** HTML5/CSS3/JavaScript
- **Contenedorización:** Docker & Docker Compose

### URLs Locales

- 🚪 **API Gateway:** http://localhost:8080
- 🔐 **MS-Auth:** http://localhost:8081
- 🔫 **MS-Weapons:** http://localhost:8082
- 🎮 **MS-Agents:** http://localhost:8083
- ⚙️ **Config Server:** http://localhost:8888
- 🔍 **Eureka Dashboard:** http://localhost:8761
- 🗄️ **PostgreSQL:** localhost:5432
- 📨 **Kafka:** localhost:9092

### Credenciales por Defecto

```
PostgreSQL User: hitbox_admin
PostgreSQL Password: SecurePass123!Hitbox
```

### Archivos Importantes

- `docker-compose.yml` - Orquestación de contenedores
- `config-server/src/main/resources/config/` - Configuración centralizada
- `.env.example` - Plantilla de variables de ambiente
- `pom.xml` - Dependencias Maven

---

## 🚀 PRÓXIMOS PASOS

### Para Principiantes

1. ✅ Leer este índice completo (10 min)
2. ✅ Ejecutar [Instalación Paso a Paso](GUIA_OPERACIONES_TROUBLESHOOTING.md#-instalación-paso-a-paso) (20 min)
3. ✅ Visualizar [Diagramas](DIAGRAMAS_ARQUITECTONICOS.md) (15 min)
4. ✅ Realizar [Testing Manual](GUIA_OPERACIONES_TROUBLESHOOTING.md#-testing-manual) (15 min)
5. ✅ Leer [Flujos Completos](INFORME_ARQUITECTONICO_EJECUTIVO.md#-flujos-transaccionales-completos) (30 min)

### Para Desarrolladores

1. ✅ Entender [Decisiones Técnicas](INFORME_ARQUITECTONICO_EJECUTIVO.md#-decisiones-técnicas-clave)
2. ✅ Estudiar [Arquitectura Multicapa](DIAGRAMAS_ARQUITECTONICOS.md#6-arquitectura-multicapa-de-ms-agents)
3. ✅ Revisar [Mejores Prácticas](GUIA_OPERACIONES_TROUBLESHOOTING.md#-mejores-prácticas-de-desarrollo)
4. ✅ Hacer cambios en rama feature
5. ✅ Crear Pull Request

### Para Arquitectos

1. ✅ Revisar [Visión Arquitectónica](INFORME_ARQUITECTONICO_EJECUTIVO.md#-visión-arquitectónica)
2. ✅ Analizar [Patrones](INFORME_ARQUITECTONICO_EJECUTIVO.md#-patrones-de-integración)
3. ✅ Evaluar [Escalabilidad](DIAGRAMAS_ARQUITECTONICOS.md#14-escalabilidad-de-monolito-a-microservicios)
4. ✅ Planear evolución a Kubernetes
5. ✅ Definir estrategia de monitoring

---

## 📚 GLOSARIO

| Término           | Definición                                                   |
| ----------------- | ------------------------------------------------------------ |
| **Microservicio** | Servicio independiente, escalable, con responsabilidad única |
| **API Gateway**   | Puerta de entrada centralizada para clientes externos        |
| **Eureka**        | Servicio de registro y descubrimiento de servicios           |
| **Config Server** | Servidor centralizado de configuración                       |
| **Kafka**         | Message broker para comunicación asincrónica                 |
| **JWT**           | JSON Web Token para autenticación sin estado                 |
| **RBAC**          | Role-Based Access Control (control de acceso por roles)      |
| **3NF**           | Tercera Forma Normal (diseño de BD normalizado)              |
| **Docker**        | Plataforma de contenedorización                              |
| **Eureka Client** | Microservicio que se registra en Eureka                      |

---

## 📞 SOPORTE

### Problemas Comunes

- Puerto en uso → [Solución](GUIA_OPERACIONES_TROUBLESHOOTING.md#problema-1-puerto-ya-en-uso)
- PostgreSQL no inicia → [Solución](GUIA_OPERACIONES_TROUBLESHOOTING.md#problema-2-postgresql-no-inicia)
- JWT inválido → [Solución](GUIA_OPERACIONES_TROUBLESHOOTING.md#problema-4-jwt-expirado-o-inválido)
- Kafka no conecta → [Solución](GUIA_OPERACIONES_TROUBLESHOOTING.md#problema-5-kafka-no-conecta)

---

## ✅ CHECKLIST: Antes de empezar

- [ ] Docker Desktop instalado y corriendo
- [ ] Git configurado
- [ ] IntelliJ IDEA o IDE similar abierto
- [ ] 8GB RAM disponible
- [ ] Puerto 8080-8083, 8761, 8888, 5432, 9092, 2181 libres
- [ ] .env creado con credenciales
- [ ] docker-compose up -d ejecutado
- [ ] PostgreSQL inicializado (15 segundos de espera)

---

## 🎯 Objetivo Final

Después de leer esta documentación **completa**, cualquier ingeniero debería poder:

✅ Explicar la arquitectura a un ejecutivo
✅ Implementar un nuevo microservicio
✅ Debuggear problemas complejos
✅ Escalar el sistema horizontalmente
✅ Mantener el código limpio y seguro
✅ Contribuir efectivamente al proyecto

---

**Última actualización:** Mayo 2026
**Versión:** 1.0
**Autor:** Ingeniero Senior de Software

---

## 🔗 Enlaces a Documentos Principales

### 📄 Documentos Completos

1. [📋 INFORME_ARQUITECTONICO_EJECUTIVO.md](INFORME_ARQUITECTONICO_EJECUTIVO.md)
   - Informe técnico completo (20,000+ palabras)
   - Decisiones, patrones, flujos

2. [📊 DIAGRAMAS_ARQUITECTONICOS.md](DIAGRAMAS_ARQUITECTONICOS.md)
   - 15 diagramas Mermaid
   - Visualización completa

3. [🛠️ GUIA_OPERACIONES_TROUBLESHOOTING.md](GUIA_OPERACIONES_TROUBLESHOOTING.md)
   - Guía práctica operacional
   - Troubleshooting y best practices

---

**FIN DEL ÍNDICE**

_Este documento es tu brújula en la arquitectura de Hitboxking._
_Navega, aprende, construye, escala._
