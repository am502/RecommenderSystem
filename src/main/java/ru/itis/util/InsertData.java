package ru.itis.util;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.itis.dao.impl.ArticleDaoImpl;
import ru.itis.dao.impl.UserDaoImpl;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.model.Article;
import ru.itis.model.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class InsertData {
    private static final String URL = "https://habr.com/ru/post/";

    public static void main(String[] args) {
        Random random = new Random();

        UserDao userDao = new UserDaoImpl();
        ArticleDao articleDao = new ArticleDaoImpl();

        Scanner in = null;
        try {
            in = new Scanner(new FileInputStream("src/main/resources/dataset/user_fav.csv"));
            in.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<User> users = new LinkedList<>();
        Set<String> posts = new HashSet<>();

        int userId = 0;
        while (in.hasNextLine()) {
            userId++;

            String[] line = in.nextLine().split(",");

            User user = User.builder()
                    .username(line[0])
                    .password("password" + userId)
                    .build();

            users.add(user);

            for (String postId : line[1].split(" ")) {
                posts.add(postId);
            }
        }

        userDao.addUsers(users);

        List<String> postIds = new ArrayList<>(posts);

        List<Article> articles = new LinkedList<>();
        for (int i = 22000; i < postIds.size(); i++) {
            if (i % 1000 == 0 && !articles.isEmpty()) {
                articleDao.addArticles(articles);
                articles = new LinkedList<>();
                System.out.println(i);
            }
            try {
                String postId = postIds.get(i);
                Document doc = Jsoup.connect(URL + postId + "/").get();

                Article article = Article.builder()
                        .articleId(postId)
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

        if (!articles.isEmpty()) {
            articleDao.addArticles(articles);
        }
    }
}
