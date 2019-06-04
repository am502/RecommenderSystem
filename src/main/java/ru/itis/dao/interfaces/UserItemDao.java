package ru.itis.dao.interfaces;

import ru.itis.model.UserItem;

import java.util.List;

public interface UserItemDao {
    void addUserItem(UserItem userItem);

    void addUserItems(List<UserItem> userItems);
}
