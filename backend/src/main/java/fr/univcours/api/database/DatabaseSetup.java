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
                    "`article_id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`prix` FLOAT NOT NULL, " +
                    "`description` TEXT NULL, " +
                    "`image_url` TEXT NOT NULL, " +
                    "`stock` INTEGER NOT NULL " +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_article);
            System.out.println("✅ Table 'article' OK.");

            // --- TABLE CATEGORIE ---
            String sql_categorie = "CREATE TABLE IF NOT EXISTS `categorie` (" +
                    "`categorie_id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`description` TEXT NOT NULL" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_categorie);
            System.out.println("✅ Table 'categorie' OK.");

            // --- TABLE DE LIAISON ARTICLE_CATEGORIE ---
            String sql_article_categorie = "CREATE TABLE IF NOT EXISTS `article_categorie` (" +
                    "`article_id` INTEGER UNSIGNED NOT NULL, " +
                    "`categorie_id` INTEGER UNSIGNED NOT NULL, " +
                    "PRIMARY KEY (`article_id`, `categorie_id`), " +
                    "CONSTRAINT `fk_pivot_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE, " +
                    "CONSTRAINT `fk_pivot_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie`(`categorie_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_article_categorie);
            System.out.println("✅ Table 'article_categorie' OK.");

            // --- TABLE COMMANDE ---
            String sql_commande = "CREATE TABLE IF NOT EXISTS `commande` (" +
                    "`commande_id` INTEGER UNSIGNED NOT NULL, " + // On enlève PRIMARY KEY ici
                    "`article_id` INTEGER UNSIGNED NOT NULL, " +  // On enlève PRIMARY KEY ici
                    "`quantite_article` INTEGER NOT NULL, " +
                    "`numero_commande` INTEGER NOT NULL, " +
                    "`niveau_epice` INTEGER NOT NULL, " +
                    "PRIMARY KEY (`commande_id`, `article_id`), " +
                    "CONSTRAINT `fk_commande_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_commande);
            System.out.println("✅ Table 'commande' OK.");

            // --- TABLE MENU ---
            String sql_menu = "CREATE TABLE IF NOT EXISTS `menu` (" +
                    "`menu_id` INTEGER UNSIGNED NOT NULL, " + // Pas d'auto-increment ici, c'est l'ID du groupe menu
                    "`article_id` INTEGER UNSIGNED NOT NULL, " +
                    "`nom` VARCHAR(255) NOT NULL, " +
                    "`image_url` TEXT NOT NULL, " +
                    "PRIMARY KEY (`menu_id`, `article_id`), " +
                    "CONSTRAINT `fk_menu_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE " +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(sql_menu);
            System.out.println("✅ Table 'menu' OK.");

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
                "('Nems au Poulet (x4)', 5.50, 'Rouleaux de printemps frits au poulet et légumes', 'url_nems.jpg', 100), " +   // ID 1
                "('Salade de Chou', 3.50, 'Salade croquante vinaigrée', 'url_salade.jpg', 50), " +                             // ID 2
                "('Poulet au Curry Rouge', 12.90, 'Poulet mijoté au lait de coco et curry rouge thai', 'url_curry.jpg', 80), " + // ID 3
                "('Bœuf aux Oignons', 13.50, 'Lamelles de bœuf sautées au wok', 'url_boeuf.jpg', 60), " +                       // ID 4
                "('Nouilles Sautées Légumes', 11.00, 'Nouilles aux oeufs et légumes de saison', 'url_nouilles.jpg', 40), " +    // ID 5
                "('Perles de Coco (x2)', 4.00, 'Boules de riz gluant à la vapeur, coeur soja jaune', 'url_perles.jpg', 100), " + // ID 6
                "('Mochis Glacés Mangue', 4.50, 'Dessert japonais glacé', 'url_mochi.jpg', 120);";                              // ID 7
        stmt.executeUpdate(insertArticles);

        // 3. Insertion des LIAISONS (Article <-> Catégorie)
        // Syntaxe : (article_id, categorie_id)
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
        // On crée des groupes de menus. menu_id=1 (Menu Midi), menu_id=2 (Menu Gourmand)
        // Syntaxe : (menu_id, article_id, nom, image_url)
        String insertMenus = "INSERT INTO menu (menu_id, article_id, nom, image_url) VALUES " +
                // Menu Midi (Nems + Nouilles)
                "(1, 1, 'Menu Midi Express', 'url_menu_midi.jpg'), " +
                "(1, 5, 'Menu Midi Express', 'url_menu_midi.jpg'), " +

                // Menu Gourmand (Salade + Poulet Curry + Perles coco)
                "(2, 2, 'Menu Gourmand', 'url_menu_gourmand.jpg'), " +
                "(2, 3, 'Menu Gourmand', 'url_menu_gourmand.jpg'), " +
                "(2, 6, 'Menu Gourmand', 'url_menu_gourmand.jpg');";
        stmt.executeUpdate(insertMenus);

        System.out.println("✅ Données par défaut insérées avec succès !");
    }
}