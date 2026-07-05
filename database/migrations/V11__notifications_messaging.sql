-- Notification Templates
CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) UNIQUE NOT NULL,
    title_template VARCHAR(255) NOT NULL,
    body_template TEXT NOT NULL,
    channels JSONB DEFAULT '["IN_APP"]',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Push Notification Log
CREATE TABLE IF NOT EXISTS push_notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    device_id UUID REFERENCES user_devices(id),
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    data JSONB DEFAULT '{}',
    status VARCHAR(50) DEFAULT 'PENDING',
    sent_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_push_user ON push_notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_push_status ON push_notifications(status);

-- SMS Log
CREATE TABLE IF NOT EXISTS sms_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID,
    phone_number VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    provider VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING',
    message_id VARCHAR(255),
    cost DECIMAL(10,4),
    sent_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sms_user ON sms_logs(user_id);

-- Email Log
CREATE TABLE IF NOT EXISTS email_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID,
    to_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body_html TEXT,
    body_text TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    message_id VARCHAR(255),
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_email_user ON email_logs(user_id);
