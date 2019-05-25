package ru.itis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dao.interfaces.ArticleDao;
import ru.itis.dao.interfaces.UserDao;
import ru.itis.dto.RequestArticleDto;
import ru.itis.service.interfaces.UserService;
import ru.itis.util.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private ArticleDao articleDao;

    @Override
    public void addArticle(RequestArticleDto requestArticleDto) {
        List<String> processedWords = new LinkedList<>();
        Matcher matcher = Utils.getInstance().getRussianWordsPattern().matcher(requestArticleDto.getContent());
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (!Utils.getInstance().getStopWords().contains(word) && word.length() >= 2) {
                String processedWord = Utils.getInstance().processPorterStem(word);
                processedWords.add(processedWord);
            }
        }
    }
}
