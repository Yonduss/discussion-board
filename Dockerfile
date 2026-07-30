# Stage 1: Build Spring Boot application
FROM gradle:8-jdk21 AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean bootJar --no-daemon

# Stage 2: Run Spring Boot application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]