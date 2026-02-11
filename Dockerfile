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
COPY --from=frontend-builder /frontend/build /app/src/main/resources/static
RUN gradle clean bootJar

# Etapa 3: Run
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=render","-jar","app.jar"]