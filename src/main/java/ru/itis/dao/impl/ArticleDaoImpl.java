package ru.itis.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.model.Article;

import java.util.List;

@Repository
public class ArticleDaoImpl implements ArticleDao {
    private static final String INSERT_ARTICLE = "INSERT INTO articles (article_id, title, content, owner_id) " +
            "VALUES (:articleId, :title, :content, :ownerId);";
    private static final String GET_ARTICLE_BY_ID = "SELECT * FROM articles WHERE article_id = :articleId;";

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
    public void addArticles(List<Article> articles) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(articles.toArray());
        namedParameterJdbcTemplate.batchUpdate(INSERT_ARTICLE, batch);
    }

    @Override
    public Article getArticleById(String articleId) {
        return namedParameterJdbcTemplate.queryForObject(GET_ARTICLE_BY_ID, new MapSqlParameterSource()
                .addValue("articleId", articleId), new BeanPropertyRowMapper<>(Article.class));
    }

    @Override
    public List<Article> getSimilarArticles(String articleId) {
        return null;
    }
}
