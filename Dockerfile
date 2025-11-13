#Etapa 1: Build - Construcción
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar

#Etapa 2: Run - Ejecución
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=render","-jar","app.jar"]

