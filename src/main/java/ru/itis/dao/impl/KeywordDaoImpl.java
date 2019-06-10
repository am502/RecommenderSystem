package ru.itis.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.KeywordDao;
import ru.itis.model.Keyword;

import java.util.List;

@Repository
public class KeywordDaoImpl implements KeywordDao {
	private static final String INSERT_KEYWORD = "INSERT INTO keywords (article_id, word, tf) " +
			"VALUES (:articleId, :word, :tf);";
	private static final String GET_ALL_KEYWORDS = "SELECT * FROM keywords;";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public KeywordDaoImpl() {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
	}

	@Override
	public void addKeywords(List<Keyword> keywords) {
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(keywords.toArray());
		namedParameterJdbcTemplate.batchUpdate(INSERT_KEYWORD, batch);
	}

	@Override
	public List<Keyword> getAllKeywords() {
		return namedParameterJdbcTemplate.query(GET_ALL_KEYWORDS, new BeanPropertyRowMapper<>(Keyword.class));
	}
}
