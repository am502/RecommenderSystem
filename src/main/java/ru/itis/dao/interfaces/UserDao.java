package ru.itis.dao.interfaces;

import ru.itis.model.User;

import java.util.List;

public interface UserDao {
    void addUsers(List<User> users);

    long getMaxId();
}
