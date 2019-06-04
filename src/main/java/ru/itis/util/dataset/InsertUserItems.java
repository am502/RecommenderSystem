package ru.itis.util.dataset;

import ru.itis.dao.impl.ArticleDaoImpl;
import ru.itis.dao.impl.UserItemDaoImpl;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.dao.interfaces.UserItemDao;
import ru.itis.model.UserItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class InsertUserItems {
    public static void main(String[] args) {
        UserItemDao userItemDao = new UserItemDaoImpl();
        ArticleDao articleDao = new ArticleDaoImpl();

        Scanner in = DatasetReader.getScanner();

        List<UserItem> userItems = new LinkedList<>();

        int userId = 0;
        while (in.hasNextLine()) {
            userId++;
            String[] line = in.nextLine().split(",");
            for (String postId : line[1].split(" ")) {
                if (articleDao.articleExistenceById(postId)) {
                    UserItem userItem = UserItem.builder()
                            .articleId(postId)
                            .userId(userId)
                            .build();
                    userItems.add(userItem);
                }
            }
        }

        userItemDao.addUserItems(userItems);
    }
}
