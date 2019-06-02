package ru.itis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.dao.interfaces.ArticleWordDao;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.dao.interfaces.WordDao;
import ru.itis.dto.RequestArticleDto;
import ru.itis.model.Article;
import ru.itis.model.ArticleWord;
import ru.itis.model.User;
import ru.itis.service.interfaces.UserService;
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
        String articleId = UUID.randomUUID().toString();
        Map<String, Integer> wordsWithCount = new HashMap<>();
        int totalWordCount = 0;
        Matcher matcher = Utils.getInstance().getRussianWordsPattern().matcher(requestArticleDto.getContent());
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (!Utils.getInstance().getStopWords().contains(word) && word.length() >= 3) {
                String processedWord = Utils.getInstance().processPorterStem(word);
                if (wordsWithCount.containsKey(processedWord)) {
                    wordsWithCount.put(processedWord, wordsWithCount.get(processedWord) + 1);
                } else {
                    wordsWithCount.put(processedWord, 1);
                }
                totalWordCount++;
            }
        }
        User user = userDao.getUserByToken(token);
        Article article = Article.builder()
                .articleId(articleId)
                .title(requestArticleDto.getTitle())
                .content(requestArticleDto.getContent())
                .ownerId(user.getId())
                .build();
        articleDao.addArticle(article);
        List<String> words = new ArrayList<>(wordsWithCount.keySet());
        wordDao.addWords(words);
        List<ArticleWord> articleWords = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : wordsWithCount.entrySet()) {
            ArticleWord articleWord = ArticleWord.builder()
                    .articleId(articleId)
                    .word(entry.getKey())
                    .tf((double) entry.getValue() / totalWordCount)
                    .build();
            articleWords.add(articleWord);
        }
        articleWordDao.addArticleWords(articleWords);
    }
}
