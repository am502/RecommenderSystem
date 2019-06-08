package ru.itis.util;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Utils {
	private static volatile Utils instance;

	private SnowballStemmer processorPorterStem;
	private Pattern russianWordsPattern;
	private Set<String> stopWords;

	private Utils() {
		processorPorterStem = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
		russianWordsPattern = Pattern.compile("[А-Яа-яЁё]+(-[А-Яа-яЁё]+)*");
		stopWords = new HashSet<>();
		try (Scanner in = new Scanner(new FileInputStream(Constants.PATH_TO_RESOURCES + "stopwords-ru.txt"))) {
			while (in.hasNextLine()) {
				stopWords.add(in.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Utils getInstance() {
		Utils localInstance = instance;
		if (localInstance == null) {
			synchronized (Utils.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new Utils();
				}
			}
		}
		return localInstance;
	}

	public String processPorterStem(String word) {
		return processorPorterStem.stem(word).toString();
	}

	public Pattern getRussianWordsPattern() {
		return russianWordsPattern;
	}

	public Set<String> getStopWords() {
		return stopWords;
	}
}
