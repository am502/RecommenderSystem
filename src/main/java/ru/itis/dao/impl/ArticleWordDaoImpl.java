package ru.itis.dao.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.ArticleWordDao;
import ru.itis.model.ArticleWord;

import java.util.List;

@Repository
public class ArticleWordDaoImpl implements ArticleWordDao {
    private static final String INSERT_ARTICLE_WORD = "INSERT INTO article_words (article_id, word, tf) " +
            "VALUES (:articleId, :word, :tf);";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ArticleWordDaoImpl() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
    }

    @Override
    public void addArticleWords(List<ArticleWord> articleWords) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(articleWords.toArray());
        namedParameterJdbcTemplate.batchUpdate(INSERT_ARTICLE_WORD, batch);
    }
}
