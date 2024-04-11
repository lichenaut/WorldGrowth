package com.lichenaut.worldgrowth.db;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.runnable.WGBoost;
import com.lichenaut.worldgrowth.runnable.WGHourCounter;
import com.lichenaut.worldgrowth.runnable.WGRunnable;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.Configuration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class WGSQLiteManager implements WGDBManager {

    private final Main main;
    private final Configuration configuration;
    private HikariDataSource dataSource;

    @Override
    public void initializeDataSource(String url, String user, String password, int maxPoolSize) {
        dataSource = new HikariDataSource();
        dataSource.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        dataSource.addDataSourceProperty("url", url);
        dataSource.setUsername(user);
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
                statement.execute("CREATE TABLE IF NOT EXISTS boosts (position INTEGER PRIMARY KEY AUTOINCREMENT, multiplier INTEGER NOT NULL, delay INTEGER NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS events (type VARCHAR(30) PRIMARY KEY NOT NULL, count INTEGER NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS global (quota INTEGER PRIMARY KEY NOT NULL, points INTEGER NOT NULL, size INTEGER NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS hours (position INTEGER PRIMARY KEY AUTOINCREMENT, delay INTEGER NOT NULL)");
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
                    "INSERT OR REPLACE INTO events (type, count) VALUES (?, ?)")) {
                statement.setString(1, type);
                statement.setInt(2, count);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public int getQuota() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT `quota` FROM `global`")) {
                if (resultSet.next()) {
                    return resultSet.getInt("quota");
                } else {
                    return configuration.getInt("starting-growth-quota"); //TODO: change the way this is retrieved once more is done.
                }
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
                    return 0;
                }
            }
        }
    }

    @Override
    public int getSize() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT `size` FROM `global`")) {
                if (resultSet.next()) {
                    return resultSet.getInt("size");
                } else {
                    Object size = main.getBorderManager().getRunnableQueue().get(0).getMainWorldBorderStartSize();
                    assert size != null;
                    return (int) size;
                }
            }
        }
    }

    @Override
    public void setGlobal(int quota, int points, int size) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO global (quota, points, size) VALUES (?, ?, ?)")) {
                statement.setInt(1, quota);
                statement.setInt(2, points);
                statement.setInt(3, size);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void serializeRunnableQueue(WGRunnableManager runnableManager, String statementString) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    statementString)) {
                LinkedList<WGRunnable> runnableQueue = runnableManager.getRunnableQueue();
                if (runnableQueue.isEmpty()) return;

                BukkitRunnable firstRunnable = runnableQueue.get(0).runnable();
                if (firstRunnable instanceof WGBoost) {
                    for (int i = 0; i < runnableQueue.size(); i++) {
                        WGRunnable runnable = runnableQueue.get(i);

                        Object multiplier = runnable.getMultiplier();
                        assert multiplier != null;
                        statement.setInt(1, (Integer) multiplier);

                        long delay = runnable.delay();
                        if (i == 0) { //Shorten the duration of the current boost.
                            Object timeStarted = runnable.getTimeStarted();
                            assert timeStarted != null;
                            delay -= (System.currentTimeMillis() - (Long) runnable.getTimeStarted());
                        }
                        statement.setLong(2, delay);

                        statement.addBatch();
                    }
                } else if (firstRunnable instanceof WGHourCounter) {
                    for (int i = 0; i < runnableQueue.size(); i++) {
                        WGRunnable runnable = runnableQueue.get(i);

                        long delay = runnable.delay();
                        if (i == 0) { //Shorten the duration of the timer.
                            Object timeStarted = runnable.getTimeStarted();
                            assert timeStarted != null;
                            delay -= (System.currentTimeMillis() - (Long) runnable.getTimeStarted());
                        }
                        statement.setLong(1, delay);

                        statement.addBatch();
                    }
                }

                statement.executeBatch();
            }
        }
    }

    @Override
    public void deserializeRunnableQueue(WGRunnableManager runnableManager, String statementString, String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    statementString)) {
                if (tableName.equals("boosts")) {
                    while (resultSet.next()) {
                        int multiplier = resultSet.getInt("multiplier");
                        long delay = resultSet.getLong("delay");
                        runnableManager.addRunnable(new WGBoost(main, multiplier) {
                            @Override
                            public void run() {
                                CompletableFuture
                                        .runAsync(() -> runBoost(delay));
                            }
                        }, 0L);
                        runnableManager.addRunnable(new WGBoost(main, 1) {
                            @Override
                            public void run() {
                                CompletableFuture
                                        .runAsync(this::runReset);
                            }
                        }, delay);
                    }
                } else if (tableName.equals("hours")) {
                    if (resultSet.next()) {
                        long delay = resultSet.getLong("delay");
                        runnableManager.addRunnable(new WGHourCounter(main), delay);
                    }
                }
            }
        }
    }
}
