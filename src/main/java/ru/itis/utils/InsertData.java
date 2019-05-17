package ru.itis.utils;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.itis.dao.ArticleDao;
import ru.itis.dao.UserDao;
import ru.itis.dao.impl.ArticleDaoImpl;
import ru.itis.dao.impl.UserDaoImpl;
import ru.itis.model.Article;
import ru.itis.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class InsertData {
    private static final String URL = "https://habr.com/ru/post/";

    public static void main(String[] args) {
        Random random = new Random();

        UserDao userDao = new UserDaoImpl();
        ArticleDao articleDao = new ArticleDaoImpl();

        Scanner sc = null;
        try {
            sc = new Scanner(new File("src/main/resources/dataset/user_fav.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sc.nextLine();

        Set<String> postIds = new HashSet<>();
        List<User> users = new ArrayList<>();

        int userId = 0;
        while (sc.hasNextLine()) {
            userId++;

            String[] line = sc.nextLine().split(",");

            User user = User.builder()
                    .username(line[0])
                    .password("password" + userId)
                    .build();

            users.add(user);

            for (String postId : line[1].split(" ")) {
                postIds.add(postId);
            }
        }

//        userDao.addUsers(users);

        List<Article> articles = new ArrayList<>();

        int count = 0;
        for (String postId : postIds) {
            if (count == 100) {
                break;
            }
            count++;
            System.out.println(count);
            try {
                Document doc = Jsoup.connect(URL + postId + "/").get();

                Article article = Article.builder()
                        .title(doc.select("h1 > span").text())
                        .content(doc.select("div[class=post__body post__body_full]").text())
                        .ownerId(random.nextInt(userId) + 1)
                        .build();

                if (!article.getContent().isEmpty()) {
                    articles.add(article);
                }
            } catch (HttpStatusException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        articleDao.addArticles(articles);
    }
}