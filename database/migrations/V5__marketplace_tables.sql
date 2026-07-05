-- ============================================
-- AgriHub Malawi - Marketplace Schema
-- Version: 1.0
-- ============================================

-- ============================================
-- 1. PRODUCE LISTINGS
-- ============================================

CREATE TYPE listing_status AS ENUM (
    'ACTIVE', 'PENDING', 'SOLD', 'EXPIRED', 'CANCELLED'
);

CREATE TABLE produce_listings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    seller_id       UUID NOT NULL REFERENCES users(id),
    farm_id         UUID REFERENCES farms(id),
    
    -- Produce details
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    variety_id      UUID REFERENCES crop_varieties(id),
    quality_grade   VARCHAR(20) DEFAULT 'GRADE_A',
    
    -- Quantity & Price
    quantity_kg     DECIMAL(10,2) NOT NULL,
    price_per_kg    DECIMAL(10,2) NOT NULL,
    total_price     DECIMAL(12,2) GENERATED ALWAYS AS 
        (quantity_kg * price_per_kg) STORED,
    currency        VARCHAR(5) DEFAULT 'MWK',
    is_negotiable   BOOLEAN DEFAULT TRUE,
    
    -- Dates
    available_from  DATE NOT NULL,
    expiry_date     DATE NOT NULL DEFAULT (NOW() + INTERVAL '30 days'),
    
    -- Location
    pickup_location GEOGRAPHY(POINT, 4326),
    pickup_address  TEXT,
    nearest_town    VARCHAR(255),
    
    -- Quality info
    moisture_content DECIMAL(5,2),
    is_organic      BOOLEAN DEFAULT FALSE,
    organic_certification TEXT,
    
    -- Status
    status          listing_status DEFAULT 'ACTIVE',
    
    -- Description & Media
    title           VARCHAR(255),
    description     TEXT,
    photo_urls      JSONB DEFAULT '[]',
    
    -- Harvest link
    harvest_id      UUID,
    
    -- Stats
    view_count      INTEGER DEFAULT 0,
    offer_count     INTEGER DEFAULT 0,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_listing_status ON produce_listings(status);
CREATE INDEX idx_listing_crop ON produce_listings(crop_id);
CREATE INDEX idx_listing_seller ON produce_listings(seller_id);
CREATE INDEX idx_listing_price ON produce_listings(price_per_kg);
CREATE INDEX idx_listing_location ON produce_listings USING GIST(pickup_location);
CREATE INDEX idx_listing_expiry ON produce_listings(expiry_date);

-- ============================================
-- 2. BULK PURCHASE REQUESTS
-- ============================================

CREATE TYPE request_status AS ENUM ('OPEN', 'PARTIALLY_FILLED', 'FILLED', 'EXPIRED', 'CANCELLED');

CREATE TABLE bulk_purchase_requests (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    buyer_id        UUID NOT NULL REFERENCES users(id),
    
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    variety_id      UUID REFERENCES crop_varieties(id),
    quality_grade   VARCHAR(20),
    
    quantity_needed_kg DECIMAL(10,2) NOT NULL,
    quantity_fulfilled_kg DECIMAL(10,2) DEFAULT 0,
    max_price_per_kg DECIMAL(10,2),
    
    delivery_location GEOGRAPHY(POINT, 4326),
    delivery_address TEXT,
    deadline_date   DATE,
    
    status          request_status DEFAULT 'OPEN',
    description     TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_bulk_buyer ON bulk_purchase_requests(buyer_id);
CREATE INDEX idx_bulk_status ON bulk_purchase_requests(status);
CREATE INDEX idx_bulk_crop ON bulk_purchase_requests(crop_id);

-- ============================================
-- 3. OFFERS
-- ============================================

CREATE TYPE offer_status AS ENUM (
    'PENDING', 'ACCEPTED', 'REJECTED', 'COUNTERED', 'EXPIRED', 'WITHDRAWN'
);

CREATE TABLE offers (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    listing_id      UUID NOT NULL REFERENCES produce_listings(id),
    buyer_id        UUID NOT NULL REFERENCES users(id),
    
    -- Offer
    offered_price_per_kg DECIMAL(10,2) NOT NULL,
    quantity_kg     DECIMAL(10,2) NOT NULL,
    total_offer     DECIMAL(12,2) GENERATED ALWAYS AS 
        (offered_price_per_kg * quantity_kg) STORED,
    
    -- Status
    status          offer_status DEFAULT 'PENDING',
    
    -- Counter history
    original_offer_id UUID REFERENCES offers(id),
    counter_number  INTEGER DEFAULT 0,
    
    -- Messages
    buyer_message   TEXT,
    seller_message  TEXT,
    
    -- Dates
    expires_at      TIMESTAMPTZ,
    accepted_at     TIMESTAMPTZ,
    rejected_at     TIMESTAMPTZ,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_offer_listing ON offers(listing_id);
CREATE INDEX idx_offer_buyer ON offers(buyer_id);
CREATE INDEX idx_offer_status ON offers(status);

-- ============================================
-- 4. DIGITAL CONTRACTS
-- ============================================

CREATE TYPE contract_status AS ENUM (
    'PENDING_ACKNOWLEDGMENT', 'ACTIVE', 'FULFILLED', 'BREACHED', 'CANCELLED'
);

CREATE TYPE delivery_terms AS ENUM (
    'DELIVERY_EX_FARM', 'DELIVERY_TO_BUYER', 'FOB', 'CIF'
);

CREATE TYPE payment_terms AS ENUM (
    'FULL_ADVANCE', 'PARTIAL_ADVANCE', 'ON_DELIVERY', 'NET_7', 'NET_30', 'NET_60'
);

CREATE TABLE contracts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    offer_id        UUID UNIQUE REFERENCES offers(id),
    listing_id      UUID NOT NULL REFERENCES produce_listings(id),
    
    -- Parties
    seller_id       UUID NOT NULL REFERENCES users(id),
    buyer_id        UUID NOT NULL REFERENCES users(id),
    
    -- Terms
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    quantity_kg     DECIMAL(10,2) NOT NULL,
    price_per_kg    DECIMAL(10,2) NOT NULL,
    total_value     DECIMAL(12,2) NOT NULL,
    currency        VARCHAR(5) DEFAULT 'MWK',
    
    delivery_terms  delivery_terms NOT NULL,
    payment_terms   payment_terms NOT NULL,
    
    -- Dates
    contract_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    delivery_deadline DATE,
    expected_delivery_date DATE,
    
    -- Status
    status          contract_status DEFAULT 'PENDING_ACKNOWLEDGMENT',
    seller_acknowledged BOOLEAN DEFAULT FALSE,
    buyer_acknowledged BOOLEAN DEFAULT FALSE,
    
    -- Fulfillment
    quantity_delivered_kg DECIMAL(10,2) DEFAULT 0,
    amount_paid     DECIMAL(12,2) DEFAULT 0,
    
    -- Special conditions
    special_conditions TEXT,
    
    contract_pdf_url TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_contract_seller ON contracts(seller_id);
CREATE INDEX idx_contract_buyer ON contracts(buyer_id);
CREATE INDEX idx_contract_status ON contracts(status);

-- ============================================
-- 5. ORDERS
-- ============================================

CREATE TYPE order_status AS ENUM (
    'CONFIRMED', 'PROCESSING', 'READY_FOR_PICKUP', 'IN_TRANSIT',
    'DELIVERED', 'COMPLETED', 'CANCELLED', 'DISPUTED'
);

CREATE TABLE orders (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    contract_id     UUID UNIQUE REFERENCES contracts(id),
    tenant_id       UUID NOT NULL,
    
    buyer_id        UUID NOT NULL REFERENCES users(id),
    seller_id       UUID NOT NULL REFERENCES users(id),
    
    -- Order details
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    quantity_kg     DECIMAL(10,2) NOT NULL,
    price_per_kg    DECIMAL(10,2) NOT NULL,
    total_amount    DECIMAL(12,2) NOT NULL,
    
    -- Status
    status          order_status DEFAULT 'CONFIRMED',
    status_history  JSONB DEFAULT '[]',
    
    -- Delivery
    delivery_method VARCHAR(50) DEFAULT 'BUYER_COLLECTS',
    transporter_id  UUID REFERENCES users(id),
    pickup_location GEOGRAPHY(POINT, 4326),
    delivery_location GEOGRAPHY(POINT, 4326),
    estimated_delivery TIMESTAMPTZ,
    actual_delivery TIMESTAMPTZ,
    
    -- Quality at delivery
    inspection_result VARCHAR(50),
    inspection_notes TEXT,
    
    -- Payment
    amount_paid     DECIMAL(12,2) DEFAULT 0,
    payment_status  VARCHAR(50) DEFAULT 'PENDING',
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_order_buyer ON orders(buyer_id);
CREATE INDEX idx_order_seller ON orders(seller_id);
CREATE INDEX idx_order_status ON orders(status);

-- ============================================
-- 6. DELIVERY TRACKING
-- ============================================

CREATE TYPE delivery_status AS ENUM (
    'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'ARRIVED', 'DELIVERED', 'FAILED'
);

CREATE TABLE delivery_tracking (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id        UUID NOT NULL REFERENCES orders(id),
    
    transporter_id  UUID REFERENCES users(id),
    vehicle_details TEXT,
    driver_name     VARCHAR(255),
    driver_contact  VARCHAR(100),
    
    status          delivery_status DEFAULT 'ASSIGNED',
    current_location GEOGRAPHY(POINT, 4326),
    
    pickup_time     TIMESTAMPTZ,
    estimated_arrival TIMESTAMPTZ,
    actual_arrival  TIMESTAMPTZ,
    
    tracking_updates JSONB DEFAULT '[]',
    -- [{timestamp, status, location, note}]
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_delivery_order ON delivery_tracking(order_id);
CREATE INDEX idx_delivery_status ON delivery_tracking(status);

-- ============================================
-- 7. MESSAGES (Buyer-Seller Chat)
-- ============================================

CREATE TABLE marketplace_messages (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    listing_id      UUID REFERENCES produce_listings(id),
    offer_id        UUID REFERENCES offers(id),
    order_id        UUID REFERENCES orders(id),
    
    sender_id       UUID NOT NULL REFERENCES users(id),
    receiver_id     UUID NOT NULL REFERENCES users(id),
    
    message_text    TEXT NOT NULL,
    attachment_url  TEXT,
    
    is_read         BOOLEAN DEFAULT FALSE,
    read_at         TIMESTAMPTZ,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_msg_listing ON marketplace_messages(listing_id);
CREATE INDEX idx_msg_sender ON marketplace_messages(sender_id);
CREATE INDEX idx_msg_receiver ON marketplace_messages(receiver_id, is_read);

-- ============================================
-- 8. RATINGS & REVIEWS
-- ============================================

CREATE TABLE marketplace_ratings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id        UUID NOT NULL REFERENCES orders(id),
    
    reviewer_id     UUID NOT NULL REFERENCES users(id),
    reviewed_id     UUID NOT NULL REFERENCES users(id),
    
    rating          INTEGER CHECK (rating BETWEEN 1 AND 5),
    review_text     TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(order_id, reviewer_id)
);

CREATE INDEX idx_rating_reviewed ON marketplace_ratings(reviewed_id);

-- ============================================
-- 9. PRICE HISTORY & TRENDS
-- ============================================

CREATE TABLE market_prices (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    market_name     VARCHAR(255) NOT NULL,
    district        VARCHAR(100),
    
    price_date      DATE NOT NULL,
    min_price_kg    DECIMAL(10,2),
    max_price_kg    DECIMAL(10,2),
    avg_price_kg    DECIMAL(10,2),
    currency        VARCHAR(5) DEFAULT 'MWK',
    
    source          VARCHAR(100), -- PLATFORM, MIS, MANUAL_ENTRY
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(crop_id, market_name, price_date)
);

CREATE INDEX idx_price_crop ON market_prices(crop_id);
CREATE INDEX idx_price_date ON market_prices(price_date);
CREATE INDEX idx_price_market ON market_prices(market_name);

-- ============================================
-- 10. DISPUTES
-- ============================================

CREATE TYPE dispute_status AS ENUM (
    'OPEN', 'UNDER_REVIEW', 'RESOLVED_BUYER', 'RESOLVED_SELLER', 'ESCALATED', 'CLOSED'
);

CREATE TABLE disputes (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id        UUID NOT NULL REFERENCES orders(id),
    
    raised_by       UUID NOT NULL REFERENCES users(id),
    raised_against  UUID NOT NULL REFERENCES users(id),
    
    dispute_type    VARCHAR(50) NOT NULL, -- QUALITY, QUANTITY, DELIVERY, PAYMENT, OTHER
    description     TEXT NOT NULL,
    evidence_urls   JSONB DEFAULT '[]',
    
    status          dispute_status DEFAULT 'OPEN',
    resolution      TEXT,
    resolved_by     UUID REFERENCES users(id),
    resolved_at     TIMESTAMPTZ,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_dispute_order ON disputes(order_id);
CREATE INDEX idx_dispute_status ON disputes(status);

-- ============================================
-- 11. FAVORITES / WATCHLIST
-- ============================================

CREATE TABLE marketplace_favorites (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id),
    listing_id      UUID REFERENCES produce_listings(id),
    seller_id       UUID REFERENCES users(id),
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(user_id, listing_id)
);

CREATE INDEX idx_fav_user ON marketplace_favorites(user_id);

-- ============================================
-- 12. CONTRACT FARMING AGREEMENTS
-- ============================================

CREATE TYPE contract_farming_status AS ENUM (
    'DRAFT', 'ACTIVE', 'IN_PROGRESS', 'COMPLETED', 'TERMINATED'
);

CREATE TABLE contract_farming (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    buyer_id        UUID NOT NULL REFERENCES users(id),
    farmer_id       UUID NOT NULL REFERENCES users(id),
    farm_id         UUID NOT NULL REFERENCES farms(id),
    
    crop_id         UUID NOT NULL REFERENCES crop_catalog(id),
    area_contracted_ha DECIMAL(8,2),
    expected_yield_kg DECIMAL(10,2),
    agreed_price_per_kg DECIMAL(10,2),
    
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          contract_farming_status DEFAULT 'DRAFT',
    
    -- Advance payment
    advance_amount  DECIMAL(12,2),
    advance_paid    BOOLEAN DEFAULT FALSE,
    
    -- Inputs supplied by buyer
    inputs_provided JSONB DEFAULT '[]',
    
    -- Deliveries
    total_delivered_kg DECIMAL(10,2) DEFAULT 0,
    total_paid      DECIMAL(12,2) DEFAULT 0,
    
    agreement_pdf_url TEXT,
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_cf_buyer ON contract_farming(buyer_id);
CREATE INDEX idx_cf_farmer ON contract_farming(farmer_id);
CREATE INDEX idx_cf_status ON contract_farming(status);

