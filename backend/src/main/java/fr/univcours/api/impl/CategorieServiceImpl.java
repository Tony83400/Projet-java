package fr.univcours.api.impl;

import fr.univcours.api.services.CategorieService;
import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieServiceImpl extends CategorieService {

    @Override
    public List<Categorie> GetCategories() {
        List<Categorie> categories = new ArrayList<>();
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM categorie");) {
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Categorie getCategorieByid(int id) {
        String sql = "SELECT * FROM categorie WHERE categorie_id =?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategorie(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Categorie mapResultSetToCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        categorie.setCategorie_id(rs.getInt("categorie_id"));
        categorie.setNom(rs.getString("nom"));
        categorie.setDescription(rs.getString("description"));
        return categorie;
    }
}