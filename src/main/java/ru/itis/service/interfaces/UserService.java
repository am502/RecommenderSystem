package ru.itis.service.interfaces;

import ru.itis.dto.RequestArticleDto;

public interface UserService {
    void addArticle(RequestArticleDto requestArticleDto, String token);
}
