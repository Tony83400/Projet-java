package fr.univcours.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String DATABASE_NAME = "javalin_db";
    
    private static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";
    
    private static final String JDBC_URL = JDBC_ROOT_URL + DATABASE_NAME;
    
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = ""; 

    /**
     * Méthode pour récupérer une connexion vers NOTRE base (utilisée par les Services)
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    /**
     * Méthode appelée au démarrage pour tout initialiser
     */
    public static void start() {
        // Création de la Base de Données (Connexion au serveur racine)
        try (Connection connection = DriverManager.getConnection(JDBC_ROOT_URL, JDBC_USER, JDBC_PASSWORD)) {
            Statement stmt = connection.createStatement();
            
            // On crée la base si elle n'existe pas
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            System.out.println("✅ Base de données '" + DATABASE_NAME + "' vérifiée/créée.");
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création de la BDD : " + e.getMessage());
            e.printStackTrace();
            return; // On arrête tout si on ne peut même pas créer la base
        }

        //Création des Tables (Connexion à la base qu'on vient de créer)
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();

            // Création de la table users
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "age INT NOT NULL CHECK ( age>0 )"+
                    ")";

            stmt.execute(sql);
            System.out.println("✅ Table 'users' vérifiée/créée avec succès.");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création des tables : " + e.getMessage());
            e.printStackTrace();
        }
    }
}