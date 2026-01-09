package fr.univcours.api.impl;

import fr.univcours.api.services.MenuService;
import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;
import fr.univcours.api.models.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuServiceImpl extends MenuService {

    @Override
    public List<Map<String, Object>> findCompositionForMenu(int menuId, int langueId) {
        List<Map<String, Object>> composition = new ArrayList<>();

        // SQL inchangé pour la composition (il récupérait déjà la description des articles)
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
            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);
            stmt.setInt(3, menuId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();
                    article.setArticle_id(rs.getInt("article_id"));
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

    @Override
    public List<Menu> GetMenus(int langueId) {
        List<Menu> menus = new ArrayList<>();
        // MODIFICATION ICI : Ajout de la description dans le SELECT
        String sql = "SELECT menu_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(description_en, description) ELSE description END AS description, " + // <-- AJOUTÉ
                "prix, image_url FROM menu";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId); // <-- AJOUTÉ (2ème paramètre pour le CASE description)

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    menus.add(mapResultSetToMenu(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return menus;
    }

    @Override
    public Menu getMenuByid(int id, int langueId) {
        // MODIFICATION ICI : Ajout de la description dans le SELECT
        String sql = "SELECT menu_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(description_en, description) ELSE description END AS description, " + // <-- AJOUTÉ
                "prix, image_url FROM menu WHERE menu_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId); // <-- AJOUTÉ
            stmt.setInt(3, id);       // <-- DÉCALÉ (3ème position)

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMenu(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // MODIFICATION ICI : Mapping de la description
    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setMenu_id(rs.getInt("menu_id"));
        menu.setNom(rs.getString("nom"));
        menu.setDescription(rs.getString("description")); // <-- C'est cette ligne qui manquait !
        menu.setPrix(rs.getFloat("prix"));
        menu.setImage_url(rs.getString("image_url"));
        return menu;
    }
}