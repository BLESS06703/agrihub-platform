-- QR/RFID Animal Tags
ALTER TABLE animals ADD COLUMN IF NOT EXISTS rfid_tag VARCHAR(100);
ALTER TABLE animals ADD COLUMN IF NOT EXISTS qr_code_url TEXT;
ALTER TABLE animals ADD COLUMN IF NOT EXISTS tag_type VARCHAR(20) DEFAULT 'NONE';

CREATE TABLE IF NOT EXISTS tag_scan_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    animal_id UUID NOT NULL REFERENCES animals(id),
    scanned_by UUID NOT NULL REFERENCES users(id),
    scan_type VARCHAR(20) NOT NULL,
    location GEOGRAPHY(POINT, 4326),
    scanned_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Banking & Credit
CREATE TABLE IF NOT EXISTS bank_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    bank_name VARCHAR(255) NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    account_type VARCHAR(50),
    branch_code VARCHAR(50),
    is_verified BOOLEAN DEFAULT FALSE,
    linked_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS credit_scores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    score INTEGER CHECK (score BETWEEN 300 AND 850),
    factors JSONB DEFAULT '{}',
    calculated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    valid_until TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS loan_applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    bank_account_id UUID REFERENCES bank_accounts(id),
    credit_score_id UUID REFERENCES credit_scores(id),
    amount_requested DECIMAL(12,2) NOT NULL,
    purpose TEXT,
    term_months INTEGER,
    status VARCHAR(50) DEFAULT 'SUBMITTED',
    reviewed_by UUID REFERENCES users(id),
    reviewed_at TIMESTAMPTZ,
    approved_amount DECIMAL(12,2),
    interest_rate DECIMAL(5,2),
    disbursed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Carbon Credits
CREATE TABLE IF NOT EXISTS carbon_projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    project_type VARCHAR(100) NOT NULL,
    methodology VARCHAR(255),
    registered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status VARCHAR(50) DEFAULT 'PENDING_VERIFICATION',
    total_credits_issued DECIMAL(12,2) DEFAULT 0,
    credits_sold DECIMAL(12,2) DEFAULT 0,
    price_per_credit DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS carbon_activities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL REFERENCES carbon_projects(id),
    activity_type VARCHAR(100) NOT NULL,
    description TEXT,
    co2_sequestered_kg DECIMAL(10,2) NOT NULL,
    verified_by UUID REFERENCES users(id),
    verified_at TIMESTAMPTZ,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS carbon_credit_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL REFERENCES carbon_projects(id),
    buyer_id UUID NOT NULL REFERENCES users(id),
    credits_amount DECIMAL(10,2) NOT NULL,
    price_per_credit DECIMAL(10,2) NOT NULL,
    total_value DECIMAL(12,2) NOT NULL,
    transaction_date TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Smart Contracts for supply chain
CREATE TABLE IF NOT EXISTS smart_contracts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    contract_type VARCHAR(100) NOT NULL,
    parties JSONB NOT NULL DEFAULT '[]',
    terms JSONB NOT NULL DEFAULT '{}',
    status VARCHAR(50) DEFAULT 'DRAFT',
    blockchain_tx_hash VARCHAR(255),
    executed_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_animals_rfid ON animals(rfid_tag);
CREATE INDEX IF NOT EXISTS idx_credit_tenant ON credit_scores(tenant_id);
CREATE INDEX IF NOT EXISTS idx_loan_status ON loan_applications(status);
CREATE INDEX IF NOT EXISTS idx_carbon_project ON carbon_projects(tenant_id);
