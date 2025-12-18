# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
ARG SERVICE_NAME
WORKDIR /app
COPY ${SERVICE_NAME}/pom.xml .
COPY ${SERVICE_NAME}/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]