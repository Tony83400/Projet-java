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

    // Méthode adaptée pour prendre en compte la langue dans la requête SQL des articles du menu
    // Attention: MenuService (classe parent) doit aussi être mise à jour si elle définit ces méthodes abstraites.
    // Si MenuService est une classe abstraite, changez les signatures là-bas aussi.

    public List<Map<String, Object>> findCompositionForMenu(int menuId, int langueId) {
        List<Map<String, Object>> composition = new ArrayList<>();

        // SQL Dynamique : Si langueId=2, on prend _en, sinon le français par défaut.
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
                    // Grâce à l'alias SQL, rs.getString("nom") contient déjà la bonne langue
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

    public List<Menu> GetMenus(int langueId) {
        List<Menu> menus = new ArrayList<>();
        // Sélection conditionnelle du nom selon la langue
        String sql = "SELECT menu_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "prix, image_url FROM menu";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId); // Paramètre pour le CASE

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

    public Menu getMenuByid(int id, int langueId) {
        String sql = "SELECT menu_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "prix, image_url FROM menu WHERE menu_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, id);

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

    // Le mapper reste simple car SQL a déjà fait le travail de renommage
    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setMenu_id(rs.getInt("menu_id"));
        menu.setNom(rs.getString("nom")); // Contient anglais ou français selon la requête
        menu.setPrix(rs.getFloat("prix"));
        menu.setImage_url(rs.getString("image_url"));
        return menu;
    }

    // Méthodes Override obligatoires de la classe mère (si la signature ne change pas là-bas)
    // Idéalement, mettez à jour MenuService pour inclure ces nouvelles signatures
    // ou surchargez-les ici. Pour ce code, je pars du principe qu'on utilise les nouvelles méthodes.
}