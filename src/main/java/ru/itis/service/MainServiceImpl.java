package ru.itis.service;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dao.interfaces.*;
import ru.itis.dto.CollaborativeRecommendationsDto;
import ru.itis.dto.ContentRecommendationsDto;
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
	private SimilarityDao similarityDao;

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
		for (String articleId : articleIds) {
			try {
				Document document = Jsoup.connect(url + "/" + articleId).get();

				Article article = Article.builder()
						.articleId(articleId)
						.title(document.select("h1 > span").text())
						.content(document.select("div[class=post__body post__body_full]").text())
						.build();

				if (!article.getContent().isEmpty()) {
					long start = System.currentTimeMillis();
					articleDao.addArticle(article);
					addKeywords(article);
					System.out.println("Добавление статьи: " + (System.currentTimeMillis() - start));
				}
			} catch (HttpStatusException ignored) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

		Map<String, Double> multiplication = new HashMap<>();
		Map<String, Double> articleLengths = new HashMap<>();

		List<Keyword> keywords = keywordDao.getAllKeywords();

		if (keywords != null) {
			for (Keyword keyword : keywords) {
				if (popularWords.containsKey(keyword.getWord())) {
					multiplication.put(keyword.getArticleId(),
							multiplication.getOrDefault(keyword.getArticleId(), 0.0)
									+ popularWords.get(keyword.getWord()) * keyword.getTf());
				}
				articleLengths.put(keyword.getArticleId(), articleLengths.getOrDefault(
						keyword.getArticleId(), 0.0) + keyword.getTf() * keyword.getTf());
			}
		}

		keywordDao.addKeywords(popularWords.entrySet().stream()
				.map(e -> Keyword.builder()
						.articleId(article.getArticleId())
						.word(e.getKey())
						.tf(e.getValue())
						.build())
				.collect(Collectors.toList()));

		double length = popularWords.values().stream().mapToDouble(v -> v * v).sum();

		if (keywords != null) {
			List<Similarity> similarities = new ArrayList<>();
			for (Map.Entry<String, Double> entry : multiplication.entrySet()) {
				similarities.add(Similarity.builder()
						.firstArticleId(article.getArticleId())
						.secondArticleId(entry.getKey())
						.similarity(entry.getValue() / Math.sqrt(length)
								/ Math.sqrt(articleLengths.get(entry.getKey())))
						.build());
			}
			similarities.sort(Comparator.comparingDouble(Similarity::getSimilarity).reversed());
			similarityDao.addSimilarities(similarities.stream().limit(Constants.N).collect(Collectors.toList()));
		}
	}

	@Override
	public ContentRecommendationsDto getArticleByTitle(String title) {
		long start = System.currentTimeMillis();
		Article article = articleDao.getArticleByTitle(title);
		List<Article> recommendations = articleDao.getSimilarArticles(article.getArticleId());
		System.out.println("Поиск статьи и формирование рекомендаций: " + (System.currentTimeMillis() - start));
		return ContentRecommendationsDto.builder()
				.article(article)
				.recommendations(recommendations)
				.build();
	}

	@Override
	public CollaborativeRecommendationsDto getPersonalRecommendations(String username) {
		User user = userDao.getUserByUsername(username);
		List<UserItem> userItems = userItemDao.getPopularUserItems();

		Map<Long, Set<String>> userArticles = new HashMap<>();
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

		long start = System.currentTimeMillis();
		Map<Long, Map<Long, Double>> userUser = new HashMap<>();
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
			userUser.put(currentUser.getKey(), userSim);
		}
		System.out.println("Формирование матрицы user-user: " + (System.currentTimeMillis() - start));

		Map<Long, Double> neighbours = userUser.get(user.getUserId());

		List<Article> articles = articleDao.getAllArticlesUserNotRate(user.getUserId());

		start = System.currentTimeMillis();
		double sum = neighbours.values().stream().mapToDouble(Double::doubleValue).sum();

		Map<Article, Double> ratings = new HashMap<>();
		for (Article article : articles) {
			double k = 0;
			for (Map.Entry<Long, Double> entry : neighbours.entrySet()) {
				if (userArticles.get(entry.getKey()).contains(article.getArticleId())) {
					k += entry.getValue();
				}
			}
			ratings.put(article, k / sum);
		}

		List<Article> recommendations = new LinkedList<>(ratings.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(Constants.N)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(k, v) -> k, LinkedHashMap::new)).keySet());
		System.out.println("Формирование персональных рекомендаций: " + (System.currentTimeMillis() - start));

		return CollaborativeRecommendationsDto.builder()
				.recommendations(recommendations)
				.build();
	}

	public void setKeywordDao(KeywordDao keywordDao) {
		this.keywordDao = keywordDao;
	}

	public void setSimilarityDao(SimilarityDao similarityDao) {
		this.similarityDao = similarityDao;
	}
}
