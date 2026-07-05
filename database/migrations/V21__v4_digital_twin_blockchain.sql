-- Digital Twin Models
CREATE TABLE IF NOT EXISTS digital_twins (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    farm_id UUID NOT NULL REFERENCES farms(id),
    twin_name VARCHAR(255) NOT NULL,
    model_data JSONB NOT NULL DEFAULT '{}',
    last_synced_at TIMESTAMPTZ,
    simulation_status VARCHAR(50) DEFAULT 'IDLE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(farm_id)
);

-- Simulation Runs
CREATE TABLE IF NOT EXISTS simulations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    twin_id UUID NOT NULL REFERENCES digital_twins(id),
    simulation_type VARCHAR(100) NOT NULL,
    parameters JSONB NOT NULL DEFAULT '{}',
    results JSONB,
    status VARCHAR(50) DEFAULT 'QUEUED',
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- What-if Scenarios
CREATE TABLE IF NOT EXISTS scenarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    twin_id UUID NOT NULL REFERENCES digital_twins(id),
    scenario_name VARCHAR(255) NOT NULL,
    description TEXT,
    variables JSONB NOT NULL DEFAULT '{}',
    predicted_yield_kg DECIMAL(10,2),
    predicted_revenue DECIMAL(12,2),
    risk_score INTEGER CHECK (risk_score BETWEEN 1 AND 100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Blockchain Supply Chain
CREATE TABLE IF NOT EXISTS supply_chain_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    actor_id UUID NOT NULL REFERENCES users(id),
    location GEOGRAPHY(POINT, 4326),
    data JSONB NOT NULL DEFAULT '{}',
    previous_hash VARCHAR(255),
    block_hash VARCHAR(255) NOT NULL,
    block_number BIGINT,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sc_product ON supply_chain_events(product_id);
CREATE INDEX IF NOT EXISTS idx_sc_block ON supply_chain_events(block_number);

-- Product Provenance
CREATE TABLE IF NOT EXISTS product_provenance (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL UNIQUE,
    product_name VARCHAR(255) NOT NULL,
    origin_farm_id UUID REFERENCES farms(id),
    origin_harvest_id UUID,
    current_owner_id UUID REFERENCES users(id),
    current_status VARCHAR(100),
    quality_certificates JSONB DEFAULT '[]',
    journey JSONB DEFAULT '[]',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_prov_owner ON product_provenance(current_owner_id);
CREATE INDEX IF NOT EXISTS idx_prov_farm ON product_provenance(origin_farm_id);

-- Blockchain Transactions
CREATE TABLE IF NOT EXISTS blockchain_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tx_hash VARCHAR(255) UNIQUE NOT NULL,
    block_number BIGINT,
    contract_address VARCHAR(255),
    from_address VARCHAR(255),
    to_address VARCHAR(255),
    value DECIMAL(20,6),
    gas_used BIGINT,
    status VARCHAR(50) DEFAULT 'PENDING',
    confirmed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
