package fr.univcours.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

        // 2. Création des Tables et Insertion des données
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();

            // --- TABLE ARTICLE ---
            String sql_article = "CREATE TABLE IF NOT EXISTS `article` (" +
                    "`article_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`description` TEXT NULL," +
                    "`prix` DECIMAL(10, 2) NOT NULL," +
                    "`image_url` VARCHAR(500) NOT NULL," +
                    "`stock` INT NOT NULL DEFAULT 0" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_article);
            System.out.println("✅ Table 'article' OK.");

            // --- TABLE CATEGORIE ---
            String sql_categorie = "CREATE TABLE IF NOT EXISTS `categorie` (" +
                    "`categorie_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`description` TEXT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_categorie);
            System.out.println("✅ Table 'categorie' OK.");

            // --- TABLE DE LIAISON ARTICLE_CATEGORIE ---
            String sql_article_categorie = "CREATE TABLE IF NOT EXISTS `article_categorie` (" +
                    "`article_id` INT UNSIGNED NOT NULL," +
                    "`categorie_id` INT UNSIGNED NOT NULL," +
                    "PRIMARY KEY (`article_id`, `categorie_id`)," +
                    "CONSTRAINT `fk_artcat_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE," +
                    "CONSTRAINT `fk_artcat_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie`(`categorie_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_article_categorie);
            System.out.println("✅ Table 'article_categorie' OK.");

            // --- TABLE MENU ---
            String sql_menu = "CREATE TABLE IF NOT EXISTS `menu` (" +
                    "`menu_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`prix` DECIMAL(10, 2) NOT NULL," +
                    "`image_url` VARCHAR(500) NOT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_menu);
            System.out.println("✅ Table 'menu' OK.");

            // --- TABLE COMPOSITION MENU ---
            String sql_menu_composition = "CREATE TABLE IF NOT EXISTS `menu_composition` (" +
                    "`menu_id` INT UNSIGNED NOT NULL," +
                    "`article_id` INT UNSIGNED NOT NULL," +
                    "`quantite` INT NOT NULL DEFAULT 1," +
                    "PRIMARY KEY (`menu_id`, `article_id`)," +
                    "CONSTRAINT `fk_menucomp_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu`(`menu_id`) ON DELETE CASCADE," +
                    "CONSTRAINT `fk_menucomp_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_menu_composition);
            System.out.println("✅ Table 'menu_composition' OK.");

            // --- TABLE COMMANDE ---
            String sql_commande = "CREATE TABLE IF NOT EXISTS `commande` (" +
                    "`commande_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`statut` VARCHAR(50) DEFAULT 'EN_PREPARATION'," +
                    "`numero_ticket` INT NOT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_commande);
            System.out.println("✅ Table 'commande' OK.");

            // --- TABLE LIGNE DE COMMANDE ---
            String sql_ligne_commande = "CREATE TABLE IF NOT EXISTS `ligne_commande` (" +
                    "`ligne_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`commande_id` INT UNSIGNED NOT NULL," +
                    "`article_id` INT UNSIGNED NULL," +
                    "`menu_id` INT UNSIGNED NULL," +
                    "`quantite` INT NOT NULL DEFAULT 1," +
                    "`prix_unitaire_facture` DECIMAL(10, 2) NOT NULL," +
                    "CONSTRAINT `fk_ligne_commande` FOREIGN KEY (`commande_id`) REFERENCES `commande`(`commande_id`) ON DELETE CASCADE," +
                    "CONSTRAINT `fk_ligne_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE SET NULL," +
                    "CONSTRAINT `fk_ligne_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu`(`menu_id`) ON DELETE SET NULL," +
                    "CONSTRAINT `chk_article_or_menu` CHECK ((`article_id` IS NOT NULL AND `menu_id` IS NULL) OR (`article_id` IS NULL AND `menu_id` IS NOT NULL))" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_ligne_commande);
            System.out.println("✅ Table 'ligne_commande' OK.");

            // 3. Insertion des données par défaut
            insertDefaultData(connection);

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertDefaultData(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();

        // On vérifie si la table article est vide pour ne pas dupliquer les données à chaque redémarrage
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM article");
        if (rs.next() && rs.getInt("total") > 0) {
            System.out.println("ℹ️ Les données par défaut existent déjà.");
            return;
        }

        System.out.println("⏳ Insertion des données Asiatiques par défaut...");

        // 1. Insertion des CATÉGORIES
        // ID 1: Entrées, ID 2: Plats, ID 3: Desserts, ID 4: Végétarien, ID 5: Épicé
        String insertCats = "INSERT INTO categorie (nom, description) VALUES " +
                "('Entrées', 'Pour bien commencer le repas'), " +
                "('Plats Résistants', 'Nos spécialités du chef'), " +
                "('Desserts', 'Douceurs sucrées asiatiques'), " +
                "('Végétarien', 'Plats sans viande ni poisson'), " +
                "('Épicé', 'Attention ça pique !');";
        stmt.executeUpdate(insertCats);

        // 2. Insertion des ARTICLES
        String insertArticles = "INSERT INTO article (nom, prix, description, image_url, stock) VALUES " +
                "('Nems au Poulet (x4)', 5.50, 'Rouleaux de printemps frits au poulet et légumes', 'images/articles/nems.jpg', 100), " +   // ID 1
                "('Salade de Chou', 3.50, 'Salade croquante vinaigrée', 'images/articles/salade.jpg', 50), " +                             // ID 2
                "('Poulet au Curry Rouge', 12.90, 'Poulet mijoté au lait de coco et curry rouge thai', 'images/articles/curry.jpg', 80), " + // ID 3
                "('Bœuf aux Oignons', 13.50, 'Lamelles de bœuf sautées au wok', 'images/articles/boeuf.jpg', 60), " +                       // ID 4
                "('Nouilles Sautées Légumes', 11.00, 'Nouilles aux oeufs et légumes de saison', 'images/articles/nouilles.jpg', 40), " +    // ID 5
                "('Perles de Coco (x2)', 4.00, 'Boules de riz gluant à la vapeur, coeur soja jaune', 'images/articles/perles.jpg', 100), " + // ID 6
                "('Mochis Glacés Mangue', 4.50, 'Dessert japonais glacé', 'images/articles/mochi.jpg', 120);";                              // ID 7
        stmt.executeUpdate(insertArticles);

        // 3. Insertion des LIAISONS (Article <-> Catégorie)
        String insertPivot = "INSERT INTO article_categorie (article_id, categorie_id) VALUES " +
                "(1, 1), " + // Nems -> Entrée
                "(2, 1), (2, 4), " + // Salade -> Entrée, Végétarien
                "(3, 2), (3, 5), " + // Poulet Curry -> Plat, Épicé
                "(4, 2), " + // Bœuf -> Plat
                "(5, 2), (5, 4), " + // Nouilles -> Plat, Végétarien
                "(6, 3), (6, 4), " + // Perles -> Dessert, Végétarien
                "(7, 3), (7, 4);";   // Mochis -> Dessert, Végétarien
        stmt.executeUpdate(insertPivot);

        // 4. Insertion des MENUS
        String insertMenus = "INSERT INTO menu (nom, prix, image_url) VALUES " +
                "('Menu Midi Express', 15.00, 'images/menus/menu_midi.jpg'), " +      // ID 1
                "('Menu Gourmand', 18.50, 'images/menus/menu_gourmand.jpg');";   // ID 2
        stmt.executeUpdate(insertMenus);

        // 5. Insertion de la COMPOSITION des MENUS
        String insertMenuComp = "INSERT INTO menu_composition (menu_id, article_id, quantite) VALUES " +
                // Menu Midi (Nems + Nouilles)
                "(1, 1, 1), " + // 1x Nems
                "(1, 5, 1), " + // 1x Nouilles

                // Menu Gourmand (Salade + Poulet Curry + Perles coco)
                "(2, 2, 1), " + // 1x Salade
                "(2, 3, 1), " + // 1x Poulet Curry
                "(2, 6, 1);";   // 1x Perles de coco
        stmt.executeUpdate(insertMenuComp);

        System.out.println("✅ Données par défaut insérées avec succès !");
    }
}