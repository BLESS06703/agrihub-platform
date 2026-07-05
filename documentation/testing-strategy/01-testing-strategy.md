# AgriHub Platform - Testing Strategy

## Testing Pyramid

        +--------+
        |  E2E   |  5 percent - Critical user journeys
       +----------+
       |Integration| 15 percent - API, DB, external services
      +--------------+
      |    Unit      | 80 percent - Services, repositories
     +------------------+

## Unit Tests (80 percent)

Scope: Individual functions, services, repositories
Framework: JUnit 5 + MockK + Kotest assertions
Coverage Target: 80 percent line coverage

Examples:
- FarmService.createFarm() with valid input returns FarmResponse
- FarmService.createFarm() with duplicate name throws exception
- PasswordHasher.verify() with correct password returns true
- PasswordHasher.verify() with wrong password returns false
- TokenService.generateAccessToken() produces valid JWT
- TokenService.verifyToken() with expired token returns null

## Integration Tests (15 percent)

Scope: API endpoints, database queries, external integrations
Framework: Ktor test engine + TestContainers (real PostgreSQL)

Examples:
- POST /v1/farms returns 201 with valid request body
- POST /v1/farms returns 422 when name is missing
- GET /v1/farms returns only tenant-scoped results
- GET /v1/farms supports pagination (page and per_page)
- Database migrations run without errors
- Redis cache stores and retrieves values correctly

## End-to-End Tests (5 percent)

Scope: Complete user journeys across multiple screens
Framework: Appium (mobile), Playwright (web admin)

Examples:
- Farmer flow: Register, create farm, add field, plant crop, record harvest
- Buyer flow: Browse listings, make offer, accept contract, complete payment
- Offline flow: Create records offline, reconnect, verify sync

## Tenant Isolation Tests (Critical)

Test: Tenant A cannot access Tenant B data

val tenantA = createTenant("Tenant A")
val tenantB = createTenant("Tenant B")
val farmA = createFarm(tenantA, "Farm A")

// Tenant B tries to access Tenant A farm
assertThrows<ForbiddenException> {
    getFarmAsTenant(farmA.id, tenantB)
}

Test: RLS prevents cross-tenant insert
// Verified at database level (see RLS tests)

Test: API returns 404 (not 403) for other tenant resources
// Don't reveal that the resource exists

## Offline Sync Tests

Test: Offline records sync when connectivity restored

// Create records offline
val offlineRecords = createRecordsOffline(5)

// Verify they are in local queue
assertEquals(5, syncQueue.pendingCount())

// Restore connectivity
networkMonitor.setOnline()

// Verify sync completed
eventually {
    assertEquals(0, syncQueue.pendingCount())
}
assertEquals(5, serverFarmCount())

Test: Conflict resolution preserves server data for financial records

Test: Sync queue persists across app restarts

Test: Large sync batch (1000 records) completes within 60 seconds

## Performance Tests

API response time: p95 under 300ms for standard requests
Database query: under 100ms for single record operations
Concurrent users: 1000 with under 20 percent response degradation
Offline sync: 24-hour backlog syncs within 60 seconds
File upload: 10MB file uploads within 30 seconds

## Security Tests

- SQL injection attempts on all endpoints
- XSS payloads in text fields
- JWT token tampering (invalid signature)
- Expired token rejection
- Cross-tenant access attempts
- Rate limiting enforcement
- File upload of executable content
- Password brute force (account lock after 5 failures)

## Test Data Management

- Use TestContainers for real PostgreSQL (never H2 in-memory)
- Seed data per test class with beforeEach
- Never use production data in tests
- Clean up after each test with afterEach
- Use fixed UUIDs for predictable test assertions

## CI/CD Integration

Pipeline stages:
Push to branch
  -> Lint (ktlint, detekt)
  -> Unit Tests (must pass)
  -> Integration Tests (TestContainers)
  -> Coverage Report (minimum 80 percent)
  -> Build Docker Image
  -> Deploy to Staging
  -> Smoke Tests (critical endpoints)
  -> Manual Approval (production)
  -> Deploy to Production
  -> Post-deploy Health Check

## Coverage Requirements

- Line coverage: minimum 80 percent
- Branch coverage: minimum 70 percent
- Fail CI build if coverage drops below threshold
- Exclude from coverage: DTOs, configuration classes, generated code
- Generate reports with JaCoCo

## Bug Regression Tests

For every bug fix:
1. Write failing test that reproduces the bug
2. Apply fix
3. Verify test passes
4. Keep test in suite permanently
