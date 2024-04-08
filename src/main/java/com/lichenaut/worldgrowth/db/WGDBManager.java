package com.lichenaut.worldgrowth.db;

import com.lichenaut.worldgrowth.runnable.WGRunnableManager;

import java.sql.SQLException;

public interface WGDBManager { //Currently, all implementations have the exact same code.

    void initializeDataSource(String url, String username, String password, int maxPoolSize);

    void closeDataSource();

    void createStructure() throws SQLException;

    int getEventCount(String type) throws SQLException;

    void setEventCount(String type, int count) throws SQLException;

    void incrementEventCount(String type) throws SQLException;

    int getPoints() throws SQLException;

    void addPoints(int points) throws SQLException;

    void serializeRunnableQueue(WGRunnableManager runnableManager) throws SQLException;

    void deserializeRunnableQueue(WGRunnableManager runnableManager) throws SQLException;
}
