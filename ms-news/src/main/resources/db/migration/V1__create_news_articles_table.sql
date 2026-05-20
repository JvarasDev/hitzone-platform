CREATE TABLE IF NOT EXISTS news_articles (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    summary      TEXT,
    content      TEXT         NOT NULL,
    category     VARCHAR(50)  NOT NULL DEFAULT 'GENERAL',
    author       VARCHAR(100),
    image_url    VARCHAR(500),
    published    BOOLEAN      DEFAULT FALSE,
    created_at   TIMESTAMP    DEFAULT NOW(),
    updated_at   TIMESTAMP    DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_news_category   ON news_articles (category);
CREATE INDEX IF NOT EXISTS idx_news_published  ON news_articles (published);
CREATE INDEX IF NOT EXISTS idx_news_created_at ON news_articles (created_at DESC);