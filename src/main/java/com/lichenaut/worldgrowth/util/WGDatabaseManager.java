package com.lichenaut.worldgrowth.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
public class WGDatabaseManager {

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
                "CREATE TABLE IF NOT EXISTS `boosts` " +
                        "(`position` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `multiplier` int NOT NULL, `delay` int NOT NULL)");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `events` " +
                        "(`type` varchar(40) NOT NULL PRIMARY KEY, `count` int NOT NULL)");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `global` " +
                        "(`quota` int NOT NULL, `points` int NOT NULL)");
    }

    public void incrementEvent(String type) throws SQLException {
        connection.createStatement().execute("INSERT INTO `events` (`type`, `count`) VALUES ('" + type + "', 1) " +
                "ON DUPLICATE KEY UPDATE `count` = `count` + 1");
    }
}
