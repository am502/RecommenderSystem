DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS user_item;
DROP TABLE IF EXISTS article_words;
DROP TABLE IF EXISTS words;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGSERIAL PRIMARY KEY,
  username VARCHAR(256),
  password VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS articles (
  article_id VARCHAR(256) PRIMARY KEY,
  title VARCHAR(512),
  content TEXT,
  owner_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_item (
  user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
  article_id VARCHAR(256) REFERENCES articles(article_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS words (
  word VARCHAR(256) PRIMARY KEY,
  articles_count BIGINT
);

CREATE TABLE IF NOT EXISTS article_words (
  article_id VARCHAR(256) REFERENCES articles(article_id) ON DELETE CASCADE,
  word VARCHAR(256) REFERENCES words(word) ON DELETE CASCADE,
  tf FLOAT
);

CREATE OR REPLACE FUNCTION idf(w VARCHAR, total_articles_count BIGINT)
  RETURNS FLOAT AS $$
BEGIN
  RETURN (SELECT ln(total_articles_count / (SELECT articles_count FROM words WHERE word = w)::FLOAT));
END; $$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION cosine_measure(a1_id VARCHAR, a2_id VARCHAR)
  RETURNS FLOAT AS $$
DECLARE
  total_articles_count BIGINT;
  dividend FLOAT;
  a1_length FLOAT;
  a2_length FLOAT;
BEGIN
  SELECT COUNT(*) INTO total_articles_count FROM articles;
  SELECT SUM(a1.tf * (SELECT idf(a1.word, total_articles_count))
    * a2.tf * (SELECT idf(a2.word, total_articles_count))) INTO dividend
  FROM article_words a1 INNER JOIN article_words a2 ON a1.word = a2.word
  WHERE a1.article_id = a1_id AND a2.article_id = a2_id;
  SELECT SUM(a1.tf * (SELECT idf(a1.word, total_articles_count))
    * a1.tf * (SELECT idf(a1.word, total_articles_count))) INTO a1_length
  FROM article_words a1 WHERE a1.article_id = a1_id;
  SELECT SUM(a2.tf * (SELECT idf(a2.word, total_articles_count))
    * a2.tf * (SELECT idf(a2.word, total_articles_count))) INTO a2_length
  FROM article_words a2 WHERE a2.article_id = a2_id;
  RETURN (dividend / (SELECT sqrt(a1_length)) / (SELECT sqrt(a2_length)));
END; $$
LANGUAGE 'plpgsql';
