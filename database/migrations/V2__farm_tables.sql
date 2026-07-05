-- ============================================
-- AgriHub Malawi - Farm Management Schema
-- Version: 1.0
-- ============================================

-- ============================================
-- 1. FARMS
-- ============================================

CREATE TYPE farm_status AS ENUM ('ACTIVE', 'INACTIVE', 'ARCHIVED');
CREATE TYPE soil_type AS ENUM ('SANDY', 'LOAMY', 'CLAY', 'SILTY', 'PEATY', 'CHALKY', 'UNKNOWN');
CREATE TYPE water_source AS ENUM ('RAINFED', 'IRRIGATION', 'RIVER', 'BOREHOLE', 'DAM', 'CANAL', 'MUNICIPAL');

CREATE TABLE farms (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    -- Basic info
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    area_hectares   DECIMAL(10,2),
    
    -- Location
    location        GEOGRAPHY(POINT, 4326),  -- GPS coordinates
    boundary        GEOGRAPHY(POLYGON, 4326), -- Farm boundary polygon
    altitude_meters DECIMAL(7,2),
    
    -- Characteristics
    soil_type       soil_type,
    water_source    water_source,
    has_electricity BOOLEAN DEFAULT FALSE,
    distance_to_road_km DECIMAL(5,2),
    
    -- Status
    status          farm_status NOT NULL DEFAULT 'ACTIVE',
    
    -- Region info
    region          VARCHAR(100),
    district        VARCHAR(100),
    ta              VARCHAR(100),  -- Traditional Authority
    village         VARCHAR(100),
    
    -- Media
    photo_urls      JSONB DEFAULT '[]',
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_farms_tenant ON farms(tenant_id);
CREATE INDEX idx_farms_status ON farms(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_farms_location ON farms USING GIST(location);
CREATE INDEX idx_farms_boundary ON farms USING GIST(boundary);
CREATE INDEX idx_farms_district ON farms(district);

-- ============================================
-- 2. FIELDS
-- ============================================

CREATE TYPE field_status AS ENUM ('EMPTY', 'PLANTED', 'FALLOW', 'HARVESTED', 'PREPARING');
CREATE TYPE irrigation_type AS ENUM ('NONE', 'DRIP', 'SPRINKLER', 'FLOOD', 'FURROW', 'PIVOT');

CREATE TABLE fields (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    farm_id         UUID NOT NULL REFERENCES farms(id),
    
    -- Basic info
    field_name      VARCHAR(255) NOT NULL,
    field_code      VARCHAR(50),  -- Unique within farm
    area_hectares   DECIMAL(8,2),
    
    -- Location
    boundary        GEOGRAPHY(POLYGON, 4326),
    centroid        GEOGRAPHY(POINT, 4326),
    
    -- Characteristics
    soil_type       soil_type,
    irrigation_type irrigation_type DEFAULT 'NONE',
    drainage_quality VARCHAR(50),
    slope_percent   DECIMAL(5,2),
    
    -- Status
    status          field_status NOT NULL DEFAULT 'EMPTY',
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(farm_id, field_code)
);

CREATE INDEX idx_fields_farm ON fields(farm_id);
CREATE INDEX idx_fields_tenant ON fields(tenant_id);
CREATE INDEX idx_fields_status ON fields(status) WHERE deleted_at IS NULL;

-- ============================================
-- 3. CROP CATALOG
-- ============================================

CREATE TYPE crop_category AS ENUM (
    'CEREAL', 'LEGUME', 'VEGETABLE', 'FRUIT', 'CASH_CROP',
    'TUBER', 'OILSEED', 'FODDER', 'SPICE', 'OTHER'
);

CREATE TABLE crop_catalog (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Identity
    name_en         VARCHAR(100) NOT NULL,
    name_ny         VARCHAR(100),  -- Chichewa name
    scientific_name VARCHAR(255),
    category        crop_category NOT NULL,
    
    -- Growing info
    growing_period_days INTEGER,
    min_temp_celsius DECIMAL(5,2),
    max_temp_celsius DECIMAL(5,2),
    optimal_ph_min  DECIMAL(3,1),
    optimal_ph_max  DECIMAL(3,1),
    water_requirement_mm DECIMAL(7,2),
    
    -- Yield info
    expected_yield_min_kg_ha DECIMAL(10,2),
    expected_yield_max_kg_ha DECIMAL(10,2),
    
    -- Seed info
    seed_rate_kg_ha DECIMAL(7,2),
    plants_per_ha   INTEGER,
    
    -- Description
    description     TEXT,
    growing_tips    TEXT,
    common_pests    TEXT,
    common_diseases TEXT,
    
    -- Media
    image_url       TEXT,
    
    -- Status
    is_active       BOOLEAN DEFAULT TRUE,
    
    -- Audit
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_crop_category ON crop_catalog(category);
CREATE INDEX idx_crop_name ON crop_catalog(name_en);

-- ============================================
-- 4. CROP VARIETIES
-- ============================================

CREATE TABLE crop_varieties (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    variety_name    VARCHAR(255) NOT NULL,
    description     TEXT,
    maturity_days   INTEGER,
    yield_potential_kg_ha DECIMAL(10,2),
    drought_tolerant BOOLEAN DEFAULT FALSE,
    disease_resistant BOOLEAN DEFAULT FALSE,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_variety_crop ON crop_varieties(crop_id);

-- ============================================
-- 5. GROWTH STAGES
-- ============================================

CREATE TABLE crop_growth_stages (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    stage_order     INTEGER NOT NULL,
    stage_name      VARCHAR(100) NOT NULL,
    days_from_planting INTEGER,
    description     TEXT,
    recommended_actions TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(crop_id, stage_order)
);

-- ============================================
-- 6. PLANTING RECORDS
-- ============================================

CREATE TYPE planting_method AS ENUM (
    'MANUAL', 'MECHANICAL', 'DIRECT_SEEDING', 'TRANSPLANTING', 'BROADCAST', 'DIBBLING'
);

CREATE TABLE planting_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    farm_id         UUID NOT NULL REFERENCES farms(id),
    field_id        UUID NOT NULL REFERENCES fields(id),
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    variety_id      UUID REFERENCES crop_varieties(id),
    
    -- Planting details
    planting_date   DATE NOT NULL,
    seed_quantity_kg DECIMAL(10,2),
    area_planted_ha DECIMAL(8,2),
    planting_method planting_method DEFAULT 'MANUAL',
    plant_spacing_cm VARCHAR(20),
    seed_source     VARCHAR(255),
    seed_lot_number VARCHAR(100),
    
    -- GPS
    planting_location GEOGRAPHY(POINT, 4326),
    
    -- Expected
    expected_harvest_date DATE,
    expected_yield_kg DECIMAL(10,2),
    
    -- Status
    status          VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, HARVESTED, FAILED
    
    -- Weather at planting
    weather_conditions TEXT,
    soil_moisture   VARCHAR(50),
    
    -- Media
    photo_urls      JSONB DEFAULT '[]',
    notes           TEXT,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_planting_tenant ON planting_records(tenant_id);
CREATE INDEX idx_planting_farm ON planting_records(farm_id);
CREATE INDEX idx_planting_field ON planting_records(field_id);
CREATE INDEX idx_planting_crop ON planting_records(crop_id);
CREATE INDEX idx_planting_date ON planting_records(planting_date);
CREATE INDEX idx_planting_status ON planting_records(status);

-- ============================================
-- 7. INPUT APPLICATIONS
-- ============================================

CREATE TYPE input_type AS ENUM (
    'FERTILIZER', 'PESTICIDE', 'HERBICIDE', 'FUNGICIDE', 
    'INSECTICIDE', 'GROWTH_REGULATOR', 'FOLIAR_FEED', 'MANURE', 'COMPOST'
);

CREATE TYPE application_method AS ENUM (
    'SPRAY', 'BROADCAST', 'BANDING', 'FOLIAR', 'DRENCH', 'INJECTION', 'TOP_DRESSING', 'SIDE_DRESSING'
);

CREATE TABLE input_applications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    planting_id     UUID REFERENCES planting_records(id),
    field_id        UUID NOT NULL REFERENCES fields(id),
    
    -- Input details
    input_type      input_type NOT NULL,
    product_name    VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255),
    quantity        DECIMAL(10,2) NOT NULL,
    unit            VARCHAR(20) NOT NULL, -- KG, LITER, ML, GRAM
    application_method application_method,
    
    -- Timing
    application_date DATE NOT NULL,
    application_time TIME,
    
    -- Safety
    pre_harvest_interval_days INTEGER,
    re_entry_interval_hours INTEGER,
    safety_notes    TEXT,
    
    -- Weather conditions
    temperature_celsius DECIMAL(5,2),
    wind_speed_kmh  DECIMAL(5,2),
    weather_conditions TEXT,
    
    -- Cost
    cost_per_unit   DECIMAL(10,2),
    total_cost      DECIMAL(12,2),
    
    -- Inventory link
    inventory_transaction_id UUID,
    
    -- Effectiveness
    effectiveness   VARCHAR(50), -- POOR, FAIR, GOOD, EXCELLENT
    notes           TEXT,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_input_planting ON input_applications(planting_id);
CREATE INDEX idx_input_field ON input_applications(field_id);
CREATE INDEX idx_input_date ON input_applications(application_date);
CREATE INDEX idx_input_type ON input_applications(input_type);

-- ============================================
-- 8. IRRIGATION RECORDS
-- ============================================

CREATE TABLE irrigation_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    field_id        UUID NOT NULL REFERENCES fields(id),
    planting_id     UUID REFERENCES planting_records(id),
    
    irrigation_date DATE NOT NULL,
    duration_hours  DECIMAL(5,2),
    water_volume_liters DECIMAL(10,2),
    water_source    VARCHAR(100),
    method          irrigation_type DEFAULT 'DRIP',
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_irrigation_field ON irrigation_records(field_id);
CREATE INDEX idx_irrigation_date ON irrigation_records(irrigation_date);

-- ============================================
-- 9. PEST & DISEASE OBSERVATIONS
-- ============================================

CREATE TYPE severity_level AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');

CREATE TABLE pest_disease_observations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    field_id        UUID NOT NULL REFERENCES fields(id),
    planting_id     UUID REFERENCES planting_records(id),
    
    -- Observation
    observed_date   DATE NOT NULL,
    pest_or_disease VARCHAR(255) NOT NULL,
    is_disease      BOOLEAN DEFAULT FALSE, -- TRUE = disease, FALSE = pest
    severity        severity_level NOT NULL DEFAULT 'MEDIUM',
    area_affected_percent DECIMAL(5,2),
    
    -- Description
    symptoms        TEXT,
    photo_urls      JSONB DEFAULT '[]',
    
    -- Treatment
    treatment_applied BOOLEAN DEFAULT FALSE,
    treatment_product VARCHAR(255),
    treatment_date  DATE,
    treatment_effectiveness VARCHAR(50),
    
    -- Follow-up
    follow_up_date  DATE,
    resolved        BOOLEAN DEFAULT FALSE,
    resolved_date   DATE,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_pest_field ON pest_disease_observations(field_id);
CREATE INDEX idx_pest_date ON pest_disease_observations(observed_date);
CREATE INDEX idx_pest_severity ON pest_disease_observations(severity);

-- ============================================
-- 10. HARVEST RECORDS
-- ============================================

CREATE TYPE harvest_method AS ENUM ('MANUAL', 'MECHANICAL', 'COMBINE');
CREATE TYPE quality_grade AS ENUM ('GRADE_A', 'GRADE_B', 'GRADE_C', 'REJECTED');
CREATE TYPE harvest_destination AS ENUM ('WAREHOUSE', 'DIRECT_SALE', 'PROCESSING', 'HOME_STORAGE');

CREATE TABLE harvest_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    planting_id     UUID NOT NULL REFERENCES planting_records(id),
    field_id        UUID NOT NULL REFERENCES fields(id),
    
    -- Harvest details
    harvest_date    DATE NOT NULL,
    quantity_kg     DECIMAL(10,2) NOT NULL,
    quality_grade   quality_grade DEFAULT 'GRADE_A',
    harvest_method  harvest_method DEFAULT 'MANUAL',
    destination     harvest_destination DEFAULT 'WAREHOUSE',
    
    -- Labor
    number_of_workers INTEGER,
    total_labor_hours DECIMAL(8,2),
    labor_cost      DECIMAL(12,2),
    
    -- Moisture & quality
    moisture_content_percent DECIMAL(5,2),
    damaged_percent DECIMAL(5,2),
    foreign_matter_percent DECIMAL(5,2),
    
    -- Yield analysis
    yield_kg_ha     DECIMAL(10,2), -- Auto-calculated: quantity / area
    expected_yield_kg DECIMAL(10,2),
    yield_variance_percent DECIMAL(6,2),
    
    -- Media
    photo_urls      JSONB DEFAULT '[]',
    notes           TEXT,
    
    -- Inventory link
    inventory_batch_id UUID,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_harvest_planting ON harvest_records(planting_id);
CREATE INDEX idx_harvest_field ON harvest_records(field_id);
CREATE INDEX idx_harvest_date ON harvest_records(harvest_date);
CREATE INDEX idx_harvest_grade ON harvest_records(quality_grade);
CREATE INDEX idx_harvest_tenant ON harvest_records(tenant_id);

-- ============================================
-- 11. FARM ACTIVITY LOG
-- ============================================

CREATE TYPE activity_type AS ENUM (
    'PLANTING', 'HARVEST', 'INPUT_APPLICATION', 'IRRIGATION',
    'PEST_OBSERVATION', 'FIELD_PREPARATION', 'WEEDING', 'PRUNING',
    'SOIL_TEST', 'GENERAL_OBSERVATION', 'PHOTO', 'OTHER'
);

CREATE TABLE farm_activities (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    farm_id         UUID NOT NULL REFERENCES farms(id),
    field_id        UUID REFERENCES fields(id),
    
    activity_type   activity_type NOT NULL,
    activity_date   DATE NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    
    -- Linked records
    reference_type  VARCHAR(50),
    reference_id    UUID,
    
    -- Media
    photo_urls      JSONB DEFAULT '[]',
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_activity_farm ON farm_activities(farm_id);
CREATE INDEX idx_activity_date ON farm_activities(activity_date DESC);
CREATE INDEX idx_activity_type ON farm_activities(activity_type);

-- ============================================
-- 12. FARM TASKS
-- ============================================

CREATE TYPE task_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'URGENT');
CREATE TYPE task_status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE task_recurrence AS ENUM ('NONE', 'DAILY', 'WEEKLY', 'BIWEEKLY', 'MONTHLY', 'SEASONAL');

CREATE TABLE farm_tasks (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    farm_id         UUID NOT NULL REFERENCES farms(id),
    field_id        UUID REFERENCES fields(id),
    
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    priority        task_priority DEFAULT 'MEDIUM',
    status          task_status DEFAULT 'PENDING',
    
    -- Assignment
    assigned_to     UUID REFERENCES users(id),
    assigned_by     UUID REFERENCES users(id),
    
    -- Timing
    due_date        DATE,
    due_time        TIME,
    completed_at    TIMESTAMPTZ,
    completed_by    UUID REFERENCES users(id),
    
    -- Recurrence
    recurrence      task_recurrence DEFAULT 'NONE',
    recurrence_end_date DATE,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_tasks_farm ON farm_tasks(farm_id);
CREATE INDEX idx_tasks_assigned ON farm_tasks(assigned_to);
CREATE INDEX idx_tasks_status ON farm_tasks(status);
CREATE INDEX idx_tasks_due ON farm_tasks(due_date);

-- ============================================
-- 13. FARM EQUIPMENT
-- ============================================

CREATE TYPE equipment_type AS ENUM (
    'TRACTOR', 'PLOW', 'HARROW', 'PLANTER', 'HARVESTER', 
    'SPRAYER', 'IRRIGATION_SYSTEM', 'TRAILER', 'HAND_TOOL', 'OTHER'
);
CREATE TYPE equipment_status AS ENUM ('AVAILABLE', 'IN_USE', 'MAINTENANCE', 'RETIRED');

CREATE TABLE farm_equipment (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    farm_id         UUID REFERENCES farms(id),
    
    name            VARCHAR(255) NOT NULL,
    equipment_type  equipment_type NOT NULL,
    make            VARCHAR(100),
    model           VARCHAR(100),
    serial_number   VARCHAR(100),
    year_manufactured INTEGER,
    
    purchase_date   DATE,
    purchase_price  DECIMAL(12,2),
    current_value   DECIMAL(12,2),
    
    status          equipment_status DEFAULT 'AVAILABLE',
    
    -- Specs
    fuel_type       VARCHAR(50),
    power_hp        DECIMAL(7,2),
    notes           TEXT,
    photo_urls      JSONB DEFAULT '[]',
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_equipment_tenant ON farm_equipment(tenant_id);
CREATE INDEX idx_equipment_status ON farm_equipment(status);

-- ============================================
-- 14. EQUIPMENT USAGE LOG
-- ============================================

CREATE TABLE equipment_usage (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equipment_id    UUID NOT NULL REFERENCES farm_equipment(id),
    field_id        UUID REFERENCES fields(id),
    operator_id     UUID REFERENCES users(id),
    
    usage_date      DATE NOT NULL,
    hours_used      DECIMAL(5,2),
    fuel_consumed_liters DECIMAL(7,2),
    activity        TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_equip_usage_equipment ON equipment_usage(equipment_id);
CREATE INDEX idx_equip_usage_date ON equipment_usage(usage_date);

-- ============================================
-- 15. SOIL TEST RECORDS
-- ============================================

CREATE TABLE soil_tests (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    field_id        UUID NOT NULL REFERENCES fields(id),
    
    test_date       DATE NOT NULL,
    lab_name        VARCHAR(255),
    lab_report_url  TEXT,
    
    -- Results
    ph_level        DECIMAL(3,1),
    nitrogen_percent DECIMAL(5,2),
    phosphorus_ppm  DECIMAL(7,2),
    potassium_ppm   DECIMAL(7,2),
    organic_matter_percent DECIMAL(5,2),
    soil_texture    VARCHAR(50),
    
    -- Recommendations
    lime_recommendation_kg_ha DECIMAL(10,2),
    fertilizer_recommendation TEXT,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_soil_field ON soil_tests(field_id);
CREATE INDEX idx_soil_date ON soil_tests(test_date);

-- ============================================
-- 16. CERTIFICATIONS
-- ============================================

CREATE TYPE certification_type AS ENUM (
    'ORGANIC', 'FAIR_TRADE', 'GLOBAL_GAP', 'RAINFOREST_ALLIANCE',
    'UTZ', 'BIRD_FRIENDLY', 'OTHER'
);

CREATE TABLE certifications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    farm_id         UUID REFERENCES farms(id),
    
    certification_type certification_type NOT NULL,
    certifying_body VARCHAR(255) NOT NULL,
    certificate_number VARCHAR(100),
    issued_date     DATE NOT NULL,
    expiry_date     DATE NOT NULL,
    certificate_url TEXT,
    
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_cert_farm ON certifications(farm_id);
CREATE INDEX idx_cert_expiry ON certifications(expiry_date);

