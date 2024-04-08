package com.lichenaut.worldgrowth.db;

import java.sql.SQLException;

public interface WGDBManager {

    void updateConnection(String url, String username, String password) throws SQLException, ClassNotFoundException;

    void closeConnection() throws SQLException;

    void createStructure() throws SQLException;

    int getEventCount(String type) throws SQLException;

    void setEventCount(String type, int count) throws SQLException;

    void incrementEventCount(String type) throws SQLException;

    int getPoints() throws SQLException;

    void addPoints(int points) throws SQLException;
}
