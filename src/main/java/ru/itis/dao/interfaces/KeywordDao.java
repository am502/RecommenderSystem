package ru.itis.dao.interfaces;

import ru.itis.model.Keyword;

import java.util.List;

public interface KeywordDao {
    void addKeywords(List<Keyword> keywords);

    List<Keyword> getAllKeywords();
}
