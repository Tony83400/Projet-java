package fr.univcours.api.impl;

import fr.univcours.api.services.*; // Importe les interfaces
import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeServiceImpl extends CommandeService {

    // On utilise les implémentations concrètes ici
    private final ArticleService articleService = new ArticleServiceImpl();
    private final MenuService menuService = new MenuServiceImpl();

    @Override
    public List<LigneCommande> findLignesForCommande(int commandeId) throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM ligne_commande WHERE commande_id = ?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LigneCommande ligne = new LigneCommande();
                    ligne.setLigne_id(rs.getInt("ligne_id"));
                    ligne.setQuantite(rs.getInt("quantite"));
                    ligne.setPrix_unitaire_facture(rs.getFloat("prix_unitaire_facture"));

                    int articleId = rs.getInt("article_id");
                    boolean articleWasNull = rs.wasNull();
                    int menuId = rs.getInt("menu_id");

                    if (!articleWasNull) {
                        ligne.setArticle(articleService.getArticleByid(articleId));
                    } else {
                        ligne.setMenu(menuService.getMenuByid(menuId));
                    }
                    lignes.add(ligne);
                }
            }
        }
        return lignes;
    }

    @Override
    public List<Commande> GetCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                commandes.add(mapResultSetToCommande(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return commandes;
    }

    @Override
    public Commande getCommandeByid(int id) throws SQLException {
        String sql = "SELECT * FROM commande WHERE commande_id = ?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCommande(rs);
                }
            }
            return null;
        }
    }

    @Override
    public Commande createEmptyCommande() {
        String sqlCmd = "INSERT INTO commande (numero_ticket, statut) VALUES (?, ?)";
        try (Connection conn = DatabaseSetup.getConnection()) {
            conn.setAutoCommit(false);
            int nextTicket = getNextNumeroTicket(conn);

            try (PreparedStatement stmtCmd = conn.prepareStatement(sqlCmd, Statement.RETURN_GENERATED_KEYS)) {
                stmtCmd.setInt(1, nextTicket);
                stmtCmd.setString(2, "EN_PREPARATION");
                stmtCmd.executeUpdate();

                try (ResultSet generatedKeys = stmtCmd.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int commandeId = generatedKeys.getInt(1);
                        conn.commit();
                        return getCommandeByid(commandeId);
                    } else {
                        throw new SQLException("Creating commande failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating empty commande", e);
        }
    }

    @Override
    public float calculateTotalForCommande(int commandeId) throws SQLException {
        List<LigneCommande> lignes = findLignesForCommande(commandeId);
        float total = 0.0f;
        for (LigneCommande ligne : lignes) {
            total += ligne.getPrix_unitaire_facture() * ligne.getQuantite();
        }
        return total;
    }

    @Override
    public LigneCommande addLigneToCommande(int commandeId, CommandeItem item) throws SQLException {
        String sqlLigne = "INSERT INTO ligne_commande (commande_id, article_id, menu_id, quantite, prix_unitaire_facture) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseSetup.getConnection()) {
            float price;
            if (item.getArticleId() != null) {
                Article article = articleService.getArticleByid(item.getArticleId());
                if(article == null) throw new SQLException("Article not found.");
                price = article.getPrix();
            } else if (item.getMenuId() != null) {
                Menu menu = menuService.getMenuByid(item.getMenuId());
                if(menu == null) throw new SQLException("Menu not found.");
                price = menu.getPrix();
            } else {
                throw new SQLException("Item must have either an articleId or a menuId.");
            }

            try (PreparedStatement stmtLigne = conn.prepareStatement(sqlLigne, Statement.RETURN_GENERATED_KEYS)) {
                stmtLigne.setInt(1, commandeId);
                stmtLigne.setInt(4, item.getQuantite());
                stmtLigne.setFloat(5, price);

                if (item.getArticleId() != null) {
                    stmtLigne.setInt(2, item.getArticleId());
                    stmtLigne.setNull(3, Types.INTEGER);
                } else {
                    stmtLigne.setNull(2, Types.INTEGER);
                    stmtLigne.setInt(3, item.getMenuId());
                }

                int affectedRows = stmtLigne.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmtLigne.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newLigneId = generatedKeys.getInt(1);
                            return findLignesForCommande(commandeId).stream()
                                    .filter(l -> l.getLigne_id() == newLigneId)
                                    .findFirst().orElse(null);
                        }
                    }
                }
            }
        }
        return null;
    }

    // Helper methodes privées
    private int getNextNumeroTicket(Connection conn) throws SQLException {
        String sql = "SELECT MAX(numero_ticket) FROM commande";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            } else {
                return 1;
            }
        }
    }

    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        Commande commande = new Commande();
        commande.setCommande_id(rs.getInt("commande_id"));
        commande.setStatut(rs.getString("statut"));
        commande.setNumero_ticket(rs.getInt("numero_ticket"));
        return commande;
    }
}