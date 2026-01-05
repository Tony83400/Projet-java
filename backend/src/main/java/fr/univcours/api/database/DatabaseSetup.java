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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public static void start() {
        // 1. Création de la Base de Données
        try (Connection connection = DriverManager.getConnection(JDBC_ROOT_URL, JDBC_USER, JDBC_PASSWORD)) {
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            System.out.println("✅ Base de données '" + DATABASE_NAME + "' prête.");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // 2. Création des Tables
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();

            // --- TABLE IMAGE (Doit être créée en premier car Article et Menu l'utilisent) ---
            String sql_image = "CREATE TABLE IF NOT EXISTS `image` (" +
                    "`image_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`url` LINESTRING NOT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_image);
            System.out.println("✅ Table 'image' OK.");

            // --- TABLE ARTICLE ---
            String sql_article = "CREATE TABLE IF NOT EXISTS `article` (" +
                    "`article_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`prix` BIGINT NOT NULL, " +
                    "`description` TEXT NULL, " +
                    "`image_id` BIGINT UNSIGNED NOT NULL, " +
                    "CONSTRAINT `fk_article_image` FOREIGN KEY (`image_id`) REFERENCES `image`(`image_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_article);
            System.out.println("✅ Table 'article' OK.");

            // --- TABLE CATEGORIE ---
            String sql_categorie = "CREATE TABLE IF NOT EXISTS `categorie` (" +
                    "`categorie_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`description` TEXT NOT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_categorie);
            System.out.println("✅ Table 'categorie' OK.");

            // --- TABLE DE LIAISON ARTICLE_CATEGORIE ---
            String sql_article_categorie = "CREATE TABLE IF NOT EXISTS `article_categorie` (" +
                    "`article_id` BIGINT UNSIGNED NOT NULL, " +
                    "`categorie_id` BIGINT UNSIGNED NOT NULL, " +
                    "PRIMARY KEY (`article_id`, `categorie_id`), " +
                    "CONSTRAINT `fk_pivot_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE, " +
                    "CONSTRAINT `fk_pivot_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie`(`categorie_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_article_categorie);
            System.out.println("✅ Table 'article_categorie' OK.");

            // --- TABLE COMMANDE ---
            String sql_commande = "CREATE TABLE IF NOT EXISTS `commande` (" +
                    "`commande_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`article_id` BIGINT UNSIGNED NOT NULL, " +
                    "`quantite_article` BIGINT NOT NULL, " +
                    "`numero_commande` BIGINT NOT NULL, " +
                    "`niveau_epices` BIGINT NOT NULL, " +
                    "CONSTRAINT `fk_commande_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_commande);
            System.out.println("✅ Table 'commande' OK.");

            // --- TABLE MENU ---
            String sql_menu = "CREATE TABLE IF NOT EXISTS `menu` (" +
                    "`menu_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "`article_id` BIGINT UNSIGNED NOT NULL, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`image_id` BIGINT UNSIGNED NOT NULL, " +
                    "PRIMARY KEY (`menu_id`), " +
                    "CONSTRAINT `fk_menu_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE, " +
                    "CONSTRAINT `fk_menu_image` FOREIGN KEY (`image_id`) REFERENCES `image`(`image_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_menu);
            System.out.println("✅ Table 'menu' OK.");

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}