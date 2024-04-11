package com.lichenaut.worldgrowth.db;

import com.lichenaut.worldgrowth.runnable.WGRunnableManager;

import java.sql.SQLException;

public interface WGDBManager {

    void initializeDataSource(String url, String user, String password, int maxPoolSize);

    void closeDataSource();

    void createStructure() throws SQLException;

    int getEventCount(String type) throws SQLException;

    void setEventCount(String type, int count) throws SQLException;

    int getQuota() throws SQLException;

    int getPoints() throws SQLException;

    int getSize() throws SQLException;

    void setGlobal(int quota, int points, int size) throws SQLException;

    void serializeRunnableQueue(WGRunnableManager runnableManager, String statementString) throws SQLException;

    void deserializeRunnableQueue(WGRunnableManager runnableManager, String statementString, String tableName) throws SQLException;
}
