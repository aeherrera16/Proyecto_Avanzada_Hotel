# 🏨 Sistema de Gestión Hotelera - Programación Reactiva

Sistema de gestión integral para hoteles desarrollado con **Spring Boot WebFlux** y **React**, implementando el patrón **Publicador-Suscriptor** y programación reactiva para un manejo eficiente de operaciones asíncronas no bloqueantes.

## ✨ Características Principales

- **Arquitectura Reactiva**: Implementación completa con Spring WebFlux y R2DBC
- **Patrón Publicador-Suscriptor**: Control de backpressure y flujos de datos
- **Gestión de Habitaciones**: CRUD reactivo para administrar habitaciones
- **Gestión de Huéspedes**: Registro y administración asíncrona de huéspedes
- **Sistema de Reservas**: Creación y seguimiento de reservas con validación reactiva
- **Procesamiento de Pagos**: Registro y control transaccional de pagos
- **Interfaz Moderna**: Frontend React conectado a endpoints reactivos
- **API RESTful Reactiva**: Endpoints que retornan `Flux<T>` y `Mono<T>`

---

## 🚀 Ejecución Rápida

### Paso 1: Ejecutar el Backend
```bash
cd /Users/anahy/Desktop/Programacion\ Avanzada/proyectoSpring/Proyecto_Avanzada_Hotel
./gradlew bootRun
```
> El backend estará disponible en **http://localhost:8085**

### Paso 2: Ejecutar el Frontend (en otra terminal)
```bash
cd /Users/anahy/Desktop/Programacion\ Avanzada/proyectoSpring/Proyecto_Avanzada_Hotel/frontend
npm install   # Solo la primera vez
npm start
```
> El frontend estará disponible en **http://localhost:3000**

---

## � Demostración de Programación Reactiva

### 1. Búsqueda con Recuperación de Errores (onErrorResume)

En las páginas de **Habitaciones**, **Huéspedes** o **Reservas**:

1. Usa la barra de búsqueda con un **ID inexistente** (ej: 999)
2. Selecciona **"Con Recuperación"** → El flujo continúa (badge amarillo "Recuperado")
3. Selecciona **"Sin Recuperación"** → El flujo se detiene (badge rojo "Error")

**Qué demuestra:** El operador `onErrorResume` permite que el flujo continúe con un valor por defecto en lugar de fallar.

### 2. Streaming en Tiempo Real (Patrón Publicador-Suscriptor)

En la página **Actividad**:

1. Haz clic en **"Ver Streaming"**
2. Observa cómo las habitaciones llegan **una por una** al panel de logs
3. Cada elemento muestra: `onNext: Habitación #101 (Simple) → Disponible`

**Qué demuestra:** El flujo de datos reactivo con `Flux` y `delayElements`, similar al patrón publicador-suscriptor.

### 3. Panel de Actividad (Operadores Reactivos)

El panel muestra los operadores reactivos en acción:
- `onSubscribe` - Cuando un cliente se suscribe
- `onNext` - Por cada elemento del flujo
- `onComplete` - Cuando el flujo termina
- `onError` - Cuando ocurre un error

### 4. Consola del Backend

Al ejecutar `./gradlew bootRun`, observa la consola:
```
onSubscribe: buscando habitación 999
switchIfEmpty → Habitación NO EXISTE
🔄 onErrorResume ACTIVADO!
onNext: Retornando habitación por defecto
```

### 5. Operadores Reactivos Implementados

| Operador | Uso en el Proyecto |
|----------|-------------------|
| `Mono<T>` | Búsqueda por ID, operaciones únicas |
| `Flux<T>` | Listados, streaming de datos |
| `map()` | Transformación de datos |
| `filter()` | Filtrar habitaciones disponibles |
| `flatMap()` | Operaciones encadenadas |
| `onErrorResume()` | Recuperación de errores |
| `switchIfEmpty()` | Valor por defecto si no hay datos |
| `delayElements()` | Simular procesamiento lento (streaming) |
| `doOnSubscribe/Next/Complete/Error` | Logging de eventos |

---


## �🏗️ Arquitectura del Sistema

### Diagrama de Arquitectura Reactiva

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND (React)                                │
│                          http://localhost:3000                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ Habitaciones │  │  Huéspedes   │  │   Reservas   │  │    Pagos     │    │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘    │
└────────────────────────────────┬────────────────────────────────────────────┘
                                 │ HTTP/JSON (Axios)
                                 ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    BACKEND REACTIVO (Spring WebFlux)                         │
│                         http://localhost:8085                                │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                    CONTROLADORES REACTIVOS                              │ │
│  │  ┌────────────────────┐  ┌────────────────────┐                        │ │
│  │  │ HabitacionController│  │ HuespedController  │  Retornan:            │ │
│  │  │     Reactivo       │  │     Reactivo       │  • Flux<T> (múltiples)│ │
│  │  └────────────────────┘  └────────────────────┘  • Mono<T> (único)    │ │
│  │  ┌────────────────────┐  ┌────────────────────┐                        │ │
│  │  │ ReservaController  │  │  PagoController    │                        │ │
│  │  │     Reactivo       │  │     Reactivo       │                        │ │
│  │  └────────────────────┘  └────────────────────┘                        │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                         │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                     SERVICIOS REACTIVOS                                 │ │
│  │  • HabitacionServiceReactivo  → Flux/Mono + doOnSubscribe/doOnNext     │ │
│  │  • HuespedServiceReactivo     → Validación reactiva                    │ │
│  │  • ReservaServiceReactivo     → Transacciones reactivas                │ │
│  │  • PagoServiceReactivo        → Manejo de errores con onErrorResume    │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                         │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                 COMPONENTES REACTIVOS                                   │ │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌────────────────────┐   │ │
│  │  │ CustomSubscriber │  │ ReactiveFilters  │  │HotelWebSocketHandler│  │ │
│  │  │  (Backpressure)  │  │ (Operadores Rx)  │  │   (WebSocket RT)   │   │ │
│  │  └──────────────────┘  └──────────────────┘  └────────────────────┘   │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                         │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │              REPOSITORIOS R2DBC (ReactiveCrudRepository)                │ │
│  │  • HabitacionRepository → findByEstado(), findByTipo()                 │ │
│  │  • HuespedRepository    → CRUD reactivo                                │ │
│  │  • ReservaRepository    → findByHuespedId(), findByHabitacionId()      │ │
│  │  • PagoRepository       → CRUD reactivo                                │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────┬────────────────────────────────────────┘
                                     │ R2DBC (Non-blocking)
                                     ▼
                    ┌────────────────────────────────┐
                    │       BASE DE DATOS H2         │
                    │   (In-memory, desarrollo)      │
                    │  Tablas: habitacion, huesped,  │
                    │  reservas, pagos               │
                    └────────────────────────────────┘
```

---

## 🔄 Programación Reactiva Implementada

### Patrón Publicador-Suscriptor

El proyecto implementa los conceptos del laboratorio de programación reactiva:

| Componente | Descripción | Operadores Usados |
|------------|-------------|-------------------|
| **CustomSubscriber** | Control de backpressure con lotes | `onSubscribe`, `onNext`, `onError`, `onComplete`, `request(n)` |
| **ReactiveFilters** | Filtros y transformaciones | `filter`, `map`, `window`, `buffer`, `retry`, `timeout` |
| **Servicios Reactivos** | Lógica de negocio no bloqueante | `doOnSubscribe`, `doOnNext`, `doOnError`, `doOnComplete`, `switchIfEmpty` |

### Ejemplo de Flujo Reactivo

```java
// Servicio Reactivo - HabitacionServiceReactivo.java
public Flux<Habitacion> findAll() {
    return habitacionRepository.findAll()
        .doOnSubscribe(s -> log.debug("Suscripción iniciada"))
        .doOnNext(h -> log.trace("Habitación: {}", h.getId()))
        .doOnError(e -> log.error("Error: {}", e.getMessage()))
        .doOnComplete(() -> log.info("Búsqueda completada"));
}
```

---

## 🛠️ Tecnologías

### Backend (Reactivo)
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 17 | Lenguaje base |
| Spring Boot | 3.5.6 | Framework principal |
| Spring WebFlux | 6.2.11 | Controladores reactivos |
| Spring Data R2DBC | 3.5.4 | Repositorios reactivos |
| Project Reactor | 3.7.11 | Flux, Mono, operadores |
| R2DBC H2 | 1.0.0 | Driver reactivo H2 |
| Gradle | 8.4+ | Gestión de dependencias |

### Frontend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| React | 19.2.0 | UI Framework |
| React Router | 7.9.5 | Navegación SPA |
| Axios | 1.13.2 | Cliente HTTP |

---

## 📁 Estructura del Proyecto

```
Proyecto_Avanzada_Hotel/
├── src/main/java/edu/espe/springlab/
│   ├── SpringLabApplication.java          # Clase principal
│   ├── component/                          # Componentes reactivos
│   │   ├── CustomSubscriber.java          # Backpressure control
│   │   ├── ReactiveFilters.java           # Operadores Rx personalizados
│   │   └── HotelWebSocketHandler.java     # WebSocket tiempo real
│   ├── config/                             # Configuraciones
│   │   ├── CorsConfig.java
│   │   └── OpenApiConfig.java
│   ├── controller/                         # Controladores REACTIVOS
│   │   ├── HabitacionControllerReactivo.java
│   │   ├── HuespedControllerReactivo.java
│   │   ├── ReservaControllerReactivo.java
│   │   └── PagoControllerReactivo.java
│   ├── domain/                             # Entidades
│   │   ├── Habitacion.java
│   │   ├── Huesped.java
│   │   ├── Reserva.java
│   │   └── Pago.java
│   ├── dto/                                # Data Transfer Objects
│   ├── exception/reactive/                 # Excepciones reactivas
│   │   ├── ReactiveResourceNotFoundException.java
│   │   └── ReactiveValidationException.java
│   ├── repository/                         # Repositorios R2DBC
│   │   ├── HabitacionRepository.java       # extends ReactiveCrudRepository
│   │   ├── HuespedRepository.java
│   │   ├── ReservaRepository.java
│   │   └── PagoRepository.java
│   ├── service/reactive/                   # Servicios REACTIVOS
│   │   ├── HabitacionServiceReactivo.java
│   │   ├── HuespedServiceReactivo.java
│   │   ├── ReservaServiceReactivo.java
│   │   └── PagoServiceReactivo.java
│   └── validator/reactive/                 # Validadores reactivos
│       ├── ReactiveHabitacionValidator.java
│       └── ReactiveReservaValidator.java
├── src/main/resources/
│   ├── application.yml                     # Configuración principal
│   └── schema.sql                          # Esquema de BD
├── frontend/                               # Aplicación React
│   ├── src/
│   │   ├── components/                     # Componentes reutilizables
│   │   ├── pages/                          # Páginas principales
│   │   ├── services/api.js                 # Conexión al backend
│   │   └── context/                        # Context API
│   └── package.json
├── build.gradle                            # Dependencias Gradle
└── README.md
```

---

## 🌐 API Endpoints (Reactivos)

Base URL: `http://localhost:8085/api/reactive`

### Habitaciones
| Método | Endpoint | Retorno | Descripción |
|--------|----------|---------|-------------|
| GET | `/habitaciones` | `Flux<Habitacion>` | Listar todas |
| GET | `/habitaciones/{id}` | `Mono<Habitacion>` | Obtener por ID |
| GET | `/habitaciones/disponibles` | `Flux<Habitacion>` | Solo disponibles |
| POST | `/habitaciones` | `Mono<Habitacion>` | Crear nueva |
| PUT | `/habitaciones/{id}` | `Mono<Habitacion>` | Actualizar |
| DELETE | `/habitaciones/{id}` | `Mono<Void>` | Eliminar |

### Huéspedes
| Método | Endpoint | Retorno | Descripción |
|--------|----------|---------|-------------|
| GET | `/huespedes` | `Flux<Huesped>` | Listar todos |
| GET | `/huespedes/{id}` | `Mono<Huesped>` | Obtener por ID |
| POST | `/huespedes` | `Mono<Huesped>` | Crear nuevo |
| PUT | `/huespedes/{id}` | `Mono<Huesped>` | Actualizar |
| DELETE | `/huespedes/{id}` | `Mono<Void>` | Eliminar |

### Reservas
| Método | Endpoint | Retorno | Descripción |
|--------|----------|---------|-------------|
| GET | `/reservas` | `Flux<Reserva>` | Listar todas |
| GET | `/reservas/{id}` | `Mono<Reserva>` | Obtener por ID |
| GET | `/reservas/{id}/detalles` | `Mono<ReservaDetallada>` | Con detalles |
| POST | `/reservas` | `Mono<Reserva>` | Crear nueva |
| POST | `/reservas/completa` | `Mono<Reserva>` | Crear transaccional |
| PUT | `/reservas/{id}` | `Mono<Reserva>` | Actualizar |
| DELETE | `/reservas/{id}` | `Mono<Void>` | Eliminar |

### Pagos
| Método | Endpoint | Retorno | Descripción |
|--------|----------|---------|-------------|
| GET | `/pagos` | `Flux<Pago>` | Listar todos |
| GET | `/pagos/{id}` | `Mono<Pago>` | Obtener por ID |
| POST | `/pagos` | `Mono<Pago>` | Registrar nuevo |
| PUT | `/pagos/{id}` | `Mono<Pago>` | Actualizar |
| DELETE | `/pagos/{id}` | `Mono<Void>` | Eliminar |

---

## 🧪 Testing

```bash
# Ejecutar tests
./gradlew test

# Compilar sin tests
./gradlew build -x test
```

---

## 🎓 Demostración en Clase - Manejo de Errores Reactivo

### Endpoints de Demostración

El proyecto incluye endpoints especiales para demostrar los conceptos de programación reactiva:

```
Base URL: http://localhost:8085/api/demo
```

| Endpoint | Descripción |
|----------|-------------|
| `GET /api/demo` | Lista todos los endpoints de demostración disponibles |
| `GET /api/demo/flujo-sin-recuperacion` | Flujo que **SE DETIENE** cuando encuentra un error |
| `GET /api/demo/flujo-con-recuperacion` | Flujo que **CONTINÚA** usando `onErrorResume` |
| `GET /api/demo/calificaciones` | Sistema de calificaciones con filtro, error y recuperación |
| `GET /api/demo/habitacion/{id}` | Búsqueda con fallback (prueba con id=999) |
| `GET /api/demo/backpressure` | Procesamiento en lotes (backpressure) |

### Cómo Probar

1. **Iniciar el backend** (si no está corriendo):
   ```bash
   ./gradlew bootRun
   ```

2. **Abrir el navegador** y probar los endpoints:

   - **Sin recuperación** (el flujo se detiene en el error):
     ```
     http://localhost:8085/api/demo/flujo-sin-recuperacion
     ```
     Verás: 1, 2, 3, 4 → ERROR (se detiene)

   - **Con recuperación** (el flujo continúa):
     ```
     http://localhost:8085/api/demo/flujo-con-recuperacion
     ```
     Verás: 1, 2, 3, 4 → ERROR → Valores de recuperación → Completa exitosamente

3. **Ver los logs** en la terminal para ver los mensajes de `doOnNext`, `doOnError`, `onErrorResume`

### Código Clave (onErrorResume)

```java
// ANTES del error resume - flujo se detiene
Flux.range(1, 10)
    .map(n -> {
        if (n == 5) throw new RuntimeException("Error!");
        return n;
    })
    .subscribe(System.out::println); // Solo imprime 1, 2, 3, 4

// DESPUÉS con onErrorResume - flujo continúa
Flux.range(1, 10)
    .map(n -> {
        if (n == 5) throw new RuntimeException("Error!");
        return n;
    })
    .onErrorResume(err -> Flux.just(100, 200, 300)) // Valores de recuperación
    .subscribe(System.out::println); // Imprime 1, 2, 3, 4, 100, 200, 300
```

---

## 📚 Referencias del Laboratorio

Este proyecto aplica los conceptos del laboratorio **ModeloPublicadorSuscriptor**:

| Laboratorio | Proyecto Hotel |
|-------------|----------------|
| `CustomSubscribe.java` | `CustomSubscriber.java` - con estadísticas |
| `ErrorHandlingExamples.java` | `onErrorResume()` en servicios |
| `Flux.range()`, `interval()` | Repositorios R2DBC reales |
| `delayElements()` | Operaciones de BD no bloqueantes |

---

