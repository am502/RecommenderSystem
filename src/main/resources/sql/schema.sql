DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS articles CASCADE;
DROP TABLE IF EXISTS user_item CASCADE;
DROP TABLE IF EXISTS keywords CASCADE;
DROP TABLE IF EXISTS similarities CASCADE;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGSERIAL PRIMARY KEY,
  username VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS articles (
  article_id VARCHAR(255) PRIMARY KEY,
  title VARCHAR(255),
  content TEXT
);

CREATE TABLE IF NOT EXISTS user_item (
  user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
  article_id VARCHAR(255) REFERENCES articles (article_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS keywords (
  keyword_id BIGSERIAL PRIMARY KEY,
  article_id VARCHAR(255) REFERENCES articles (article_id) ON DELETE CASCADE,
  word VARCHAR(255),
  tf FLOAT
);

CREATE TABLE IF NOT EXISTS similarities (
  first_article_id VARCHAR(255) REFERENCES articles (article_id) ON DELETE CASCADE,
  second_article_id VARCHAR(255) REFERENCES articles (article_id) ON DELETE CASCADE,
  similarity FLOAT
);