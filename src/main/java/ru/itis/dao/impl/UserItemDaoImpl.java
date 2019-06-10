package ru.itis.dao.impl;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.UserItemDao;
import ru.itis.model.UserItem;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserItemDaoImpl implements UserItemDao {
	private static final String INSERT_USER_ITEM = "INSERT INTO user_item (user_id, article_id) " +
			"VALUES (?, ?) ON CONFLICT (article_id) DO NOTHING;";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public UserItemDaoImpl() {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
	}

	@Override
	public void addUserItems(List<UserItem> userItems) {
		namedParameterJdbcTemplate.getJdbcTemplate()
				.batchUpdate(INSERT_USER_ITEM, new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, userItems.get(i).getUserId());
						ps.setString(2, userItems.get(i).getArticleId());
					}

					@Override
					public int getBatchSize() {
						return userItems.size();
					}
				});
	}
}
