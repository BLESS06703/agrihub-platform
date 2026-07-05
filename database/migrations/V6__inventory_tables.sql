-- ============================================
-- AgriHub Malawi - Inventory Management Schema
-- Version: 1.0
-- ============================================

-- ============================================
-- 1. INVENTORY ITEMS CATALOG
-- ============================================

CREATE TYPE inventory_category AS ENUM (
    'SEEDS', 'FERTILIZER', 'PESTICIDE', 'HERBICIDE', 'FUNGICIDE',
    'INSECTICIDE', 'FUEL', 'SPARE_PARTS', 'TOOLS', 'FEED',
    'ANIMAL_HEALTH', 'PACKAGING', 'PRODUCE', 'LIVESTOCK', 'OTHER'
);

CREATE TYPE unit_of_measure AS ENUM (
    'KG', 'GRAM', 'LITER', 'ML', 'PIECE', 'BAG', 'BOTTLE',
    'SACHET', 'CAN', 'DRUM', 'BALE', 'SACK', 'CARTON', 'PACKET', 'OTHER'
);

CREATE TABLE inventory_items (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    -- Identity
    item_name       VARCHAR(255) NOT NULL,
    item_code       VARCHAR(50),
    category        inventory_category NOT NULL,
    unit            unit_of_measure NOT NULL,
    
    -- Details
    brand           VARCHAR(255),
    manufacturer    VARCHAR(255),
    description     TEXT,
    barcode         VARCHAR(100),
    
    -- Stock
    stock_on_hand   DECIMAL(10,2) DEFAULT 0,
    minimum_stock   DECIMAL(10,2) DEFAULT 0,
    reorder_point   DECIMAL(10,2),
    
    -- Pricing
    unit_cost       DECIMAL(10,2),
    average_cost    DECIMAL(10,2),  -- For FIFO/moving average
    last_purchase_price DECIMAL(10,2),
    
    -- Seed-specific
    variety         VARCHAR(255),
    germination_rate DECIMAL(5,2),
    seed_class      VARCHAR(50),    -- CERTIFIED, STANDARD, FARMER_SAVED
    
    -- Chemical-specific
    active_ingredient VARCHAR(255),
    concentration   VARCHAR(100),
    toxicity_class  VARCHAR(50),
    safety_interval_days INTEGER,
    safety_data_sheet_url TEXT,
    is_restricted   BOOLEAN DEFAULT FALSE,
    
    -- Feed-specific
    feed_type       VARCHAR(100),
    crude_protein_percent DECIMAL(5,2),
    
    -- Status
    is_active       BOOLEAN DEFAULT TRUE,
    
    -- Media
    photo_url       TEXT,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, item_code)
);

CREATE INDEX idx_inv_item_tenant ON inventory_items(tenant_id);
CREATE INDEX idx_inv_item_category ON inventory_items(category);
CREATE INDEX idx_inv_item_barcode ON inventory_items(barcode);

-- ============================================
-- 2. STORAGE LOCATIONS
-- ============================================

CREATE TYPE location_type AS ENUM (
    'WAREHOUSE', 'SHED', 'STORE_ROOM', 'COLD_ROOM', 'SILO', 
    'BIN', 'SHELF', 'FIELD', 'VEHICLE', 'OTHER'
);

CREATE TABLE storage_locations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    location_name   VARCHAR(255) NOT NULL,
    location_code   VARCHAR(50) NOT NULL,
    location_type   location_type NOT NULL,
    
    parent_id       UUID REFERENCES storage_locations(id),
    
    -- Hierarchy
    zone            VARCHAR(50),
    row             VARCHAR(50),
    rack            VARCHAR(50),
    shelf           VARCHAR(50),
    bin             VARCHAR(50),
    
    capacity_kg     DECIMAL(10,2),
    current_fill_kg DECIMAL(10,2) DEFAULT 0,
    
    -- Environmental
    has_cold_storage BOOLEAN DEFAULT FALSE,
    temperature_min  DECIMAL(5,2),
    temperature_max  DECIMAL(5,2),
    humidity_control BOOLEAN DEFAULT FALSE,
    
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, location_code)
);

CREATE INDEX idx_loc_tenant ON storage_locations(tenant_id);
CREATE INDEX idx_loc_parent ON storage_locations(parent_id);
CREATE INDEX idx_loc_type ON storage_locations(location_type);

-- ============================================
-- 3. STOCK TRANSACTIONS (Master Table)
-- ============================================

CREATE TYPE transaction_direction AS ENUM ('IN', 'OUT', 'TRANSFER', 'ADJUSTMENT');
CREATE TYPE transaction_reason AS ENUM (
    'PURCHASE', 'SALE', 'PRODUCE_INTAKE', 'RETURN_TO_SUPPLIER',
    'TRANSFER_IN', 'TRANSFER_OUT', 'ISSUE_TO_FARM', 'ISSUE_TO_FIELD',
    'CONSUMPTION', 'DAMAGE', 'EXPIRY', 'THEFT', 'COUNT_ADJUSTMENT',
    'OPENING_BALANCE', 'WRITE_OFF', 'OTHER'
);

CREATE TABLE stock_transactions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    item_id         UUID NOT NULL REFERENCES inventory_items(id),
    location_id     UUID REFERENCES storage_locations(id),
    
    -- Transaction
    direction       transaction_direction NOT NULL,
    reason          transaction_reason NOT NULL,
    quantity        DECIMAL(10,2) NOT NULL,
    unit_cost       DECIMAL(10,2),
    total_cost      DECIMAL(12,2),
    
    -- Dates
    transaction_date DATE NOT NULL,
    
    -- Batch tracking
    batch_number    VARCHAR(100),
    lot_number      VARCHAR(100),
    expiry_date     DATE,
    
    -- Source/Destination
    source_location_id UUID REFERENCES storage_locations(id),
    destination_location_id UUID REFERENCES storage_locations(id),
    
    -- Links
    reference_type  VARCHAR(50),  -- purchase_order, harvest, sale, etc.
    reference_id    UUID,
    
    -- Quality at transaction
    quality_grade   VARCHAR(20),
    moisture_content DECIMAL(5,2),
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_stock_txn_item ON stock_transactions(item_id);
CREATE INDEX idx_stock_txn_date ON stock_transactions(transaction_date);
CREATE INDEX idx_stock_txn_direction ON stock_transactions(direction);
CREATE INDEX idx_stock_txn_tenant ON stock_transactions(tenant_id);
CREATE INDEX idx_stock_txn_batch ON stock_transactions(batch_number);
CREATE INDEX idx_stock_txn_expiry ON stock_transactions(expiry_date);

-- ============================================
-- 4. PURCHASE ORDERS
-- ============================================

CREATE TYPE po_status AS ENUM (
    'DRAFT', 'SUBMITTED', 'APPROVED', 'PARTIALLY_RECEIVED',
    'RECEIVED', 'CANCELLED', 'REJECTED'
);

CREATE TABLE purchase_orders (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    po_number       VARCHAR(50) NOT NULL,
    supplier_id     UUID,
    
    order_date      DATE NOT NULL,
    expected_delivery_date DATE,
    status          po_status DEFAULT 'DRAFT',
    
    subtotal        DECIMAL(12,2) DEFAULT 0,
    tax_amount      DECIMAL(12,2) DEFAULT 0,
    total_amount    DECIMAL(12,2) DEFAULT 0,
    
    approved_by     UUID REFERENCES users(id),
    approved_at     TIMESTAMPTZ,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, po_number)
);

CREATE INDEX idx_po_tenant ON purchase_orders(tenant_id);
CREATE INDEX idx_po_status ON purchase_orders(status);
CREATE INDEX idx_po_supplier ON purchase_orders(supplier_id);

-- ============================================
-- 5. PO LINE ITEMS
-- ============================================

CREATE TYPE po_item_status AS ENUM ('ORDERED', 'PARTIALLY_RECEIVED', 'RECEIVED', 'CANCELLED');

CREATE TABLE purchase_order_items (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    po_id           UUID NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    item_id         UUID NOT NULL REFERENCES inventory_items(id),
    
    quantity_ordered DECIMAL(10,2) NOT NULL,
    quantity_received DECIMAL(10,2) DEFAULT 0,
    unit_price      DECIMAL(10,2) NOT NULL,
    total_price     DECIMAL(12,2),
    
    status          po_item_status DEFAULT 'ORDERED',
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_po_item_po ON purchase_order_items(po_id);

-- ============================================
-- 6. SUPPLIERS
-- ============================================

CREATE TYPE supplier_category AS ENUM (
    'SEED_SUPPLIER', 'AGROCHEMICAL', 'EQUIPMENT', 'FEED_SUPPLIER',
    'VETERINARY', 'PACKAGING', 'FUEL', 'GENERAL', 'OTHER'
);

CREATE TABLE suppliers (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    supplier_name   VARCHAR(255) NOT NULL,
    supplier_code   VARCHAR(50),
    category        supplier_category NOT NULL,
    
    contact_person  VARCHAR(255),
    phone           VARCHAR(50),
    email           VARCHAR(255),
    address         TEXT,
    location        GEOGRAPHY(POINT, 4326),
    
    tax_id          VARCHAR(100),
    payment_terms   VARCHAR(100),
    
    rating          INTEGER CHECK (rating BETWEEN 1 AND 5),
    is_active       BOOLEAN DEFAULT TRUE,
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_supplier_tenant ON suppliers(tenant_id);
CREATE INDEX idx_supplier_category ON suppliers(category);

-- ============================================
-- 7. STOCK COUNTS (Physical Inventory)
-- ============================================

CREATE TYPE count_status AS ENUM ('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');

CREATE TABLE stock_counts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    count_name      VARCHAR(255) NOT NULL,
    count_date      DATE NOT NULL,
    location_id     UUID REFERENCES storage_locations(id),
    
    status          count_status DEFAULT 'PLANNED',
    completed_by    UUID REFERENCES users(id),
    completed_at    TIMESTAMPTZ,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_count_tenant ON stock_counts(tenant_id);
CREATE INDEX idx_count_status ON stock_counts(status);

-- ============================================
-- 8. STOCK COUNT ITEMS
-- ============================================

CREATE TABLE stock_count_items (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    count_id        UUID NOT NULL REFERENCES stock_counts(id) ON DELETE CASCADE,
    item_id         UUID NOT NULL REFERENCES inventory_items(id),
    location_id     UUID REFERENCES storage_locations(id),
    
    system_quantity DECIMAL(10,2) DEFAULT 0,
    counted_quantity DECIMAL(10,2),
    variance        DECIMAL(10,2),
    
    counted_by      UUID REFERENCES users(id),
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_count_item_count ON stock_count_items(count_id);

-- ============================================
-- 9. FUMIGATION & TREATMENT LOGS
-- ============================================

CREATE TABLE fumigation_logs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    location_id     UUID REFERENCES storage_locations(id),
    
    treatment_date  DATE NOT NULL,
    chemical_used   VARCHAR(255) NOT NULL,
    dosage          VARCHAR(100),
    target_pest     VARCHAR(255),
    
    treated_by      VARCHAR(255),
    re_entry_date   DATE,
    
    notes           TEXT,
    certificate_url TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fum_location ON fumigation_logs(location_id);
CREATE INDEX idx_fum_date ON fumigation_logs(treatment_date);

-- ============================================
-- 10. PRODUCE LOTS (for warehouse produce tracking)
-- ============================================

CREATE TYPE lot_status AS ENUM ('RECEIVED', 'IN_STORAGE', 'TREATED', 'DISPATCHED', 'CLOSED');

CREATE TABLE produce_lots (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    location_id     UUID REFERENCES storage_locations(id),
    
    lot_number      VARCHAR(100) NOT NULL,
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    variety_id      UUID REFERENCES crop_varieties(id),
    
    -- Source
    source_type     VARCHAR(50),  -- FARMER, COOPERATIVE, PURCHASE
    source_id       UUID,
    source_name     VARCHAR(255),
    
    -- Quantity & Quality
    initial_quantity_kg DECIMAL(10,2) NOT NULL,
    current_quantity_kg DECIMAL(10,2),
    quality_grade   VARCHAR(20),
    moisture_content DECIMAL(5,2),
    
    -- Dates
    received_date   DATE NOT NULL,
    dispatch_date   DATE,
    status          lot_status DEFAULT 'RECEIVED',
    
    -- Warehouse Receipt
    warehouse_receipt_number VARCHAR(100),
    receipt_status  VARCHAR(50),  -- ACTIVE, PLEDGED, TRANSFERRED, REDEEMED
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, lot_number)
);

CREATE INDEX idx_lot_tenant ON produce_lots(tenant_id);
CREATE INDEX idx_lot_location ON produce_lots(location_id);
CREATE INDEX idx_lot_status ON produce_lots(status);
CREATE INDEX idx_lot_crop ON produce_lots(crop_id);

-- ============================================
-- 11. QUALITY INSPECTIONS
-- ============================================

CREATE TYPE inspection_result AS ENUM ('PASSED', 'PASSED_WITH_NOTES', 'FAILED');

CREATE TABLE quality_inspections (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    lot_id          UUID REFERENCES produce_lots(id),
    stock_transaction_id UUID REFERENCES stock_transactions(id),
    
    inspection_date DATE NOT NULL,
    inspector       VARCHAR(255),
    result          inspection_result NOT NULL,
    
    -- Parameters
    moisture        DECIMAL(5,2),
    purity_percent  DECIMAL(5,2),
    foreign_matter_percent DECIMAL(5,2),
    damaged_percent DECIMAL(5,2),
    insect_damage_percent DECIMAL(5,2),
    mold_present    BOOLEAN,
    odor_normal     BOOLEAN,
    color_grade     VARCHAR(50),
    size_grade      VARCHAR(50),
    
    -- Additional checks
    germination_test DECIMAL(5,2),  -- For seeds
    aflatoxin_ppb   DECIMAL(7,2),   -- For grains/groundnuts
    
    notes           TEXT,
    photo_urls      JSONB DEFAULT '[]',
    report_url      TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inspection_lot ON quality_inspections(lot_id);
CREATE INDEX idx_inspection_date ON quality_inspections(inspection_date);
CREATE INDEX idx_inspection_result ON quality_inspections(result);

