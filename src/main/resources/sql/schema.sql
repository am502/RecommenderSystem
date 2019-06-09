DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS articles CASCADE;
DROP TABLE IF EXISTS user_item CASCADE;
DROP TABLE IF EXISTS keywords CASCADE;
DROP TABLE IF EXISTS lengths CASCADE;

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

CREATE OR REPLACE FUNCTION cosine_measure(a1_id VARCHAR, a2_id VARCHAR)
  RETURNS FLOAT AS $$
BEGIN
  RETURN ((SELECT SUM(a1.tf * a2.tf) FROM keywords a1 INNER JOIN keywords a2 ON a1.word = a2.word
    WHERE a1.article_id = a1_id AND a2.article_id = a2_id)
    / (SELECT length FROM lengths WHERE article_id = a1_id)
    / (SELECT length FROM lengths WHERE article_id = a2_id));
END; $$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_similar_articles(a_id VARCHAR, n INT)
  RETURNS SETOF articles AS $$
DECLARE
  r RECORD;
BEGIN
  CREATE TEMP TABLE article_measure (article_id VARCHAR(255), measure FLOAT);
  FOR r IN SELECT article_id FROM articles WHERE article_id <> a_id
    LOOP
      INSERT INTO article_measure (article_id, measure)
      SELECT r.article_id, measure FROM cosine_measure(a_id, r.article_id) AS measure;
    END LOOP;
  RETURN QUERY
    SELECT a.* FROM articles a INNER JOIN article_measure am ON a.article_id = am.article_id
    ORDER BY (am.measure) LIMIT n;
  DROP TABLE article_measure;
END; $$
LANGUAGE 'plpgsql';
