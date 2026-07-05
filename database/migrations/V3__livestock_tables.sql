-- ============================================
-- AgriHub Malawi - Livestock Management Schema
-- Version: 1.0
-- ============================================

-- ============================================
-- 1. ANIMAL REGISTRY
-- ============================================

CREATE TYPE animal_species AS ENUM (
    'CATTLE', 'GOAT', 'SHEEP', 'PIG', 
    'CHICKEN_BROILER', 'CHICKEN_LAYER', 'DUCK', 'TURKEY',
    'RABBIT', 'GUINEA_FOWL', 'FISH', 'BEES'
);

CREATE TYPE animal_sex AS ENUM ('MALE', 'FEMALE');
CREATE TYPE animal_status AS ENUM ('ALIVE', 'SICK', 'SOLD', 'DECEASED', 'LOST', 'SLAUGHTERED');
CREATE TYPE animal_purpose AS ENUM (
    'MEAT', 'DAIRY', 'BREEDING', 'LAYER', 'DUAL_PURPOSE', 
    'DRAFT', 'FATTENING', 'WOOL', 'HONEY', 'OTHER'
);

CREATE TABLE animals (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    -- Identity
    tag_id          VARCHAR(50) NOT NULL,
    name            VARCHAR(255),
    species         animal_species NOT NULL,
    breed           VARCHAR(255),
    sex             animal_sex NOT NULL,
    purpose         animal_purpose,
    
    -- Birth & Origin
    date_of_birth   DATE,
    birth_weight_kg DECIMAL(7,2),
    purchase_date   DATE,
    purchase_price  DECIMAL(12,2),
    source          VARCHAR(255),
    
    -- Parentage
    sire_tag        VARCHAR(50),
    dam_tag         VARCHAR(50),
    
    -- Physical
    color           VARCHAR(100),
    markings        TEXT,
    initial_weight_kg DECIMAL(7,2),
    current_weight_kg DECIMAL(7,2),
    
    -- Status
    status          animal_status DEFAULT 'ALIVE',
    status_date     DATE,
    status_reason   TEXT,
    
    -- Housing
    pen_id          VARCHAR(100),
    house_id        VARCHAR(100),
    
    -- Pig-specific
    sow_parity      INTEGER,        -- Number of litters for sows
    farrowing_status VARCHAR(50),    -- OPEN, GESTATING, FARROWING, LACTATING, WEANED
    
    -- Poultry-specific
    flock_id        UUID,            -- For batch-managed poultry
    
    -- Media
    photo_urls      JSONB DEFAULT '[]',
    notes           TEXT,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, tag_id)
);

CREATE INDEX idx_animals_tenant ON animals(tenant_id);
CREATE INDEX idx_animals_species ON animals(species);
CREATE INDEX idx_animals_status ON animals(status);
CREATE INDEX idx_animals_tag ON animals(tag_id);
CREATE INDEX idx_animals_pen ON animals(pen_id);

-- ============================================
-- 2. FLOCKS (for batch-managed poultry/fish)
-- ============================================

CREATE TYPE flock_status AS ENUM ('ACTIVE', 'SOLD', 'DEPLETED', 'MERGED');

CREATE TABLE flocks (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    flock_name      VARCHAR(255) NOT NULL,
    species         animal_species NOT NULL,
    breed           VARCHAR(255),
    initial_count   INTEGER NOT NULL,
    current_count   INTEGER,
    
    start_date      DATE NOT NULL,
    end_date        DATE,
    status          flock_status DEFAULT 'ACTIVE',
    
    house_id        VARCHAR(100),
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_flocks_tenant ON flocks(tenant_id);

-- ============================================
-- 3. VACCINATION RECORDS
-- ============================================

CREATE TABLE vaccinations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    animal_id       UUID REFERENCES animals(id),
    flock_id        UUID REFERENCES flocks(id),
    
    vaccine_name    VARCHAR(255) NOT NULL,
    batch_number    VARCHAR(100),
    date_administered DATE NOT NULL,
    next_due_date   DATE,
    administered_by VARCHAR(255),
    
    dosage          VARCHAR(100),
    route           VARCHAR(50), -- INJECTION, ORAL, NASAL, IN_FEED, IN_WATER
    injection_site  VARCHAR(50),
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,
    
    CHECK (animal_id IS NOT NULL OR flock_id IS NOT NULL)
);

CREATE INDEX idx_vax_animal ON vaccinations(animal_id);
CREATE INDEX idx_vax_flock ON vaccinations(flock_id);
CREATE INDEX idx_vax_date ON vaccinations(date_administered);
CREATE INDEX idx_vax_next_due ON vaccinations(next_due_date);

-- ============================================
-- 4. VACCINE SCHEDULE TEMPLATES
-- ============================================

CREATE TABLE vaccine_schedules (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    species         animal_species NOT NULL,
    vaccine_name    VARCHAR(255) NOT NULL,
    age_days        INTEGER,           -- When to administer
    booster_days    INTEGER,           -- Days until booster
    is_mandatory    BOOLEAN DEFAULT FALSE,
    description     TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vax_schedule_species ON vaccine_schedules(species);

-- ============================================
-- 5. HEALTH RECORDS
-- ============================================

CREATE TYPE health_condition AS ENUM (
    'AFRICAN_SWINE_FEVER', 'FOOT_AND_MOUTH', 'ERYSIPELAS', 'PRRS',
    'NEWCASTLE', 'AVIAN_INFLUENZA', 'MASTITIS', 'BLOAT', 'PNEUMONIA',
    'SCOURS', 'MANGE', 'WORMS', 'COCCIDIOSIS', 'ANTHRAX',
    'BLACKLEG', 'LUMPY_SKIN', 'RIFT_VALLEY_FEVER', 'TUBERCULOSIS',
    'BRUCELLOSIS', 'INJURY', 'OTHER'
);

CREATE TYPE recovery_status AS ENUM (
    'UNDER_TREATMENT', 'RECOVERING', 'RECOVERED', 'CHRONIC', 'FATAL'
);

CREATE TABLE health_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    animal_id       UUID NOT NULL REFERENCES animals(id),
    
    -- Diagnosis
    diagnosis_date  DATE NOT NULL,
    condition       health_condition NOT NULL,
    severity        VARCHAR(20) DEFAULT 'MODERATE', -- MILD, MODERATE, SEVERE, CRITICAL
    symptoms        TEXT,
    
    -- Veterinarian
    vet_name        VARCHAR(255),
    vet_contact     VARCHAR(100),
    diagnosis_notes TEXT,
    
    -- Treatment
    treatment       TEXT,
    medication      VARCHAR(255),
    dosage          VARCHAR(100),
    frequency       VARCHAR(100),
    duration_days   INTEGER,
    treatment_cost  DECIMAL(12,2),
    
    -- Recovery
    recovery_status recovery_status DEFAULT 'UNDER_TREATMENT',
    follow_up_date  DATE,
    resolved_date   DATE,
    
    -- Attachments
    lab_report_urls JSONB DEFAULT '[]',
    photo_urls      JSONB DEFAULT '[]',
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_health_animal ON health_records(animal_id);
CREATE INDEX idx_health_date ON health_records(diagnosis_date);
CREATE INDEX idx_health_condition ON health_records(condition);

-- ============================================
-- 6. WEIGHT TRACKING
-- ============================================

CREATE TABLE weight_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    animal_id       UUID NOT NULL REFERENCES animals(id),
    
    weight_date     DATE NOT NULL,
    weight_kg       DECIMAL(7,2) NOT NULL,
    body_condition_score INTEGER CHECK (body_condition_score BETWEEN 1 AND 5),
    
    weighed_by      UUID REFERENCES users(id),
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_weight_animal ON weight_records(animal_id);
CREATE INDEX idx_weight_date ON weight_records(weight_date);

-- ============================================
-- 7. BREEDING RECORDS
-- ============================================

CREATE TYPE breeding_method AS ENUM ('NATURAL', 'ARTIFICIAL_INSEMINATION');
CREATE TYPE pregnancy_status AS ENUM ('PENDING', 'CONFIRMED', 'FAILED', 'ABORTED');

CREATE TABLE breeding_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    -- Female
    female_animal_id UUID NOT NULL REFERENCES animals(id),
    
    -- Male
    male_animal_id  UUID REFERENCES animals(id),
    semen_source    VARCHAR(255),   -- For AI
    semen_straw_id  VARCHAR(100),   -- For AI
    ai_technician   VARCHAR(255),   -- For AI
    
    -- Event
    breeding_date   DATE NOT NULL,
    method          breeding_method DEFAULT 'NATURAL',
    
    -- Pregnancy check
    pregnancy_check_date DATE,
    pregnancy_status pregnancy_status,
    expected_delivery_date DATE,
    
    -- Result
    delivery_date   DATE,
    delivery_notes  TEXT,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_breeding_female ON breeding_records(female_animal_id);
CREATE INDEX idx_breeding_date ON breeding_records(breeding_date);
CREATE INDEX idx_breeding_expected ON breeding_records(expected_delivery_date);

-- ============================================
-- 8. BIRTH / OFFSPRING RECORDS
-- ============================================

CREATE TABLE birth_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    breeding_id     UUID REFERENCES breeding_records(id),
    mother_id       UUID NOT NULL REFERENCES animals(id),
    
    birth_date      DATE NOT NULL,
    total_born      INTEGER NOT NULL,
    born_alive      INTEGER NOT NULL,
    stillborn       INTEGER DEFAULT 0,
    mummified       INTEGER DEFAULT 0,   -- Pig-specific
    
    -- Litter details
    litter_birth_weight_kg DECIMAL(7,2),
    average_birth_weight_kg DECIMAL(6,3),
    
    -- Weaning
    weaning_date    DATE,
    number_weaned   INTEGER,
    weaning_weight_avg_kg DECIMAL(6,3),
    pre_weaning_mortality INTEGER,
    
    -- Pig-specific
    litter_id       VARCHAR(50),
    sow_parity      INTEGER,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_birth_mother ON birth_records(mother_id);
CREATE INDEX idx_birth_date ON birth_records(birth_date);

-- ============================================
-- 9. FEEDING RECORDS
-- ============================================

CREATE TYPE feed_type AS ENUM (
    'GRAZE', 'HAY', 'SILAGE', 'CONCENTRATE', 'SUPPLEMENT',
    'CREEP_FEED', 'GROWER_MEAL', 'FINISHER_MEAL', 'SOW_MEAL',
    'LAYER_MASH', 'BROILER_STARTER', 'BROILER_FINISHER',
    'FISH_FEED', 'MINERAL_BLOCK', 'WATER', 'OTHER'
);

CREATE TABLE feeding_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    animal_id       UUID REFERENCES animals(id),
    flock_id        UUID REFERENCES flocks(id),
    pen_id          VARCHAR(100),
    
    feed_date       DATE NOT NULL,
    feed_time       TIME,
    feed_type       feed_type NOT NULL,
    quantity_kg     DECIMAL(8,2),
    cost_per_kg     DECIMAL(10,2),
    total_cost      DECIMAL(12,2),
    
    -- Feed conversion
    feed_consumed_kg DECIMAL(8,2),
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_feeding_animal ON feeding_records(animal_id);
CREATE INDEX idx_feeding_date ON feeding_records(feed_date);

-- ============================================
-- 10. MILK PRODUCTION
-- ============================================

CREATE TABLE milk_production (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    animal_id       UUID NOT NULL REFERENCES animals(id),
    
    production_date DATE NOT NULL,
    morning_yield_liters DECIMAL(6,2),
    evening_yield_liters DECIMAL(6,2),
    total_yield_liters DECIMAL(6,2) GENERATED ALWAYS AS 
        (morning_yield_liters + evening_yield_liters) STORED,
    
    fat_percentage  DECIMAL(4,2),
    protein_percentage DECIMAL(4,2),
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_milk_animal ON milk_production(animal_id);
CREATE INDEX idx_milk_date ON milk_production(production_date);

-- ============================================
-- 11. EGG PRODUCTION (Flock level)
-- ============================================

CREATE TABLE egg_production (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    flock_id        UUID NOT NULL REFERENCES flocks(id),
    
    production_date DATE NOT NULL,
    eggs_collected  INTEGER NOT NULL,
    broken_eggs     INTEGER DEFAULT 0,
    eggs_sold       INTEGER DEFAULT 0,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(flock_id, production_date)
);

CREATE INDEX idx_egg_flock ON egg_production(flock_id);
CREATE INDEX idx_egg_date ON egg_production(production_date);

-- ============================================
-- 12. LIVESTOCK SALES
-- ============================================

CREATE TYPE sale_type AS ENUM (
    'SLAUGHTER', 'LIVE_WEANER', 'LIVE_GROWER', 
    'BREEDING_STOCK', 'CULL', 'OTHER'
);

CREATE TABLE livestock_sales (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    animal_id       UUID REFERENCES animals(id),
    flock_id        UUID REFERENCES flocks(id),
    
    sale_date       DATE NOT NULL,
    sale_type       sale_type NOT NULL,
    buyer_name      VARCHAR(255),
    buyer_contact   VARCHAR(100),
    
    quantity        INTEGER DEFAULT 1,
    weight_at_sale_kg DECIMAL(7,2),
    price_per_kg    DECIMAL(10,2),
    total_price     DECIMAL(12,2),
    
    payment_received BOOLEAN DEFAULT FALSE,
    payment_date    DATE,
    payment_method  VARCHAR(50),
    
    notes           TEXT,
    
    -- Finance link
    income_record_id UUID,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_sale_animal ON livestock_sales(animal_id);
CREATE INDEX idx_sale_date ON livestock_sales(sale_date);

-- ============================================
-- 13. MORTALITY RECORDS
-- ============================================

CREATE TYPE death_cause AS ENUM (
    'DISEASE', 'ACCIDENT', 'PREDATOR', 'OLD_AGE', 
    'UNKNOWN', 'CULLED', 'SLAUGHTERED_FOR_FOOD'
);

CREATE TABLE mortality_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    animal_id       UUID REFERENCES animals(id),
    flock_id        UUID REFERENCES flocks(id),
    
    death_date      DATE NOT NULL,
    cause           death_cause NOT NULL,
    cause_detail    TEXT,
    disposal_method VARCHAR(100),
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_mortality_date ON mortality_records(death_date);
CREATE INDEX idx_mortality_cause ON mortality_records(cause);

-- ============================================
-- 14. GRAZING MANAGEMENT
-- ============================================

CREATE TABLE grazing_paddocks (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    paddock_name    VARCHAR(255) NOT NULL,
    area_hectares   DECIMAL(8,2),
    boundary        GEOGRAPHY(POLYGON, 4326),
    status          VARCHAR(50) DEFAULT 'RESTING', -- GRAZING, RESTING, CLOSED
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE TABLE grazing_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    paddock_id      UUID NOT NULL REFERENCES grazing_paddocks(id),
    
    date_in         DATE NOT NULL,
    date_out        DATE,
    head_count      INTEGER NOT NULL,
    species         animal_species NOT NULL,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_grazing_paddock ON grazing_records(paddock_id);

-- ============================================
-- 15. HOUSING MANAGEMENT
-- ============================================

CREATE TYPE house_type AS ENUM (
    'PEN', 'BARN', 'COOP', 'POND', 'HIVE', 'STABLE', 'KRAAL', 'FREE_RANGE', 'OTHER'
);

CREATE TABLE animal_housing (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    house_name      VARCHAR(255) NOT NULL,
    house_type      house_type NOT NULL,
    capacity        INTEGER,
    current_occupancy INTEGER DEFAULT 0,
    
    location        GEOGRAPHY(POINT, 4326),
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    
    last_cleaned    DATE,
    cleaning_schedule VARCHAR(100),
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_housing_tenant ON animal_housing(tenant_id);

