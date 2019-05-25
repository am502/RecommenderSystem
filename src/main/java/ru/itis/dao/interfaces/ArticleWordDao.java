package ru.itis.dao.interfaces;

import ru.itis.model.ArticleWord;

import java.util.List;

public interface ArticleWordDao {
    void addArticleWords(List<ArticleWord> articleWords);
}
