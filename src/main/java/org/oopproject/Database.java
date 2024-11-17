package org.oopproject;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    private Connection connection;

    public Database() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("assets/config.properties")) {
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
        String insertIdQuery = "INSERT INTO users (chatId) VALUES (?) ON DUPLICATE KEY UPDATE chatId=chatId";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertIdQuery)) {
            insertStatement.setLong(1, chatId);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserAge(long chatId, int userAge) {
        String updateAgeQuery = "UPDATE users SET userAge = ? WHERE chatId = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateAgeQuery)) {
            updateStatement.setInt(1, userAge);
            updateStatement.setLong(2, chatId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Integer getUserAge(long chatId) {
        String selectUserAgeQuery = "SELECT userAge FROM users WHERE chatId = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectUserAgeQuery)) {
            selectStatement.setLong(1, chatId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("userAge");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

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
    public void updateSubscribe(long chatId, boolean subscribed) {
        String query = "UPDATE users SET subscribed = ? WHERE chatId = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setBoolean(1, subscribed);
            updateStatement.setLong(2, chatId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Long> getSubscribedUsers() {
        String selectQuery = "SELECT chatId FROM users WHERE subscribed = TRUE";
        List<Long> users = new ArrayList<>();
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                users.add(resultSet.getLong("chatId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}

