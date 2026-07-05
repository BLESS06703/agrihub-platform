-- Dashboard Widgets
CREATE TABLE IF NOT EXISTS dashboard_widgets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    widget_type VARCHAR(50) NOT NULL,
    title VARCHAR(255),
    config JSONB NOT NULL DEFAULT '{}',
    position_x INTEGER DEFAULT 0,
    position_y INTEGER DEFAULT 0,
    width INTEGER DEFAULT 1,
    height INTEGER DEFAULT 1,
    is_visible BOOLEAN DEFAULT TRUE,
    refresh_interval_seconds INTEGER DEFAULT 300,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_dash_widget_user ON dashboard_widgets(user_id);

-- Dashboard Layouts
CREATE TABLE IF NOT EXISTS dashboard_layouts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    config JSONB NOT NULL DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_dash_layout_user ON dashboard_layouts(user_id);

-- KPI Snapshots
CREATE TABLE IF NOT EXISTS kpi_snapshots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    snapshot_type VARCHAR(100) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    metrics JSONB NOT NULL DEFAULT '{}',
    captured_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_kpi_tenant ON kpi_snapshots(tenant_id);
CREATE INDEX IF NOT EXISTS idx_kpi_period ON kpi_snapshots(period_start, period_end);

-- Report Cache
CREATE TABLE IF NOT EXISTS report_cache (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    parameters JSONB NOT NULL DEFAULT '{}',
    result_data JSONB NOT NULL,
    generated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_report_cache_tenant ON report_cache(tenant_id);
CREATE INDEX IF NOT EXISTS idx_report_cache_expires ON report_cache(expires_at);
