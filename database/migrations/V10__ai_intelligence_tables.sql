-- AI Conversations
CREATE TABLE IF NOT EXISTS ai_conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    query TEXT NOT NULL,
    response TEXT NOT NULL,
    context JSONB DEFAULT '{}',
    model VARCHAR(100),
    tokens_used INTEGER,
    confidence DECIMAL(4,3),
    feedback VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ai_conv_tenant ON ai_conversations(tenant_id);
CREATE INDEX IF NOT EXISTS idx_ai_conv_user ON ai_conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_conv_created ON ai_conversations(created_at DESC);

-- Disease Detection Results
CREATE TABLE IF NOT EXISTS disease_detections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    crop_id UUID REFERENCES crop_catalog(id),
    image_url TEXT NOT NULL,
    detected_disease VARCHAR(255),
    confidence DECIMAL(4,3),
    description TEXT,
    treatment TEXT,
    urgency VARCHAR(20) DEFAULT 'MEDIUM',
    is_verified BOOLEAN DEFAULT FALSE,
    verified_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_disease_tenant ON disease_detections(tenant_id);
CREATE INDEX IF NOT EXISTS idx_disease_crop ON disease_detections(crop_id);

-- Yield Predictions
CREATE TABLE IF NOT EXISTS yield_predictions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    field_id UUID REFERENCES fields(id),
    crop_id UUID REFERENCES crop_catalog(id),
    predicted_yield_kg DECIMAL(10,2),
    confidence DECIMAL(4,3),
    factors JSONB DEFAULT '[]',
    predicted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    actual_yield_kg DECIMAL(10,2),
    accuracy DECIMAL(5,2)
);

CREATE INDEX IF NOT EXISTS idx_yield_field ON yield_predictions(field_id);

-- Market Price Forecasts
CREATE TABLE IF NOT EXISTS price_forecasts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    crop_id UUID REFERENCES crop_catalog(id),
    market_name VARCHAR(255),
    forecast_date DATE NOT NULL,
    predicted_min DECIMAL(10,2),
    predicted_max DECIMAL(10,2),
    predicted_avg DECIMAL(10,2),
    confidence DECIMAL(4,3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_price_forecast_crop ON price_forecasts(crop_id);
CREATE INDEX IF NOT EXISTS idx_price_forecast_date ON price_forecasts(forecast_date);

-- Weather Cache
CREATE TABLE IF NOT EXISTS weather_cache (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    location GEOGRAPHY(POINT, 4326),
    forecast_date DATE NOT NULL,
    temperature_min DECIMAL(5,2),
    temperature_max DECIMAL(5,2),
    humidity_percent DECIMAL(5,2),
    rainfall_mm DECIMAL(7,2),
    wind_speed_kmh DECIMAL(5,2),
    condition VARCHAR(100),
    source VARCHAR(100),
    fetched_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_weather_location ON weather_cache USING GIST(location);
CREATE INDEX IF NOT EXISTS idx_weather_date ON weather_cache(forecast_date);

-- Agricultural Intelligence Reports
CREATE TABLE IF NOT EXISTS intelligence_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    summary TEXT,
    data JSONB NOT NULL DEFAULT '{}',
    generated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    is_read BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_intel_tenant ON intelligence_reports(tenant_id);
CREATE INDEX IF NOT EXISTS idx_intel_type ON intelligence_reports(report_type);
CREATE INDEX IF NOT EXISTS idx_intel_generated ON intelligence_reports(generated_at DESC);
