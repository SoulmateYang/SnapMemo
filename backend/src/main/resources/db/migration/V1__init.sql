CREATE TABLE briefing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    title VARCHAR(100) NOT NULL,
    summary VARCHAR(200),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_briefing_date UNIQUE (date)
);

CREATE TABLE briefing_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    briefing_id BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    url VARCHAR(1000),
    source VARCHAR(100),
    original_content VARCHAR(500),
    ai_summary VARCHAR(1000),
    importance_score INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_briefing_item FOREIGN KEY (briefing_id) REFERENCES briefing(id)
);

CREATE TABLE news_source (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    keywords VARCHAR(500),
    enabled BOOLEAN DEFAULT TRUE,
    max_items INT DEFAULT 20,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_briefing_date ON briefing(date);
CREATE INDEX idx_briefing_item_briefing_id ON briefing_item(briefing_id);

-- 初始数据源
INSERT INTO news_source (name, type, url, enabled, max_items) VALUES
    ('MIT Technology Review AI', 'RSS', 'https://www.technologyreview.com/feed/', TRUE, 20),
    ('The Verge AI', 'RSS', 'https://www.theverge.com/rss/ai-artificial-intelligence/index.xml', TRUE, 20),
    ('HackerNews', 'API', 'https://hacker-news.firebaseio.com/v0', TRUE, 20);
