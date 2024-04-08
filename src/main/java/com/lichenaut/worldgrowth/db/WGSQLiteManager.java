package com.lichenaut.worldgrowth.db;

import java.sql.*;

public class WGSQLiteManager implements WGDBManager {

    private Connection connection;

    public void updateConnection(String url, String username, String password) throws SQLException, ClassNotFoundException {
        closeConnection();
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(url, username, password);
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    public void createStructure() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS boosts (position INTEGER PRIMARY KEY AUTOINCREMENT, multiplier INTEGER NOT NULL, delay INTEGER NOT NULL)");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS events (type TEXT PRIMARY KEY NOT NULL, count INTEGER NOT NULL)");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS global (quota INTEGER PRIMARY KEY NOT NULL, points INTEGER NOT NULL)");
    }

    public int getEventCount(String type) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT count FROM events WHERE type = ?")) {
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
                "INSERT INTO events (type, count) VALUES (?, ?) ON CONFLICT(type) DO UPDATE SET count = ?")) {
            statement.setString(1, type);
            statement.setInt(2, count);
            statement.setInt(3, count);
            statement.executeUpdate();
        }
    }

    public void incrementEventCount(String type) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO events (type, count) VALUES (?, 1) ON CONFLICT(type) DO UPDATE SET count = count + 1")) {
            statement.setString(1, type);
            statement.executeUpdate();
        }
    }

    public int getPoints() throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(
                "SELECT points FROM global")) {
            if (resultSet.next()) {
                return resultSet.getInt("points");
            } else {
                throw new RuntimeException();
            }
        }
    }

    public void addPoints(int points) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO global (quota, points) VALUES (0, ?) ON CONFLICT(quota) DO UPDATE SET points = points + ?")) {
            statement.setInt(1, points);
            statement.setInt(2, points);
            statement.executeUpdate();
        }
    }
}
