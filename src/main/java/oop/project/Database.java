package oop.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Database {
    private Connection connection;
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    public Database() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("assets/config.properties")) {
            properties.load(input);
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String connectionUrl = properties.getProperty("db.url");
            connection = DriverManager.getConnection(connectionUrl, username, password);
            LOG.info("Connected to database");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void insertChatId(long chatId) {
        String insertIdQuery = "INSERT INTO users (chatId) VALUES (?) ON DUPLICATE KEY UPDATE chatId=chatId";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertIdQuery)) {
            insertStatement.setLong(1, chatId);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGenreIndexesJson(long chatId, String genreIndexesJson) {
        String updateQuery = "UPDATE users SET genreIndexesJson = ? WHERE chatId = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, genreIndexesJson);
            updateStatement.setLong(2, chatId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getGenreIndexesJson(long chatId) {
        String selectQuery = "SELECT genreIndexesJson FROM users WHERE chatId = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setLong(1, chatId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("genreIndexesJson");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateYearIndexesJson(long chatId, String yearIndexesJson) {
        String updateQuery = "UPDATE users SET yearIndexesJson = ? WHERE chatId = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, yearIndexesJson);
            updateStatement.setLong(2, chatId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getYearIndexesJson(long chatId) {
        String selectQuery = "SELECT yearIndexesJson FROM users WHERE chatId = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setLong(1, chatId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("yearIndexesJson");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}