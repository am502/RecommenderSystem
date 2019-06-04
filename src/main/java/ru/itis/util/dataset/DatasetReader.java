package ru.itis.util.dataset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DatasetReader {
    private static final String PATH_TO_FILE = "src/main/resources/dataset/user_fav.csv";

    public static Scanner getScanner() {
        Scanner in = null;
        try {
            in = new Scanner(new FileInputStream(PATH_TO_FILE));
            in.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return in;
    }
}
