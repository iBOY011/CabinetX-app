# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
ARG SERVICE_NAME
WORKDIR /app

# 1) Copy only pom first (maximizes cache hits)
COPY ${SERVICE_NAME}/pom.xml ./pom.xml

# 2) Pre-fetch deps (fast rebuilds, fewer flaky downloads)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp \
      -Dmaven.repo.local=/root/.m2/repository \
      -Dmaven.wagon.http.retryHandler.count=5 \
      -Dmaven.wagon.http.retryHandler.requestSentEnabled=true \
      -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
      -Dmaven.artifact.threads=3 \
      dependency:go-offline

# 3) Now copy sources
COPY ${SERVICE_NAME}/src ./src

# 4) Build (uses cached deps)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp \
      -Dmaven.repo.local=/root/.m2/repository \
      -Dmaven.wagon.http.retryHandler.count=5 \
      -Dmaven.wagon.http.retryHandler.requestSentEnabled=true \
      -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
      -Dmaven.artifact.threads=3 \
      clean package -DskipTests


# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Optional hardening: run as non-root
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
