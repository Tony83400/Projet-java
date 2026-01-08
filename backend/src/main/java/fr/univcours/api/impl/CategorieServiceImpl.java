package fr.univcours.api.impl;

import fr.univcours.api.services.CategorieService;
import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieServiceImpl extends CategorieService {

    // Helper pour la requête SQL
    private String getSelectQuery() {
        return "SELECT categorie_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(description_en, description) ELSE description END AS description " +
                "FROM categorie";
    }

    // On surcharge ou on modifie la méthode pour accepter l'ID langue
    public List<Categorie> GetCategories(int langueId) {
        List<Categorie> categories = new ArrayList<>();
        String sql = getSelectQuery();

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategorie(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    public Categorie getCategorieByid(int id, int langueId) {
        String sql = getSelectQuery() + " WHERE categorie_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);
            stmt.setInt(3, id);

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
        categorie.setNom(rs.getString("nom")); // Déjà traduit par SQL
        categorie.setDescription(rs.getString("description")); // Déjà traduit par SQL
        return categorie;
    }

    // Note: Si vous devez garder les anciennes méthodes sans langueId pour compatibilité,
    // vous pouvez les garder et appeler this.GetCategories(1) par défaut.
    // Sinon, mettez à jour votre classe abstraite CategorieService.
}