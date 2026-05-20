# Refactoring — ms-maps

## Contexto
Previo a la refactorización, el microservicio `ms-maps` presentaba fugas de detalles de persistencia (Entity Leaking) hacia la capa de presentación, ya que el servicio retornaba directamente la entidad JPA `modelMaps`. Además, el controlador asumía responsabilidades de mapeo de DTOs, la nomenclatura de los métodos públicos utilizaba el español mezclado con inglés, el código contenía abundante documentación Javadoc redundante y el manejador global de errores de validación no respetaba el formato estándar `ApiError` del ecosistema HitZone. Esta refactorización tuvo por objetivo corregir estas desviaciones de arquitectura, mejorar la legibilidad y homogenizar el control de errores.

## Decisiones tomadas

### 1. Encapsulamiento de entidad JPA (No Entity Leaking)
- **Por qué:** Exponer entidades JPA directamente en las firmas de servicio acopla innecesariamente la capa de presentación con el esquema de base de datos.
- **Cómo funciona:** Todos los métodos de `MapService` y `MapServiceImpl` fueron modificados para retornar `MapResponseDTO` (o listas del mismo). El mapeo de la entidad `modelMaps` hacia su DTO se realiza exclusivamente en la capa de negocio mediante un método privado auxiliar `mapToResponseDTO` que utiliza el constructor explícito del DTO.

### 2. Eliminación de `convertirADTO()` en el controlador
- **Por qué:** Siguiendo el principio de separación de responsabilidades y el patrón CSR, el controlador no debe contener lógica de transformación de datos ni importar o hacer referencia a las entidades del modelo persistido.
- **Cómo funciona:** Se eliminó por completo el método privado `convertirADTO(modelMaps mapa)` de `MapController.java`. Ahora los endpoints obtienen directamente del servicio los DTOs listos para ser encapsulados en un `ResponseEntity`, logrando un controlador limpio y desacoplado del modelo de base de datos.

### 3. Endpoint `GET /api/maps/filter`
- **Por qué:** Se requería exponer un endpoint de reporte especializado que permitiera filtrar mapas por múltiples tipos simultáneamente, devolviendo una respuesta limpia de DTOs.
- **Cómo funciona:** Se creó el endpoint `GET /api/maps/filter` en `MapController` que recibe el query parameter `types` como una colección multi-valor (`Collection<typeMaps>`). Este delega la consulta en la base de datos al método del repositorio `findByTypeIn` a través del servicio, retornando una lista de `MapResponseDTO` y una lista vacía en caso de no hallar coincidencias (con estado 200 OK).

### 4. Traducción de nomenclatura ES → EN
- **Por qué:** Para cumplir con los estándares de codificación del equipo y mantener el idioma inglés consistente en todo el proyecto.
- **Cómo funciona:** Se renombraron los métodos públicos en todas las capas del microservicio. Por ejemplo:
  - `crearMapa` se convirtió en `createMap`
  - `obtenerTodosMaps` se convirtió en `getAllMaps`
  - `obtenerMapaPorId` se convirtió en `getMapById`
  - `obtenerMapasPorTipo` se convirtió en `getMapsByType`
  - `analizarMapa` se convirtió en `analyzeMap`
  - `filtrarMapas` se convirtió en `filterMaps`
  - `actualizarMapa` se convirtió en `updateMap`
  - `eliminarMapa` se convirtió en `deleteMap`

### 5. Limpieza de Javadoc redundante
- **Por qué:** El exceso de comentarios obvios que simplemente repiten la firma del método o explican conceptos básicos ensucian el código y dificultan la lectura.
- **Cómo funciona:** Se removieron los bloques Javadoc de `MapServiceImpl.java` que carecían de valor real. Se mantuvieron únicamente aquellos comentarios que documentan lógica compleja o decisiones no evidentes (como el uso del switch mejorado de Java 14+ y el comportamiento transaccional read-only).

### 6. Estandarización de `GlobalExceptionHandler`
- **Por qué:** El formato devuelto en excepciones de validación de campos era un mapa simple `Map<String, String>`, lo cual rompía la uniformidad del formato de error global de la plataforma.
- **Cómo funciona:** Se modificó `handleValidation` en `GlobalExceptionHandler.java` para retornar un objeto `ApiError` estándar y un estado `400 Bad Request`. Los mensajes individuales de validación de campos se concatenan en un único `String` delimitado por `"; "`.

---

## Impacto esperado
- **Mantenibilidad:** El acoplamiento entre la base de datos y los clientes REST se reduce a cero al encapsular completamente el modelo en la capa de negocio.
- **Consistencia:** Las respuestas de error del microservicio ante payloads de entrada inválidos son ahora homogéneas con el resto de servicios (`ms-weapons` y `ms-agents`).
- **Legibilidad:** El código en inglés resulta más natural para el ecosistema de Java y Spring Boot, y se eliminó el "ruido visual" provisto por comentarios redundantes.

---

## Lo que NO cambió
- El esquema de base de datos de mapas (`maps` / `modelMaps`).
- La estructura interna del DTO de entrada (`MapRequestDTO`) ni de salida (`MapResponseDTO`).
- El comportamiento semántico de los endpoints CRUD existentes.
- La estructura del formato de respuesta general de excepciones (`ApiError`).
