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
            // CHANGEMENT ICI : DECIMAL(10, 2) -> FLOAT
            String sql_article = "CREATE TABLE IF NOT EXISTS `article` (" +
                    "`article_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`description` TEXT NULL," +
                    "`prix` FLOAT NOT NULL," +
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
            // CHANGEMENT ICI : DECIMAL(10, 2) -> FLOAT
            String sql_menu = "CREATE TABLE IF NOT EXISTS `menu` (" +
                    "`menu_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`prix` FLOAT NOT NULL," +
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
            // CHANGEMENT ICI : DECIMAL(10, 2) -> FLOAT
            String sql_ligne_commande = "CREATE TABLE IF NOT EXISTS `ligne_commande` (" +
                    "`ligne_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`commande_id` INT UNSIGNED NOT NULL," +
                    "`article_id` INT UNSIGNED NULL," +
                    "`menu_id` INT UNSIGNED NULL," +
                    "`quantite` INT NOT NULL DEFAULT 1," +
                    "`prix_unitaire_facture` FLOAT NOT NULL," +
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

        // On vérifie si la table article est vide
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM article");
        if (rs.next() && rs.getInt("total") > 0) {
            System.out.println("ℹ️ Les données par défaut existent déjà.");
            return;
        }

        System.out.println("⏳ Insertion des données Asiatiques par défaut...");

        // Note : Les insertions ci-dessous fonctionnent aussi bien pour DECIMAL que pour FLOAT
        // car SQL traite "6.50" comme un nombre dans les deux cas.

        // 1. Insertion des CATÉGORIES
        String insertCats = "INSERT INTO categorie (nom, description) VALUES " +
                "('Entrées', 'Pour bien commencer le repas'), " +
                "('Plats', 'Nos spécialités copieuses'), " +
                "('Desserts', 'Douceurs sucrées asiatiques'), " +
                "('Végétarien', 'Plats sans viande ni poisson'), " +
                "('Épicé', 'Pour les amateurs de sensations fortes !'), " +
                "('Soupes', 'Bouillons parfumés et réconfortants'), " +
                "('Sushis & Makis', 'La finesse du poisson cru'), " +
                "('Grillades', 'Délicieuses brochettes marinées'), " +
                "('Boissons', 'Pour se désaltérer');";
        stmt.executeUpdate(insertCats);

        // 2. Insertion des ARTICLES
        String insertArticles = "INSERT INTO article (nom, prix, description, image_url, stock) VALUES " +
                "('Nems au Poulet (x4)', 6.50, 'Rouleaux frits croustillants au poulet et légumes', 'images/articles/nems_poulet.jpg', 100), " +
                "('Gyoza aux Légumes (x6)', 7.00, 'Raviolis japonais grillés et fondants', 'images/articles/gyoza.jpg', 80), " +
                "('Salade de Wakamé', 4.50, 'Salade d''algues japonaises fraîches et assaisonnées', 'images/articles/salade_wakame.jpg', 60), " +
                "('Rouleaux de Printemps (x2)', 6.00, 'Garniture fraîche de crevettes et légumes dans une feuille de riz', 'images/articles/rouleaux_printemps.jpg', 70), " +
                "('Soupe Miso', 3.50, 'Bouillon traditionnel japonais à base de soja fermenté', 'images/articles/soupe_miso.jpg', 150), " +
                "('Soupe Pho au Boeuf', 12.50, 'Grande soupe vietnamienne, nouilles de riz et boeuf émincé', 'images/articles/soupe_pho.jpg', 50), " +
                "('Poulet au Curry Rouge', 13.90, 'Poulet mijoté, lait de coco et curry rouge thaïlandais', 'images/articles/poulet_curry_rouge.jpg', 80), " +
                "('Boeuf Loc Lac', 15.50, 'Cubes de boeuf marinés et sautés, servis avec riz et oeuf', 'images/articles/boeuf_loc_lac.jpg', 60), " +
                "('Pad Thai aux Crevettes', 14.00, 'Nouilles de riz sautées, crevettes, cacahuètes et citron vert', 'images/articles/pad_thai.jpg', 75), " +
                "('Porc Tonkatsu', 13.00, 'Escalope de porc panée et frite, sauce tonkatsu', 'images/articles/porc_tonkatsu.jpg', 65), " +
                "('Tofu Sauté aux Légumes', 11.50, 'Tofu ferme sauté au wok avec des légumes de saison', 'images/articles/tofu_saute.jpg', 55), " +
                "('Assortiment Sushi (8 pièces)', 16.00, 'Sélection du chef: 4 sushis saumon, 4 sushis thon', 'images/articles/assortiment_sushi.jpg', 40), " +
                "('California Rolls Saumon Avocat (6 pièces)', 7.50, 'Maki inversé avec saumon frais et avocat fondant', 'images/articles/california_rolls.jpg', 90), " +
                "('Maki Concombre (6 pièces)', 4.50, 'Maki classique et rafraîchissant au concombre', 'images/articles/maki_concombre.jpg', 100), " +
                "('Brochettes de Boeuf au Fromage (x2)', 6.00, 'Fines tranches de boeuf enroulées sur du fromage fondant', 'images/articles/brochettes_boeuf_fromage.jpg', 80), " +
                "('Brochettes de Poulet Satay (x2)', 5.50, 'Poulet mariné dans une sauce satay aux cacahuètes', 'images/articles/brochettes_poulet_satay.jpg', 85), " +
                "('Perles de Coco (x2)', 4.50, 'Boules de riz gluant à la vapeur, coeur pâte de soja', 'images/articles/perles_coco.jpg', 120), " +
                "('Mochi Glacé Mangue (x1)', 4.00, 'Dessert japonais glacé, coeur sorbet mangue', 'images/articles/mochi_mangue.jpg', 150), " +
                "('Bubble Tea Original', 6.00, 'Thé au lait Taïwanais avec perles de tapioca', 'images/articles/bubble_tea.jpg', 200), " +
                "('Bière Asahi (33cl)', 5.00, 'Célèbre bière japonaise sèche et rafraîchissante', 'images/articles/biere_asahi.jpg', 100), " +
                "('Saké Doux (15cl)', 7.00, 'Alcool de riz japonais, servi tiède ou froid', 'images/articles/sake.jpg', 50);";
        stmt.executeUpdate(insertArticles);

        // 3. Insertion des LIAISONS (Article <-> Catégorie)
        String insertPivot = "INSERT INTO article_categorie (article_id, categorie_id) VALUES " +
                "(1, 1), (2, 1), (2, 4), (3, 1), (3, 4), (4, 1), (5, 6), (5, 4), (6, 6), " +
                "(7, 2), (7, 5), (8, 2), (9, 2), (10, 2), (11, 2), (11, 4), " +
                "(12, 7), (13, 7), (14, 7), (14, 4), (15, 8), (16, 8), " +
                "(17, 3), (17, 4), (18, 3), (18, 4), (19, 9), (20, 9), (21, 9);";
        stmt.executeUpdate(insertPivot);

        // 4. Insertion des MENUS
        String insertMenus = "INSERT INTO menu (nom, prix, image_url) VALUES " +
                "('Menu Déjeuner', 16.00, 'images/menus/menu_dejeuner.jpg'), " +
                "('Menu Bento', 22.50, 'images/menus/menu_bento.jpg'), " +
                "('Menu Yakitori', 19.00, 'images/menus/menu_yakitori.jpg'), " +
                "('Menu Végétarien', 18.00, 'images/menus/menu_vegetarien.jpg');";
        stmt.executeUpdate(insertMenus);

        // 5. Insertion de la COMPOSITION des MENUS
        String insertMenuComp = "INSERT INTO menu_composition (menu_id, article_id, quantite) VALUES " +
                "(1, 2, 1), (1, 9, 1), " +
                "(2, 3, 1), (2, 12, 1), (2, 18, 1), " +
                "(3, 5, 1), (3, 15, 2), (3, 16, 1), " +
                "(4, 2, 1), (4, 11, 1), (4, 17, 1);";
        stmt.executeUpdate(insertMenuComp);

        System.out.println("✅ Données par défaut étendues insérées avec succès !");
    }
}