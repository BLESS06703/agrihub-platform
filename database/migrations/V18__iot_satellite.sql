-- IoT Devices
CREATE TABLE IF NOT EXISTS iot_devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    farm_id UUID REFERENCES farms(id),
    field_id UUID REFERENCES fields(id),
    device_name VARCHAR(255) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    device_eui VARCHAR(100) UNIQUE,
    firmware_version VARCHAR(50),
    battery_level DECIMAL(5,2),
    last_reading_at TIMESTAMPTZ,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Sensor Readings
CREATE TABLE IF NOT EXISTS sensor_readings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id UUID NOT NULL REFERENCES iot_devices(id),
    reading_type VARCHAR(50) NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    battery_level DECIMAL(5,2),
    signal_strength INTEGER,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_readings_device ON sensor_readings(device_id);
CREATE INDEX IF NOT EXISTS idx_readings_time ON sensor_readings(recorded_at DESC);

-- Satellite Imagery
CREATE TABLE IF NOT EXISTS satellite_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    farm_id UUID REFERENCES farms(id),
    image_url TEXT NOT NULL,
    capture_date DATE NOT NULL,
    resolution_meters DECIMAL(5,2),
    cloud_cover_percent DECIMAL(5,2),
    satellite_source VARCHAR(100),
    ndvi_min DECIMAL(5,4),
    ndvi_max DECIMAL(5,4),
    ndvi_mean DECIMAL(5,4),
    bounds JSONB,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sat_farm ON satellite_images(farm_id);
CREATE INDEX IF NOT EXISTS idx_sat_date ON satellite_images(capture_date DESC);

-- Vegetation Analysis
CREATE TABLE IF NOT EXISTS vegetation_analysis (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    satellite_image_id UUID NOT NULL REFERENCES satellite_images(id),
    field_id UUID REFERENCES fields(id),
    ndvi_value DECIMAL(5,4),
    evi_value DECIMAL(5,4),
    lai_value DECIMAL(5,2),
    crop_health_score INTEGER CHECK (crop_health_score BETWEEN 1 AND 100),
    stress_detected BOOLEAN DEFAULT FALSE,
    stress_type VARCHAR(100),
    analysis_date TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_veg_image ON vegetation_analysis(satellite_image_id);
CREATE INDEX IF NOT EXISTS idx_veg_field ON vegetation_analysis(field_id);
