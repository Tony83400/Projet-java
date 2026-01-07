package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;
import fr.univcours.api.models.Commande;
import fr.univcours.api.models.LigneCommande;
import fr.univcours.api.models.Menu;
import fr.univcours.api.models.CommandeItem;
import fr.univcours.api.models.CommandeRequest;

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

    public Commande addCommande(CommandeRequest commandeRequest) {
        String sqlCmd = "INSERT INTO commande (numero_ticket, statut) VALUES (?, ?)";
        String sqlLigne = "INSERT INTO ligne_commande (commande_id, article_id, menu_id, quantite, prix_unitaire_facture) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseSetup.getConnection();
            conn.setAutoCommit(false); // Start transaction

            int nextTicket = getNextNumeroTicket(conn);

            int commandeId;
            // Insert commande
            try (PreparedStatement stmtCmd = conn.prepareStatement(sqlCmd, Statement.RETURN_GENERATED_KEYS)) {
                stmtCmd.setInt(1, nextTicket);
                stmtCmd.setString(2, "EN_PREPARATION");
                stmtCmd.executeUpdate();

                try (ResultSet generatedKeys = stmtCmd.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        commandeId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating commande failed, no ID obtained.");
                    }
                }
            }

            // Insert lignes
            for (CommandeItem item : commandeRequest.getItems()) {
                try (PreparedStatement stmtLigne = conn.prepareStatement(sqlLigne)) {
                    stmtLigne.setInt(1, commandeId);
                    stmtLigne.setInt(4, item.getQuantite());

                    BigDecimal price;
                    if (item.getArticleId() != null) {
                        Article article = articleService.getArticleByid(item.getArticleId());
                        if(article == null) throw new SQLException("Article with id " + item.getArticleId() + " not found.");
                        price = article.getPrix();
                        stmtLigne.setInt(2, item.getArticleId());
                        stmtLigne.setNull(3, Types.INTEGER);
                    } else if (item.getMenuId() != null) {
                        Menu menu = menuService.getMenuByid(item.getMenuId());
                        if(menu == null) throw new SQLException("Menu with id " + item.getMenuId() + " not found.");
                        price = menu.getPrix();
                        stmtLigne.setNull(2, Types.INTEGER);
                        stmtLigne.setInt(3, item.getMenuId());
                    } else {
                        // Ni article ni menu, on ignore ou on lance une erreur
                        continue; // simple skip
                    }
                    stmtLigne.setBigDecimal(5, price);
                    stmtLigne.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            return getCommandeByid(commandeId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    throw new RuntimeException("Error during rollback", ex);
                }
            }
            throw new RuntimeException("Error adding commande", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
}