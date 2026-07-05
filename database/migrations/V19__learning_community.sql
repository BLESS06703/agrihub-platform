-- Courses
CREATE TABLE IF NOT EXISTS courses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    language VARCHAR(10) DEFAULT 'en',
    difficulty VARCHAR(20) DEFAULT 'BEGINNER',
    duration_minutes INTEGER,
    thumbnail_url TEXT,
    is_published BOOLEAN DEFAULT FALSE,
    enrollment_count INTEGER DEFAULT 0,
    rating DECIMAL(3,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Lessons
CREATE TABLE IF NOT EXISTS lessons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL REFERENCES courses(id),
    title VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) DEFAULT 'VIDEO',
    content_url TEXT,
    text_content TEXT,
    duration_minutes INTEGER,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- User Progress
CREATE TABLE IF NOT EXISTS learning_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    course_id UUID NOT NULL REFERENCES courses(id),
    lesson_id UUID REFERENCES lessons(id),
    completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    quiz_score DECIMAL(5,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, lesson_id)
);

-- Community Forums
CREATE TABLE IF NOT EXISTS forums (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    country_code VARCHAR(2),
    language VARCHAR(10) DEFAULT 'en',
    member_count INTEGER DEFAULT 0,
    post_count INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Forum Posts
CREATE TABLE IF NOT EXISTS forum_posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    forum_id UUID NOT NULL REFERENCES forums(id),
    user_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    tags JSONB DEFAULT '[]',
    view_count INTEGER DEFAULT 0,
    reply_count INTEGER DEFAULT 0,
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_posts_forum ON forum_posts(forum_id);
CREATE INDEX IF NOT EXISTS idx_posts_date ON forum_posts(created_at DESC);

-- Post Replies
CREATE TABLE IF NOT EXISTS post_replies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id UUID NOT NULL REFERENCES forum_posts(id),
    user_id UUID NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    is_best_answer BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_replies_post ON post_replies(post_id);

-- Expert Directory
CREATE TABLE IF NOT EXISTS experts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) UNIQUE,
    specializations JSONB DEFAULT '[]',
    years_experience INTEGER,
    certifications JSONB DEFAULT '[]',
    languages JSONB DEFAULT '[]',
    rating DECIMAL(3,2),
    response_count INTEGER DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Expert Questions
CREATE TABLE IF NOT EXISTS expert_questions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    expert_id UUID REFERENCES experts(id),
    category VARCHAR(100),
    question TEXT NOT NULL,
    answer TEXT,
    status VARCHAR(50) DEFAULT 'OPEN',
    asked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    answered_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_questions_status ON expert_questions(status);
