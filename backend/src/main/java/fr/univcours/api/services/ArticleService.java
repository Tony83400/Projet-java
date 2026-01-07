package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;
import fr.univcours.api.models.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleService {

    private CategorieService categorieService = new CategorieService();

    public Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setArticle_id(rs.getInt("article_id"));
        article.setNom(rs.getString("nom"));
        article.setDescription(rs.getString("description"));
        article.setPrix(rs.getBigDecimal("prix"));
        article.setImage_url(rs.getString("image_url"));
        article.setStock(rs.getInt("stock"));
        return article;
    }

    public List<Categorie> findCategoriesForArticle(int articleId) {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT c.categorie_id, c.nom, c.description FROM categorie c " +
                     "JOIN article_categorie ac ON c.categorie_id = ac.categorie_id " +
                     "WHERE ac.article_id = ?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, articleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(categorieService.mapResultSetToCategorie(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding categories for article", e);
        }
        return categories;
    }

    public List<Article> GetArticles() {
        List<Article> articles = new ArrayList<>();
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM article");) {
            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return articles;
    }

    public Article getArticleByid(int id) {
        String sql = "SELECT * FROM article WHERE article_id =?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Article article = mapResultSetToArticle(rs);
                    return article;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Article> getArticleForCategorie(int categorie_id) {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.* FROM article a " +
                "JOIN article_categorie ac ON a.article_id = ac.article_id " +
                "WHERE ac.categorie_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categorie_id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(mapResultSetToArticle(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des articles pour la catégorie " + categorie_id, e);
        }
        return articles;
    }
}
