package ru.itis.dao.interfaces;

import ru.itis.model.Article;

import java.util.List;

public interface ArticleDao {
	void addArticle(Article article);

	Article getArticleByTitle(String title);

	List<Article> getSimilarArticles(String articleId);

	List<Article> getAllArticles();

	List<Article> getAllArticlesUserNotRate(long userId);
}
