package ru.itis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.model.Article;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborativeRecommendationsDto {
	private List<Article> recommendations;
}