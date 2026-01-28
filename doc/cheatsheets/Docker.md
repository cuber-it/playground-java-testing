# Docker Cheatsheet

## Images

```bash
# Image bauen
docker build -t myapp .
docker build -t myapp:1.0 .
docker build -f Dockerfile.prod -t myapp .

# Images auflisten
docker images
docker images -a     # Alle (auch intermediate)

# Image löschen
docker rmi myapp
docker rmi myapp:1.0
docker image prune   # Unbenutzte löschen
```

## Container

```bash
# Starten
docker run myapp
docker run -d myapp                    # Detached
docker run -p 8080:8080 myapp          # Port mapping
docker run -e "ENV=prod" myapp         # Environment
docker run -v /host:/container myapp   # Volume
docker run --name myapp myapp          # Name vergeben
docker run --rm myapp                  # Nach Stop löschen

# Kombiniert
docker run -d -p 8080:8080 --name app -e "SPRING_PROFILES_ACTIVE=docker" myapp

# Container auflisten
docker ps            # Laufende
docker ps -a         # Alle

# Stoppen / Löschen
docker stop <id>
docker start <id>
docker restart <id>
docker rm <id>
docker rm -f <id>    # Force (auch laufende)

# Alle stoppen / löschen
docker stop $(docker ps -q)
docker rm $(docker ps -aq)
```

## Logs & Debugging

```bash
# Logs anzeigen
docker logs <id>
docker logs -f <id>      # Follow
docker logs --tail 100 <id>

# In Container verbinden
docker exec -it <id> bash
docker exec -it <id> sh

# Prozesse anzeigen
docker top <id>

# Ressourcen
docker stats
docker stats <id>

# Inspect
docker inspect <id>
```

## Docker Compose

```bash
# Starten
docker compose up
docker compose up -d           # Detached
docker compose up --build      # Mit Build

# Stoppen
docker compose down
docker compose down -v         # Mit Volumes

# Status
docker compose ps
docker compose logs
docker compose logs -f service

# Einzelner Service
docker compose up -d service
docker compose restart service
```

## docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    # oder
    image: myapp:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=db
    depends_on:
      - db
    volumes:
      - ./data:/app/data
    networks:
      - mynet

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: mydb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: pass
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - mynet

volumes:
  db-data:

networks:
  mynet:
```

## Dockerfile

```dockerfile
# Multi-Stage Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Volumes

```bash
# Volumes auflisten
docker volume ls

# Volume erstellen
docker volume create mydata

# Volume löschen
docker volume rm mydata
docker volume prune   # Unbenutzte

# Mit Volume starten
docker run -v mydata:/app/data myapp
docker run -v $(pwd)/local:/container myapp
```

## Netzwerk

```bash
# Netzwerke auflisten
docker network ls

# Netzwerk erstellen
docker network create mynet

# Mit Netzwerk starten
docker run --network mynet myapp

# Container verbinden
docker network connect mynet <container>
docker network disconnect mynet <container>
```

## Cleanup

```bash
# Alles aufräumen (Vorsicht!)
docker system prune
docker system prune -a --volumes

# Einzeln
docker container prune   # Gestoppte Container
docker image prune       # Dangling Images
docker image prune -a    # Alle unbenutzten
docker volume prune      # Unbenutzte Volumes
docker network prune     # Unbenutzte Netzwerke

# Speicherverbrauch
docker system df
```

## Registry

```bash
# Login
docker login
docker login registry.example.com

# Push
docker tag myapp registry.example.com/myapp:1.0
docker push registry.example.com/myapp:1.0

# Pull
docker pull registry.example.com/myapp:1.0
```

## Nützliche Befehle

```bash
# Container IP ermitteln
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <id>

# In Container kopieren
docker cp file.txt <id>:/path/
docker cp <id>:/path/file.txt .

# Environment anzeigen
docker exec <id> env

# Health Check
docker inspect --format='{{.State.Health.Status}}' <id>
```

