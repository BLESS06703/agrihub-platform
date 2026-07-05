-- Login Attempts (success and failure)
CREATE TABLE IF NOT EXISTS login_attempts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID,
    user_id UUID,
    email VARCHAR(255),
    ip_address INET,
    user_agent TEXT,
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(100),
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_login_user ON login_attempts(user_id);
CREATE INDEX IF NOT EXISTS idx_login_ip ON login_attempts(ip_address);
CREATE INDEX IF NOT EXISTS idx_login_date ON login_attempts(attempted_at DESC);

-- Security Events
CREATE TABLE IF NOT EXISTS security_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID,
    user_id UUID,
    event_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) DEFAULT 'INFO',
    description TEXT,
    source_ip INET,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sec_event_type ON security_events(event_type);
CREATE INDEX IF NOT EXISTS idx_sec_event_severity ON security_events(severity);
CREATE INDEX IF NOT EXISTS idx_sec_event_date ON security_events(created_at DESC);

-- Active Device Sessions
CREATE TABLE IF NOT EXISTS device_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    device_id VARCHAR(255) NOT NULL,
    refresh_token_hash VARCHAR(255),
    ip_address INET,
    user_agent TEXT,
    location JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    last_activity_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_session_user ON device_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_session_active ON device_sessions(is_active) WHERE is_active = TRUE;

-- Blocked IPs
CREATE TABLE IF NOT EXISTS blocked_ips (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ip_address INET NOT NULL,
    reason VARCHAR(255),
    blocked_by UUID REFERENCES users(id),
    blocked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_blocked_ip ON blocked_ips(ip_address);

-- Token Blacklist
CREATE TABLE IF NOT EXISTS token_blacklist (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token_hash VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    user_id UUID REFERENCES users(id),
    revoked_by VARCHAR(100),
    revoked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_token_hash ON token_blacklist(token_hash);
CREATE INDEX IF NOT EXISTS idx_token_expires ON token_blacklist(expires_at);

-- Rate Limit Tracking
CREATE TABLE IF NOT EXISTS rate_limits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    identifier VARCHAR(255) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    request_count INTEGER DEFAULT 1,
    window_start TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    window_end TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rate_limit_id ON rate_limits(identifier, endpoint);
CREATE INDEX IF NOT EXISTS idx_rate_limit_window ON rate_limits(window_start, window_end);
