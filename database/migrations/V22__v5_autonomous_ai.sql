-- AI Agent Decisions
CREATE TABLE IF NOT EXISTS agent_decisions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    farm_id UUID REFERENCES farms(id),
    agent_type VARCHAR(100) NOT NULL,
    decision_type VARCHAR(100) NOT NULL,
    input_data JSONB NOT NULL DEFAULT '{}',
    decision JSONB NOT NULL DEFAULT '{}',
    confidence DECIMAL(4,3),
    executed BOOLEAN DEFAULT FALSE,
    executed_at TIMESTAMPTZ,
    feedback_score INTEGER CHECK (feedback_score BETWEEN 1 AND 5),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_agent_farm ON agent_decisions(farm_id);
CREATE INDEX IF NOT EXISTS idx_agent_type ON agent_decisions(agent_type);

-- Automated Actions
CREATE TABLE IF NOT EXISTS automated_actions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    decision_id UUID REFERENCES agent_decisions(id),
    action_type VARCHAR(100) NOT NULL,
    target_type VARCHAR(100),
    target_id UUID,
    parameters JSONB NOT NULL DEFAULT '{}',
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    scheduled_for TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    result JSONB,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Market Predictions
CREATE TABLE IF NOT EXISTS market_predictions_advanced (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    crop_id UUID REFERENCES crop_catalog(id),
    prediction_date DATE NOT NULL,
    predicted_price_min DECIMAL(10,2),
    predicted_price_max DECIMAL(10,2),
    predicted_price_avg DECIMAL(10,2),
    confidence DECIMAL(4,3),
    factors JSONB DEFAULT '{}',
    model_version VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Risk Assessments
CREATE TABLE IF NOT EXISTS risk_assessments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    farm_id UUID REFERENCES farms(id),
    risk_type VARCHAR(100) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    probability DECIMAL(4,3),
    impact_score INTEGER CHECK (impact_score BETWEEN 1 AND 100),
    mitigation TEXT,
    assessed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    valid_until TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_risk_farm ON risk_assessments(farm_id);
CREATE INDEX IF NOT EXISTS idx_risk_level ON risk_assessments(risk_level);

-- Autonomous Agent Configuration
CREATE TABLE IF NOT EXISTS agent_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    farm_id UUID REFERENCES farms(id),
    agent_name VARCHAR(255) NOT NULL,
    agent_type VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    autonomy_level VARCHAR(20) DEFAULT 'ADVISORY',
    schedule_config JSONB DEFAULT '{}',
    notification_channels JSONB DEFAULT '["IN_APP"]',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    UNIQUE(tenant_id, farm_id, agent_type)
);
