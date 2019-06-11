package ru.itis.util.dataset;

import ru.itis.dao.impl.ArticleDaoImpl;
import ru.itis.dao.impl.UserDaoImpl;
import ru.itis.dao.impl.UserItemDaoImpl;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.dao.interfaces.UserItemDao;
import ru.itis.model.Article;
import ru.itis.model.User;
import ru.itis.model.UserItem;

import java.util.*;
import java.util.stream.Collectors;

public class InsertUsersAndUserItems {
	public static void main(String[] args) {
		UserDao userDao = new UserDaoImpl();
		UserItemDao userItemDao = new UserItemDaoImpl();
		ArticleDao articleDao = new ArticleDaoImpl();

		Map<String, Long> users = new HashMap<>();
		List<UserItem> userItems = new LinkedList<>();

		long userId = 0;

		Scanner in = DatasetReader.getScanner("dataset/data.csv");
		while (in.hasNextLine()) {
			String[] line = in.nextLine().split(" ");
			UserItem userItem;
			if (users.containsKey(line[0])) {
				userItem = UserItem.builder()
						.userId(users.get(line[0]))
						.articleId(line[1])
						.build();
			} else {
				userId++;
				users.put(line[0], userId);
				userItem = UserItem.builder()
						.userId(userId)
						.articleId(line[1])
						.build();
			}
			userItems.add(userItem);
		}

		userDao.addUsers(users.entrySet().stream()
				.map(e -> User.builder()
						.username(e.getKey())
						.userId(e.getValue())
						.build())
				.collect(Collectors.toList()));

		Set<String> articles = articleDao.getAllArticles().stream()
				.map(Article::getArticleId).collect(Collectors.toSet());

		List<UserItem> userItemsToAdd = userItems.stream().filter(e -> articles.contains(e.getArticleId()))
				.collect(Collectors.toList());

		userItemDao.addUserItems(userItemsToAdd);
	}
}
