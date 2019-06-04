package ru.itis.util.dataset;

import ru.itis.dao.impl.UserDaoImpl;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.model.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class InsertUsers {
    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();

        Scanner in = DatasetReader.getScanner();

        List<User> users = new LinkedList<>();

        int userId = 0;
        while (in.hasNextLine()) {
            userId++;
            String[] line = in.nextLine().split(",");
            User user = User.builder()
                    .username(line[0])
                    .password("password" + userId)
                    .build();

            users.add(user);
        }

        userDao.addUsers(users);
    }
}
