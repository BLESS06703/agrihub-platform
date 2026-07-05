# AgriHub Malawi - DevOps Handbook
# Version 1.0

## 1. Docker Configuration

### Backend Dockerfile
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S agrihub && adduser -S agrihub -G agrihub
WORKDIR /app
COPY build/libs/agrihub-backend.jar app.jar
USER agrihub
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

### Docker Compose (Development)
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: agrihub
      POSTGRES_USER: agrihub
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "5432:5432"
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: agrihub
      MINIO_ROOT_PASSWORD: ${MINIO_PASSWORD}
    volumes:
      - minio_data:/data
    ports:
      - "9000:9000"
      - "9001:9001"
  
  backend:
    build: ./backend
    environment:
      DATABASE_URL: jdbc:postgresql://postgres:5432/agrihub
      REDIS_URL: redis://redis:6379
      MINIO_URL: http://minio:9000
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
      - minio

## 2. CI/CD Pipeline (GitHub Actions)

### Pipeline Stages

1. Lint & Check
   - ktlint (formatting)
   - detekt (static analysis)
   - Dependency vulnerability scan

2. Test
   - Unit tests
   - Integration tests (TestContainers for PostgreSQL)
   - Coverage report (minimum 80%)

3. Build
   - Compile backend JAR
   - Build Docker image
   - Tag with git SHA and version

4. Deploy Staging
   - Push to staging registry
   - Deploy to staging server
   - Run smoke tests

5. Deploy Production
   - Manual approval required
   - Blue-green deployment
   - Health check verification
   - Auto-rollback on failure

### Workflow File (.github/workflows/ci.yml)

name: CI/CD Pipeline
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew ktlintCheck detekt

  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_DB: agrihub_test
          POSTGRES_PASSWORD: test
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew test jacocoTestReport

  build:
    needs: [lint, test]
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew shadowJar
      - uses: docker/build-push-action@v5
        with:
          tags: agrihub/backend:${{ github.sha }}

  deploy-staging:
    needs: build
    if: github.ref == 'refs/heads/develop'
    runs-on: ubuntu-latest
    steps:
      - run: deploy-to-staging.sh

  deploy-production:
    needs: build
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: production
    steps:
      - run: deploy-to-production.sh

## 3. Environment Management

| Environment | Purpose | Data |
|-------------|---------|------|
| Development | Local dev | Mock/test data |
| Staging | Pre-production testing | Anonymized production subset |
| Production | Live system | Real user data |

Configuration via environment variables (never in code):
- DATABASE_URL
- REDIS_URL
- MINIO_URL
- JWT_PRIVATE_KEY
- JWT_PUBLIC_KEY
- SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASSWORD

## 4. Backup Strategy

Database:
- Full: Daily at 02:00 CAT
- Incremental (WAL): Every 15 minutes
- Retention: 30 days local, 90 days offsite

Object Storage (MinIO):
- Mirror to secondary MinIO instance
- Retention: 90 days

Backup Verification:
- Automated restore test weekly on staging
- Alert if backup fails

## 5. Disaster Recovery

RTO (Recovery Time Objective): 4 hours
RPO (Recovery Point Objective): 1 hour

Recovery Steps:
1. Provision new infrastructure (automated via Terraform)
2. Restore latest database backup
3. Apply WAL logs to point-in-time
4. Restore object storage
5. Update DNS to new infrastructure
6. Verify health checks
7. Communicate to users

## 6. Monitoring Stack

Prometheus metrics:
- HTTP request duration (histogram)
- HTTP request count (counter)
- Error rate (counter)
- Database query duration
- Cache hit/miss ratio
- JVM metrics (heap, GC, threads)
- Sync success/failure rate

Grafana dashboards:
- Application Overview: requests, errors, latency
- Database: connections, query performance, locks
- Business: active users, listings, transactions
- Sync: success rate, queue depth, conflicts

Alerting:
- API error rate > 5%: Critical
- P95 latency > 1s: Warning
- Database connections > 80%: Warning
- Disk usage > 85%: Warning
- Disk usage > 95%: Critical
- Sync failures > 10%: Warning

## 7. Logging

EFK Stack (Elasticsearch, Fluentd, Kibana) or simple:
- stdout/stderr → Docker logs → log aggregation

Log format: JSON
{
  "timestamp": "2026-07-04T12:00:00Z",
  "level": "INFO",
  "logger": "com.agrihub.farm.FarmService",
  "message": "Farm created",
  "correlation_id": "uuid",
  "tenant_id": "uuid",
  "user_id": "uuid"
}
