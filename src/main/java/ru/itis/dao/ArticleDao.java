package ru.itis.dao;

import ru.itis.model.Article;

import java.util.List;

public interface ArticleDao {
    void addArticles(List<Article> articles);
}
