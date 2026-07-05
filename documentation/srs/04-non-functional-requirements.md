# 4. Non-Functional Requirements (NFR)

## 4.1 Performance Requirements (NFR-001 to NFR-010)

### NFR-001: API Response Time
**Category:** Performance
**Priority:** CRITICAL
**Requirement:** 95% of API requests shall complete within 300ms.
**Measurement:** Average response time measured at application layer.
**Exception:** File uploads, report generation, and bulk operations allowed up to 5 seconds.

### NFR-002: Page Load Time (Mobile)
**Category:** Performance
**Priority:** HIGH
**Requirement:** Mobile screens shall render within 2 seconds on devices with 2GB RAM.
**Measurement:** Time from navigation action to fully interactive screen.
**Target:** Cold start < 3 seconds, warm start < 1 second.

### NFR-003: Page Load Time (Web Admin)
**Category:** Performance
**Priority:** HIGH
**Requirement:** Web admin pages shall load within 3 seconds on 3G connection.
**Measurement:** Time to Interactive (TTI) metric.
**Target:** First Contentful Paint < 1.5 seconds.

### NFR-004: Database Query Performance
**Category:** Performance
**Priority:** CRITICAL
**Requirement:** All database queries shall execute within 100ms for single record operations.
**Measurement:** Query execution time at database level.
**Exception:** Analytical queries and reports allowed up to 5 seconds.

### NFR-005: Concurrent Users
**Category:** Performance
**Priority:** HIGH
**Requirement:** System shall support minimum 1,000 concurrent users.
**Measurement:** Load testing with simulated concurrent sessions.
**Target:** Response time degradation < 20% at peak load.

### NFR-006: Throughput
**Category:** Performance
**Priority:** HIGH
**Requirement:** API shall handle minimum 500 requests per second.
**Measurement:** Requests per second sustained over 5-minute period.
**Target:** Zero failed requests under sustained load.

### NFR-007: Offline Sync Speed
**Category:** Performance
**Priority:** HIGH
**Requirement:** Full sync after 24 hours offline shall complete within 60 seconds.
**Measurement:** Time from connectivity restored to sync completion.
**Target:** Incremental sync (1 hour offline) < 10 seconds.

### NFR-008: Image Loading
**Category:** Performance
**Priority:** MEDIUM
**Requirement:** Images shall load progressively with thumbnails first.
**Measurement:** Time to display thumbnail < 500ms, full image < 3 seconds.
**Implementation:** Lazy loading, compression, CDN caching.

### NFR-009: Search Response
**Category:** Performance
**Priority:** HIGH
**Requirement:** Search results shall return within 500ms.
**Measurement:** Time from query submission to results display.
**Implementation:** Full-text search indexes, result caching.

### NFR-010: Report Generation
**Category:** Performance
**Priority:** MEDIUM
**Requirement:** Standard reports shall generate within 10 seconds.
**Measurement:** Time from report request to downloadable file.
**Exception:** Large reports (>10,000 rows) processed asynchronously.

## 4.2 Security Requirements (NFR-011 to NFR-025)

### NFR-011: Authentication Security
**Category:** Security
**Priority:** CRITICAL
**Requirement:** All authentication shall use JWT with RS256 signing.
**Measurement:** Token validation on every request.
**Implementation:** Access tokens 15-minute TTL, refresh tokens 7-day TTL with rotation.

### NFR-012: Password Policy
**Category:** Security
**Priority:** CRITICAL
**Requirement:** Passwords shall meet minimum complexity requirements.
**Specification:**
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 number
- At least 1 special character
- Password history: cannot reuse last 3 passwords
- Password expiry: 90 days (configurable)

### NFR-013: Password Hashing
**Category:** Security
**Priority:** CRITICAL
**Requirement:** All passwords shall be hashed using Argon2id.
**Parameters:** Memory: 64MB, iterations: 3, parallelism: 4.
**Measurement:** Audit of password storage confirms no plaintext or weak hashes.

### NFR-014: Data Encryption at Rest
**Category:** Security
**Priority:** CRITICAL
**Requirement:** Sensitive data shall be encrypted at rest using AES-256.
**Scope:** PII, financial data, authentication tokens, tenant secrets.
**Implementation:** PostgreSQL pgcrypto or application-level encryption.

### NFR-015: Data Encryption in Transit
**Category:** Security
**Priority:** CRITICAL
**Requirement:** All data in transit shall be encrypted using TLS 1.3.
**Scope:** Client-to-server, server-to-server, server-to-database.
**Measurement:** TLS certificate validity and protocol version monitoring.

### NFR-016: Tenant Data Isolation
**Category:** Security
**Priority:** CRITICAL
**Requirement:** No tenant shall access another tenant's data.
**Measurement:** Penetration testing confirms isolation.
**Implementation:** Row-Level Security, schema isolation, connection routing.

### NFR-017: API Rate Limiting
**Category:** Security
**Priority:** HIGH
**Requirement:** API shall enforce rate limits per user and per IP.
**Specification:**
- Per user: 100 requests per minute
- Per IP: 300 requests per minute
- Burst allowance: 150% for 30 seconds
**Response:** 429 Too Many Requests with Retry-After header.

### NFR-018: Input Validation
**Category:** Security
**Priority:** CRITICAL
**Requirement:** All user input shall be validated and sanitized.
**Protection against:** SQL injection, XSS, CSRF, command injection, path traversal.
**Implementation:** Server-side validation, parameterized queries, content security policy.

### NFR-019: Session Management
**Category:** Security
**Priority:** HIGH
**Requirement:** Secure session handling.
**Specification:**
- JWT tokens stored in HTTP-only cookies (web) or Android Keystore (mobile)
- Token invalidation on logout
- Token invalidation on password change
- Concurrent session limit: 5 per user

### NFR-020: Audit Logging
**Category:** Security
**Priority:** HIGH
**Requirement:** All security-relevant events shall be logged.
**Events logged:** Login, logout, failed login, password change, permission change, data export, tenant config change.
**Log contents:** Timestamp, user_id, tenant_id, action, IP address, user agent, result.

### NFR-021: Vulnerability Management
**Category:** Security
**Priority:** HIGH
**Requirement:** Regular security assessment and patching.
**Specification:**
- Dependency scanning: weekly automated scans
- OWASP Top 10 compliance
- Penetration testing: bi-annual
- Critical patches deployed within 48 hours

### NFR-022: File Upload Security
**Category:** Security
**Priority:** HIGH
**Requirement:** All file uploads shall be validated and scanned.
**Specification:**
- Allowed types: JPEG, PNG, PDF, CSV, XLSX
- Maximum file size: 10MB
- Malware scanning before storage
- Randomised filenames on storage
- No executable files permitted

### NFR-023: Backup Encryption
**Category:** Security
**Priority:** HIGH
**Requirement:** All backups shall be encrypted.
**Specification:** AES-256 encryption, secure key management, encrypted in transit.
**Measurement:** Restore test verifies encryption.

### NFR-024: API Key Management
**Category:** Security
**Priority:** MEDIUM
**Requirement:** API keys for programmatic access shall be securely managed.
**Specification:**
- Keys generated with sufficient entropy (256-bit)
- Hashed storage (never stored in plaintext)
- Key rotation support
- Scope-limited keys (read-only, specific resources)

### NFR-025: GDPR/Data Protection Readiness
**Category:** Security
**Priority:** MEDIUM (for future expansion)
**Requirement:** Architecture shall support data subject rights.
**Specification:**
- Data export capability (user data download)
- Data deletion capability (right to be forgotten)
- Consent management framework
- Data processing records

## 4.3 Reliability Requirements (NFR-026 to NFR-035)

### NFR-026: Service Availability
**Category:** Reliability
**Priority:** CRITICAL
**Requirement:** System shall maintain 99.9% uptime (8.76 hours downtime/year).
**Measurement:** Uptime monitoring with 1-minute check intervals.
**Exclusions:** Planned maintenance (announced 48 hours in advance, off-peak).

### NFR-027: Disaster Recovery
**Category:** Reliability
**Priority:** HIGH
**Requirement:** Recovery Time Objective (RTO) of 4 hours.
**Specification:** Recovery Point Objective (RPO) of 1 hour (max data loss).
**Implementation:** Automated failover, database replication, regular backup testing.

### NFR-028: Backup Schedule
**Category:** Reliability
**Priority:** CRITICAL
**Requirement:** Automated backups with defined schedule.
**Specification:**
- Full backup: Daily at 02:00 CAT
- Incremental backup: Every 6 hours
- Transaction log backup: Every 15 minutes
- Retention: 30 days local, 90 days offsite

### NFR-029: Graceful Degradation
**Category:** Reliability
**Priority:** HIGH
**Requirement:** System shall degrade gracefully during partial failures.
**Specification:**
- Database down: Read-only mode from cache
- Storage down: Uploads queued
- External API down: Stale data with warning
- No cascading failures

### NFR-030: Error Handling
**Category:** Reliability
**Priority:** HIGH
**Requirement:** All errors shall be handled gracefully.
**Specification:**
- No stack traces exposed to users
- Consistent error response format
- User-friendly error messages
- Error logging with correlation IDs
- Retry logic for transient failures

### NFR-031: Data Integrity
**Category:** Reliability
**Priority:** CRITICAL
**Requirement:** Data shall maintain integrity across all operations.
**Specification:**
- ACID compliance for financial transactions
- Referential integrity enforced at database level
- Checksum verification for critical data
- No data loss during sync operations

### NFR-032: Offline Reliability
**Category:** Reliability
**Priority:** HIGH
**Requirement:** Offline mode shall be reliable.
**Specification:**
- Local data persistence across app restarts
- Sync queue persistence across app restarts
- Sync success rate > 95%
- Conflict resolution without data loss
- Offline mode degradation notice

### NFR-033: Load Handling
**Category:** Reliability
**Priority:** HIGH
**Requirement:** System shall handle peak loads without failure.
**Specification:**
- Auto-scaling triggers at 70% CPU utilization
- Connection pooling with maximum 100 connections per instance
- Queue-based processing for heavy operations
- Circuit breaker pattern for external services

### NFR-034: Health Monitoring
**Category:** Reliability
**Priority:** HIGH
**Requirement:** Comprehensive system health monitoring.
**Specification:**
- Health check endpoint: /health
- Metrics exported to Prometheus
- Alert thresholds defined for: CPU > 80%, memory > 85%, disk > 90%, error rate > 5%
- Alerting via email and on-call notification

### NFR-035: Zero-Downtime Deployments
**Category:** Reliability
**Priority:** MEDIUM
**Requirement:** Deployments shall not cause service interruption.
**Specification:**
- Blue-green deployment strategy
- Database migrations backward-compatible
- Rolling updates with health checks
- Automated rollback on deployment failure

## 4.4 Scalability Requirements (NFR-036 to NFR-045)

### NFR-036: Horizontal Scaling
**Category:** Scalability
**Priority:** HIGH
**Requirement:** Application tier shall scale horizontally.
**Specification:** Stateless application servers, shared nothing architecture.
**Target:** Support 10x current load by adding instances.

### NFR-037: Database Scaling
**Category:** Scalability
**Priority:** HIGH
**Requirement:** Database shall support scaling strategies.
**Specification:**
- Read replicas for reporting queries
- Connection pooling with PgBouncer
- Table partitioning for large tables (audit_logs, sync_queue)
- Sharding readiness for tenant data

### NFR-038: Storage Scaling
**Category:** Scalability
**Priority:** MEDIUM
**Requirement:** Object storage shall scale independently.
**Specification:** MinIO distributed mode for horizontal scaling.
**Target:** Support up to 100TB of stored data.

### NFR-039: Tenant Growth
**Category:** Scalability
**Priority:** HIGH
**Requirement:** Platform shall support tenant growth targets.
**Targets:**
- Year 1: 10,000 tenants
- Year 3: 100,000 tenants
- Year 5: 1,000,000 tenants
**Implementation:** Multi-tier multi-tenancy with tier migration path.

### NFR-040: Data Volume
**Category:** Scalability
**Priority:** MEDIUM
**Requirement:** System shall handle expected data volumes.
**Targets:**
- 1 million farm records
- 10 million harvest records
- 100 million sync events
- 1TB total database size (Year 3)

### NFR-041: Async Processing
**Category:** Scalability
**Priority:** HIGH
**Requirement:** Heavy operations shall be processed asynchronously.
**Scope:** Report generation, bulk imports, image processing, notifications, sync batch processing.
**Implementation:** Job queue with workers, status tracking, retry with backoff.

### NFR-042: Caching Strategy
**Category:** Scalability
**Priority:** HIGH
**Requirement:** Multi-level caching for performance.
**Levels:**
- L1: In-memory (Caffeine) - frequent lookups
- L2: Redis - shared cache, sessions, rate limiting
- L3: Database query result cache
**Cache invalidation:** TTL-based with explicit invalidation on writes.

### NFR-043: CDN for Static Assets
**Category:** Scalability
**Priority:** MEDIUM
**Requirement:** Static assets served via CDN.
**Scope:** Images, documents, mobile app assets, web admin static files.
**Implementation:** MinIO + Nginx caching or Cloud CDN.

### NFR-044: API Versioning Support
**Category:** Scalability
**Priority:** HIGH
**Requirement:** Support multiple API versions concurrently.
**Specification:** URL-based versioning (/v1/, /v2/).
**Migration:** 6-month deprecation window with Sunset headers.

### NFR-045: Database Connection Management
**Category:** Scalability
**Priority:** HIGH
**Requirement:** Efficient database connection utilization.
**Specification:**
- Connection pool: 20-100 connections per application instance
- PgBouncer for transaction pooling
- Timeout: idle connections 10 minutes
- Max lifetime: 30 minutes

## 4.5 Usability Requirements (NFR-046 to NFR-055)

### NFR-046: Mobile-First Design
**Category:** Usability
**Priority:** CRITICAL
**Requirement:** Primary user experience optimized for mobile devices.
**Specification:**
- Touch targets minimum 48x48dp
- Gesture-based navigation
- Responsive layouts
- Bottom navigation for key actions
- Thumb-zone optimization

### NFR-047: Offline-First UX
**Category:** Usability
**Priority:** HIGH
**Requirement:** App shall be fully usable offline.
**Specification:**
- Clear offline indicator
- Offline actions queued visually
- Sync status visible
- No features hidden when offline
- Graceful degradation notification

### NFR-048: Multi-Language
**Category:** Usability
**Priority:** HIGH
**Requirement:** Support English and Chichewa.
**Specification:**
- Language detection from device settings
- Manual language toggle
- All UI text translated (no hardcoded strings)
- Error messages translated
- Date/number/currency formats localized

### NFR-049: Accessibility
**Category:** Usability
**Priority:** MEDIUM
**Requirement:** Meet WCAG 2.1 Level AA standards.
**Specification:**
- Screen reader support (TalkBack)
- Sufficient color contrast (4.5:1 minimum)
- Text scaling support (up to 200%)
- Keyboard navigation (web admin)

### NFR-050: Onboarding Experience
**Category:** Usability
**Priority:** HIGH
**Requirement:** New users shall understand platform within 5 minutes.
**Specification:**
- Interactive tutorial on first launch
- Contextual tooltips
- Sample data for exploration
- Quick start guide
- Skip option for experienced users

### NFR-051: Error Messages
**Category:** Usability
**Priority:** HIGH
**Requirement:** Error messages shall be clear and actionable.
**Specification:**
- Plain language (no technical jargon)
- What went wrong
- Why it happened
- How to fix it
- Support contact for unresolved issues

### NFR-052: Form Design
**Category:** Usability
**Priority:** MEDIUM
**Requirement:** Forms shall be simple and efficient.
**Specification:**
- Progressive disclosure (show relevant fields only)
- Inline validation with real-time feedback
- Auto-save drafts
- Smart defaults where possible
- Minimum required fields

### NFR-053: Loading States
**Category:** Usability
**Priority:** MEDIUM
**Requirement:** Clear loading indicators for all async operations.
**Specification:**
- Skeleton screens for page loads
- Progress bars for uploads
- Spinners for quick actions
- Estimated time for long operations
- Background operations with subtle indicators

### NFR-054: Search Usability
**Category:** Usability
**Priority:** MEDIUM
**Requirement:** Search shall be intuitive and forgiving.
**Specification:**
- Fuzzy matching for typos
- Recent searches saved
- Search suggestions
- No results: helpful suggestions
- Barcode/QR scanning option

### NFR-055: Consistent Design Language
**Category:** Usability
**Priority:** HIGH
**Requirement:** Consistent visual design across platform.
**Specification:**
- Design system with reusable components
- Consistent color palette
- Consistent typography
- Consistent iconography
- Platform-specific adaptations (Material Design for Android)

## 4.6 Maintainability Requirements (NFR-056 to NFR-065)

### NFR-056: Code Quality Standards
**Category:** Maintainability
**Priority:** HIGH
**Requirement:** Code shall meet defined quality standards.
**Specification:**
- Kotlin coding conventions
- Static analysis: Detekt (backend), ktlint (formatting)
- Code coverage minimum: 80%
- No commented-out code in production
- Meaningful variable and function names

### NFR-057: Documentation
**Category:** Maintainability
**Priority:** HIGH
**Requirement:** Comprehensive documentation maintained.
**Specification:**
- API documentation: OpenAPI 3.0 (Swagger)
- Database schema documentation
- Architecture decision records (ADRs)
- Setup and deployment guide
- Code comments for complex logic

### NFR-058: Logging Standards
**Category:** Maintainability
**Priority:** HIGH
**Requirement:** Structured logging throughout application.
**Specification:**
- JSON log format for machine parsing
- Log levels: DEBUG, INFO, WARN, ERROR, FATAL
- Correlation ID for request tracing
- No PII in logs
- Log retention: 30 days

### NFR-059: Testing Requirements
**Category:** Maintainability
**Priority:** CRITICAL
**Requirement:** Comprehensive test coverage.
**Specification:**
- Unit tests: 80% minimum coverage
- Integration tests: All API endpoints
- UI tests: Critical user journeys
- Performance tests: Before major releases
- Security tests: Before every release

### NFR-060: CI/CD Pipeline
**Category:** Maintainability
**Priority:** HIGH
**Requirement:** Automated build, test, and deployment pipeline.
**Specification:**
- Build triggered on push to main/develop
- Automated tests run on every build
- Failed tests block merge
- Automated deployment to staging
- Manual approval for production

### NFR-061: Dependency Management
**Category:** Maintainability
**Priority:** MEDIUM
**Requirement:** Dependencies shall be managed and updated.
**Specification:**
- Version pinning (no wildcard versions)
- Automated vulnerability scanning
- Quarterly dependency review
- License compliance check
- Deprecated dependency replacement plan

### NFR-062: Configuration Management
**Category:** Maintainability
**Priority:** HIGH
**Requirement:** Configuration externalized from code.
**Specification:**
- Environment variables for all config
- Secrets never in code or version control
- Config validation on startup
- Feature flags for gradual rollout
- Per-tenant configuration support

### NFR-063: Monitoring & Observability
**Category:** Maintainability
**Priority:** HIGH
**Requirement:** Comprehensive system observability.
**Specification:**
- Metrics: Prometheus + Grafana dashboards
- Logging: Structured logs to stdout/stderr
- Tracing: Request tracing with correlation IDs
- Alerting: Defined SLOs with alert thresholds
- Health dashboard for operations team

### NFR-064: Database Migration Management
**Category:** Maintainability
**Priority:** HIGH
**Requirement:** Database changes managed through migrations.
**Specification:**
- Version-controlled migration scripts
- Forward and rollback scripts
- Migration testing in CI
- Zero-downtime migrations where possible
- Migration history tracked in database

### NFR-065: Technical Debt Management
**Category:** Maintainability
**Priority:** MEDIUM
**Requirement:** Technical debt tracked and managed.
**Specification:**
- TODO/FIXME comments linked to issues
- Technical debt backlog maintained
- 20% of sprint capacity for debt reduction
- Refactoring as continuous activity
- Architecture review every 6 months

## 4.7 Compatibility Requirements (NFR-066 to NFR-072)

### NFR-066: Android Compatibility
**Category:** Compatibility
**Priority:** CRITICAL
**Requirement:** Support specified Android versions.
**Specification:**
- Minimum: Android 8.0 (API 26)
- Target: Android 14 (API 34)
- Support for low-end devices (2GB RAM)
- Adaptive layouts for different screen sizes
- Play Store and direct APK distribution

### NFR-067: Browser Compatibility (Web Admin)
**Category:** Compatibility
**Priority:** HIGH
**Requirement:** Support modern browsers.
**Specification:**
- Chrome: Last 2 versions
- Firefox: Last 2 versions
- Edge: Last 2 versions
- Safari: Last 2 versions
- Minimum screen width: 1024px

### NFR-068: Database Compatibility
**Category:** Compatibility
**Priority:** CRITICAL
**Requirement:** PostgreSQL 16+ compatibility.
**Specification:**
- Use of PostgreSQL-specific features documented
- Migration path for major version upgrades
- Extension usage: PostGIS, pgcrypto, uuid-ossp

### NFR-069: API Compatibility
**Category:** Compatibility
**Priority:** HIGH
**Requirement:** Backward-compatible API changes.
**Specification:**
- Additive changes only within a version
- Breaking changes only in new API version
- Deprecation notices 6 months ahead
- Old versions maintained during deprecation

### NFR-070: Third-Party Integration Compatibility
**Category:** Compatibility
**Priority:** MEDIUM
**Requirement:** Integration interfaces shall be stable.
**Specification:**
- Webhook payloads versioned
- API key authentication for external access
- Rate limits documented
- Integration sandbox for testing

### NFR-071: Import/Export Compatibility
**Category:** Compatibility
**Priority:** MEDIUM
**Requirement:** Support standard data interchange formats.
**Specification:**
- Import formats: CSV, JSON, Excel (.xlsx)
- Export formats: CSV, PDF, Excel (.xlsx)
- Encoding: UTF-8
- Date format: ISO 8601

### NFR-072: Network Compatibility
**Category:** Compatibility
**Priority:** HIGH
**Requirement:** Function on limited connectivity.
**Specification:**
- 2G/3G/4G/5G/WiFi support
- Bandwidth-aware image quality
- Request compression (gzip)
- Timeout handling for slow connections
- Works through typical mobile network firewalls

## 4.8 Compliance Requirements (NFR-073 to NFR-078)

### NFR-073: Data Protection (Malawi)
**Category:** Compliance
**Priority:** HIGH
**Requirement:** Comply with Malawi data protection regulations.
**Specification:**
- Personal data inventory maintained
- Consent management for data collection
- Data subject access request process
- Data breach notification procedure
- Cross-border data transfer controls

### NFR-074: Agricultural Standards
**Category:** Compliance
**Priority:** MEDIUM
**Requirement:** Align with Malawi agricultural standards.
**Specification:**
- Malawi Bureau of Standards produce grading
- Warehouse Receipt Act compliance
- FISP (Farm Input Subsidy Programme) compatibility
- Ministry of Agriculture reporting formats

### NFR-075: Financial Compliance
**Category:** Compliance
**Priority:** HIGH
**Requirement:** Support financial regulatory requirements.
**Specification:**
- Audit trail immutability
- Financial data retention: 7 years
- Transaction sequencing
- No deletion of financial records (soft delete)
- Reconciliation support

### NFR-076: Mobile Money Compliance
**Category:** Compliance
**Priority:** MEDIUM
**Requirement:** Align with mobile money regulations.
**Specification:**
- Reserve Bank of Malawi guidelines
- Airtel Money API compliance
- TNM Mpamba API compliance
- Transaction limits respected
- KYC requirements supported

### NFR-077: Accessibility Compliance
**Category:** Compliance
**Priority:** LOW
**Requirement:** Meet accessibility standards.
**Specification:** WCAG 2.1 Level AA, Malawi disability inclusion guidelines.

### NFR-078: Software License Compliance
**Category:** Compliance
**Priority:** HIGH
**Requirement:** All dependencies shall have compatible licenses.
**Specification:**
- License audit before release
- No GPL dependencies in mobile app
- Attribution for required licenses
- License file in repository

