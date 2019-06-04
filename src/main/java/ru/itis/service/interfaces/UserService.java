package ru.itis.service.interfaces;

import ru.itis.dto.request.RequestArticleDto;
import ru.itis.dto.response.ResponseArticleDto;

public interface UserService {
    void addArticle(RequestArticleDto requestArticleDto, String token);

    ResponseArticleDto getArticleById(String articleId, String token);

    void addToFavorites(String articleId, String token);
}
