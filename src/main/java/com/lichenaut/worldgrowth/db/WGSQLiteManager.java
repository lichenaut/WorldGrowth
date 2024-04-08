package com.lichenaut.worldgrowth.db;

import com.lichenaut.worldgrowth.Main;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

import java.sql.*;

@RequiredArgsConstructor
public class WGSQLiteManager implements WGDBManager {

    private final Main plugin;
    private HikariDataSource dataSource;

    @Override
    public void initializeDataSource(String url, String username, String password, int maxPoolSize) {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(maxPoolSize);
    }

    @Override
    public void closeDataSource() {
        if (dataSource != null) dataSource.close();
    }

    @Override
    public void createStructure() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS `boosts` (`position` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `multiplier` int NOT NULL, `delay` int NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS `events` (`type` varchar(30) NOT NULL PRIMARY KEY, `count` int NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS `global` (`quota` int NOT NULL PRIMARY KEY, `points` int NOT NULL)");
            }
        }
    }

    @Override
    public int getEventCount(String type) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT `count` FROM `events` WHERE `type` = ?")) {
                statement.setString(1, type);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count");
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    @Override
    public void setEventCount(String type, int count) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `events` (`type`, `count`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `count` = ?")) {
                statement.setString(1, type);
                statement.setInt(2, count);
                statement.setInt(3, count);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void incrementEventCount(String type) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `events` (`type`, `count`) VALUES (?, 1) ON DUPLICATE KEY UPDATE `count` = `count` + 1")) {
                statement.setString(1, type);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public int getPoints() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT `points` FROM `global`")) {
                if (resultSet.next()) {
                    return resultSet.getInt("points");
                } else {
                    throw new RuntimeException();
                }
            }
        }
    }

    @Override
    public void addPoints(int points) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `global` (`quota`, `points`) VALUES (0, ?) ON DUPLICATE KEY UPDATE `points` = `points` + ?")) {
                statement.setInt(1, points);
                statement.setInt(2, points);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void serializeRunnableQueue() {

    }

    @Override
    public void deserializeRunnableQueue() {

    }
}
