-- ============================================
-- AgriHub Malawi - Core Schema
-- Version: 1.0
-- Description: Foundation tables for multi-tenancy,
--              authentication, and audit logging
-- ============================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- ============================================
-- 1. TENANT MANAGEMENT
-- ============================================

-- Tenant types enum
CREATE TYPE tenant_type AS ENUM (
    'INDIVIDUAL_FARMER',
    'COOPERATIVE',
    'AGRIBUSINESS',
    'NGO',
    'GOVERNMENT',
    'MICROFINANCE',
    'INSURANCE',
    'EQUIPMENT_RENTAL',
    'PRODUCE_BUYER',
    'WAREHOUSE_OPERATOR'
);

-- Tenant tiers for multi-tenancy strategy
CREATE TYPE tenant_tier AS ENUM (
    'SHARED',    -- Shared schema with RLS
    'SCHEMA',    -- Schema per tenant
    'DATABASE'   -- Dedicated database per tenant
);

-- Tenant status
CREATE TYPE tenant_status AS ENUM (
    'PENDING',
    'ACTIVE',
    'SUSPENDED',
    'INACTIVE',
    'DELETED'
);

-- Main tenant table
CREATE TABLE tenants (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(100) UNIQUE NOT NULL,
    tenant_type     tenant_type NOT NULL,
    tenant_tier     tenant_tier NOT NULL DEFAULT 'SHARED',
    status          tenant_status NOT NULL DEFAULT 'PENDING',
    
    -- Contact info
    email           VARCHAR(255),
    phone           VARCHAR(50),
    address         TEXT,
    
    -- Configuration
    logo_url        TEXT,
    default_language VARCHAR(10) DEFAULT 'en',
    default_currency VARCHAR(5) DEFAULT 'MWK',
    timezone        VARCHAR(50) DEFAULT 'Africa/Blantyre',
    settings        JSONB DEFAULT '{}',
    
    -- Tier-specific routing
    db_name         VARCHAR(100),
    schema_name     VARCHAR(100),
    
    -- Subscription
    subscription_tier VARCHAR(50) DEFAULT 'FREE',
    subscription_expires_at TIMESTAMPTZ,
    max_users       INTEGER DEFAULT 5,
    
    -- Audit
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_tenants_slug ON tenants(slug);
CREATE INDEX idx_tenants_status ON tenants(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_tenants_type ON tenants(tenant_type);

-- ============================================
-- 2. USER & AUTHENTICATION
-- ============================================

CREATE TYPE user_status AS ENUM (
    'PENDING',
    'ACTIVE',
    'SUSPENDED',
    'LOCKED',
    'INACTIVE'
);

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    
    -- Identity
    email           VARCHAR(255) NOT NULL,
    phone           VARCHAR(50),
    full_name       VARCHAR(255) NOT NULL,
    username        VARCHAR(100),
    
    -- Security
    password_hash   VARCHAR(255) NOT NULL,
    password_changed_at TIMESTAMPTZ,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until    TIMESTAMPTZ,
    mfa_enabled     BOOLEAN DEFAULT FALSE,
    mfa_secret      VARCHAR(255),
    
    -- Status
    status          user_status NOT NULL DEFAULT 'PENDING',
    email_verified  BOOLEAN DEFAULT FALSE,
    email_verified_at TIMESTAMPTZ,
    
    -- Profile
    avatar_url      TEXT,
    job_title       VARCHAR(255),
    department      VARCHAR(255),
    
    -- Preferences
    language        VARCHAR(10) DEFAULT 'en',
    preferences     JSONB DEFAULT '{}',
    
    -- Tokens
    refresh_token_hash VARCHAR(255),
    
    -- Audit
    last_login_at   TIMESTAMPTZ,
    last_login_ip   INET,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, email),
    UNIQUE(tenant_id, username)
);

CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status) WHERE deleted_at IS NULL;

-- ============================================
-- 3. ROLES & PERMISSIONS
-- ============================================

CREATE TABLE roles (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID REFERENCES tenants(id), -- NULL = system role
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    is_system       BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, name)
);

CREATE TABLE permissions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code            VARCHAR(100) UNIQUE NOT NULL, -- e.g., "farm:create:own"
    resource        VARCHAR(50) NOT NULL,
    action          VARCHAR(50) NOT NULL,
    scope           VARCHAR(50) NOT NULL DEFAULT 'own',
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role_permissions (
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE user_roles (
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_by     UUID REFERENCES users(id),
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, role_id)
);

-- ============================================
-- 4. INVITATIONS
-- ============================================

CREATE TYPE invitation_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'EXPIRED',
    'REVOKED'
);

CREATE TABLE invitations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    email           VARCHAR(255) NOT NULL,
    invited_by      UUID NOT NULL REFERENCES users(id),
    token           VARCHAR(255) UNIQUE NOT NULL,
    status          invitation_status NOT NULL DEFAULT 'PENDING',
    expires_at      TIMESTAMPTZ NOT NULL,
    accepted_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invitations_token ON invitations(token);
CREATE INDEX idx_invitations_email ON invitations(email);

-- ============================================
-- 5. AUDIT LOGS
-- ============================================

CREATE TYPE audit_action AS ENUM (
    'CREATE',
    'UPDATE',
    'DELETE',
    'LOGIN',
    'LOGOUT',
    'EXPORT',
    'APPROVE',
    'REJECT',
    'LOGIN_FAILED'
);

CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    user_id         UUID,
    action          audit_action NOT NULL,
    table_name      VARCHAR(100),
    record_id       UUID,
    old_values      JSONB,
    new_values      JSONB,
    ip_address      INET,
    user_agent      TEXT,
    correlation_id  UUID,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_tenant ON audit_logs(tenant_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_table ON audit_logs(table_name, record_id);
CREATE INDEX idx_audit_created ON audit_logs(created_at DESC);

-- Partition audit logs by month (for scalability)
-- This will be enabled when data volume grows:
-- CREATE TABLE audit_logs_partitioned (...) PARTITION BY RANGE (created_at);

-- ============================================
-- 6. SESSIONS & TOKENS
-- ============================================

CREATE TABLE refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) UNIQUE NOT NULL,
    device_info     TEXT,
    ip_address      INET,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens(token_hash);

-- ============================================
-- 7. NOTIFICATIONS
-- ============================================

CREATE TYPE notification_type AS ENUM (
    'SYSTEM',
    'ALERT',
    'REMINDER',
    'MARKET',
    'SOCIAL'
);

CREATE TYPE notification_channel AS ENUM (
    'IN_APP',
    'PUSH',
    'EMAIL',
    'SMS'
);

CREATE TABLE notifications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    user_id         UUID NOT NULL REFERENCES users(id),
    type            notification_type NOT NULL,
    channel         notification_channel NOT NULL DEFAULT 'IN_APP',
    title           VARCHAR(255) NOT NULL,
    body            TEXT,
    data            JSONB,
    is_read         BOOLEAN DEFAULT FALSE,
    read_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_created ON notifications(created_at DESC);

-- ============================================
-- 8. SYSTEM SETTINGS
-- ============================================

CREATE TABLE system_settings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    key             VARCHAR(255) UNIQUE NOT NULL,
    value           JSONB NOT NULL,
    description     TEXT,
    updated_by      UUID REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

-- ============================================
-- 9. SYNC TRACKING
-- ============================================

CREATE TABLE sync_log (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    user_id         UUID NOT NULL REFERENCES users(id),
    device_id       VARCHAR(255) NOT NULL,
    sync_type       VARCHAR(20) NOT NULL, -- 'PUSH' or 'PULL'
    records_count   INTEGER DEFAULT 0,
    success_count   INTEGER DEFAULT 0,
    failure_count   INTEGER DEFAULT 0,
    errors          JSONB,
    started_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMPTZ
);

CREATE INDEX idx_sync_log_user ON sync_log(user_id, device_id);
CREATE INDEX idx_sync_log_tenant ON sync_log(tenant_id);

-- ============================================
-- SEED DATA: Default System Roles & Permissions
-- ============================================

-- System Roles
INSERT INTO roles (id, name, description, is_system) VALUES
    (uuid_generate_v4(), 'TENANT_OWNER', 'Full control of tenant', TRUE),
    (uuid_generate_v4(), 'FARM_MANAGER', 'Farm operations management', TRUE),
    (uuid_generate_v4(), 'FARMER', 'Daily farming activities', TRUE),
    (uuid_generate_v4(), 'AGRONOMIST', 'Crop advisory', TRUE),
    (uuid_generate_v4(), 'VET_OFFICER', 'Livestock management', TRUE),
    (uuid_generate_v4(), 'EXTENSION_OFFICER', 'Field support', TRUE),
    (uuid_generate_v4(), 'ACCOUNTANT', 'Financial operations', TRUE),
    (uuid_generate_v4(), 'WAREHOUSE_MANAGER', 'Storage management', TRUE),
    (uuid_generate_v4(), 'BUYER', 'Procurement', TRUE),
    (uuid_generate_v4(), 'TRANSPORTER', 'Logistics', TRUE),
    (uuid_generate_v4(), 'DATA_ANALYST', 'Reporting only', TRUE),
    (uuid_generate_v4(), 'VIEWER', 'Read-only access', TRUE);

-- Core Permissions
INSERT INTO permissions (code, resource, action, scope) VALUES
    -- Farm permissions
    ('farm:create:own', 'farm', 'create', 'own'),
    ('farm:read:own', 'farm', 'read', 'own'),
    ('farm:read:tenant', 'farm', 'read', 'tenant'),
    ('farm:update:own', 'farm', 'update', 'own'),
    ('farm:delete:own', 'farm', 'delete', 'own'),
    
    -- Field permissions
    ('field:create:own', 'field', 'create', 'own'),
    ('field:read:own', 'field', 'read', 'own'),
    ('field:update:own', 'field', 'update', 'own'),
    
    -- Crop permissions
    ('crop:create:own', 'crop', 'create', 'own'),
    ('crop:read:own', 'crop', 'read', 'own'),
    
    -- Harvest permissions
    ('harvest:create:own', 'harvest', 'create', 'own'),
    ('harvest:read:own', 'harvest', 'read', 'own'),
    
    -- Livestock permissions
    ('livestock:create:own', 'livestock', 'create', 'own'),
    ('livestock:read:own', 'livestock', 'read', 'own'),
    ('livestock:update:own', 'livestock', 'update', 'own'),
    
    -- Finance permissions
    ('finance:create:own', 'finance', 'create', 'own'),
    ('finance:read:own', 'finance', 'read', 'own'),
    ('finance:read:tenant', 'finance', 'read', 'tenant'),
    ('finance:approve:tenant', 'finance', 'approve', 'tenant'),
    
    -- Inventory permissions
    ('inventory:create:own', 'inventory', 'create', 'own'),
    ('inventory:read:own', 'inventory', 'read', 'own'),
    ('inventory:update:own', 'inventory', 'update', 'own'),
    
    -- Marketplace permissions
    ('marketplace:create:own', 'marketplace', 'create', 'own'),
    ('marketplace:read:tenant', 'marketplace', 'read', 'tenant'),
    
    -- Report permissions
    ('report:read:tenant', 'report', 'read', 'tenant'),
    ('report:export:tenant', 'report', 'export', 'tenant'),
    
    -- User management
    ('user:manage:tenant', 'user', 'manage', 'tenant'),
    
    -- Tenant management
    ('tenant:manage:own', 'tenant', 'manage', 'own');

