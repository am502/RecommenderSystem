package ru.itis.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.model.Article;
import ru.itis.util.Constants;

import java.util.List;

@Repository
public class ArticleDaoImpl implements ArticleDao {
	private static final String INSERT_ARTICLE = "INSERT INTO articles (article_id, title, content) " +
			"VALUES (:articleId, :title, :content);";
	private static final String GET_ARTICLE_BY_ID = "SELECT * FROM articles WHERE title = :title;";
	private static final String GET_SIMILAR_ARTICLES = "SELECT a.* FROM articles a INNER JOIN (" +
			"(SELECT first_article_id AS first, second_article_id AS second, similarity " +
			"FROM similarities WHERE first_article_id = :articleId) UNION ALL " +
			"(SELECT second_article_id AS first, first_article_id AS second, similarity " +
			"FROM similarities WHERE second_article_id = :articleId)) s " +
			"ON s.second = a.article_id ORDER BY (s.similarity) LIMIT :limit";
	private static final String GET_ALL_ARTICLES = "SELECT * FROM articles;";
	private static final String GET_ALL_ARTICLES_USER_NOT_RATE = "SELECT a.* FROM articles a WHERE a.article_id " +
			"NOT IN (SELECT article_id FROM user_item WHERE user_id = :userId);";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ArticleDaoImpl() {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
	}

	@Override
	public void addArticle(Article article) {
		SqlParameterSource parameters = new BeanPropertySqlParameterSource(article);
		namedParameterJdbcTemplate.update(INSERT_ARTICLE, parameters);
	}

	@Override
	public Article getArticleByTitle(String title) {
		return namedParameterJdbcTemplate.queryForObject(GET_ARTICLE_BY_ID, new MapSqlParameterSource()
				.addValue("title", title), new BeanPropertyRowMapper<>(Article.class));
	}

	@Override
	public List<Article> getSimilarArticles(String articleId) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("articleId", articleId);
		parameters.addValue("limit", Constants.N);
		return namedParameterJdbcTemplate.query(GET_SIMILAR_ARTICLES, parameters,
				new BeanPropertyRowMapper<>(Article.class));
	}

	@Override
	public List<Article> getAllArticles() {
		return namedParameterJdbcTemplate.query(GET_ALL_ARTICLES, new BeanPropertyRowMapper<>(Article.class));
	}

	@Override
	public List<Article> getAllArticlesUserNotRate(long userId) {
		return namedParameterJdbcTemplate.query(GET_ALL_ARTICLES_USER_NOT_RATE, new MapSqlParameterSource()
				.addValue("userId", userId), new BeanPropertyRowMapper<>(Article.class));
	}
}
