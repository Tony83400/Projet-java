package fr.univcours.api.impl;

import fr.univcours.api.services.ArticleService;
import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ArticleServiceImpl extends ArticleService {

    // Helper pour construire la requête SQL avec sélection de langue
    private String getSelectQuery() {
        return "SELECT article_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(nom_en, nom) ELSE nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(description_en, description) ELSE description END AS description, " +
                "prix, stock, image_url FROM article";
    }

    public List<Article> GetArticles(int langueId) {
        List<Article> articles = new ArrayList<>();
        String sql = getSelectQuery();

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(mapResultSetToArticle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return articles;
    }

    public Article getArticleByid(int id, int langueId) {
        String sql = getSelectQuery() + " WHERE article_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);
            stmt.setInt(3, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArticle(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Article> getArticleForCategorie(int categorieId, int langueId) {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.article_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(a.nom_en, a.nom) ELSE a.nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(a.description_en, a.description) ELSE a.description END AS description, " +
                "a.prix, a.stock, a.image_url " +
                "FROM article a " +
                "JOIN article_categorie ac ON a.article_id = ac.article_id " +
                "WHERE ac.categorie_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);
            stmt.setInt(3, categorieId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(mapResultSetToArticle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return articles;
    }

    // N'oubliez pas de mettre à jour findCategoriesForArticle si vous voulez traduire les noms de catégories aussi
    public List<Map<String, Object>> findCategoriesForArticle(int articleId, int langueId) {
        // Logique similaire pour récupérer les catégories traduites
        List<Map<String, Object>> categories = new ArrayList<>();
        String sql = "SELECT c.categorie_id, " +
                "CASE WHEN ? = 2 THEN IFNULL(c.nom_en, c.nom) ELSE c.nom END AS nom, " +
                "CASE WHEN ? = 2 THEN IFNULL(c.description_en, c.description) ELSE c.description END AS description " +
                "FROM categorie c " +
                "JOIN article_categorie ac ON c.categorie_id = ac.categorie_id " +
                "WHERE ac.article_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, langueId);
            stmt.setInt(2, langueId);
            stmt.setInt(3, articleId);

            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()){
                    Map<String, Object> catMap = new HashMap<>();
                    catMap.put("id", rs.getInt("categorie_id"));
                    catMap.put("nom", rs.getString("nom"));
                    catMap.put("description", rs.getString("description"));
                    categories.add(catMap);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setArticle_id(rs.getInt("article_id"));
        article.setNom(rs.getString("nom")); // Déjà traduit par SQL
        article.setDescription(rs.getString("description")); // Déjà traduit par SQL
        article.setPrix(rs.getFloat("prix"));
        article.setStock(rs.getInt("stock"));
        article.setImage_url(rs.getString("image_url"));
        return article;
    }
}