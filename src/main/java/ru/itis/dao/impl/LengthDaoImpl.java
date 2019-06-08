package ru.itis.dao.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.LengthDao;
import ru.itis.model.Length;

import java.util.List;

@Repository
public class LengthDaoImpl implements LengthDao {
    private static final String INSERT_LENGTH = "INSERT INTO lengths (article_id, length) " +
            "VALUES (:articleId, :length);";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LengthDaoImpl() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
    }

    @Override
    public void addLengths(List<Length> lengths) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(lengths.toArray());
        namedParameterJdbcTemplate.batchUpdate(INSERT_LENGTH, batch);
    }
}
