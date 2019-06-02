package ru.itis.dao.config;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.itis.util.PropertiesLoader;

import javax.sql.DataSource;
import java.util.Properties;

public class DaoConfig {
    public static DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        Properties settings = PropertiesLoader.getProperties();

        dataSource.setUrl(settings.getProperty("db.url"));
        dataSource.setUsername(settings.getProperty("db.username"));
        dataSource.setPassword(settings.getProperty("db.password"));
        dataSource.setDriverClassName(settings.getProperty("db.driver"));

        return dataSource;
    }
}
