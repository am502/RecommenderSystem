DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS user_item;
DROP TABLE IF EXISTS keywords;
DROP TABLE IF EXISTS lengths;

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
  tf DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS lengths (
  article_id VARCHAR(255) REFERENCES articles (article_id) ON DELETE CASCADE,
  length DOUBLE PRECISION
);