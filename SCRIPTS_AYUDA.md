# Scripts de Inicio - HITZONE Fullstack

Este directorio contiene scripts para gestionar los microservicios del proyecto HITZONE.

## 📋 Scripts Disponibles

### 1. **START_SERVICES.bat** ⭐ (RECOMENDADO - NUEVA VERSION)

**Lo más simple: una sola terminal, todos los servicios, solo errores en pantalla**

**Características:**

- ✅ Todos los servicios en UNA SOLA terminal
- ✅ Solo muestra errores (sin checks de éxito)
- ✅ Incluye MS-Auth (fullstack) en el puerto 8081
- ✅ Usa PowerShell para mejor control
- ✅ Ejecuta en background con logs de errores

**Uso:**

```
Double-click START_SERVICES.bat
```

**Acceso directo al Frontend:**

```
http://localhost:8080/login.html
```

---

### 2. **START_ALL_SERVICES_OPTIMIZED.bat** (Alternativa)

Script que inicia cada servicio en una terminal separada.

**Uso:**

```
Double-click START_ALL_SERVICES_OPTIMIZED.bat
```

### 3. **START_ALL_SERVICES.bat** (Alternativa con Maven global)

Requiere Maven instalado en PATH.

**Uso:**

```
Double-click START_ALL_SERVICES.bat
```

### 4. **STOP_ALL_SERVICES.bat**

Detiene todos los servicios inmediatamente.

**Uso:**

```
Double-click STOP_ALL_SERVICES.bat
```

---

## 🚀 Orden de Inicio Automático

Los scripts inician los servicios en este orden:

1. **Config Server** (puerto 8888)
   - Servidor de configuración centralizado

2. **Eureka Server** (puerto 8761)
   - Registro de servicios y descubrimiento

3. **API Gateway** (puerto 8080)
   - Punto de entrada para todas las peticiones

4. **MS-Auth** (puerto 8081) ⭐ NUEVO
   - Servicio de autenticación y usuarios

5. **Microservicios** (en paralelo):
   | MS-Weapons | 8082 | http://localhost:8082 |
   | MS-Maps | 8084 | http://localhost:8084 |
   | MS-Persistence | 8085 | http://localhost:8085 |

---

## 🎨 Accediendo al Frontend

### Opción 1: Live Server (Recomendado)

```
1. En VS Code, abre la carpeta "frontend"
2. Click derecho en cualquier archivo .html
3. Selecciona "Open with Live Server"
4. El frontend se conectará automáticamente a http://localhost:8080
```

### Opción 2: Navegador Directo

```
1. Abre http://localhost:8080/frontend/dashboard.html
   O el archivo HTML deseado
2. La aplicación se conectará al API Gateway
```

### Opción 3: Línea de Comandos

```
# Opción A: Usando Python (si está instalado)
cd frontend
python -m http.server 3000

# Opción B: Usando Node.js (si está instalado)
cd frontend
npx http-server -p 3000
```

---

## 🔍 Verificación de Servicios

### 1. **Eureka Dashboard**

Accede a: http://localhost:8761

**Deberías ver:**

- Todas las instancias marcadas como "UP"
- Cada microservicio registrado

### 2. **Logs de Servicios**

Cada servicio abre en una terminal separada mostrando sus logs en tiempo real.

**Para ver errores:**

- Revisa la ventana de terminal del servicio específico
- Busca mensajes de error o excepciones

### 3. **Prueba de API**

```bash
# Test del API Gateway
curl http://localhost:8080

# Test de MS-Weapons
curl http://localhost:8082/health

# Test de MS-Agents
curl http://localhost:8083/health
```

---

## ⚙️ Requisitos del Sistema

### Software Requerido:

- **Java 17+** (OpenJDK o Similar)
- **Maven 3.6+** (O usar mvnw incluido)

### Configuración de Base de Datos:

Los microservicios requieren PostgreSQL con estas bases de datos:

```sql
- db_hitbox_stats_agents
- db_hitbox_stats_weapons
- db_hitbox_stats_maps
```

**Credenciales por defecto:**

- Usuario: `postgres`
- Password: `HitzonePassword123!`
- Host: `localhost:5432`

---

## 🆘 Troubleshooting

### Error: "Puerto ya está en uso"

```
Otro proceso está usando el puerto. Opciones:
1. Ejecuta STOP_ALL_SERVICES.bat
2. O termina la aplicación que usa ese puerto:
   netstat -ano | findstr :8080
```

### Error: "Connection refused"

```
Los servicios aún se están iniciando.
- Espera 1-2 minutos más
- Revisa los logs en las terminales de cada servicio
```

### Error: "Config Server no responde"

```
1. Verifica que Config Server está corriendo (puerto 8888)
2. Revisa http://localhost:8888
3. Comprueba los logs del Config Server
```

### Error: "Eureka Server no muestra servicios"

```
1. Asegúrate que Eureka Server está corriendo
2. Verifica que el puerto 8761 está disponible
3. Los servicios pueden tardar hasta 1 minuto en registrarse
4. Recarga la página de Eureka
```

### Error: "Base de datos - Connection refused"

```
1. Verifica que PostgreSQL está corriendo
2. Crea las bases de datos necesarias
3. Revisa las credenciales en application.yml de cada servicio
```

---

## 📝 Variables de Entorno (Opcional)

Si necesitas cambiar puertos o credenciales, edita los archivos:

```
config-server/src/main/resources/application.yml
eureka-server/src/main/resources/application.yml
api-gateway/src/main/resources/application.yml
ms-agents/src/main/resources/application.yml
ms-weapons/src/main/resources/application.yml
ms-maps/src/main/resources/application.yml
ms-persistence/src/main/resources/application.yml
```

---

## 🛑 Detener Servicios

### Opción 1: Script Automatizado

```
Double-click STOP_ALL_SERVICES.bat
```

### Opción 2: Manualmente

```
1. Cierra cada ventana de terminal (Ctrl+C o X)
2. O ejecuta: taskkill /F /IM java.exe
```

---

## 💡 Tips & Consejos

1. **Espera a que Eureka esté listo**
   - El API Gateway necesita que Eureka tenga los microservicios registrados
   - Espera a ver todos los servicios como "UP" en http://localhost:8761

2. **Revisa los logs**
   - Cada terminal muestra logs del servicio en tiempo real
   - Muy útil para debugging

3. **Reinicia limpio**
   - Si algo falla, ejecuta STOP_ALL_SERVICES.bat y comienza de nuevo

4. **Para desarrollo**
   - Puedes dejar los servicios corriendo mientras desarrollas en el frontend
   - Hot-reload del frontend funciona con Live Server

---

## 📞 Support

Para reportar problemas o solicitar mejoras:

1. Revisa los logs de cada servicio
2. Verifica que PostgreSQL está corriendo
3. Asegúrate que todos los puertos están disponibles
4. Revisa la sección de Troubleshooting arriba

---

**Última actualización:** 13 de Mayo, 2026
**Versión:** 1.0
**Proyecto:** HITZONE Fullstack Microservices
