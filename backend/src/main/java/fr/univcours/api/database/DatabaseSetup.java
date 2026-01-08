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

        // 2. Création des Tables
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();

            // --- TABLE ARTICLE ---
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `article` (" +
                    "`article_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`description` TEXT NULL," +
                    "`prix` FLOAT NOT NULL," +
                    "`image_url` VARCHAR(500) NOT NULL," +
                    "`stock` INT NOT NULL DEFAULT 0" +
                    ") ENGINE=InnoDB;");

            // --- TABLE CATEGORIE ---
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `categorie` (" +
                    "`categorie_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`description` TEXT NULL" +
                    ") ENGINE=InnoDB;");

            // --- TABLE ARTICLE_CATEGORIE ---
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `article_categorie` (" +
                    "`article_id` INT UNSIGNED NOT NULL," +
                    "`categorie_id` INT UNSIGNED NOT NULL," +
                    "PRIMARY KEY (`article_id`, `categorie_id`)," +
                    "CONSTRAINT `fk_artcat_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE," +
                    "CONSTRAINT `fk_artcat_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie`(`categorie_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;");

            // --- TABLE MENU ---
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `menu` (" +
                    "`menu_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`nom` VARCHAR(255) NOT NULL," +
                    "`prix` FLOAT NOT NULL," +
                    "`image_url` VARCHAR(500) NOT NULL" +
                    ") ENGINE=InnoDB;");

            // --- TABLE MENU_COMPOSITION ---
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `menu_composition` (" +
                    "`menu_id` INT UNSIGNED NOT NULL," +
                    "`article_id` INT UNSIGNED NOT NULL," +
                    "`quantite` INT NOT NULL DEFAULT 1," +
                    "PRIMARY KEY (`menu_id`, `article_id`)," +
                    "CONSTRAINT `fk_menucomp_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu`(`menu_id`) ON DELETE CASCADE," +
                    "CONSTRAINT `fk_menucomp_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`article_id`) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;");

            // --- TABLE COMMANDE ---
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `commande` (" +
                    "`commande_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`statut` VARCHAR(50) DEFAULT 'EN_PREPARATION'," +
                    "`numero_ticket` INT NOT NULL" +
                    ") ENGINE=InnoDB;");

            // --- TABLE LIGNE_COMMANDE ---
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `ligne_commande` (" +
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
                    ") ENGINE=InnoDB;");

            System.out.println("✅ Tables vérifiées/créées.");

            // 3. Insertion des données
            insertDefaultData(connection);

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertDefaultData(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM article");
        if (rs.next() && rs.getInt("total") > 0) {
            System.out.println("ℹ️ Les données par défaut existent déjà.");
            return;
        }

        System.out.println("⏳ Insertion des catégories de LANGUE (1=EN, 2=FR)...");
        // Insertion explicite des langues aux IDs 1 et 2
        stmt.executeUpdate("INSERT INTO categorie (categorie_id, nom, description) VALUES (1, 'ENGLISH', 'Language : English')");
        stmt.executeUpdate("INSERT INTO categorie (categorie_id, nom, description) VALUES (2, 'FRANÇAIS', 'Langue : Français')");

        System.out.println("⏳ Insertion des données FR...");
        insertDataFR(stmt);

        System.out.println("⏳ Insertion des données EN...");
        insertDataEN(stmt);

        System.out.println("✅ Données FR et EN insérées avec succès !");
    }

    // ==========================================
    // SECTION FRANÇAISE
    // ==========================================
    private static void insertDataFR(Statement stmt) throws SQLException {
        // 1. Catégories FR (IDs 3 à 11)
        // Décalage de +2 par rapport à l'index naturel (1->3, 2->4, etc.)
        String insertCats = "INSERT INTO categorie (categorie_id, nom, description) VALUES " +
                "(3, 'Entrées', 'Pour bien commencer le repas'), " +
                "(4, 'Plats', 'Nos spécialités copieuses'), " +
                "(5, 'Desserts', 'Douceurs sucrées asiatiques'), " +
                "(6, 'Végétarien', 'Plats sans viande ni poisson'), " +
                "(7, 'Épicé', 'Pour les amateurs de sensations fortes !'), " +
                "(8, 'Soupes', 'Bouillons parfumés et réconfortants'), " +
                "(9, 'Sushis & Makis', 'La finesse du poisson cru'), " +
                "(10, 'Grillades', 'Délicieuses brochettes marinées'), " +
                "(11, 'Boissons', 'Pour se désaltérer');";
        stmt.executeUpdate(insertCats);

        // 2. Articles FR (IDs 1 à 21)
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

        // 3. Liaison FR
        // Langue FR = ID 2
        // Catégories Culinaires FR = ID 3 à 11
        String insertPivot = "INSERT INTO article_categorie (article_id, categorie_id) VALUES " +
                "(1, 3), (1, 2), (2, 3), (2, 6), (2, 2), (3, 3), (3, 6), (3, 2), (4, 3), (4, 2), (5, 8), (5, 6), (5, 2), (6, 8), (6, 2), " +
                "(7, 4), (7, 7), (7, 2), (8, 4), (8, 2), (9, 4), (9, 2), (10, 4), (10, 2), (11, 4), (11, 6), (11, 2), " +
                "(12, 9), (12, 2), (13, 9), (13, 2), (14, 9), (14, 6), (14, 2), (15, 10), (15, 2), (16, 10), (16, 2), " +
                "(17, 5), (17, 6), (17, 2), (18, 5), (18, 6), (18, 2), (19, 11), (19, 2), (20, 11), (20, 2), (21, 11), (21, 2);";
        stmt.executeUpdate(insertPivot);

        // 4. Menus FR (IDs 1-4)
        String insertMenus = "INSERT INTO menu (nom, prix, image_url) VALUES " +
                "('Menu Déjeuner', 16.00, 'images/menus/menu_dejeuner.jpg'), " +
                "('Menu Bento', 22.50, 'images/menus/menu_bento.jpg'), " +
                "('Menu Yakitori', 19.00, 'images/menus/menu_yakitori.jpg'), " +
                "('Menu Végétarien', 18.00, 'images/menus/menu_vegetarien.jpg');";
        stmt.executeUpdate(insertMenus);

        // 5. Composition Menus FR
        String insertMenuComp = "INSERT INTO menu_composition (menu_id, article_id, quantite) VALUES " +
                "(1, 2, 1), (1, 9, 1), " +
                "(2, 3, 1), (2, 12, 1), (2, 18, 1), " +
                "(3, 5, 1), (3, 15, 2), (3, 16, 1), " +
                "(4, 2, 1), (4, 11, 1), (4, 17, 1);";
        stmt.executeUpdate(insertMenuComp);
    }

    // ==========================================
    // SECTION ANGLAISE
    // ==========================================
    private static void insertDataEN(Statement stmt) throws SQLException {
        // 1. Catégories EN (IDs 12 à 20)
        String insertCats = "INSERT INTO categorie (categorie_id, nom, description) VALUES " +
                "(12, 'Starters', 'To start your meal properly'), " +
                "(13, 'Mains', 'Our hearty specialties'), " +
                "(14, 'Desserts', 'Sweet Asian treats'), " +
                "(15, 'Vegetarian', 'Meat and fish free dishes'), " +
                "(16, 'Spicy', 'For thrill seekers!'), " +
                "(17, 'Soups', 'Fragrant and comforting broths'), " +
                "(18, 'Sushi & Maki', 'The finesse of raw fish'), " +
                "(19, 'Grills', 'Delicious marinated skewers'), " +
                "(20, 'Drinks', 'To quench your thirst');";
        stmt.executeUpdate(insertCats);

        // 2. Articles EN (IDs 22 à 42)
        String insertArticles = "INSERT INTO article (nom, prix, description, image_url, stock) VALUES " +
                "('Chicken Spring Rolls (x4)', 6.50, 'Crispy fried rolls with chicken and vegetables', 'images/articles/nems_poulet.jpg', 100), " +
                "('Vegetable Gyoza (x6)', 7.00, 'Grilled and melting Japanese dumplings', 'images/articles/gyoza.jpg', 80), " +
                "('Wakame Salad', 4.50, 'Fresh seasoned Japanese seaweed salad', 'images/articles/salade_wakame.jpg', 60), " +
                "('Summer Rolls (x2)', 6.00, 'Fresh filling of shrimp and vegetables in rice paper', 'images/articles/rouleaux_printemps.jpg', 70), " +
                "('Miso Soup', 3.50, 'Traditional Japanese broth made from fermented soy', 'images/articles/soupe_miso.jpg', 150), " +
                "('Beef Pho Soup', 12.50, 'Large Vietnamese soup, rice noodles and sliced beef', 'images/articles/soupe_pho.jpg', 50), " +
                "('Red Curry Chicken', 13.90, 'Simmered chicken, coconut milk and Thai red curry', 'images/articles/poulet_curry_rouge.jpg', 80), " +
                "('Loc Lac Beef', 15.50, 'Marinated and sautéed beef cubes, served with rice and egg', 'images/articles/boeuf_loc_lac.jpg', 60), " +
                "('Shrimp Pad Thai', 14.00, 'Stir-fried rice noodles, shrimp, peanuts and lime', 'images/articles/pad_thai.jpg', 75), " +
                "('Tonkatsu Pork', 13.00, 'Breaded and fried pork cutlet, tonkatsu sauce', 'images/articles/porc_tonkatsu.jpg', 65), " +
                "('Stir-fried Tofu with Veggies', 11.50, 'Firm tofu stir-fried in a wok with seasonal vegetables', 'images/articles/tofu_saute.jpg', 55), " +
                "('Sushi Assortment (8 pcs)', 16.00, 'Chef''s selection: 4 salmon sushi, 4 tuna sushi', 'images/articles/assortiment_sushi.jpg', 40), " +
                "('Salmon Avocado Rolls (6 pcs)', 7.50, 'Inside-out roll with fresh salmon and melting avocado', 'images/articles/california_rolls.jpg', 90), " +
                "('Cucumber Maki (6 pcs)', 4.50, 'Classic and refreshing cucumber maki', 'images/articles/maki_concombre.jpg', 100), " +
                "('Beef Cheese Skewers (x2)', 6.00, 'Thin slices of beef wrapped around melting cheese', 'images/articles/brochettes_boeuf_fromage.jpg', 80), " +
                "('Chicken Satay Skewers (x2)', 5.50, 'Chicken marinated in peanut satay sauce', 'images/articles/brochettes_poulet_satay.jpg', 85), " +
                "('Coco Pearls (x2)', 4.50, 'Steamed sticky rice balls, soy paste heart', 'images/articles/perles_coco.jpg', 120), " +
                "('Mango Mochi Ice Cream (x1)', 4.00, 'Japanese frozen dessert, mango sorbet heart', 'images/articles/mochi_mangue.jpg', 150), " +
                "('Original Bubble Tea', 6.00, 'Taiwanese milk tea with tapioca pearls', 'images/articles/bubble_tea.jpg', 200), " +
                "('Asahi Beer (33cl)', 5.00, 'Famous dry and refreshing Japanese beer', 'images/articles/biere_asahi.jpg', 100), " +
                "('Sweet Sake (15cl)', 7.00, 'Japanese rice alcohol, served warm or cold', 'images/articles/sake.jpg', 50);";
        stmt.executeUpdate(insertArticles);

        // 3. Liaison EN
        // Langue EN = ID 1
        // Catégories Culinaires EN = ID 12 à 20
        String insertPivot = "INSERT INTO article_categorie (article_id, categorie_id) VALUES " +
                "(22, 12), (22, 1), (23, 12), (23, 15), (23, 1), (24, 12), (24, 15), (24, 1), (25, 12), (25, 1), (26, 17), (26, 15), (26, 1), (27, 17), (27, 1), " +
                "(28, 13), (28, 16), (28, 1), (29, 13), (29, 1), (30, 13), (30, 1), (31, 13), (31, 1), (32, 13), (32, 15), (32, 1), " +
                "(33, 18), (33, 1), (34, 18), (34, 1), (35, 18), (35, 15), (35, 1), (36, 19), (36, 1), (37, 19), (37, 1), " +
                "(38, 14), (38, 15), (38, 1), (39, 14), (39, 15), (39, 1), (40, 20), (40, 1), (41, 20), (41, 1), (42, 20), (42, 1);";
        stmt.executeUpdate(insertPivot);

        // 4. Menus EN (IDs 5-8)
        String insertMenus = "INSERT INTO menu (nom, prix, image_url) VALUES " +
                "('Lunch Menu', 16.00, 'images/menus/menu_dejeuner.jpg'), " +
                "('Bento Menu', 22.50, 'images/menus/menu_bento.jpg'), " +
                "('Yakitori Menu', 19.00, 'images/menus/menu_yakitori.jpg'), " +
                "('Vegetarian Menu', 18.00, 'images/menus/menu_vegetarien.jpg');";
        stmt.executeUpdate(insertMenus);

        // 5. Composition Menus EN
        String insertMenuComp = "INSERT INTO menu_composition (menu_id, article_id, quantite) VALUES " +
                "(5, 23, 1), (5, 30, 1), " +
                "(6, 24, 1), (6, 33, 1), (6, 39, 1), " +
                "(7, 26, 1), (7, 36, 2), (7, 37, 1), " +
                "(8, 23, 1), (8, 32, 1), (8, 38, 1);";
        stmt.executeUpdate(insertMenuComp);
    }
}