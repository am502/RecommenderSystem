package ru.itis.dao.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.ArticleDao;
import ru.itis.dao.config.DaoConfig;
import ru.itis.model.Article;

import java.util.List;

@Repository
public class ArticleDaoImpl implements ArticleDao {
    private static final String INSERT_ARTICLE = "INSERT INTO articles (title, content, owner_id) " +
            "VALUES (:title, :content, :ownerId);";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ArticleDaoImpl() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
    }

    @Override
    public void addArticles(List<Article> articles) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(articles.toArray());
        namedParameterJdbcTemplate.batchUpdate(INSERT_ARTICLE, batch);
    }
}