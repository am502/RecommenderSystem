package ru.itis.service;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dao.interfaces.*;
import ru.itis.model.*;
import ru.itis.util.Constants;
import ru.itis.util.Utils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Transactional
@Service
public class MainServiceImpl implements MainService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private UserItemDao userItemDao;
	@Autowired
	private KeywordDao keywordDao;
	@Autowired
	private LengthDao lengthDao;

	@Override
	public void upload(String url, Scanner in) {
		long userId = userDao.getMaxId();

		Map<String, Long> users = new HashMap<>();
		List<UserItem> userItems = new LinkedList<>();
		Set<String> articleIds = new HashSet<>();

		while (in.hasNextLine()) {
			String[] line = in.nextLine().split(" ");
			if (line.length == 2) {
				UserItem userItem;
				if (users.containsKey(line[1])) {
					userItem = UserItem.builder()
							.userId(users.get(line[1]))
							.articleId(line[0])
							.build();
				} else {
					userId++;
					users.put(line[1], userId);
					userItem = UserItem.builder()
							.userId(userId)
							.articleId(line[0])
							.build();
				}
				userItems.add(userItem);
			}
			articleIds.add(line[0]);
		}

		if (users.size() != 0) {
			userDao.addUsers(users.entrySet().stream()
					.map(e -> User.builder()
							.username(e.getKey())
							.userId(e.getValue())
							.build())
					.collect(Collectors.toList()));
		}

		addArticles(url, articleIds);

		if (users.size() != 0) {
			userItemDao.addUserItems(userItems);
		}
	}

	public void addArticles(String url, Set<String> articleIds) {
		List<Article> articles = new LinkedList<>();

		for (String articleId : articleIds) {
			try {
				Document document = Jsoup.connect(url + articleId).get();

				Article article = Article.builder()
						.articleId(articleId)
						.title(document.select("h1 > span").text())
						.content(document.select("div[class=post__body post__body_full]").text())
						.build();

				if (!article.getContent().isEmpty()) {
					articles.add(article);
					addKeywords(article);
				}
			} catch (HttpStatusException ignored) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		articleDao.addArticles(articles);
	}

	public void addKeywords(Article article) {
		int totalWordCount = 0;
		Map<String, Double> wordsWithCount = new HashMap<>();
		Matcher matcher = Utils.getInstance().getRussianWordsPattern().matcher(article.getContent());
		while (matcher.find()) {
			String word = matcher.group().toLowerCase();
			if (!Utils.getInstance().getStopWords().contains(word) && word.length() >= 3) {
				String processedWord = Utils.getInstance().processPorterStem(word);
				wordsWithCount.put(processedWord, wordsWithCount.getOrDefault(processedWord, 0.0) + 1);
				totalWordCount++;
			}
		}
		for (Map.Entry<String, Double> entry : wordsWithCount.entrySet()) {
			entry.setValue(entry.getValue() / totalWordCount);
		}

		Map<String, Double> popularWords = wordsWithCount.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(Constants.KEYWORDS_LIMIT)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> k, LinkedHashMap::new));

		keywordDao.addKeywords(popularWords.entrySet().stream()
				.map(e -> Keyword.builder()
						.articleId(article.getArticleId())
						.word(e.getKey())
						.tf(e.getValue())
						.build())
				.collect(Collectors.toList()));

		double length = popularWords.values().stream().mapToDouble(v -> v * v).sum();
		lengthDao.addLength(Length.builder()
				.articleId(article.getArticleId())
				.length(Math.sqrt(length))
				.build());
	}
}
