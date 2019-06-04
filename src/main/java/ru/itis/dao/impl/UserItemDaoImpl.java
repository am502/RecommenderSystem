package ru.itis.dao.impl;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.itis.dao.config.DaoConfig;
import ru.itis.dao.interfaces.UserItemDao;
import ru.itis.model.UserItem;

import java.util.List;

@Repository
public class UserItemDaoImpl implements UserItemDao {
    private static final String INSERT_USER_ITEM = "INSERT INTO user_item (user_id, article_id) " +
            "VALUES (:userId, :articleId);";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserItemDaoImpl() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(DaoConfig.dataSource());
    }

    @Override
    public void addUserItem(UserItem userItem) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(userItem);
        namedParameterJdbcTemplate.update(INSERT_USER_ITEM, parameters);
    }

    @Override
    public void addUserItems(List<UserItem> userItems) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(userItems.toArray());
        namedParameterJdbcTemplate.batchUpdate(INSERT_USER_ITEM, batch);
    }
}
