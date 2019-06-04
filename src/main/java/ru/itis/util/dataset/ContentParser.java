package ru.itis.util.dataset;

import ru.itis.dao.impl.ArticleDaoImpl;
import ru.itis.dao.impl.ArticleWordDaoImpl;
import ru.itis.dao.impl.WordDaoImpl;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.model.Article;
import ru.itis.service.impl.UserServiceImpl;

import java.util.List;

public class ContentParser {
    public static void main(String[] args) {
        ArticleDao articleDao = new ArticleDaoImpl();
        List<Article> articles = articleDao.getAllArticles();

        UserServiceImpl userService = new UserServiceImpl();
        userService.setWordDao(new WordDaoImpl());
        userService.setArticleWordDao(new ArticleWordDaoImpl());

        for (Article article : articles) {
            userService.addWords(article);
        }
    }
}
