package ru.itis.service.impl;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dao.interfaces.*;
import ru.itis.model.Article;
import ru.itis.model.User;
import ru.itis.model.UserItem;
import ru.itis.service.interfaces.MainService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class MainServiceImpl implements MainService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private WordDao wordDao;
	@Autowired
	private ArticleWordDao articleWordDao;
	@Autowired
	private UserItemDao userItemDao;

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


		userDao.addUsers(users.entrySet().stream()
				.map(e -> User.builder()
						.username(e.getKey())
						.userId(e.getValue())
						.build())
				.collect(Collectors.toList()));

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
				}
			} catch (HttpStatusException ignored) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		articleDao.addArticles(articles);
	}

	public void addKeywords(Article article) {

	}

//    @Override
//    public void addArticle(RequestArticleDto requestArticleDto, String token) {
//        User user = userDao.getUserByToken(token);
//        String articleId = UUID.randomUUID().toString();
//        Article article = Article.builder()
//                .articleId(articleId)
//                .title(requestArticleDto.getTitle())
//                .content(requestArticleDto.getContent())
//                .build();
//        articleDao.addArticle(article);
//        addWords(article);
//    }
//
//    public void addWords(Article article) {
//        int totalWordCount = 0;
//        Map<String, Integer> wordsWithCount = new HashMap<>();
//        Matcher matcher = Utils.getInstance().getRussianWordsPattern().matcher(article.getContent());
//        while (matcher.find()) {
//            String word = matcher.group().toLowerCase();
//            if (!Utils.getInstance().getStopWords().contains(word) && word.length() >= 3) {
//                String processedWord = Utils.getInstance().processPorterStem(word);
//                wordsWithCount.put(processedWord, wordsWithCount.getOrDefault(processedWord, 0) + 1);
//                totalWordCount++;
//            }
//        }
//        List<String> words = new LinkedList<>(wordsWithCount.keySet());
//        wordDao.addWords(words);
//        List<ArticleWord> articleWords = new LinkedList<>();
//        for (Map.Entry<String, Integer> entry : wordsWithCount.entrySet()) {
//            ArticleWord articleWord = ArticleWord.builder()
//                    .articleId(article.getArticleId())
//                    .word(entry.getKey())
//                    .tf((double) entry.getValue() / totalWordCount)
//                    .build();
//            articleWords.add(articleWord);
//        }
//        articleWordDao.addArticleWords(articleWords);
//    }
//
//    @Override
//    public ResponseArticleDto getArticleById(String articleId, String token) {
//        User user = userDao.getUserByToken(token);
//        Article article = articleDao.getArticleById(articleId);
//        User articleOwner = userDao.getUserById(article.getOwnerId());
//        List<RecommendationDto> contentRecommendations = ArticlesToRecommendationDtosConverter.getInstance()
//                .convert(articleDao.getSimilarArticles(articleId, Constants.LIMIT_CONTENT_FILTERING));
//        List<RecommendationDto> collaborativeRecommendations = null;
//        return ResponseArticleDto.builder()
//                .title(article.getTitle())
//                .content(article.getContent())
//                .ownerName(articleOwner.getUsername())
//                .contentRecommendations(contentRecommendations)
//                .collaborativeRecommendations(collaborativeRecommendations)
//                .build();
//    }
//
//    @Override
//    public void addToFavorites(String articleId, String token) {
//        User user = userDao.getUserByToken(token);
//        UserItem userItem = UserItem.builder()
//                .articleId(articleId)
//                .build();
//        userItemDao.addUserItem(userItem);
//    }

	public void setWordDao(WordDao wordDao) {
		this.wordDao = wordDao;
	}

	public void setArticleWordDao(ArticleWordDao articleWordDao) {
		this.articleWordDao = articleWordDao;
	}
}
