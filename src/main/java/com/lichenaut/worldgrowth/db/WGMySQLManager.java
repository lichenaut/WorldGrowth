package com.lichenaut.worldgrowth.db;

import java.sql.*;

public class WGMySQLManager implements WGDBManager {

    private Connection connection;

    public void updateConnection(String url, String username, String password) throws SQLException, ClassNotFoundException {
        closeConnection();
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, username, password);
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    public void createStructure() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `boosts` (`position` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `multiplier` int NOT NULL, `delay` int NOT NULL)");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `events` (`type` varchar(40) NOT NULL PRIMARY KEY, `count` int NOT NULL)");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `global` (`quota` int NOT NULL PRIMARY KEY, `points` int NOT NULL)");
    }

    public int getEventCount(String type) throws SQLException {
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

    public void setEventCount(String type, int count) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `events` (`type`, `count`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `count` = ?")) {
            statement.setString(1, type);
            statement.setInt(2, count);
            statement.setInt(3, count);
            statement.executeUpdate();
        }
    }

    public void incrementEventCount(String type) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `events` (`type`, `count`) VALUES (?, 1) ON DUPLICATE KEY UPDATE `count` = `count` + 1")) {
            statement.setString(1, type);
            statement.executeUpdate();
        }
    }

    public int getPoints() throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(
                "SELECT `points` FROM `global`")) {
            if (resultSet.next()) {
                return resultSet.getInt("points");
            } else {
                throw new RuntimeException();
            }
        }
    }

    public void addPoints(int points) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `global` (`quota`, `points`) VALUES (0, ?) ON DUPLICATE KEY UPDATE `points` = `points` + ?")) {
            statement.setInt(1, points);
            statement.setInt(2, points);
            statement.executeUpdate();
        }
    }
}
