package ru.itis.dao.interfaces;

import ru.itis.model.UserItem;

import java.util.List;

public interface UserItemDao {
	void addUserItems(List<UserItem> userItems);

	List<UserItem> getPopularUserItems();
}
