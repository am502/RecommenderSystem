package ru.itis.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.model.User;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String INSERT_USER = "INSERT INTO users (username, password) VALUES (:username, :password);";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = :userId;";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserDaoImpl() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
    }

    @Override
    public void addUser(User user) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(user);
        namedParameterJdbcTemplate.update(INSERT_USER, parameters);
    }

    @Override
    public void addUsers(List<User> users) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(users.toArray());
        namedParameterJdbcTemplate.batchUpdate(INSERT_USER, batch);
    }

    @Override
    public User getUserByToken(String token) {
        return User.builder()
                .id(2)
                .build();
    }

    @Override
    public User getUserById(long userId) {
        return namedParameterJdbcTemplate.queryForObject(GET_USER_BY_ID, new MapSqlParameterSource()
                .addValue("userId", userId), new BeanPropertyRowMapper<>(User.class));
    }
}
