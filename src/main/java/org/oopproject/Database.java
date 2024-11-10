package org.oopproject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Database {
    private Connection connection;

    public Database() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("assets/config.properties")){
            properties.load(input);
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String connectionUrl = properties.getProperty("db.url");

            connection = DriverManager.getConnection(connectionUrl, username, password);
            System.out.println("Connected to database");
        } catch (SQLException | IOException e) {
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
