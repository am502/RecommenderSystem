package ru.itis.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseArticleDto {
    private String title;
    private String content;
    private String ownerName;
    private List<RecommendationDto> contentRecommendations;
    private List<RecommendationDto> collaborativeRecommendations;
}
