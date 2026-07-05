## 8. Deployment Architecture

### 8.1 Docker Compose (Development)

services:
  nginx:        (ports: 443)
  backend:      (Ktor, port 8080)
  postgres:     (PostgreSQL 16)
  redis:        (Redis 7 Alpine)
  minio:        (Object Storage)
  prometheus:   (Metrics)
  grafana:      (Dashboards)

### 8.2 Production Layout

              LOAD BALANCER
                   │
      ┌────────────┼────────────┐
      │            │            │
  Backend-1   Backend-2   Backend-3
  (Ktor)      (Ktor)      (Ktor)
      │            │            │
      └────────────┼────────────┘
                   │
         ┌─────────┼─────────┐
         │         │         │
    Primary PG  Replica PG  MinIO Cluster

## 9. Monitoring & Observability

METRICS (Prometheus):
- Request count, duration, error rate
- Database query performance
- Cache hit/miss ratio
- Sync success/failure rate

DASHBOARDS (Grafana):
- Application health
- Business metrics (farms, users)
- Tenant growth

ALERTING:
- CPU > 80% → Warning
- Error rate > 5% → Critical
- DB connections > 80% → Warning
- Sync failures > 10% → Warning
- Disk > 90% → Critical
