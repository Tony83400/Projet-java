package fr.univcours.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static  final  String DATABASE_NAME = "javalin_db";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME;
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = null;

    /**
     * Méthode pour récupérer une connexion (utilisée par les Services)
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    /**
     * Méthode appelée au démarrage pour créer les tables
     */
    public static void start() {
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();

            // Requête SQL pour créer la table si elle n'existe pas
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "age INT NOT NULL CHECK ( age>0 )"+
                    ")";

            stmt.execute(sql);
            System.out.println("✅ Base de données initialisée avec succès.");

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }
}