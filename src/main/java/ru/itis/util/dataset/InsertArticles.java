package ru.itis.util.dataset;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.itis.dao.impl.ArticleDaoImpl;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.model.Article;

import java.io.IOException;
import java.util.*;

public class InsertArticles {
    private static final String URL = "https://habr.com/ru/post/";

    public static void main(String[] args) {
        Random random = new Random();

        ArticleDao articleDao = new ArticleDaoImpl();

        Scanner in = DatasetReader.getScanner();

        Set<String> posts = new HashSet<>();

        int userId = 0;
        while (in.hasNextLine()) {
            userId++;
            String[] line = in.nextLine().split(",");
            for (String postId : line[1].split(" ")) {
                posts.add(postId);
            }
        }

        List<Article> articles = new LinkedList<>();

        List<String> postIds = new ArrayList<>(posts);
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
