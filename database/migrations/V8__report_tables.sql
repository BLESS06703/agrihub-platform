-- ============================================
-- AgriHub Malawi - Reports & Analytics Schema
-- Version: 1.0
-- ============================================

CREATE TYPE report_format AS ENUM ('PDF', 'CSV', 'EXCEL', 'JSON');
CREATE TYPE report_frequency AS ENUM ('ONCE', 'DAILY', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'ANNUALLY');

CREATE TABLE saved_reports (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    report_name     VARCHAR(255) NOT NULL,
    report_type     VARCHAR(100) NOT NULL,
    description     TEXT,
    parameters      JSONB NOT NULL DEFAULT '{}',
    filters         JSONB DEFAULT '{}',
    columns         JSONB,
    sort_by         VARCHAR(100),
    chart_type      VARCHAR(50),
    is_scheduled    BOOLEAN DEFAULT FALSE,
    frequency       report_frequency,
    next_run_at     TIMESTAMPTZ,
    recipients      JSONB DEFAULT '[]',
    output_format   report_format DEFAULT 'PDF',
    last_run_at     TIMESTAMPTZ,
    last_run_status VARCHAR(50),
    run_count       INTEGER DEFAULT 0,
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_report_tenant ON saved_reports(tenant_id);
CREATE INDEX idx_report_scheduled ON saved_reports(is_scheduled) WHERE is_scheduled = TRUE;

CREATE TABLE report_executions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    report_id       UUID NOT NULL REFERENCES saved_reports(id),
    tenant_id       UUID NOT NULL,
    started_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMPTZ,
    status          VARCHAR(50) DEFAULT 'RUNNING',
    output_url      TEXT,
    file_size_bytes BIGINT,
    row_count       INTEGER,
    error_message   TEXT,
    triggered_by    UUID REFERENCES users(id),
    trigger_type    VARCHAR(50) DEFAULT 'MANUAL',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_exec_report ON report_executions(report_id);
CREATE INDEX idx_exec_date ON report_executions(created_at DESC);

CREATE TYPE widget_type AS ENUM (
    'METRIC_CARD', 'LINE_CHART', 'BAR_CHART', 'PIE_CHART',
    'TABLE', 'LIST', 'MAP', 'GAUGE', 'CALENDAR'
);

CREATE TABLE dashboard_widgets (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    user_id         UUID NOT NULL REFERENCES users(id),
    widget_name     VARCHAR(255),
    widget_type     widget_type NOT NULL,
    data_source     VARCHAR(100) NOT NULL,
    config          JSONB NOT NULL DEFAULT '{}',
    position_x      INTEGER DEFAULT 0,
    position_y      INTEGER DEFAULT 0,
    width           INTEGER DEFAULT 1,
    height          INTEGER DEFAULT 1,
    is_visible      BOOLEAN DEFAULT TRUE,
    refresh_interval_seconds INTEGER,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX idx_widget_user ON dashboard_widgets(user_id);

CREATE TABLE analytics_events (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    user_id         UUID,
    event_name      VARCHAR(100) NOT NULL,
    event_category  VARCHAR(50),
    event_data      JSONB DEFAULT '{}',
    session_id      UUID,
    device_id       VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_analytics_tenant ON analytics_events(tenant_id);
CREATE INDEX idx_analytics_event ON analytics_events(event_name);
CREATE INDEX idx_analytics_date ON analytics_events(created_at DESC);

CREATE TABLE export_logs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL,
    user_id         UUID NOT NULL REFERENCES users(id),
    export_type     VARCHAR(100) NOT NULL,
    export_format   report_format NOT NULL,
    filters_applied JSONB DEFAULT '{}',
    record_count    INTEGER,
    file_size_bytes BIGINT,
    download_url    TEXT,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_export_tenant ON export_logs(tenant_id);
CREATE INDEX idx_export_user ON export_logs(user_id);
CREATE INDEX idx_export_expires ON export_logs(expires_at);
