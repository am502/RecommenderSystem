package ru.itis.dao.config;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class DaoConfig {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/rs";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres1";
    private static final String DB_DRIVER_CLASSNAME = "org.postgresql.Driver";

    public static DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setDriverClassName(DB_DRIVER_CLASSNAME);

        return dataSource;
    }
}
