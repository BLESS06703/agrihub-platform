# AgriHub Platform - Test Suite

## Unit Tests (80%)
- FarmService, FinanceService, AuthService
- PasswordHasher, JwtService, AuthorizationService
- Repository methods with mock database

## Integration Tests (15%)
- API endpoints with Ktor test engine
- Database migrations with TestContainers
- Tenant isolation verification

## E2E Tests (5%)
- Farmer: register, create farm, plant, harvest
- Buyer: browse, offer, purchase
- Admin: manage tenants and users

## Security Tests
- JWT tampering detection
- Cross-tenant access prevention
- Rate limiting enforcement
- SQL injection resistance

## Performance Targets
- API: p95 < 300ms
- DB query: < 100ms
- 1000 concurrent users
- Offline sync: < 60s for 24hr backlog

Status: Test plan defined. Implementation during Sprint 16 QA phase.
