# 3. Functional Requirements

## 3.1 Platform Core (FR-001 to FR-020)

### FR-001: User Registration
**Priority:** CRITICAL
**Description:** New users can create an account with email and password.
**Acceptance Criteria:**
- User submits: full_name, email, phone_number, password
- Password must be minimum 8 characters with at least 1 uppercase, 1 number
- Email verification sent within 60 seconds
- Account inactive until email verified
- Duplicate email returns clear error message

### FR-002: User Login
**Priority:** CRITICAL
**Description:** Registered users can authenticate and receive tokens.
**Acceptance Criteria:**
- User submits email + password
- Invalid credentials return 401 with generic message
- Successful login returns access_token and refresh_token
- Access token expires in 15 minutes
- Account locks after 5 failed attempts for 30 minutes

### FR-003: Token Refresh
**Priority:** CRITICAL
**Description:** Users can obtain new access token using refresh token.
**Acceptance Criteria:**
- Valid refresh token returns new access token
- Expired refresh token requires full re-login
- Refresh token rotation implemented (old token invalidated)
- Max refresh token lifetime: 7 days

### FR-004: Password Reset
**Priority:** HIGH
**Description:** Users can reset forgotten password via email.
**Acceptance Criteria:**
- Reset link sent to registered email
- Reset token expires in 1 hour
- New password must differ from last 3 passwords
- Old tokens invalidated after successful reset

### FR-005: Multi-Factor Authentication (Future)
**Priority:** LOW (V2.0)
**Description:** Optional TOTP-based second factor.
**Acceptance Criteria:**
- User can enable/disable MFA
- QR code setup for authenticator apps
- Backup recovery codes provided (8 codes)

### FR-006: Tenant Registration
**Priority:** CRITICAL
**Description:** Organizations register as tenants with isolated workspace.
**Acceptance Criteria:**
- Required fields: tenant_name, tenant_type, admin_email, phone
- Tenant types: FARMER, COOPERATIVE, AGRIBUSINESS, NGO, GOVERNMENT, MICROFINANCE, INSURANCE, EQUIPMENT_RENTAL, BUYER, WAREHOUSE
- Admin user auto-created as TENANT_OWNER
- Default roles and permissions auto-provisioned
- Tenant status: PENDING -> ACTIVE after admin approval

### FR-007: Tenant Configuration
**Priority:** HIGH
**Description:** Tenant owners can configure organization settings.
**Acceptance Criteria:**
- Organization name and logo upload
- Default language selection (English, Chichewa)
- Default currency (MWK)
- Default measurement units (Metric)
- Timezone setting (Africa/Blantyre)
- Notification preferences

### FR-008: User Invitation
**Priority:** HIGH
**Description:** Tenant admins invite users to join their organization.
**Acceptance Criteria:**
- Invite by email with role assignment
- Invitation link expires in 7 days
- Invited user creates own password
- Invitation status tracked: PENDING, ACCEPTED, EXPIRED, REVOKED
- Max users per tenant configurable by tier

### FR-009: Role Management
**Priority:** CRITICAL
**Description:** Tenant owners manage roles and assign to users.
**Acceptance Criteria:**
- Create custom roles with selected permissions
- Assign multiple roles to one user
- Remove roles from users
- Cannot delete predefined system roles
- Role changes take effect on next token refresh

### FR-010: Permission System
**Priority:** CRITICAL
**Description:** Granular permission control using resource:action:scope pattern.
**Acceptance Criteria:**
- Permissions follow format: resource:action:scope
- Resources: farm, field, crop, harvest, livestock, finance, inventory, market, report, user, tenant
- Actions: create, read, update, delete, approve, export, manage
- Scopes: own, tenant, all
- Permission checks on every API request
- 403 returned for insufficient permissions

### FR-011: Audit Logging
**Priority:** HIGH
**Description:** Track all critical actions for security and compliance.
**Acceptance Criteria:**
- Logged actions: CREATE, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT
- Logged data: user_id, tenant_id, action, table, record_id, timestamp, IP
- Old and new values stored for UPDATE operations
- Audit logs immutable (no update or delete)
- Retention period: minimum 1 year
- Exportable to CSV for compliance

### FR-012: Notification System
**Priority:** HIGH
**Description:** In-app and push notifications for users.
**Acceptance Criteria:**
- Notification types: SYSTEM, ALERT, REMINDER, MARKET, SOCIAL
- Delivery channels: IN_APP, PUSH, EMAIL, SMS (future)
- User notification preferences per channel
- Mark as read / Mark all as read
- Notification history with pagination
- Real-time push via WebSocket or FCM

### FR-013: Dashboard
**Priority:** HIGH
**Description:** Role-based dashboard with relevant metrics.
**Acceptance Criteria:**
- Farmer: active crops, upcoming tasks, recent harvests, weather
- Manager: farm productivity, expenses vs budget, team activity
- Buyer: available produce, pending orders, price trends
- Admin: tenant stats, system health, user activity
- Dashboard data refreshes on pull-to-refresh
- Configurable widgets (future)

### FR-014: Search
**Priority:** MEDIUM
**Description:** Global search across authorized tenant data.
**Acceptance Criteria:**
- Search across: farms, fields, crops, livestock, inventory
- Full-text search with relevance ranking
- Filters by type and status
- Results paginated (20 per page)
- Search scoped to user's tenant only
- Recent searches saved locally

### FR-015: Data Export
**Priority:** MEDIUM
**Description:** Export data in common formats.
**Acceptance Criteria:**
- Export formats: CSV, PDF, Excel
- Export types: farm records, financial reports, inventory
- Date range filter on exports
- Large exports (>1000 rows) processed async with email notification
- Export history with download links (valid 24 hours)

### FR-016: Offline Mode
**Priority:** HIGH
**Description:** Mobile app functions without internet connectivity.
**Acceptance Criteria:**
- All CRUD operations available offline
- Data saved to local Room database
- Visual indicator showing offline status
- Sync automatically when connectivity restored
- Sync conflicts flagged for user resolution
- Minimum 95% sync success rate

### FR-017: Multi-Language Support
**Priority:** MEDIUM
**Description:** Platform available in English and Chichewa.
**Acceptance Criteria:**
- Language toggle in settings
- All UI text translated
- Error messages translated
- Email notifications in user's language
- Language persisted across sessions
- Extensible for additional languages

### FR-018: Data Backup & Restore
**Priority:** HIGH (Admin only)
**Description:** Tenant data can be backed up and restored.
**Acceptance Criteria:**
- Automated daily backups
- Tenant admins can request manual backup
- Backup includes all tenant data
- Point-in-time recovery within 30 days
- Backup encryption at rest
- Restore creates new copy, never overwrites

### FR-019: API Rate Limiting
**Priority:** HIGH
**Description:** Protect API from abuse with rate limits.
**Acceptance Criteria:**
- Per-user rate limits: 100 requests/minute
- Per-tenant rate limits: 1000 requests/minute
- Rate limit headers in response
- 429 status code when exceeded
- Whitelist for internal services
- Configurable by admin

### FR-020: Health Check Endpoint
**Priority:** HIGH
**Description:** Public endpoint for monitoring system health.
**Acceptance Criteria:**
- GET /health returns 200 with service status
- Checks: database connectivity, redis, storage
- Response includes version and uptime
- No authentication required
- Used by load balancers and monitoring

