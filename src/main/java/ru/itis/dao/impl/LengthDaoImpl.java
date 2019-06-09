package ru.itis.dao.impl;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.LengthDao;
import ru.itis.model.Length;

@Repository
public class LengthDaoImpl implements LengthDao {
	private static final String INSERT_LENGTH = "INSERT INTO lengths (article_id, length) " +
			"VALUES (:articleId, :length);";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public LengthDaoImpl() {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
	}

	@Override
	public void addLength(Length length) {
		SqlParameterSource parameters = new BeanPropertySqlParameterSource(length);
		namedParameterJdbcTemplate.update(INSERT_LENGTH, parameters);
	}
}
