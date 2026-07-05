-- ============================================
-- AgriHub Malawi - Warehouse Management Schema
-- Version: 1.0
-- ============================================

-- ============================================
-- 1. WAREHOUSES
-- ============================================

CREATE TYPE warehouse_type AS ENUM (
    'PRIVATE', 'PUBLIC', 'COOPERATIVE', 'WRS_CERTIFIED', 'COLD_STORAGE'
);

CREATE TYPE storage_condition AS ENUM (
    'AMBIENT', 'COLD_STORAGE', 'CONTROLLED_ATMOSPHERE', 'HERMETIC', 'OPEN_YARD'
);

CREATE TABLE warehouses (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    warehouse_name  VARCHAR(255) NOT NULL,
    warehouse_code  VARCHAR(50) NOT NULL,
    warehouse_type  warehouse_type NOT NULL,
    
    -- Location
    location        GEOGRAPHY(POINT, 4326),
    address         TEXT,
    district        VARCHAR(100),
    
    -- Capacity
    total_capacity_kg DECIMAL(12,2),
    current_occupancy_kg DECIMAL(12,2) DEFAULT 0,
    number_of_bays  INTEGER,
    
    -- Conditions
    storage_condition storage_condition DEFAULT 'AMBIENT',
    has_weighbridge BOOLEAN DEFAULT FALSE,
    has_dryer       BOOLEAN DEFAULT FALSE,
    has_cleaner     BOOLEAN DEFAULT FALSE,
    has_lab         BOOLEAN DEFAULT FALSE,
    
    -- Certification
    wrs_certified   BOOLEAN DEFAULT FALSE,
    wrs_certificate_number VARCHAR(100),
    gmp_certified   BOOLEAN DEFAULT FALSE,
    
    -- Contact
    manager_name    VARCHAR(255),
    manager_phone   VARCHAR(50),
    manager_email   VARCHAR(255),
    
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    operating_hours TEXT,
    notes           TEXT,
    photo_urls      JSONB DEFAULT '[]',
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, warehouse_code)
);

CREATE INDEX idx_warehouse_tenant ON warehouses(tenant_id);
CREATE INDEX idx_warehouse_location ON warehouses USING GIST(location);
CREATE INDEX idx_warehouse_type ON warehouses(warehouse_type);

-- ============================================
-- 2. WAREHOUSE SECTIONS
-- ============================================

CREATE TABLE warehouse_sections (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_id    UUID NOT NULL REFERENCES warehouses(id),
    
    section_name    VARCHAR(255) NOT NULL,
    section_code    VARCHAR(50) NOT NULL,
    capacity_kg     DECIMAL(10,2),
    current_fill_kg DECIMAL(10,2) DEFAULT 0,
    
    storage_condition storage_condition,
    has_pest_control BOOLEAN DEFAULT FALSE,
    
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(warehouse_id, section_code)
);

CREATE INDEX idx_section_warehouse ON warehouse_sections(warehouse_id);

-- ============================================
-- 3. WAREHOUSE RECEIPTS
-- ============================================

CREATE TYPE receipt_status AS ENUM (
    'ACTIVE', 'PLEDGED', 'PARTIALLY_REDEEMED', 'REDEEMED', 'TRANSFERRED', 'CANCELLED'
);

CREATE TABLE warehouse_receipts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    warehouse_id    UUID NOT NULL REFERENCES warehouses(id),
    
    receipt_number  VARCHAR(100) NOT NULL,
    depositor_id    UUID NOT NULL REFERENCES users(id),
    depositor_name  VARCHAR(255) NOT NULL,
    
    -- Produce details
    lot_id          UUID REFERENCES produce_lots(id),
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    quantity_kg     DECIMAL(10,2) NOT NULL,
    quality_grade   VARCHAR(20),
    
    -- Dates
    issue_date      DATE NOT NULL,
    expiry_date     DATE,
    
    -- Status
    status          receipt_status DEFAULT 'ACTIVE',
    remaining_quantity_kg DECIMAL(10,2),
    
    -- Pledge info (if used as collateral)
    pledged_to      VARCHAR(255),
    pledge_date     DATE,
    pledge_amount   DECIMAL(12,2),
    pledge_released_date DATE,
    
    -- Fees
    storage_rate_per_kg_per_day DECIMAL(10,4),
    handling_fee    DECIMAL(10,2),
    
    notes           TEXT,
    document_url    TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, receipt_number)
);

CREATE INDEX idx_receipt_warehouse ON warehouse_receipts(warehouse_id);
CREATE INDEX idx_receipt_depositor ON warehouse_receipts(depositor_id);
CREATE INDEX idx_receipt_status ON warehouse_receipts(status);
CREATE INDEX idx_receipt_lot ON warehouse_receipts(lot_id);

-- ============================================
-- 4. WAREHOUSE INTAKE
-- ============================================

CREATE TABLE warehouse_intake (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    warehouse_id    UUID NOT NULL REFERENCES warehouses(id),
    section_id      UUID REFERENCES warehouse_sections(id),
    
    intake_date     DATE NOT NULL,
    intake_number   VARCHAR(50) NOT NULL,
    
    -- Source
    source_type     VARCHAR(50),  -- FARMER, COOPERATIVE, PURCHASE, TRANSFER
    source_id       UUID,
    source_name     VARCHAR(255),
    
    -- Transport
    vehicle_number  VARCHAR(50),
    driver_name     VARCHAR(255),
    transporter_name VARCHAR(255),
    
    -- Produce
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    quantity_kg     DECIMAL(10,2) NOT NULL,
    number_of_bags  INTEGER,
    bag_weight_kg   DECIMAL(7,2),
    
    -- Quality at intake
    moisture_content DECIMAL(5,2),
    damaged_percent DECIMAL(5,2),
    foreign_matter_percent DECIMAL(5,2),
    initial_grade   VARCHAR(20),
    
    -- Inspection
    inspected_by    UUID REFERENCES users(id),
    inspection_notes TEXT,
    
    -- Result
    accepted        BOOLEAN DEFAULT TRUE,
    rejection_reason TEXT,
    
    -- Receipt
    receipt_id      UUID REFERENCES warehouse_receipts(id),
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(tenant_id, intake_number)
);

CREATE INDEX idx_intake_warehouse ON warehouse_intake(warehouse_id);
CREATE INDEX idx_intake_date ON warehouse_intake(intake_date);

-- ============================================
-- 5. WAREHOUSE DISPATCH
-- ============================================

CREATE TYPE dispatch_type AS ENUM (
    'SALE', 'TRANSFER', 'RETURN_TO_DEPOSITOR', 'PROCESSING', 'DONATION', 'DESTRUCTION'
);

CREATE TABLE warehouse_dispatch (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    warehouse_id    UUID NOT NULL REFERENCES warehouses(id),
    
    dispatch_date   DATE NOT NULL,
    dispatch_number VARCHAR(50) NOT NULL,
    dispatch_type   dispatch_type NOT NULL,
    
    -- Destination
    destination_name VARCHAR(255),
    destination_address TEXT,
    recipient_name  VARCHAR(255),
    recipient_contact VARCHAR(100),
    
    -- Produce
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    quantity_kg     DECIMAL(10,2) NOT NULL,
    number_of_bags  INTEGER,
    quality_grade   VARCHAR(20),
    
    -- Source (which intake/lot)
    lot_id          UUID REFERENCES produce_lots(id),
    receipt_id      UUID REFERENCES warehouse_receipts(id),
    
    -- Transport
    vehicle_number  VARCHAR(50),
    driver_name     VARCHAR(255),
    seal_number     VARCHAR(100),
    
    -- Documents
    dispatch_note_url TEXT,
    invoice_id      UUID,
    
    -- Link to marketplace order
    order_id        UUID,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(tenant_id, dispatch_number)
);

CREATE INDEX idx_dispatch_warehouse ON warehouse_dispatch(warehouse_id);
CREATE INDEX idx_dispatch_date ON warehouse_dispatch(dispatch_date);
CREATE INDEX idx_dispatch_type ON warehouse_dispatch(dispatch_type);

-- ============================================
-- 6. STORAGE FEES
-- ============================================

CREATE TYPE fee_status AS ENUM ('ACCRUING', 'INVOICED', 'PARTIALLY_PAID', 'PAID', 'WAIVED');

CREATE TABLE storage_fees (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    receipt_id      UUID NOT NULL REFERENCES warehouse_receipts(id),
    depositor_id    UUID NOT NULL REFERENCES users(id),
    
    -- Fee calculation
    fee_period_start DATE NOT NULL,
    fee_period_end  DATE NOT NULL,
    quantity_kg     DECIMAL(10,2) NOT NULL,
    days_in_storage INTEGER NOT NULL,
    rate_per_kg_per_day DECIMAL(10,4) NOT NULL,
    calculated_fee  DECIMAL(12,2) NOT NULL,
    
    -- Additional charges
    handling_charge DECIMAL(10,2) DEFAULT 0,
    fumigation_charge DECIMAL(10,2) DEFAULT 0,
    other_charges   DECIMAL(10,2) DEFAULT 0,
    total_fee       DECIMAL(12,2),
    
    status          fee_status DEFAULT 'ACCRUING',
    invoice_id      UUID,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_fee_receipt ON storage_fees(receipt_id);
CREATE INDEX idx_fee_depositor ON storage_fees(depositor_id);
CREATE INDEX idx_fee_status ON storage_fees(status);

-- ============================================
-- 7. COLD STORAGE MONITORING
-- ============================================

CREATE TABLE cold_storage_logs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_id    UUID NOT NULL REFERENCES warehouses(id),
    section_id      UUID REFERENCES warehouse_sections(id),
    
    recorded_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    temperature_celsius DECIMAL(5,2),
    humidity_percent DECIMAL(5,2),
    
    door_open       BOOLEAN DEFAULT FALSE,
    alarm_triggered BOOLEAN DEFAULT FALSE,
    
    notes           TEXT
);

CREATE INDEX idx_cold_warehouse ON cold_storage_logs(warehouse_id);
CREATE INDEX idx_cold_time ON cold_storage_logs(recorded_at DESC);

-- ============================================
-- 8. WEIGHBRIDGE RECORDS
-- ============================================

CREATE TABLE weighbridge_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    warehouse_id    UUID NOT NULL REFERENCES warehouses(id),
    
    ticket_number   VARCHAR(50) NOT NULL,
    vehicle_number  VARCHAR(50) NOT NULL,
    driver_name     VARCHAR(255),
    
    -- Weights
    gross_weight_kg DECIMAL(10,2) NOT NULL,
    tare_weight_kg  DECIMAL(10,2),
    net_weight_kg   DECIMAL(10,2),
    
    weigh_in_time   TIMESTAMPTZ,
    weigh_out_time  TIMESTAMPTZ,
    
    crop_id         UUID REFERENCES crop_catalog(id),
    
    operator_id     UUID REFERENCES users(id),
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(tenant_id, ticket_number)
);

CREATE INDEX idx_weigh_warehouse ON weighbridge_records(warehouse_id);
CREATE INDEX idx_weigh_date ON weighbridge_records(created_at);

