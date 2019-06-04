package ru.itis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.conversion.ArticlesToRecommendationDtosConverter;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.dao.interfaces.ArticleWordDao;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.dao.interfaces.WordDao;
import ru.itis.dto.request.RequestArticleDto;
import ru.itis.dto.response.RecommendationDto;
import ru.itis.dto.response.ResponseArticleDto;
import ru.itis.model.Article;
import ru.itis.model.ArticleWord;
import ru.itis.model.User;
import ru.itis.service.interfaces.UserService;
import ru.itis.util.Constants;
import ru.itis.util.Utils;

import java.util.*;
import java.util.regex.Matcher;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private WordDao wordDao;
    @Autowired
    private ArticleWordDao articleWordDao;

    @Override
    public void addArticle(RequestArticleDto requestArticleDto, String token) {
        User user = userDao.getUserByToken(token);
        String articleId = UUID.randomUUID().toString();
        Article article = Article.builder()
                .articleId(articleId)
                .title(requestArticleDto.getTitle())
                .content(requestArticleDto.getContent())
                .ownerId(user.getId())
                .build();
        articleDao.addArticle(article);
        addWords(article);
    }

    public void addWords(Article article) {
        int totalWordCount = 0;
        Map<String, Integer> wordsWithCount = new HashMap<>();
        Matcher matcher = Utils.getInstance().getRussianWordsPattern().matcher(article.getContent());
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (!Utils.getInstance().getStopWords().contains(word) && word.length() >= 3) {
                String processedWord = Utils.getInstance().processPorterStem(word);
                wordsWithCount.put(processedWord, wordsWithCount.getOrDefault(processedWord, 0) + 1);
                totalWordCount++;
            }
        }
        List<String> words = new LinkedList<>(wordsWithCount.keySet());
        wordDao.addWords(words);
        List<ArticleWord> articleWords = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : wordsWithCount.entrySet()) {
            ArticleWord articleWord = ArticleWord.builder()
                    .articleId(article.getArticleId())
                    .word(entry.getKey())
                    .tf((double) entry.getValue() / totalWordCount)
                    .build();
            articleWords.add(articleWord);
        }
        articleWordDao.addArticleWords(articleWords);
    }

    @Override
    public ResponseArticleDto getArticleById(String articleId, String token) {
        User user = userDao.getUserByToken(token);
        Article article = articleDao.getArticleById(articleId);
        User articleOwner = userDao.getUserById(article.getOwnerId());
        List<Article> similarArticles = articleDao.getSimilarArticles(articleId, Constants.LIMIT_CONTENT_FILTERING);
        List<RecommendationDto> contentRecommendations = ArticlesToRecommendationDtosConverter.getInstance()
                .convert(similarArticles);
        return ResponseArticleDto.builder()
                .title(article.getTitle())
                .content(article.getContent())
                .ownerName(articleOwner.getUsername())
                .contentRecommendations(contentRecommendations)
                .collaborativeRecommendations(null)
                .build();
    }
}
