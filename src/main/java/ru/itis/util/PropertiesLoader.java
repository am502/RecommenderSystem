package ru.itis.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream("src/main/resources/properties/config.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
