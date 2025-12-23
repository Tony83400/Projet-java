package fr.univcours.api.services;

import fr.univcours.api.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les utilisateurs Charge les utilisateurs depuis un fichier
 * JSON
 */
public class UserService {
    private static  final  String DATABASE_NAME = "javalin_db";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME;
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = null;

    public UserService() {
    }


    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = " + id;

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            // Si rowsAffected > 0, c'est qu'on a bien supprimé quelqu'un
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<User> GetUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM user")) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));

                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}
