package ru.itis.service;

import ru.itis.dto.CollaborativeRecommendationsDto;
import ru.itis.dto.ContentRecommendationsDto;

import java.util.Scanner;

public interface MainService {
	void upload(String url, Scanner in);

	ContentRecommendationsDto getArticleByTitle(String title);

	CollaborativeRecommendationsDto getPersonalRecommendations(String username);
}
