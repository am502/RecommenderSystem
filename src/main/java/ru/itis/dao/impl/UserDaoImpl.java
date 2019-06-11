package ru.itis.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.model.User;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
	private static final String INSERT_USER = "INSERT INTO users (user_id, username) VALUES (:userId, :username);";
	private static final String GET_MAX_ID = "SELECT COALESCE(MAX(user_id), 0) FROM users;";
	private static final String GET_USER_BY_USERNAME = "SELECT * FROM users WHERE username = :username;";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public UserDaoImpl() {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
	}

	@Override
	public void addUsers(List<User> users) {
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(users.toArray());
		namedParameterJdbcTemplate.batchUpdate(INSERT_USER, batch);
	}

	public long getMaxId() {
		return namedParameterJdbcTemplate.getJdbcTemplate().queryForObject(GET_MAX_ID, Long.class);
	}

	@Override
	public User getUserByUsername(String username) {
		return namedParameterJdbcTemplate.queryForObject(GET_USER_BY_USERNAME, new MapSqlParameterSource()
				.addValue("username", username), new BeanPropertyRowMapper<>(User.class));
	}
}
