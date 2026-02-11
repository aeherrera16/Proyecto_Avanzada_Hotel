# Etapa 1: Build Frontend (React)
FROM node:20 AS frontend-builder
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Etapa 2: Build Backend (Java)
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
# Copiamos los archivos de React a la carpeta de recursos estáticos de Spring
# Creamos la carpeta por si acaso no existe
RUN mkdir -p src/main/resources/static
COPY --from=frontend-builder /frontend/build/ src/main/resources/static/
RUN ./gradlew bootJar --no-daemon

# Etapa 3: Run - Ejecución
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Usamos un wildcard para copiar el jar generado y renombrarlo a app.jar de forma segura
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=render","-jar","app.jar"]