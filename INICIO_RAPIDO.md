# HITZONE - Scripts de Inicio de Servicios

## 🚀 OPCIÓN RECOMENDADA: START_SERVICES.bat ⭐

### Lo más fácil - Una terminal, todos los servicios, solo errores

**Simplemente haz double-click en:**
```
START_SERVICES.bat
```

**Características:**
- ✅ Una sola terminal con todos los servicios
- ✅ Solo muestra errores en pantalla (sin checks)
- ✅ 8 servicios + MS-Auth incluido
- ✅ Muy fácil de usar

**¿Qué hace?**
1. Inicia Config Server → Eureka Server → API Gateway → MS-Auth + todos los microservicios
2. Espera 20 segundos a que se inicialicen
3. Muestra la pantalla final con los puertos
4. Solo muestra errores (sin mensajes de éxito)

---

## 🌐 ACCESO AL FRONTEND

### Directamente desde el navegador:
```
http://localhost:8080/login.html
```

### O si usas Live Server en VS Code:
```
1. Abre la carpeta "frontend" en VS Code
2. Click derecho en login.html
3. Selecciona "Open with Live Server"
4. Se abrirá en http://localhost:3000 (o similar)
5. Se conectará automáticamente a http://localhost:8080
```

---

## 📋 TODOS LOS PUERTOS

| Servicio | Puerto | URL |
|----------|--------|-----|
| **Config Server** | 8888 | http://localhost:8888 |
| **Eureka Dashboard** | 8761 | http://localhost:8761 |
| **API Gateway** | 8080 | http://localhost:8080 |
| **MS-Auth** | 8081 | http://localhost:8081 |
| **MS-Agents** | 8083 | http://localhost:8083 |
| **MS-Weapons** | 8082 | http://localhost:8082 |
| **MS-Maps** | 8084 | http://localhost:8084 |
| **MS-Persistence** | 8085 | http://localhost:8085 |

---

## ✅ VERIFICAR QUE TODO ESTÁ BIEN

### 1. Eureka Dashboard
Abre: http://localhost:8761

**Deberías ver:**
- Todos los servicios con estado "UP"
- Config-Server
- API-Gateway  
- MS-Auth
- MS-Agents
- MS-Weapons
- MS-Maps
- MS-Persistence

### 2. Frontend
Abre: http://localhost:8080/login.html

**Deberías ver:**
- La pantalla de login se carga
- Sin errores en la consola

### 3. Revisar Errores
En la terminal del script solo verás mensajes en ROJO si hay errores.

**Si no ves mensajes rojos = todo está bien ✅**

---

## ❌ SI ALGO FALLA

### Error: Puerto en uso
```
Otro programa está usando ese puerto
→ Ejecuta: STOP_ALL_SERVICES.bat
→ Espera 5 segundos
→ Vuelve a ejecutar START_SERVICES.bat
```

### Error: Connection refused
```
Los servicios aún se están iniciando
→ Espera 2-3 minutos más
→ Recarga el navegador
→ Revisa http://localhost:8761
```

### Error: Base de datos no disponible
```
PostgreSQL no está corriendo
→ Inicia PostgreSQL
→ Crea las bases de datos necesarias
→ Vuelve a ejecutar START_SERVICES.bat
```

---

## 🛑 PARA DETENER TODOS LOS SERVICIOS

### Opción 1: Script automático
```
Double-click en STOP_ALL_SERVICES.bat
```

### Opción 2: Manualmente
```
Presiona Ctrl+C en la terminal
O cierra la ventana de PowerShell
```

---

## 📁 OTROS SCRIPTS DISPONIBLES

### START_ALL_SERVICES_OPTIMIZED.bat
- Inicia cada servicio en una terminal separada
- Útil si quieres ver logs individuales
- Requiere Maven Wrapper

### START_ALL_SERVICES.bat  
- Similar pero requiere Maven instalado globalmente

### STOP_ALL_SERVICES.bat
- Detiene todos los procesos Java

---

## 💡 TIPS

1. **La primera vez tarda más**
   - Maven descarga dependencias
   - Espera 2-5 minutos en el primer inicio

2. **Revisa Eureka regularmente**
   - http://localhost:8761
   - Asegúrate que todos los servicios están "UP"

3. **Si cambias código**
   - Detén con Ctrl+C
   - Vuelve a ejecutar START_SERVICES.bat
   - Maven recompilará automáticamente

4. **En la terminal solo ves errores**
   - Eso es normal y esperado
   - Significa que los servicios están corriendo bien

---

## 🎯 FLUJO COMPLETO

```
1. Double-click START_SERVICES.bat
   ↓
2. Espera 20 segundos (los servicios se inicializan)
   ↓
3. Abre http://localhost:8080/login.html
   ↓
4. ¡A usar la aplicación!
   ↓
5. Para detener: Ctrl+C o STOP_ALL_SERVICES.bat
```

---

**Última actualización:** 13 de Mayo, 2026  
**Proyecto:** HITZONE Fullstack Microservices
