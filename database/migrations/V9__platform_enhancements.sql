-- Add country support to tenants
ALTER TABLE tenants 
ADD COLUMN IF NOT EXISTS country_code VARCHAR(2) NOT NULL DEFAULT 'MW',
ADD COLUMN IF NOT EXISTS region VARCHAR(100),
ADD COLUMN IF NOT EXISTS district VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_tenants_country ON tenants(country_code);
CREATE INDEX IF NOT EXISTS idx_tenants_district ON tenants(district);

-- Add version column for optimistic locking
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;
ALTER TABLE users ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;
ALTER TABLE farms ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;
ALTER TABLE fields ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;

-- Countries reference table
CREATE TABLE IF NOT EXISTS countries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(2) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255),
    capital VARCHAR(100),
    currency_code VARCHAR(3) NOT NULL,
    currency_symbol VARCHAR(10),
    default_language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    config JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Regions table
CREATE TABLE IF NOT EXISTS regions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    country_id UUID NOT NULL REFERENCES countries(id),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(country_id, name)
);

-- Districts table
CREATE TABLE IF NOT EXISTS districts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    region_id UUID NOT NULL REFERENCES regions(id),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(region_id, name)
);

-- Feature flags per country
CREATE TABLE IF NOT EXISTS country_features (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    country_id UUID NOT NULL REFERENCES countries(id),
    feature_key VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    config JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    UNIQUE(country_id, feature_key)
);

-- Tenant subscriptions
CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    tier VARCHAR(50) NOT NULL DEFAULT 'FREE',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    max_users INTEGER DEFAULT 1,
    max_farms INTEGER DEFAULT 1,
    features JSONB DEFAULT '[]',
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    auto_renew BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_subscriptions_tenant ON subscriptions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions(status);

-- User devices for multi-device support
CREATE TABLE IF NOT EXISTS user_devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    device_id VARCHAR(255) NOT NULL,
    device_name VARCHAR(255),
    platform VARCHAR(50),
    push_token TEXT,
    last_active_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, device_id)
);

-- API keys for programmatic access
CREATE TABLE IF NOT EXISTS api_keys (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    key_hash VARCHAR(255) UNIQUE NOT NULL,
    scopes JSONB DEFAULT '[]',
    last_used_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked_at TIMESTAMPTZ
);

-- Seed country data
INSERT INTO countries (code, name, full_name, capital, currency_code, currency_symbol, default_language, timezone) VALUES
('MW', 'Malawi', 'Republic of Malawi', 'Lilongwe', 'MWK', 'MK', 'en', 'Africa/Blantyre'),
('ZM', 'Zambia', 'Republic of Zambia', 'Lusaka', 'ZMW', 'ZK', 'en', 'Africa/Lusaka'),
('TZ', 'Tanzania', 'United Republic of Tanzania', 'Dodoma', 'TZS', 'TSh', 'sw', 'Africa/Dar_es_Salaam')
ON CONFLICT (code) DO NOTHING;

-- Seed Malawi regions
WITH mw AS (SELECT id FROM countries WHERE code = 'MW')
INSERT INTO regions (country_id, name, code) VALUES
((SELECT id FROM mw), 'Northern', 'MW-N'),
((SELECT id FROM mw), 'Central', 'MW-C'),
((SELECT id FROM mw), 'Southern', 'MW-S')
ON CONFLICT (country_id, name) DO NOTHING;

-- Seed Malawi districts
WITH north AS (SELECT id FROM regions WHERE name = 'Northern'),
     central AS (SELECT id FROM regions WHERE name = 'Central'),
     south AS (SELECT id FROM regions WHERE name = 'Southern')
INSERT INTO districts (region_id, name) VALUES
((SELECT id FROM north), 'Chitipa'),
((SELECT id FROM north), 'Karonga'),
((SELECT id FROM north), 'Rumphi'),
((SELECT id FROM north), 'Mzimba'),
((SELECT id FROM north), 'Nkhata Bay'),
((SELECT id FROM north), 'Likoma'),
((SELECT id FROM central), 'Kasungu'),
((SELECT id FROM central), 'Nkhotakota'),
((SELECT id FROM central), 'Ntchisi'),
((SELECT id FROM central), 'Dowa'),
((SELECT id FROM central), 'Mchinji'),
((SELECT id FROM central), 'Lilongwe'),
((SELECT id FROM central), 'Dedza'),
((SELECT id FROM central), 'Ntcheu'),
((SELECT id FROM central), 'Salima'),
((SELECT id FROM south), 'Mangochi'),
((SELECT id FROM south), 'Machinga'),
((SELECT id FROM south), 'Zomba'),
((SELECT id FROM south), 'Chiradzulu'),
((SELECT id FROM south), 'Blantyre'),
((SELECT id FROM south), 'Mwanza'),
((SELECT id FROM south), 'Thyolo'),
((SELECT id FROM south), 'Mulanje'),
((SELECT id FROM south), 'Phalombe'),
((SELECT id FROM south), 'Chikwawa'),
((SELECT id FROM south), 'Nsanje'),
((SELECT id FROM south), 'Balaka'),
((SELECT id FROM south), 'Neno')
ON CONFLICT (region_id, name) DO NOTHING;

-- Seed default features for Malawi
WITH mw AS (SELECT id FROM countries WHERE code = 'MW')
INSERT INTO country_features (country_id, feature_key, is_enabled) VALUES
((SELECT id FROM mw), 'ai_assistant', true),
((SELECT id FROM mw), 'marketplace', true),
((SELECT id FROM mw), 'insurance', false),
((SELECT id FROM mw), 'loans', true),
((SELECT id FROM mw), 'weather', true),
((SELECT id FROM mw), 'offline_sync', true),
((SELECT id FROM mw), 'ussd', false),
((SELECT id FROM mw), 'satellite_imagery', false)
ON CONFLICT (country_id, feature_key) DO NOTHING;
