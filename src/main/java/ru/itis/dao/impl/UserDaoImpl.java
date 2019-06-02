package ru.itis.dao.impl;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.dao.config.DaoConfig;
import ru.itis.model.User;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String INSERT_USER = "INSERT INTO users (username, password) VALUES (:username, :password);";

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
                .id(1)
                .build();
    }


}
