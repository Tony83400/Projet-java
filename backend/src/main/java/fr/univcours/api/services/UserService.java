package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les utilisateurs Charge les utilisateurs depuis un fichier
 * JSON
 */
public class UserService {

    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            // Si rowsAffected > 0, c'est qu'on a bien supprimé quelqu'un
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> GetUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseSetup.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("nom"));
                user.setAge(rs.getInt("age"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public User addUser(User userData) {
        // les ? seront remplacer par nos valeurs
        String sql = "INSERT INTO users (email, nom, age) VALUES (?, ?, ?)";

        // On demande explicitement à récupérer les clés générées (ID)
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // On remplit les "?" avec les données
            stmt.setString(1, userData.getEmail());
            stmt.setString(2, userData.getName());
            stmt.setInt(3, userData.getAge());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // On récupère l'ID généré par la base de données
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // On met à jour l'objet avec son nouvel ID
                        userData.setId(generatedKeys.getInt(1));
                        return userData;
                    }
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'utilisateur", e);
        }
    }

    public User getUserByid(int id) {
        String sql = "SELECT * FROM users WHERE id =?";
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setAge(rs.getInt("age"));
                    user.setEmail(rs.getString("email"));
                    user.setName(rs.getString("nom"));
                    return user;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateById(int id, User userData) {
        // les ? seront remplacer par nos valeurs
        String sql = "UPDATE users SET email = ? , nom = ? , age=? WHERE id = ?";

        // On demande explicitement à récupérer les clés générées (ID)
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // On remplit les "?" avec les données
            stmt.setString(1, userData.getEmail());
            stmt.setString(2, userData.getName());
            stmt.setInt(3, userData.getAge());
            stmt.setInt(4, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // On récupère l'ID généré par la base de données
                userData.setId(id);
                return userData;
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de l'utilisateur", e);
        }
    }
}
