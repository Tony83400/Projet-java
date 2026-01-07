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

    private ArticleService articleService = new ArticleService();

    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setMenu_id(rs.getInt("menu_id"));
        menu.setNom(rs.getString("nom"));

        // --- CHANGEMENT ICI : getFloat au lieu de getBigDecimal ---
        menu.setPrix(rs.getFloat("prix"));
        // ----------------------------------------------------------

        menu.setImage_url(rs.getString("image_url"));
        return menu;
    }

    public List<Map<String, Object>> findCompositionForMenu(int menuId) {
        List<Map<String, Object>> composition = new ArrayList<>();
        String sql = "SELECT a.*, mc.quantite FROM article a " +
                "JOIN menu_composition mc ON a.article_id = mc.article_id " +
                "WHERE mc.menu_id = ?";
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, menuId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // --- CODE DUPLIQUÉ MANUELLEMENT ICI AUSSI ---
                    Article article = new Article();
                    article.setArticle_id(rs.getInt("article_id"));
                    article.setNom(rs.getString("nom"));
                    article.setDescription(rs.getString("description"));
                    article.setPrix(rs.getFloat("prix"));
                    article.setImage_url(rs.getString("image_url"));
                    article.setStock(rs.getInt("stock"));
                    // --------------------------------------------

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

    public List<Menu> GetMenus() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM menu";
        try (Connection conn = DatabaseSetup.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Menu menu = mapResultSetToMenu(rs);
                menus.add(menu);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return menus;
    }

    public Menu getMenuByid(int id) {
        String sql = "SELECT * FROM menu WHERE menu_id =?";
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
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
}