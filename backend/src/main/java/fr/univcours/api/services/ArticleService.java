package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleService {

    public List<Article> GetArticles() {
        List<Article> articles = new ArrayList<>();
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM article");) {
            while (rs.next()) {
                Article article = new Article();
                article.setArticle_id(rs.getInt("article_id"));
                article.setStock(rs.getInt("stock"));
                article.setDescription(rs.getString("description"));
                article.setImage_url(rs.getString("image_url"));
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
                    Article article = new Article();
                    article.setArticle_id(rs.getInt("article_id"));
                    article.setStock(rs.getInt("stock"));
                    article.setDescription(rs.getString("description"));
                    article.setImage_url(rs.getString("image_url"));
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

        // On utilise PreparedStatement pour gérer le paramètre '?'
        String sql = "SELECT a.* FROM article a " +
                "JOIN article_categorie ac ON a.article_id = ac.article_id " +
                "WHERE ac.categorie_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categorie_id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();
                    article.setArticle_id(rs.getInt("article_id"));
                    article.setNom(rs.getString("nom"));
                    article.setPrix(rs.getInt("prix"));
                    article.setDescription(rs.getString("description"));
                    article.setStock(rs.getInt("stock"));
                    article.setImage_url(rs.getString("image_url"));

                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // C'est souvent mieux de renvoyer une liste vide ou de relancer une exception personnalisée
            throw new RuntimeException("Erreur lors de la récupération des articles pour la catégorie " + categorie_id, e);
        }
        return articles;
    }
    public List<Article> getArticlesForMenu(int menu_id) {
        List<Article> articles = new ArrayList<>();

        // On sélectionne tous les articles qui sont liés à ce menu_id dans la table menu
        String sql = "SELECT a.* FROM article a " +
                "JOIN menu m ON a.article_id = m.article_id " +
                "WHERE m.menu_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, menu_id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();
                    article.setArticle_id(rs.getInt("article_id"));
                    article.setNom(rs.getString("nom"));
                    article.setPrix(rs.getInt("prix"));
                    article.setDescription(rs.getString("description"));
                    article.setImage_url(rs.getString("image_url"));
                    article.setStock(rs.getInt("stock"));

                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des articles pour le menu " + menu_id, e);
        }
        return articles;
    }
}
