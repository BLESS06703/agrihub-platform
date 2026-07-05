-- Mobile Money Transactions
CREATE TABLE IF NOT EXISTS mobile_money_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    provider VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(5) DEFAULT 'MWK',
    phone_number VARCHAR(50) NOT NULL,
    reference VARCHAR(255),
    provider_transaction_id VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    callback_received BOOLEAN DEFAULT FALSE,
    callback_data JSONB,
    initiated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_mm_tenant ON mobile_money_transactions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_mm_reference ON mobile_money_transactions(reference);
CREATE INDEX IF NOT EXISTS idx_mm_status ON mobile_money_transactions(status);

-- External API Integrations
CREATE TABLE IF NOT EXISTS integration_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    provider VARCHAR(100) NOT NULL,
    config JSONB NOT NULL DEFAULT '{}',
    is_active BOOLEAN DEFAULT TRUE,
    last_synced_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    UNIQUE(tenant_id, provider)
);

-- Webhook Subscriptions
CREATE TABLE IF NOT EXISTS webhooks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    url TEXT NOT NULL,
    events JSONB NOT NULL DEFAULT '[]',
    secret VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    last_triggered_at TIMESTAMPTZ,
    failure_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_webhooks_tenant ON webhooks(tenant_id);
