# 🎯 HitZone Platform

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)

**HitZone Platform** es un ecosistema distribuido basado en una arquitectura de microservicios, diseñado para gestionar estadísticas, emparejamientos, armamento y datos de jugadores en un entorno competitivo de esports.

Este proyecto implementa patrones avanzados de arquitectura como **API Gateway**, **Service Discovery (Eureka)**, **Configuración Centralizada (Config Server)** y el estricto aislamiento de datos (**Database-per-Service**) garantizando escalabilidad e independencia.

---

## 🏗️ Arquitectura del Sistema

El ecosistema está compuesto por la siguiente topología de servicios:

### Infraestructura Core
* **`config-server` (8888):** Gestión centralizada de las configuraciones de la plataforma.
* **`eureka-server` (8761):** Registro y descubrimiento dinámico de los microservicios.
* **`api-gateway` (8080):** Enrutamiento perimetral y validación centralizada de seguridad (JWT).

### Microservicios de Negocio
* **`ms-auth` (8081):** Gestión de identidades, roles y emisión de tokens JWT.
* **`ms-weapons` (8082):** Estadísticas y economía del armamento.
* **`ms-agents` (8083):** Roster de agentes y habilidades tácticas.
* **`ms-maps` (8084):** Información y dificultad de escenarios geotácticos.
* **`ms-persistence` (8085):** Auditoría y trazabilidad de eventos inmutables.
* **`ms-rank` (8086):** Sistema ELO y posicionamiento competitivo.
* **`ms-news` (8087):** Portal editorial y de anuncios.
* **`ms-matches` (8088):** Registro de partidas e historial de jugadores.

---

## ⚙️ Requisitos Previos

Antes de desplegar el proyecto, asegúrate de contar con las siguientes herramientas en tu entorno local:

- **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (o Docker Engine + Docker Compose en Linux).
- **[Java Development Kit (JDK) 17 o 21](https://adoptium.net/)**.
- **[Apache Maven 3.8+](https://maven.apache.org/)** (opcional, tu IDE puede incluirlo).
- Un IDE compatible como IntelliJ IDEA, Eclipse o VS Code.

---

## 🚀 Guía de Instalación y Despliegue

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/hitzone-platform.git
cd hitzone-platform
```

### 2. Levantar la Infraestructura de Bases de Datos
La persistencia está completamente contenerizada. El script de inicialización (`init-db.sql`) creará automáticamente las 8 bases de datos aisladas necesarias para cada microservicio.

```bash
# Levantar PostgreSQL 15 en segundo plano
docker compose up -d

# (Opcional) Verificar que el contenedor esté corriendo correctamente
docker compose ps
```
*Nota: PostgreSQL se levantará con el usuario `postgres` y la contraseña por defecto `HitzonePassword123!` en el puerto `5432`.*

### 3. Secuencia de Arranque de Microservicios
Debido a las dependencias de red y configuración, los servicios de Spring Boot deben levantarse **estrictamente en el siguiente orden**. Puedes iniciarlos desde la terminal usando Maven, o directamente desde tu IDE ejecutando las clases `...Application.java`.

**Fase A: Infraestructura (Obligatorio en este orden)**
1. **`config-server`** (Espera a que termine de arrancar completamente).
2. **`eureka-server`** (Inicia y se conecta al config-server).
3. **`api-gateway`** (Inicia y se registra en Eureka).

**Fase B: Microservicios de Negocio**
Una vez que el Gateway y Eureka estén operando, puedes levantar los siguientes microservicios **en cualquier orden** o en paralelo:
- `ms-auth`, `ms-weapons`, `ms-agents`, `ms-maps`, `ms-persistence`, `ms-rank`, `ms-news`, `ms-matches`.

> [!TIP]
> **Población Automática (Flyway):** Al arrancar cada microservicio de negocio, la herramienta **Flyway** se conectará automáticamente a su base de datos correspondiente, creará las tablas (`V1__...sql`) e inyectará los datos semilla iniciales (`V2__...sql`). ¡No necesitas ejecutar ningún script SQL manual!

---

## 🧪 Verificación del Despliegue

1. **Dashboard de Eureka:** Abre en tu navegador `http://localhost:8761`. Deberías ver los microservicios listados bajo el apartado *Instances currently registered with Eureka*.
2. **Prueba de API Gateway:** Realiza una petición GET a través del Gateway.
   ```bash
   curl -X GET http://localhost:8080/api/weapons
   ```
   *(Si el endpoint está protegido, asegúrate primero de generar un token vía `/api/auth/login` y pasarlo como `Bearer Token`)*.

---

## 📚 Documentación Adicional
Para profundizar en la arquitectura, diagramas de flujo, endpoints analíticos y preguntas frecuentes de defensa técnica, revisa la **[Guía del proyecto (Onboarding_hitzone.md)](Onboarding_hitzone.md)** incluida en la raíz del proyecto.

---

## 🛡️ Estructura y Patrones
- **Patrón CSR:** Todos los servicios separan estrictamente sus responsabilidades en capas `Controller` -> `Service` -> `Repository`.
- **Comunicación Síncrona:** Interacción inter-servicio mediante **OpenFeign** con *Circuit Breaker* (Fallbacks) implementado.
- **DTOs & Seguridad:** Uso absoluto de *Data Transfer Objects* para proteger la mutación de las Entidades JPA. Manejo centralizado de excepciones con `@RestControllerAdvice`.
