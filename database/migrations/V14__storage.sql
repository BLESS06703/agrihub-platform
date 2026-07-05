-- File Records
CREATE TABLE IF NOT EXISTS files (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    original_name VARCHAR(500) NOT NULL,
    stored_name VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    storage_path TEXT NOT NULL,
    storage_url TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_files_tenant ON files(tenant_id);
CREATE INDEX IF NOT EXISTS idx_files_category ON files(category);
CREATE INDEX IF NOT EXISTS idx_files_user ON files(user_id);

-- Folders
CREATE TABLE IF NOT EXISTS folders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    parent_id UUID REFERENCES folders(id),
    name VARCHAR(255) NOT NULL,
    path TEXT NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    UNIQUE(tenant_id, parent_id, name)
);

CREATE INDEX IF NOT EXISTS idx_folders_tenant ON folders(tenant_id);
CREATE INDEX IF NOT EXISTS idx_folders_parent ON folders(parent_id);

-- File-Folder relationship
CREATE TABLE IF NOT EXISTS file_folder (
    file_id UUID NOT NULL REFERENCES files(id) ON DELETE CASCADE,
    folder_id UUID NOT NULL REFERENCES folders(id) ON DELETE CASCADE,
    added_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (file_id, folder_id)
);

-- Image Metadata (for AI processing)
CREATE TABLE IF NOT EXISTS image_metadata (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    file_id UUID NOT NULL REFERENCES files(id) ON DELETE CASCADE UNIQUE,
    width INTEGER,
    height INTEGER,
    gps_latitude DECIMAL(10,7),
    gps_longitude DECIMAL(10,7),
    taken_at TIMESTAMPTZ,
    camera_model VARCHAR(255),
    ai_labels JSONB DEFAULT '[]',
    ai_confidence DECIMAL(4,3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_image_file ON image_metadata(file_id);
