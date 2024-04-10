package com.lichenaut.worldgrowth.db;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.runnable.WGBoost;
import com.lichenaut.worldgrowth.runnable.WGRunnable;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGMessager;
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
    private final WGMessager messager;
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
                statement.execute("CREATE TABLE IF NOT EXISTS `boosts` (`position` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `multiplier` int NOT NULL, `delay` int NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS `events` (`type` varchar(30) NOT NULL PRIMARY KEY, `count` int NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS `global` (`quota` int NOT NULL PRIMARY KEY, `points` int NOT NULL, `size` int NOT NULL, `center` varchar(50) NOT NULL");
                //TODO: add size and center support. optimize center size
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
                } //Add checks for other types of runnables here. Convert if to switch-case statement if there are more than 2 cases.

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
                                runBoost(delay);
                            }
                        }, 0L);
                        runnableManager.addRunnable(new WGBoost(main, 1) {
                            @Override
                            public void run() {
                                runReset();
                            }
                        }, delay);
                    } //Add checks for other types of runnables here. Convert if to switch-case statement if there are more than 2 cases.
                }
            }
        }
    }
}
