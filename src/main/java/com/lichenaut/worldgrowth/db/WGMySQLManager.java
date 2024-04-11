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

@RequiredArgsConstructor
public class WGMySQLManager implements WGDBManager {

    private final Main main;
    private final Configuration configuration;
    private HikariDataSource dataSource;

    @Override
    public void initializeDataSource(String url, String user, String password, int maxPoolSize) {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(maxPoolSize);
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "256");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "1024");
        dataSource.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource.addDataSourceProperty("useLocalSessionState", "true");
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");
        dataSource.addDataSourceProperty("cacheResultSetMetadata", "true");
        dataSource.addDataSourceProperty("cacheServerConfiguration", "true");
        dataSource.addDataSourceProperty("elideSetAutoCommits", "true");
        dataSource.addDataSourceProperty("maintainTimeStats", "false");
    }

    @Override
    public void closeDataSource() {
        if (dataSource != null) dataSource.close();
    }

    @Override
    public void createStructure() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS `boosts` (`multiplier` DOUBLE NOT NULL, `delay` BIGINT NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS `events` (`type` VARCHAR(30) NOT NULL PRIMARY KEY, `count` INT NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS `global` (`quota` INT NOT NULL PRIMARY KEY, `points` INT NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS `hour` (`delay` BIGINT NOT NULL)");
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
    public int getQuota() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT `quota` FROM `global`")) {
                if (resultSet.next()) {
                    return resultSet.getInt("quota");
                } else {
                    return configuration.getInt("starting-growth-quota"); //TODO: change the way this is retrieved once more is done?
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
    public void setGlobal(int quota, int points) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `global` (`quota`, `points`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `quota` = ?, `points` = ?")) {
                statement.setInt(1, quota);
                statement.setInt(2, points);
                statement.setInt(3, quota);
                statement.setInt(4, points);
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

                WGRunnable firstRunnable = runnableQueue.get(0);
                BukkitRunnable firstBukkitRunnable = firstRunnable.runnable();
                if (firstBukkitRunnable instanceof WGHourCounter hourRunnable) {
                    long delay = firstRunnable.delay();
                    delay -= (System.currentTimeMillis() - hourRunnable.getTimeStarted()) / 50;

                    statement.setLong(1, delay);

                    statement.addBatch();
                } else if (firstBukkitRunnable instanceof WGBoost) {
                    for (int i = 0; i < runnableQueue.size(); i++) {
                        WGRunnable runnable = runnableQueue.get(i);
                        long delay = runnable.delay();
                        if (delay == 0) continue;

                        WGBoost boostRunnable = (WGBoost) runnable.runnable();
                        statement.setDouble(1, boostRunnable.getMultiplier());

                        if (i == 0) delay -= (System.currentTimeMillis() - boostRunnable.getTimeStarted()) / 50;
                        statement.setLong(2, delay);

                        statement.addBatch();
                    }
                }

                statement.executeBatch();
            }
        }
    }

    @Override
    public void deserializeRunnableQueue(WGRunnableManager runnableManager, String statementString) throws SQLException {
        String tableName = statementString.contains("hour") ? "hour" : "boosts"; //Change this when adding more types.
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    statementString)) {
                if (tableName.equals("hour")) {
                    if (resultSet.next()) {
                        long delay = resultSet.getLong("delay");
                        runnableManager.addRunnable(new WGHourCounter(main), delay);
                    }
                } else {
                    while (resultSet.next()) {
                        double multiplier = resultSet.getDouble("multiplier");
                        long delay = resultSet.getLong("delay");
                        runnableManager.addRunnable(new WGBoost(main, multiplier) {
                            @Override
                            public void run() {
                                runBoost(delay);
                            }
                        }, 0L);
                        runnableManager.addRunnable(new WGBoost(main, multiplier) {
                            @Override
                            public void run() {
                                runReset();
                            }
                        }, delay);
                    }
                }
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM `" + tableName + "`");
            }
        }
    }
}
