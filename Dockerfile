# Paso 1: Construir la aplicación
FROM maven:3.9.6-eclipse-temurin-21 AS build
# Forzamos que el sistema use UTF-8 para evitar el MalformedInputException
ENV LANG=C.UTF-8
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar la aplicación
FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]