package com.lichenaut.worldgrowth.db;

import com.lichenaut.worldgrowth.runnable.WGRunnableManager;

import java.sql.SQLException;

public interface WGDBManager {

    void initializeDataSource(String url, String user, String password, int maxPoolSize);

    void closeDataSource();

    void createStructure() throws SQLException;

    int getEventCount(String type) throws SQLException;

    void setEventCount(String type, int count) throws SQLException;

    int getBlocks() throws SQLException;

    int getQuota() throws SQLException;

    double getPoints() throws SQLException;

    void setGlobal(int quota, double points, int blocks) throws SQLException;

    void serializeRunnableQueue(WGRunnableManager runnableManager, String statementString) throws SQLException;

    void deserializeRunnableQueue(WGRunnableManager runnableManager, String statementString) throws SQLException;
}
