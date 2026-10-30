# Calculator DevOps — CSE 816 Mini Project

A Spring Boot calculator REST API delivered through a fully automated CI/CD
pipeline: **GitHub → Jenkins → Docker Hub → Docker Compose**.

A push to `main` automatically builds, tests, containerises, publishes and
deploys the application with no manual steps.

**Docker Hub image:** https://hub.docker.com/r/puskara/calculator-devops

## Tech stack

Java 17 · Spring Boot 4.1 · Maven · JUnit 5 + MockMvc · Docker (multi-stage) ·
Docker Compose · Nginx · Jenkins (declarative pipeline)

## API

| Operation | Endpoint | Example |
|---|---|---|
| Add | `GET /api/v1/add?a=&b=` | `/api/v1/add?a=2&b=3` → `5.0` |
| Subtract | `GET /api/v1/subtract?a=&b=` | `/api/v1/subtract?a=5&b=3` → `2.0` |
| Multiply | `GET /api/v1/multiply?a=&b=` | `/api/v1/multiply?a=4&b=5` → `20.0` |
| Divide | `GET /api/v1/divide?a=&b=` | `/api/v1/divide?a=6&b=3` → `2.0` |
| Health | `GET /actuator/health` | `{"status":"UP", ...}` |

**Success response**

```json
{"operation":"add","a":2.0,"b":3.0,"result":5.0}
```

**Error response (HTTP 400)** — returned for division by zero, a missing
parameter, or a non-numeric parameter:

```json
{"timestamp":"2026-10-30T09:51:58Z","status":400,"error":"Bad Request","message":"Division by zero is not allowed"}
```

## Running locally

### Maven

```bash
mvn clean package          # build the executable JAR
mvn test                   # run unit + integration tests
java -jar target/calculator-0.0.1-SNAPSHOT.jar --server.port=8090
```

### Docker

```bash
docker build -t calculator:local .
docker run -p 8081:8080 calculator:local
curl "localhost:8081/api/v1/add?a=2&b=3"
```

### Full stack with Docker Compose

```bash
docker compose up -d
docker compose ps
curl "localhost:8085/api/v1/add?a=7&b=8"
```

| Service | Container | Host port → container port |
|---|---|---|
| calculator | calculator-api | 8081 → 8080 |
| proxy (Nginx) | calculator-proxy | 8085 → 80 |

The proxy starts only after the API passes its `/actuator/health` check.

## CI/CD pipeline

Defined in [`Jenkinsfile`](Jenkinsfile). Triggered by a GitHub webhook on every
push to `main`, with SCM polling as a fallback.

| Stage | What it does |
|---|---|
| Checkout | Pulls the latest commit from GitHub |
| Build | `mvn clean package -DskipTests` — fast compile gate |
| Test | `mvn test`; JUnit results published; build fails on any test failure |
| Docker Build | Multi-stage build, tagged with the build number and `latest` |
| Push | Pushes both tags to Docker Hub using Jenkins-managed credentials |
| Deploy | `docker compose up -d --remove-orphans` with the new image tag |
| Verify Health | Polls `/actuator/health` through Nginx; fails the build if not `UP` |

Email notifications are sent on success and failure.

### Jenkins prerequisites

- Plugins: Docker Pipeline, Email Extension, GitHub Integration, Pipeline Utility Steps
- Credential `dockerhub-creds` (username + Docker Hub access token)
- Credential `gmail-smtp` (Gmail address + app password) used by Extended E-mail Notification
- The `jenkins` user is in the `docker` group
- GitHub webhook → `https://frozen-cheese-singer.ngrok-free.dev/github-webhook/`

No credentials are stored in this repository.

## Repository structure

```
calculator-devops/
├── src/main/java/com/spe/calculator/
│   ├── controller/      # REST endpoints
│   ├── service/         # calculator logic
│   ├── model/           # response records
│   └── exception/       # custom exception + global handler
├── src/test/java/...    # JUnit 5 unit tests + MockMvc integration tests
├── Dockerfile           # multi-stage: Maven build → JRE runtime, non-root
├── .dockerignore
├── docker-compose.yml   # API + Nginx reverse proxy, health checks
├── nginx.conf
├── Jenkinsfile          # declarative CI/CD pipeline
└── pom.xml
```