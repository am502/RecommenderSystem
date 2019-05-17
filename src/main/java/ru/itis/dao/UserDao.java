package ru.itis.dao;

import ru.itis.model.User;

import java.util.List;

public interface UserDao {
    void addUser(User user);

    void addUsers(List<User> users);
}
