package ru.itis.util.dataset;

import ru.itis.util.Constants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DatasetReader {
	public static Scanner getScanner(String path) {
		Scanner in = null;
		try {
			in = new Scanner(new FileInputStream(Constants.PATH_TO_RESOURCES + path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}
}
