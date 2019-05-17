DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGSERIAL PRIMARY KEY,
  username VARCHAR(256),
  password VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS articles (
  article_id BIGSERIAL PRIMARY KEY,
  title VARCHAR(512),
  content TEXT,
  owner_id BIGINT REFERENCES users(user_id)
);