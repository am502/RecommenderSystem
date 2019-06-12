package ru.itis.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import ru.itis.dao.impl.UserItemDaoImpl;
import ru.itis.dao.interfaces.UserItemDao;
import ru.itis.model.UserItem;
import ru.itis.util.Constants;

import javax.annotation.PostConstruct;
import javax.swing.Timer;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@ComponentScan("ru.itis")
public class App {
	private static Map<Long, Map<Long, Double>> userUserMatrix;
	private static Map<Long, Set<String>> userArticles;

	public static void main(String[] args) {
		Timer timer = new Timer(3600000, e -> initUserUserMatrix());
		timer.start();
		SpringApplication.run(App.class, args);
	}

	@PostConstruct
	private static void initUserUserMatrix() {
		System.out.println("Start init user-user.");
		UserItemDao userItemDao = new UserItemDaoImpl();
		List<UserItem> userItems = userItemDao.getPopularUserItems();

		userArticles = new HashMap<>();
		for (UserItem userItem : userItems) {
			long userId = userItem.getUserId();
			String articleId = userItem.getArticleId();
			if (userArticles.containsKey(userId)) {
				userArticles.get(userId).add(articleId);
			} else {
				userArticles.put(userId, new HashSet<String>() {{
					add(articleId);
				}});
			}
		}

		userUserMatrix = new HashMap<>();
		for (Map.Entry<Long, Set<String>> currentUser : userArticles.entrySet()) {
			Map<Long, Double> userSim = new HashMap<>();
			for (Map.Entry<Long, Set<String>> otherUser : userArticles.entrySet()) {
				if (!currentUser.getKey().equals(otherUser.getKey())) {
					int count = 0;
					if (currentUser.getValue().size() < otherUser.getValue().size()) {
						for (String articleId : currentUser.getValue()) {
							if (otherUser.getValue().contains(articleId)) {
								count++;
							}
						}
					} else {
						for (String articleId : otherUser.getValue()) {
							if (currentUser.getValue().contains(articleId)) {
								count++;
							}
						}
					}
					userSim.put(otherUser.getKey(), count * count
							/ (double) currentUser.getValue().size() / otherUser.getValue().size());
				}
			}
			userSim = userSim.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.limit(Constants.K)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
							(k, v) -> k, LinkedHashMap::new));
			userUserMatrix.put(currentUser.getKey(), userSim);
		}
		System.out.println("Finish init user-user.");
	}

	public static Map<Long, Map<Long, Double>> getUserUserMatrix() {
		return userUserMatrix;
	}

	public static Map<Long, Set<String>> getUserArticles() {
		return userArticles;
	}
}
