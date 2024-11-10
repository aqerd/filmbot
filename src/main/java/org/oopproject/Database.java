package org.oopproject;

import java.sql.*;

public class Database {
    private Connection connection;

    public Database() {
        String username = "root";
        String password = "Root2004";
        String connectionUrl = "jdbc:mysql://localhost:3306/botusers";
        try {
            connection = DriverManager.getConnection(connectionUrl, username, password);
            System.out.println("Connected to database");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertChatId(long chatId) {
        String insertQuery = "INSERT INTO users (chatId) VALUES (?) ON DUPLICATE KEY UPDATE chatId=chatId";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setLong(1, chatId);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
