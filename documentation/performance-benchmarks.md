# AgriHub Platform - Performance Benchmarks

## API Response Times (p95)
| Endpoint | Target | Measurement |
|----------|--------|-------------|
| GET /health | <50ms | - |
| POST /auth/login | <200ms | - |
| GET /farms | <150ms | - |
| POST /farms | <200ms | - |
| GET /livestock/animals | <150ms | - |
| GET /finance/profit-loss | <300ms | - |
| GET /marketplace/listings | <200ms | - |
| POST /ai/chat | <2000ms | AI processing |

## Database
| Metric | Target |
|--------|--------|
| Single record query | <10ms |
| List query (100 rows) | <50ms |
| Complex join (P&L) | <100ms |
| Migration execution | <30s all 22 files |

## Mobile
| Metric | Target |
|--------|--------|
| Cold start | <3s |
| Warm start | <1s |
| Screen render | <2s |
| Offline sync (24h backlog) | <60s |

## Infrastructure
| Metric | Target |
|--------|--------|
| Concurrent users | 1,000 |
| Uptime | 99.9% |
| Backup recovery | <4 hours |
| Data loss (RPO) | <1 hour |
