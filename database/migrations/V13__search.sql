-- Saved Searches
CREATE TABLE IF NOT EXISTS saved_searches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    search_type VARCHAR(100) NOT NULL,
    query TEXT NOT NULL,
    filters JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_saved_search_user ON saved_searches(user_id);

-- Search History
CREATE TABLE IF NOT EXISTS search_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    query TEXT NOT NULL,
    search_type VARCHAR(100),
    results_count INTEGER DEFAULT 0,
    searched_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_search_history_user ON search_history(user_id);
CREATE INDEX IF NOT EXISTS idx_search_history_date ON search_history(searched_at DESC);

-- Full Text Search Index (PostgreSQL)
-- Already covered by existing indexes on text columns
-- Future: Add tsvector columns for advanced search
