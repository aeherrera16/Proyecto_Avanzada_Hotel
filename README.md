# 🏨 Sistema de Gestión Hotelera

Sistema de gestión integral para hoteles desarrollado con Spring Boot y React, que permite administrar habitaciones, huéspedes, reservas y pagos de manera eficiente.

## 🚀 Características

- **Gestión de Habitaciones**: CRUD completo para administrar habitaciones del hotel
- **Gestión de Huéspedes**: Registro y administración de información de huéspedes
- **Sistema de Reservas**: Creación y seguimiento de reservas de habitaciones
- **Procesamiento de Pagos**: Registro y control de pagos asociados a reservas
- **Interfaz Moderna**: Frontend desarrollado con React para una experiencia de usuario fluida
- **API RESTful**: Backend robusto con Spring Boot

## 🛠️ Tecnologías

### Backend
- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring Validation**
- **H2 Database** (desarrollo)
- **MySQL/PostgreSQL** (producción)
- **Gradle** (gestión de dependencias)

### Frontend
- **React 18**
- **React Router** (navegación)
- **Axios** (llamadas HTTP)
- **CSS3** (estilos)

### DevOps
- **Docker** (containerización)
- **GitHub Actions** (CI/CD)
- **Render** (deployment)

## 📋 Requisitos Previos

- Java 17 o superior
- Node.js 16 o superior
- Gradle 8.4 o superior
- Docker (opcional)

## 🔧 Instalación y Configuración

### Backend

1. Clonar el repositorio:
```bash
git clone <url-del-repositorio>
cd Proyecto_Avanzada_Hotel
```

2. Compilar el proyecto:
```bash
./gradlew clean build
```

3. Ejecutar la aplicación:
```bash
./gradlew bootRun
```

El backend estará disponible en `http://localhost:8080`

### Frontend

1. Navegar a la carpeta del frontend:
```bash
cd frontend
```

2. Instalar dependencias:
```bash
npm install
```

3. Ejecutar en modo desarrollo:
```bash
npm start
```

El frontend estará disponible en `http://localhost:3000`

## 🐳 Docker

Ejecutar con Docker:

```bash
docker build -t hotel-system .
docker run -p 8080:8080 hotel-system
```

## 🧪 Testing

El proyecto incluye **21 tests unitarios** que verifican las operaciones CRUD de cada entidad:

- **HabitacionRepositoryTest**: 5 tests (Create, Read, Update, Delete, FindAll)
- **HuespedRepositoryTest**: 5 tests (Create, Read, Update, Delete, FindAll)
- **ReservaRepositoryTest**: 5 tests (Create, Read, Update, Delete, FindAll)
- **PagoRepositoryTest**: 5 tests (Create, Read, Update, Delete, FindAll)
- **SpringLabApplicationTests**: 1 test (contextLoads)

Ejecutar tests:
```bash
./gradlew test
```


## 📁 Estructura del Proyecto

```
├── src/
│   ├── main/
│   │   ├── java/edu/espe/springlab/
│   │   │   ├── config/          # Configuraciones
│   │   │   ├── domain/          # Entidades JPA
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── repository/      # Repositorios
│   │   │   ├── service/         # Lógica de negocio
│   │   │   └── web/             # Controladores REST
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-render.yml
│   └── test/
│       └── java/edu/espe/springlab/
│           ├── repository/      # Tests de repositorios
│           └── SpringLabApplicationTests.java
├── frontend/
│   ├── public/
│   └── src/
│       ├── components/
│       ├── pages/
│       ├── services/
│       └── context/
├── Dockerfile
├── build.gradle
└── README.md
```

## 📊 Modelo de Datos

### Entidades Principales

- **Habitacion**: Información de habitaciones del hotel
- **Huesped**: Datos de los huéspedes
- **Reserva**: Reservas de habitaciones
- **Pago**: Pagos asociados a reservas

## 🔄 CI/CD

El proyecto utiliza GitHub Actions para integración y despliegue continuos:

- ✅ Compilación automática
- ✅ Ejecución de tests
- ✅ Despliegue automático en Render

## 🌐 API Endpoints

### Habitaciones
- `GET /api/habitaciones` - Listar todas las habitaciones
- `GET /api/habitaciones/{id}` - Obtener habitación por ID
- `POST /api/habitaciones` - Crear nueva habitación
- `PUT /api/habitaciones/{id}` - Actualizar habitación
- `DELETE /api/habitaciones/{id}` - Eliminar habitación

### Huéspedes
- `GET /api/huespedes` - Listar todos los huéspedes
- `GET /api/huespedes/{id}` - Obtener huésped por ID
- `POST /api/huespedes` - Crear nuevo huésped
- `PUT /api/huespedes/{id}` - Actualizar huésped
- `DELETE /api/huespedes/{id}` - Eliminar huésped

### Reservas
- `GET /api/reservas` - Listar todas las reservas
- `GET /api/reservas/{id}` - Obtener reserva por ID
- `POST /api/reservas` - Crear nueva reserva
- `PUT /api/reservas/{id}` - Actualizar reserva
- `DELETE /api/reservas/{id}` - Eliminar reserva

### Pagos
- `GET /api/pagos` - Listar todos los pagos
- `GET /api/pagos/{id}` - Obtener pago por ID
- `POST /api/pagos` - Registrar nuevo pago
- `PUT /api/pagos/{id}` - Actualizar pago
- `DELETE /api/pagos/{id}` - Eliminar pago

## 👥 Autores

Proyecto desarrollado como parte del curso de Programación Avanzada - ESPE

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la Licencia MIT.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un issue primero para discutir los cambios que te gustaría realizar.

## 📞 Contacto

Para más información sobre este proyecto, por favor contacta al equipo de desarrollo.

---

⭐️ Si este proyecto te fue útil, considera darle una estrella en GitHub

