-- ============================================
-- AgriHub Malawi - Finance Management Schema
-- Version: 1.0
-- ============================================

-- ============================================
-- 1. CHART OF ACCOUNTS
-- ============================================

CREATE TYPE account_type AS ENUM ('INCOME', 'EXPENSE', 'ASSET', 'LIABILITY', 'EQUITY');
CREATE TYPE account_category AS ENUM (
    -- Income
    'CROP_SALES', 'LIVESTOCK_SALES', 'EQUIPMENT_RENTAL', 'GRANTS', 'SUBSIDY',
    'INSURANCE_CLAIM', 'OTHER_INCOME',
    -- Expense
    'SEEDS', 'FERTILIZER', 'PESTICIDES', 'LABOR', 'FUEL', 'EQUIPMENT_MAINTENANCE',
    'TRANSPORT', 'STORAGE', 'MARKETING', 'LOAN_INTEREST', 'INSURANCE_PREMIUM',
    'RENT', 'UTILITIES', 'VETERINARY', 'FEED', 'PACKAGING', 'OTHER_EXPENSE',
    -- Asset
    'CASH', 'BANK', 'MOBILE_MONEY', 'INVENTORY', 'EQUIPMENT', 'LAND',
    'LIVESTOCK_ASSET', 'ACCOUNTS_RECEIVABLE',
    -- Liability
    'LOAN', 'ACCOUNTS_PAYABLE', 'ACCRUED_EXPENSES',
    -- Equity
    'OWNER_EQUITY', 'RETAINED_EARNINGS'
);

CREATE TABLE chart_of_accounts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    account_code    VARCHAR(20) NOT NULL,
    account_name    VARCHAR(255) NOT NULL,
    account_type    account_type NOT NULL,
    category        account_category NOT NULL,
    description     TEXT,
    
    parent_id       UUID REFERENCES chart_of_accounts(id),
    is_system       BOOLEAN DEFAULT FALSE,
    is_active       BOOLEAN DEFAULT TRUE,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, account_code)
);

CREATE INDEX idx_coa_tenant ON chart_of_accounts(tenant_id);
CREATE INDEX idx_coa_type ON chart_of_accounts(account_type);

-- ============================================
-- 2. FINANCIAL YEARS
-- ============================================

CREATE TABLE financial_years (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    year_name       VARCHAR(50) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    is_closed       BOOLEAN DEFAULT FALSE,
    closed_at       TIMESTAMPTZ,
    closed_by       UUID REFERENCES users(id),
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(tenant_id, year_name)
);

CREATE INDEX idx_fy_tenant ON financial_years(tenant_id);

-- ============================================
-- 3. INCOME RECORDS
-- ============================================

CREATE TYPE income_source AS ENUM (
    'PRODUCE_SALE', 'LIVESTOCK_SALE', 'EQUIPMENT_RENTAL',
    'GRANT', 'SUBSIDY', 'INSURANCE_CLAIM', 'LOAN_DISBURSEMENT',
    'MILK_SALE', 'EGG_SALE', 'SERVICE_FEE', 'OTHER'
);

CREATE TYPE payment_method AS ENUM (
    'CASH', 'MOBILE_MONEY', 'BANK_TRANSFER', 'CHEQUE', 'CREDIT', 'IN_KIND'
);

CREATE TYPE payment_status AS ENUM (
    'PENDING', 'PARTIAL', 'COMPLETED', 'FAILED', 'REFUNDED'
);

CREATE TABLE income_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    account_id      UUID REFERENCES chart_of_accounts(id),
    
    -- Transaction
    transaction_date DATE NOT NULL,
    source          income_source NOT NULL,
    description     TEXT,
    amount          DECIMAL(12,2) NOT NULL,
    
    -- Payment
    payment_method  payment_method NOT NULL,
    payment_status  payment_status DEFAULT 'COMPLETED',
    payment_reference VARCHAR(100),
    payment_date    DATE,
    
    -- Mobile Money
    mobile_money_provider VARCHAR(50), -- AIRTEL_MONEY, TNM_MPAMBA
    mobile_money_transaction_id VARCHAR(100),
    
    -- Payer
    payer_name      VARCHAR(255),
    payer_contact   VARCHAR(100),
    
    -- Links
    reference_type  VARCHAR(50),  -- marketplace_order, livestock_sale, etc.
    reference_id    UUID,
    invoice_id      UUID,
    
    -- Attachments
    receipt_url     TEXT,
    notes           TEXT,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_income_tenant ON income_records(tenant_id);
CREATE INDEX idx_income_date ON income_records(transaction_date);
CREATE INDEX idx_income_source ON income_records(source);
CREATE INDEX idx_income_status ON income_records(payment_status);

-- ============================================
-- 4. EXPENSE RECORDS
-- ============================================

CREATE TABLE expense_records (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    account_id      UUID REFERENCES chart_of_accounts(id),
    
    -- Transaction
    transaction_date DATE NOT NULL,
    category        account_category NOT NULL,
    description     TEXT,
    amount          DECIMAL(12,2) NOT NULL,
    
    -- Payment
    payment_method  payment_method NOT NULL,
    payment_status  payment_status DEFAULT 'COMPLETED',
    payment_reference VARCHAR(100),
    
    -- Payee
    payee_name      VARCHAR(255),
    supplier_id     UUID,
    
    -- Links
    reference_type  VARCHAR(50),
    reference_id    UUID,
    farm_id         UUID,
    field_id        UUID,
    
    -- Recurring
    is_recurring    BOOLEAN DEFAULT FALSE,
    recurrence_pattern VARCHAR(50), -- MONTHLY, QUARTERLY, ANNUALLY
    next_due_date   DATE,
    
    -- Approval
    requires_approval BOOLEAN DEFAULT FALSE,
    approved_by     UUID REFERENCES users(id),
    approved_at     TIMESTAMPTZ,
    approval_status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    
    -- Attachments
    receipt_url     TEXT,
    notes           TEXT,
    
    -- Audit
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_expense_tenant ON expense_records(tenant_id);
CREATE INDEX idx_expense_date ON expense_records(transaction_date);
CREATE INDEX idx_expense_category ON expense_records(category);
CREATE INDEX idx_expense_farm ON expense_records(farm_id);

-- ============================================
-- 5. BUDGETS
-- ============================================

CREATE TYPE budget_type AS ENUM ('ANNUAL', 'SEASONAL', 'PER_CROP', 'PER_FARM', 'PROJECT');
CREATE TYPE budget_status AS ENUM ('DRAFT', 'APPROVED', 'ACTIVE', 'CLOSED');

CREATE TABLE budgets (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    budget_name     VARCHAR(255) NOT NULL,
    budget_type     budget_type NOT NULL,
    financial_year_id UUID REFERENCES financial_years(id),
    farm_id         UUID,
    crop_id         UUID,
    
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          budget_status DEFAULT 'DRAFT',
    
    total_budgeted  DECIMAL(14,2) DEFAULT 0,
    total_actual    DECIMAL(14,2) DEFAULT 0,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_budget_tenant ON budgets(tenant_id);
CREATE INDEX idx_budget_dates ON budgets(start_date, end_date);

-- ============================================
-- 6. BUDGET LINE ITEMS
-- ============================================

CREATE TABLE budget_items (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    budget_id       UUID NOT NULL REFERENCES budgets(id) ON DELETE CASCADE,
    account_id      UUID REFERENCES chart_of_accounts(id),
    
    category        account_category NOT NULL,
    description     TEXT,
    planned_amount  DECIMAL(12,2) NOT NULL,
    actual_amount   DECIMAL(12,2) DEFAULT 0,
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_budget_item_budget ON budget_items(budget_id);

-- ============================================
-- 7. LOANS
-- ============================================

CREATE TYPE loan_status AS ENUM ('ACTIVE', 'PAID_OFF', 'DEFAULTED', 'RESTRUCTURED', 'CANCELLED');
CREATE TYPE interest_type AS ENUM ('SIMPLE', 'COMPOUND');

CREATE TABLE loans (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    -- Lender
    lender_name     VARCHAR(255) NOT NULL,
    lender_type     VARCHAR(50),  -- BANK, MFI, COOPERATIVE, INDIVIDUAL, GOVERNMENT
    
    -- Terms
    principal_amount DECIMAL(12,2) NOT NULL,
    interest_rate   DECIMAL(5,2) NOT NULL,  -- Annual percentage
    interest_type   interest_type DEFAULT 'SIMPLE',
    term_months     INTEGER NOT NULL,
    
    -- Dates
    disbursement_date DATE NOT NULL,
    first_payment_date DATE,
    expected_end_date DATE,
    actual_end_date DATE,
    
    -- Status
    status          loan_status DEFAULT 'ACTIVE',
    total_repaid    DECIMAL(12,2) DEFAULT 0,
    outstanding_balance DECIMAL(12,2),
    
    -- Collateral
    collateral_type VARCHAR(255),
    collateral_description TEXT,
    warehouse_receipt_id UUID,
    
    -- Links
    reference_number VARCHAR(100),
    loan_agreement_url TEXT,
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_loans_tenant ON loans(tenant_id);
CREATE INDEX idx_loans_status ON loans(status);

-- ============================================
-- 8. LOAN REPAYMENTS
-- ============================================

CREATE TABLE loan_repayments (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id         UUID NOT NULL REFERENCES loans(id),
    tenant_id       UUID NOT NULL,
    
    payment_date    DATE NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    principal_portion DECIMAL(12,2) NOT NULL,
    interest_portion DECIMAL(12,2) NOT NULL,
    
    payment_method  payment_method NOT NULL,
    payment_reference VARCHAR(100),
    
    remaining_balance DECIMAL(12,2),
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_repay_loan ON loan_repayments(loan_id);
CREATE INDEX idx_repay_date ON loan_repayments(payment_date);

-- ============================================
-- 9. REPAYMENT SCHEDULES
-- ============================================

CREATE TYPE schedule_status AS ENUM ('PENDING', 'PAID', 'OVERDUE', 'PARTIALLY_PAID');

CREATE TABLE repayment_schedules (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id         UUID NOT NULL REFERENCES loans(id) ON DELETE CASCADE,
    
    installment_number INTEGER NOT NULL,
    due_date        DATE NOT NULL,
    principal_due   DECIMAL(12,2) NOT NULL,
    interest_due    DECIMAL(12,2) NOT NULL,
    total_due       DECIMAL(12,2) GENERATED ALWAYS AS 
        (principal_due + interest_due) STORED,
    
    amount_paid     DECIMAL(12,2) DEFAULT 0,
    status          schedule_status DEFAULT 'PENDING',
    paid_date       DATE,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(loan_id, installment_number)
);

CREATE INDEX idx_schedule_loan ON repayment_schedules(loan_id);
CREATE INDEX idx_schedule_due ON repayment_schedules(due_date);
CREATE INDEX idx_schedule_status ON repayment_schedules(status);

-- ============================================
-- 10. INVOICES
-- ============================================

CREATE TYPE invoice_status AS ENUM (
    'DRAFT', 'SENT', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'CANCELLED'
);

CREATE TABLE invoices (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    invoice_number  VARCHAR(50) NOT NULL,
    customer_name   VARCHAR(255) NOT NULL,
    customer_contact VARCHAR(100),
    
    issue_date      DATE NOT NULL,
    due_date        DATE NOT NULL,
    status          invoice_status DEFAULT 'DRAFT',
    
    subtotal        DECIMAL(12,2) DEFAULT 0,
    tax_amount      DECIMAL(12,2) DEFAULT 0,
    total_amount    DECIMAL(12,2) DEFAULT 0,
    amount_paid     DECIMAL(12,2) DEFAULT 0,
    
    notes           TEXT,
    terms           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, invoice_number)
);

CREATE INDEX idx_invoice_tenant ON invoices(tenant_id);
CREATE INDEX idx_invoice_status ON invoices(status);
CREATE INDEX idx_invoice_due ON invoices(due_date);

-- ============================================
-- 11. INVOICE LINE ITEMS
-- ============================================

CREATE TABLE invoice_items (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id      UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    
    description     TEXT NOT NULL,
    quantity        DECIMAL(10,2) NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    total_price     DECIMAL(12,2) GENERATED ALWAYS AS 
        (quantity * unit_price) STORED,
    
    account_id      UUID REFERENCES chart_of_accounts(id),
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================
-- 12. GRANTS
-- ============================================

CREATE TYPE grant_status AS ENUM ('ACTIVE', 'COMPLETED', 'SUSPENDED', 'CLOSED');

CREATE TABLE grants (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    grant_name      VARCHAR(255) NOT NULL,
    donor_name      VARCHAR(255) NOT NULL,
    donor_contact   VARCHAR(255),
    
    total_amount    DECIMAL(12,2) NOT NULL,
    currency        VARCHAR(5) DEFAULT 'MWK',
    
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          grant_status DEFAULT 'ACTIVE',
    
    purpose         TEXT,
    agreement_url   TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_grants_tenant ON grants(tenant_id);

-- ============================================
-- 13. GRANT DISBURSEMENTS & EXPENDITURES
-- ============================================

CREATE TABLE grant_transactions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    grant_id        UUID NOT NULL REFERENCES grants(id),
    tenant_id       UUID NOT NULL,
    
    transaction_date DATE NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- DISBURSEMENT, EXPENDITURE
    amount          DECIMAL(12,2) NOT NULL,
    description     TEXT,
    
    budget_line     VARCHAR(255),
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_grant_txn_grant ON grant_transactions(grant_id);

-- ============================================
-- 14. INSURANCE POLICIES
-- ============================================

CREATE TYPE insurance_type AS ENUM ('CROP', 'LIVESTOCK', 'EQUIPMENT', 'FARM_PROPERTY', 'HEALTH');

CREATE TABLE insurance_policies (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    policy_number   VARCHAR(100) NOT NULL,
    insurer_name    VARCHAR(255) NOT NULL,
    insurance_type  insurance_type NOT NULL,
    
    coverage_amount DECIMAL(12,2) NOT NULL,
    premium_amount  DECIMAL(12,2) NOT NULL,
    premium_frequency VARCHAR(20), -- MONTHLY, QUARTERLY, ANNUALLY
    
    start_date      DATE NOT NULL,
    expiry_date     DATE NOT NULL,
    
    farm_id         UUID,
    animal_id       UUID,
    equipment_id    UUID,
    
    policy_document_url TEXT,
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    
    notes           TEXT,
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    
    UNIQUE(tenant_id, policy_number)
);

CREATE INDEX idx_insurance_tenant ON insurance_policies(tenant_id);
CREATE INDEX idx_insurance_expiry ON insurance_policies(expiry_date);

-- ============================================
-- 15. INSURANCE CLAIMS
-- ============================================

CREATE TYPE claim_status AS ENUM ('FILED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'PAID');

CREATE TABLE insurance_claims (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    policy_id       UUID NOT NULL REFERENCES insurance_policies(id),
    tenant_id       UUID NOT NULL,
    
    claim_date      DATE NOT NULL,
    claim_amount    DECIMAL(12,2) NOT NULL,
    incident_date   DATE NOT NULL,
    incident_description TEXT,
    
    status          claim_status DEFAULT 'FILED',
    approved_amount DECIMAL(12,2),
    paid_date       DATE,
    
    documents       JSONB DEFAULT '[]',
    adjuster_notes  TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_claim_policy ON insurance_claims(policy_id);
CREATE INDEX idx_claim_status ON insurance_claims(status);

-- ============================================
-- 16. SUBSIDY TRACKING (FISP)
-- ============================================

CREATE TABLE subsidies (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    
    program_name    VARCHAR(255) NOT NULL, -- e.g., FISP
    program_year    INTEGER NOT NULL,
    
    beneficiary_name VARCHAR(255) NOT NULL,
    beneficiary_id  VARCHAR(100),
    
    total_allocation DECIMAL(12,2),
    redeemed_amount DECIMAL(12,2) DEFAULT 0,
    
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    
    notes           TEXT,
    
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_subsidy_tenant ON subsidies(tenant_id);
CREATE INDEX idx_subsidy_program ON subsidies(program_name, program_year);

