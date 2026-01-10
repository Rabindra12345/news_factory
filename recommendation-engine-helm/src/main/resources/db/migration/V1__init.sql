-- Enable gen_random_uuid() (Postgres, via pgcrypto)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS user_interaction (
                                                interaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id VARCHAR(255), -- nullable for anonymous users

    session_id VARCHAR(100) NOT NULL,
    news_id VARCHAR(255) NOT NULL,

    interaction_type VARCHAR(20) NOT NULL, -- stored as string enum
    interaction_timestamp TIMESTAMP NOT NULL,

    read_duration_seconds INTEGER,
    referrer_url VARCHAR(500),
    device_type VARCHAR(50),
    ip_address VARCHAR(45)
    );

-- Indexes from @Table(indexes=...)
CREATE INDEX IF NOT EXISTS idx_user_id
    ON user_interaction (user_id);

CREATE INDEX IF NOT EXISTS idx_session_id
    ON user_interaction (session_id);

CREATE INDEX IF NOT EXISTS idx_news_id
    ON user_interaction (news_id);

CREATE INDEX IF NOT EXISTS idx_timestamp
    ON user_interaction (interaction_timestamp);
