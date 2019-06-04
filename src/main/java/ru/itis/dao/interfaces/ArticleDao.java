package ru.itis.dao.interfaces;

import ru.itis.model.Article;

import java.util.List;

public interface ArticleDao {
    void addArticle(Article article);

    void addArticles(List<Article> articles);

    Article getArticleById(String articleId);

    List<Article> getSimilarArticles(String articleId, int limit);
}
