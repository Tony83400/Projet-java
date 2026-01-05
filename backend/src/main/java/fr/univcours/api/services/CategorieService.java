package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService {

    public List<Categorie> GetCategories() {
        List<Categorie> categories = new ArrayList<>();
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM categorie");) {
            while (rs.next()) {
                Categorie categorie = new Categorie();
                categorie.setCategorie_id(rs.getInt("categorie_id"));
                categorie.setNom(rs.getString("nom"));
                categorie.setDescription(rs.getString("description"));
                categories.add(categorie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    public Categorie getCategorieByid(int id) {
        String sql = "SELECT * FROM categorie WHERE categorie_id =?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    Categorie categorie = new Categorie();
                    categorie.setCategorie_id(rs.getInt("categorie_id"));
                    categorie.setNom(rs.getString("nom"));
                    categorie.setDescription(rs.getString("description"));
                    return categorie;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
