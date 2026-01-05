package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuService {

    public List<Menu> GetMenus() {
        List<Menu> menus = new ArrayList<>();
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu")) {
            while (rs.next()) {
                Menu menu = new Menu();
                menu.setMenu_id(rs.getInt("menu_id"));
                menu.setArticle_id(rs.getInt("article_id"));
                menu.setNom(rs.getString("nom"));
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
                    Menu menu = new Menu();
                    menu.setMenu_id(rs.getInt("menu_id"));
                    menu.setArticle_id(rs.getInt("article_id"));
                    menu.setNom(rs.getString("nom"));
                    return menu;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
