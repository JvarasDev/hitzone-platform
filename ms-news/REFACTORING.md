# Refactoring — ms-news

## Contexto
El microservicio `ms-news` presentaba tres desviaciones en su implementación con respecto a la convención global del proyecto y a las mejores prácticas arquitectónicas: la inversión de la nomenclatura de interfaz/implementación en la capa de servicio (donde la clase de implementación se llamaba `NewsArticleService` y la interfaz `NewsArticleServiceInterface`), la coexistencia innecesaria de dos archivos de propiedades de configuración (`application.properties` y `application.yml`), y la falta de estandarización en la respuesta ante errores de validación en `GlobalExceptionHandler`.

## Decisiones tomadas

### 1. Corrección de nomenclatura: interfaz e implementación de servicio
- **Problema detectado:** La interfaz se llamaba `NewsArticleServiceInterface.java` y la clase concreta de implementación `NewsArticleService.java`. Esto rompía la convención adoptada en el resto de la plataforma (`XxxService` para la interfaz y `XxxServiceImpl` para la implementación).
- **Por qué se corrigió:** Para homologar el estilo del código en todas las bases de código de los microservicios, eliminando ambigüedades e incoherencias para los desarrolladores.
- **Cómo funciona ahora:** La interfaz se renombró a `NewsArticleService.java` y la implementación a `NewsArticleServiceImpl.java` declarando `implements NewsArticleService`.
- **Referencias actualizadas:** Se actualizó `NewsArticleController.java` para inyectar correctamente `NewsArticleService` (la interfaz) en su constructor, cumpliendo con el principio de inyección de dependencias orientado a abstracciones.

### 2. Eliminación de `application.properties` redundante
- **Problema detectado:** Coexistían `application.properties` y `application.yml` en la ruta `src/main/resources/`. El archivo properties solo declaraba `spring.application.name`, propiedad que ya estaba configurada correctamente en el archivo YAML.
- **Por qué se eliminó:** Mantener fuentes de configuración duplicadas incrementa el riesgo de desincronización de configuraciones esenciales (como puertos o credenciales) al desplegar en distintos entornos de ejecución.
- **Riesgo mitigado:** Se mitigó la posibilidad de colisiones de configuración o sobreescritura silenciosa de propiedades críticas de Spring Boot.

### 3. Estandarización de `GlobalExceptionHandler`
- **Problema detectado:** El método `handleValidation` encargado de procesar errores de argumentos de payload no válidos (`@Valid`) retornaba una respuesta estructurada como `Map<String, String>` en lugar de una instancia de `ApiError` encapsulada en `ResponseEntity`.
- **Por qué se cambió:** Para unificar las respuestas de error globales de la plataforma HitZone, garantizando un contrato predecible y consistente para el cliente frontend.
- **Cómo funciona ahora:** Se reescribió `handleValidation` para retornar `ResponseEntity<ApiError>` con estado `400 Bad Request`, donde el campo `message` concatena los detalles de validación de los campos individuales separados por `"; "`.

---

## Impacto esperado
- **Consistencia del Diseño:** El microservicio se alinea totalmente al patrón arquitectónico CSR del ecosistema.
- **Predictibilidad en Configuración:** Mayor claridad en el arranque del servicio al tener una única fuente de verdad (`application.yml`).
- **Homogeneidad de Errores:** Respuestas estandarizadas de la API en todas las rutas ante fallos de payloads.

---

## Lo que NO cambió
- El esquema de persistencia y la entidad `NewsArticle`.
- Los DTOs de petición y respuesta expuestos (`NewsArticleRequestDTO` y `NewsArticleResponseDTO`).
- Los contratos HTTP de los endpoints CRUD expuestos en el controlador.
- La estructura del formato de respuesta general de excepciones (`ApiError`).
