# syntax=docker/dockerfile:1

# --- Сборка: зависимости отдельным слоем, затем mvn package вместе с тестами ---
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B package

# --- Запуск ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/ResourceBooking-1.0.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
