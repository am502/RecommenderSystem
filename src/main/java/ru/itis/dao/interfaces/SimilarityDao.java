package ru.itis.dao.interfaces;

import ru.itis.model.Similarity;

import java.util.List;

public interface SimilarityDao {
	void addSimilarities(List<Similarity> similarities);
}
