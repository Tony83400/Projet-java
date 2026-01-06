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
        String sql = "DELETE FROM commande WHERE commande_id = ?";

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
        String sql = "INSERT INTO commande (article_id, quantite_article, numero_commande,niveau_epice,commande_id) VALUES (?,?, ?, ?,?)";

        // On demande explicitement à récupérer les clés générées (ID)
        try (Connection conn = DatabaseSetup.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // On remplit les "?" avec les données
            stmt.setInt(1, commandeData.getArticle_id());
            stmt.setInt(2, commandeData.getQuantite_article());
            stmt.setInt(3, commandeData.getNumero_commande());
            stmt.setInt(4, commandeData.getNiveau_epice());
            stmt.setInt(5, commandeData.getCommande_id());



            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {

                        return commandeData;

            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'utilisateur", e);
        }
    }

    public Commande getCommandeByid(int id) {
        String sql = "SELECT * FROM commande WHERE commande_id =?";
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
    public List<Commande> getCommandesByNumero(int numeroCommande) {
        List<Commande> commandes = new ArrayList<>(); // 1. On prépare la liste vide

        // 2. On cherche par numero_commande (ex: Ticket n°100) pour avoir tous les articles
        String sql = "SELECT * FROM commande WHERE numero_commande = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numeroCommande);

            try (ResultSet rs = stmt.executeQuery()) {
                // 3. On utilise WHILE pour boucler sur tous les résultats
                while (rs.next()) {
                    Commande commande = new Commande();
                    commande.setCommande_id(rs.getInt("commande_id"));
                    commande.setArticle_id(rs.getInt("article_id")); // N'oublie pas de mapper l'article_id !
                    commande.setNumero_commande(rs.getInt("numero_commande"));
                    commande.setNiveau_epice(rs.getInt("niveau_epice"));
                    commande.setQuantite_article(rs.getInt("quantite_article"));

                    // 4. On ajoute l'objet à la liste
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 5. On retourne la liste (elle peut être vide si le numéro n'existe pas)
        return commandes;
    }

    public Commande updateById(int id, Commande commandeData) {
        // les ? seront remplacer par nos valeurs
        String sql = "UPDATE commande SET commande_id = ? , article_id = ? " +
                ", quantite_article=? , numero_commande = ? , niveau_epice=? WHERE commande_id = ?";

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

    public int getNextId() {
        int nextId = 1;
        String sql = "SELECT MAX(commande_id) FROM commande";

        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                // Si la table est vide, rs.getInt renvoie 0, donc on commence à 1
                nextId = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur calcul ID", e);
        }
        return nextId;
    }
    public double getTotalPriceByNumero(int commande_id) {
        // On multiplie la quantité par le prix pour chaque ligne, et on somme le tout
        String sql = "SELECT SUM(c.quantite_article * a.prix) AS total " +
                "FROM commande c " +
                "JOIN article a ON c.article_id = a.article_id " +
                "WHERE c.commande_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commande_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retourne la somme calculée par SQL
                    // Si aucune commande n'est trouvée, ça retournera 0.0
                    return rs.getDouble("total");
                }
            }
            return 0.0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du total", e);
        }
    }
}
