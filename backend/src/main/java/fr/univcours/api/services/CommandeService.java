package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;
import fr.univcours.api.models.Commande;
import fr.univcours.api.models.LigneCommande;
import fr.univcours.api.models.Menu;
import fr.univcours.api.models.CommandeItem;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeService {

    private final ArticleService articleService = new ArticleService();
    private final MenuService menuService = new MenuService();

    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        Commande commande = new Commande();
        commande.setCommande_id(rs.getInt("commande_id"));
        commande.setStatut(rs.getString("statut"));
        commande.setNumero_ticket(rs.getInt("numero_ticket"));
        return commande;
    }

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
                    ligne.setPrix_unitaire_facture(rs.getBigDecimal("prix_unitaire_facture"));

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
    
    public List<Commande> GetCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Commande commande = mapResultSetToCommande(rs);
                commandes.add(commande);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return commandes;
    }

    public Commande getCommandeByid(int id) throws SQLException {
        String sql = "SELECT * FROM commande WHERE commande_id = ?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = mapResultSetToCommande(rs);
                    return commande;
                }
            }
            return null;
        }
    }

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

    public Commande createEmptyCommande() {
        String sqlCmd = "INSERT INTO commande (numero_ticket, statut) VALUES (?, ?)";
        try (Connection conn = DatabaseSetup.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            int nextTicket = getNextNumeroTicket(conn);

            try (PreparedStatement stmtCmd = conn.prepareStatement(sqlCmd, Statement.RETURN_GENERATED_KEYS)) {
                stmtCmd.setInt(1, nextTicket);
                stmtCmd.setString(2, "EN_PREPARATION");
                stmtCmd.executeUpdate();

                try (ResultSet generatedKeys = stmtCmd.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int commandeId = generatedKeys.getInt(1);
                        conn.commit(); // Commit transaction
                        return getCommandeByid(commandeId);
                    } else {
                        throw new SQLException("Creating commande failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            // No need for explicit rollback, connection close will handle it if autoCommit is false.
            throw new RuntimeException("Error creating empty commande", e);
        }
    }

    public BigDecimal calculateTotalForCommande(int commandeId) throws SQLException {
        List<LigneCommande> lignes = findLignesForCommande(commandeId);
        BigDecimal total = BigDecimal.ZERO;
        for (LigneCommande ligne : lignes) {
            BigDecimal ligneTotal = ligne.getPrix_unitaire_facture().multiply(new BigDecimal(ligne.getQuantite()));
            total = total.add(ligneTotal);
        }
        return total;
    }

    public LigneCommande addLigneToCommande(int commandeId, CommandeItem item) throws SQLException {
        String sqlLigne = "INSERT INTO ligne_commande (commande_id, article_id, menu_id, quantite, prix_unitaire_facture) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseSetup.getConnection()) {
            
            BigDecimal price;
            if (item.getArticleId() != null) {
                Article article = articleService.getArticleByid(item.getArticleId());
                if(article == null) throw new SQLException("Article with id " + item.getArticleId() + " not found.");
                price = article.getPrix();
            } else if (item.getMenuId() != null) {
                Menu menu = menuService.getMenuByid(item.getMenuId());
                if(menu == null) throw new SQLException("Menu with id " + item.getMenuId() + " not found.");
                price = menu.getPrix();
            } else {
                throw new SQLException("Item must have either an articleId or a menuId.");
            }

            try (PreparedStatement stmtLigne = conn.prepareStatement(sqlLigne, Statement.RETURN_GENERATED_KEYS)) {
                stmtLigne.setInt(1, commandeId);
                stmtLigne.setInt(4, item.getQuantite());
                stmtLigne.setBigDecimal(5, price);

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
                            // Now fetch and return the newly created LigneCommande
                            List<LigneCommande> allLignes = findLignesForCommande(commandeId);
                            return allLignes.stream().filter(l -> l.getLigne_id() == newLigneId).findFirst().orElse(null);
                        }
                    }
                }
            }
        }
        return null;
    }
}