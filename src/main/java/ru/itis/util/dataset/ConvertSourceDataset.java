package ru.itis.util.dataset;

import ru.itis.util.Constants;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ConvertSourceDataset {
	public static void main(String[] args) {
		Scanner in = DatasetReader.getScanner("dataset/source/user_fav.csv");
		in.nextLine();

		StringBuilder sb = new StringBuilder();

		while (in.hasNextLine()) {
			String[] line = in.nextLine().split(",");

			for (String articleId : line[1].split(" ")) {
				sb.append(line[0]).append(" ").append(articleId).append("\n");
			}
		}
		sb.deleteCharAt(sb.length() - 1);

		try (PrintWriter out = new PrintWriter(Constants.PATH_TO_RESOURCES + "dataset/data.csv")) {
			out.print(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
