# Multi-stage Build fuer optimale Image-Groesse

# === Build Stage ===
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Maven Wrapper kopieren
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Dependencies vorab laden (besseres Caching)
RUN ./mvnw dependency:go-offline -B

# Source kopieren und bauen
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# === Runtime Stage ===
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Nicht als root laufen
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# JAR aus Build-Stage kopieren
COPY --from=builder /app/target/*.jar app.jar

# Health-Check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Port freigeben
EXPOSE 8080

# Anwendung starten mit Docker-Profil
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
