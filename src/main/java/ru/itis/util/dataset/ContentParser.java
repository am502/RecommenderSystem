package ru.itis.util.dataset;

import ru.itis.dao.impl.ArticleDaoImpl;
import ru.itis.dao.impl.KeywordDaoImpl;
import ru.itis.dao.impl.LengthDaoImpl;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.model.Article;
import ru.itis.service.MainServiceImpl;

import java.util.List;

public class ContentParser {
	public static void main(String[] args) {
		ArticleDao articleDao = new ArticleDaoImpl();

		List<Article> articles = articleDao.getAllArticles();

		MainServiceImpl mainService = new MainServiceImpl();
		mainService.setKeywordDao(new KeywordDaoImpl());
		mainService.setLengthDao(new LengthDaoImpl());

		for (Article article : articles) {
			mainService.addKeywords(article);
		}
	}
}
