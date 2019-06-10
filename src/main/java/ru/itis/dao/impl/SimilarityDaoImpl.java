package ru.itis.dao.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.SimilarityDao;
import ru.itis.model.Similarity;

import java.util.List;

@Repository
public class SimilarityDaoImpl implements SimilarityDao {
	private static final String INSERT_SIMILARITY = "INSERT INTO similarities " +
			"(first_article_id, second_article_id, similarity) " +
			"VALUES (:firstArticleId, :secondArticleId, :similarity);";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SimilarityDaoImpl() {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
	}

	@Override
	public void addSimilarities(List<Similarity> similarities) {
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(similarities.toArray());
		namedParameterJdbcTemplate.batchUpdate(INSERT_SIMILARITY, batch);
	}
}
