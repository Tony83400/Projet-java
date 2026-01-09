package fr.univcours.api.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;
import fr.univcours.api.models.Menu;

public class MenuService {

    // On garde le service au cas où, mais il n'est plus utilisé pour le mapping ici
    private ArticleService articleService = new ArticleService();

    // J'ai ajouté 'int langueId' pour que cela fonctionne avec votre MenuController
    public List<Map<String, Object>> findCompositionForMenu(int menuId, int langueId) {
        List<Map<String, Object>> composition = new ArrayList<>();

        // SQL adapté : on sélectionne les champs traduits via CASE WHEN
        String sql = "SELECT " +
                "a.article_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(a.nom_en, a.nom) ELSE a.nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(a.description_en, a.description) ELSE a.description END AS description, " +
                "a.prix, a.image_url, a.stock, mc.quantite " +
                "FROM article a " +
                "JOIN menu_composition mc ON a.article_id = mc.article_id " +
                "WHERE mc.menu_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Paramètres : Langue (x2 pour nom et desc) puis ID du menu
            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);
            stmt.setInt(3, menuId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();
                    article.setArticle_id(rs.getInt("article_id"));
                    // Grâce au SQL, "nom" et "description" contiennent déjà la bonne langue
                    article.setNom(rs.getString("nom"));
                    article.setDescription(rs.getString("description"));
                    article.setPrix(rs.getFloat("prix"));
                    article.setImage_url(rs.getString("image_url"));
                    article.setStock(rs.getInt("stock"));

                    int quantite = rs.getInt("quantite");
                    Map<String, Object> compositionMap = new HashMap<>();
                    compositionMap.put("article", article);
                    compositionMap.put("quantite", quantite);
                    composition.add(compositionMap);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding composition for menu", e);
        }
        return composition;
    }

    // Ajout de langueId pour respecter votre Controller
    public List<Menu> GetMenus(int langueId) {
        List<Menu> menus = new ArrayList<>();

        // SQL adapté pour récupérer le menu avec traduction
        String sql = "SELECT menu_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(description_en, description) ELSE description END AS description, " +
                "prix, image_url FROM menu";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Changement en PreparedStatement pour le paramètre

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Menu menu = new Menu();
                    menu.setMenu_id(rs.getInt("menu_id"));
                    // Récupère la description (traduite ou non selon la requête SQL)
                    menu.setDescription(rs.getString("description"));
                    menu.setNom(rs.getString("nom"));
                    menu.setPrix(rs.getFloat("prix"));
                    menu.setImage_url(rs.getString("image_url"));

                    menus.add(menu);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return menus;
    }

    public Menu getMenuByid(int id, int langueId) {
        // SQL identique à GetMenus mais avec un filtre WHERE
        String sql = "SELECT menu_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(description_en, description) ELSE description END AS description, " +
                "prix, image_url FROM menu WHERE menu_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);
            stmt.setInt(3, id); // L'ID passe en 3ème position

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Menu menu = new Menu();
                    menu.setMenu_id(rs.getInt("menu_id"));
                    menu.setDescription(rs.getString("description"));
                    menu.setNom(rs.getString("nom"));
                    menu.setPrix(rs.getFloat("prix"));
                    menu.setImage_url(rs.getString("image_url"));

                    return menu;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}