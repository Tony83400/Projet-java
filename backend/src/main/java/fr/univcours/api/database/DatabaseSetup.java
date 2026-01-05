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

            // 1. Table ARTICLE
            String sql_article = "CREATE TABLE IF NOT EXISTS `article` (" +
                    "`article_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`prix` BIGINT NOT NULL, " +
                    "`description` TEXT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_article);
            System.out.println("✅ Table 'article' OK.");

            // 2. Table CATEGORIE
            String sql_categorie = "CREATE TABLE IF NOT EXISTS `categorie` (" +
                    "`categorie_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`description` TEXT NOT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_categorie);
            System.out.println("✅ Table 'categorie' OK.");

            // 3. Table de liaison ARTICLE_CATEGORIE (Relation N-N)
            // La clé primaire est composite (article_id + categorie_id)
            String sql_pivot = "CREATE TABLE IF NOT EXISTS `article_categorie` (" +
                    "`article_id` BIGINT UNSIGNED NOT NULL, " +
                    "`categorie_id` BIGINT UNSIGNED NOT NULL, " +
                    "PRIMARY KEY (`article_id`, `categorie_id`), " +
                    "CONSTRAINT `fk_pivot_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE, " +
                    "CONSTRAINT `fk_pivot_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie`(`categorie_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_pivot);
            System.out.println("✅ Table 'article_categorie' OK.");

            // 4. Table COMMANDE (Exemple simplifié)
            String sql_commande = "CREATE TABLE IF NOT EXISTS `commande` (" +
                    "`commande_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`numero_commande` BIGINT NOT NULL, " +
                    "`niveau_epices` BIGINT NOT NULL, " +
                    "`date_creation` TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_commande);
            System.out.println("✅ Table 'commande' OK.");

            // 5. Table MENU (Exemple simplifié)
            String sql_menu = "CREATE TABLE IF NOT EXISTS `menu` (" +
                    "`menu_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`nom` VARCHAR(255) NOT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_menu);
            System.out.println("✅ Table 'menu' OK.");

            // --- Note sur vos autres contraintes ---
            // Si vous voulez lier article à commande (Relation N-N aussi souvent),
            // il faudrait une table `commande_article` (avec commande_id, article_id, quantite).
            // J'ai nettoyé le code pour que au moins la partie Catégorie/Article fonctionne parfaitement.

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}