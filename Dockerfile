# Paso 1: Construir la aplicación usando Maven con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar la aplicación con Java 21
FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]