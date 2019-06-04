CREATE TABLE articles_test (
  article_id VARCHAR(256) PRIMARY KEY,
  title VARCHAR(512)
);

CREATE TABLE words_test (
  word VARCHAR(256) PRIMARY KEY,
  articles_count BIGINT
);

CREATE TABLE article_words_test (
  article_id VARCHAR(256) REFERENCES articles_test (article_id) ON DELETE CASCADE,
  word VARCHAR(256) REFERENCES words_test (word) ON DELETE CASCADE,
  tf FLOAT
);

CREATE FUNCTION idf_test(w VARCHAR, total_articles_count BIGINT)
  RETURNS FLOAT AS $$
BEGIN
  RETURN (SELECT ln(total_articles_count / (SELECT articles_count FROM words_test WHERE word = w)::FLOAT));
END; $$
LANGUAGE 'plpgsql';

CREATE FUNCTION cosine_measure_test(a1_id VARCHAR, a2_id VARCHAR)
  RETURNS FLOAT AS $$
DECLARE
  total_articles_count BIGINT;
  dividend FLOAT;
  a1_length FLOAT;
  a2_length FLOAT;
BEGIN
  SELECT COUNT(*) INTO total_articles_count FROM articles_test;
  SELECT SUM(a1.tf * (SELECT idf_test(a1.word, total_articles_count))
    * a2.tf * (SELECT idf_test(a2.word, total_articles_count))) INTO dividend
  FROM article_words_test a1 INNER JOIN article_words_test a2 ON a1.word = a2.word
  WHERE a1.article_id = a1_id AND a2.article_id = a2_id;
  SELECT SUM(a1.tf * (SELECT idf_test(a1.word, total_articles_count))
    * a1.tf * (SELECT idf_test(a1.word, total_articles_count))) INTO a1_length
  FROM article_words_test a1 WHERE a1.article_id = a1_id;
  SELECT SUM(a2.tf * (SELECT idf_test(a2.word, total_articles_count))
    * a2.tf * (SELECT idf_test(a2.word, total_articles_count))) INTO a2_length
  FROM article_words_test a2 WHERE a2.article_id = a2_id;
  RETURN (dividend / (SELECT sqrt(a1_length)) / (SELECT sqrt(a2_length)));
END; $$
LANGUAGE 'plpgsql';

CREATE FUNCTION get_similar_articles_test(a_id VARCHAR, n INT)
  RETURNS SETOF articles_test AS $$
DECLARE
  r RECORD;
BEGIN
  CREATE TEMP TABLE article_measure (article_id VARCHAR(256), measure FLOAT);
  FOR r IN SELECT article_id FROM articles_test WHERE article_id <> a_id
  LOOP
    INSERT INTO article_measure (article_id, measure)
    SELECT r.article_id, measure FROM cosine_measure_test(a_id, r.article_id) AS measure;
  END LOOP;
  RETURN QUERY
    SELECT a.* FROM articles_test a INNER JOIN article_measure am ON a.article_id = am.article_id
    ORDER BY (am.measure) LIMIT n;
  DROP TABLE article_measure;
END; $$
LANGUAGE 'plpgsql';

INSERT INTO articles_test VALUES ('a1', 'article_1');
INSERT INTO articles_test VALUES ('a2', 'article_2');
INSERT INTO articles_test VALUES ('a3', 'article_3');

INSERT INTO words_test VALUES ('w1', 1);
INSERT INTO words_test VALUES ('w2', 2);
INSERT INTO words_test VALUES ('w3', 1);
INSERT INTO words_test VALUES ('w4', 2);
INSERT INTO words_test VALUES ('w5', 1);
INSERT INTO words_test VALUES ('w6', 1);

INSERT INTO article_words_test VALUES ('a1', 'w1', 0.5);
INSERT INTO article_words_test VALUES ('a1', 'w2', 0.3);
INSERT INTO article_words_test VALUES ('a1', 'w3', 0.2);
INSERT INTO article_words_test VALUES ('a1', 'w4', 0.4);

INSERT INTO article_words_test VALUES ('a2', 'w2', 0.3);
INSERT INTO article_words_test VALUES ('a2', 'w4', 0.7);
INSERT INTO article_words_test VALUES ('a2', 'w5', 0.1);
INSERT INTO article_words_test VALUES ('a2', 'w6', 0.6);

SELECT cosine_measure_test('a1', 'a2') AS measure;
SELECT cosine_measure_test('a1', 'a3');

SELECT * FROM get_similar_articles_test('a1', 10);

DROP FUNCTION idf_test;
DROP FUNCTION cosine_measure_test;
DROP FUNCTION get_similar_articles_test;

DROP TABLE article_words_test;
DROP TABLE articles_test;
DROP TABLE words_test;
