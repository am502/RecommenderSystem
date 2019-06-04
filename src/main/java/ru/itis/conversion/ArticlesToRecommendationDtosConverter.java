package ru.itis.conversion;

import ru.itis.dto.response.RecommendationDto;
import ru.itis.model.Article;

import java.util.LinkedList;
import java.util.List;

public class ArticlesToRecommendationDtosConverter {
    private static volatile ArticlesToRecommendationDtosConverter instance;

    public static ArticlesToRecommendationDtosConverter getInstance() {
        ArticlesToRecommendationDtosConverter localInstance = instance;
        if (localInstance == null) {
            synchronized (ArticlesToRecommendationDtosConverter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ArticlesToRecommendationDtosConverter();
                }
            }
        }
        return localInstance;
    }

    public List<RecommendationDto> convert(List<Article> articles) {
        List<RecommendationDto> recommendationDtos = new LinkedList<>();
        for (Article article : articles) {
            RecommendationDto recommendationDto = RecommendationDto.builder()
                    .title(article.getTitle())
                    .articleId(article.getArticleId())
                    .build();
            recommendationDtos.add(recommendationDto);
        }
        return recommendationDtos;
    }
}
