package ru.itis.dao.impl;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.WordDao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WordDaoImpl implements WordDao {
    private static final String INSERT_WORD = "INSERT INTO words (word, articles_count) VALUES (?, 1) " +
            "ON CONFLICT (word) DO UPDATE SET articles_count = words.articles_count + 1;";

    private JdbcTemplate jdbcTemplate;

    public WordDaoImpl() {
        jdbcTemplate = new JdbcTemplate(DaoConfig.dataSource());
    }

    @Override
    public void addWords(List<String> words) {
        jdbcTemplate.batchUpdate(INSERT_WORD, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, words.get(i));
            }

            @Override
            public int getBatchSize() {
                return words.size();
            }
        });
    }
}
