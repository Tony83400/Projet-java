package fr.univcours.api.services;

import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Commande;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les utilisateurs Charge les utilisateurs depuis un fichier
 * JSON
 */
public class CommandeService {

    public boolean deleteCommande(int id) throws SQLException {
        String sql = "DELETE FROM commande WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            // Si rowsAffected > 0, c'est qu'on a bien supprimé quelqu'un
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Commande> GetCommandes() {
        List<Commande> commandes = new ArrayList<>();
        try (Connection conn = DatabaseSetup.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM commande")) {
            while (rs.next()) {
                Commande commande = new Commande();
                commande.setCommande_id(rs.getInt("commande_id"));
                commande.setNumero_commande(rs.getInt("numero_commande"));
                commande.setNiveau_epice(rs.getInt("niveau_epice"));
                commande.setQuantite_article(rs.getInt("quantite_article"));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return commandes;
    }

    public Commande addCommande(Commande commandeData) {
        // les ? seront remplacer par nos valeurs
        String sql = "INSERT INTO commande (article_id, quantite_article, numero_commande,niveau_epice) VALUES (?, ?, ?,?)";

        // On demande explicitement à récupérer les clés générées (ID)
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // On remplit les "?" avec les données
            stmt.setInt(1, commandeData.getArticle_id());
            stmt.setInt(2, commandeData.getQuantite_article());
            stmt.setInt(3, commandeData.getNumero_commande());
            stmt.setInt(4, commandeData.getNiveau_epice());


            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // On récupère l'ID généré par la base de données
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // On met à jour l'objet avec son nouvel ID
                        commandeData.setCommande_id(generatedKeys.getInt(1));
                        return commandeData;
                    }
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'utilisateur", e);
        }
    }

    public Commande getCommandeByid(int id) {
        String sql = "SELECT * FROM commande WHERE id =?";
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    Commande commande = new Commande();
                    commande.setCommande_id(rs.getInt("commande_id"));
                    commande.setNumero_commande(rs.getInt("numero_commande"));
                    commande.setNiveau_epice(rs.getInt("niveau_epice"));
                    commande.setQuantite_article(rs.getInt("quantite_article"));
                    return commande;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Commande updateById(int id, Commande commandeData) {
        // les ? seront remplacer par nos valeurs
        String sql = "UPDATE commande SET commande_id = ? , article_id = ? " +
                ", quantite_article=? , numero_commande = ? , niveau_epices=? WHERE commande_id = ?";

        // On demande explicitement à récupérer les clés générées (ID)
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // On remplit les "?" avec les données
            stmt.setInt(1, commandeData.getCommande_id());
            stmt.setInt(2, commandeData.getArticle_id());
            stmt.setInt(3, commandeData.getQuantite_article());
            stmt.setInt(4, commandeData.getNumero_commande());
            stmt.setInt(5, commandeData.getNiveau_epice());

            stmt.setInt(6, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // On récupère l'ID généré par la base de données
                commandeData.setCommande_id(id);
                return commandeData;
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de l'utilisateur", e);
        }
    }
}
