package fr.univcours.api.models;

public class Commande {
    private int commande_id;
    private String statut;
    private int numero_ticket;

    public Commande() {
    }

    public Commande(int commande_id, String statut, int numero_ticket) {
        this.commande_id = commande_id;
        this.statut = statut;
        this.numero_ticket = numero_ticket;
    }

    public int getCommande_id() {
        return commande_id;
    }

    public void setCommande_id(int commande_id) {
        this.commande_id = commande_id;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getNumero_ticket() {
        return numero_ticket;
    }

    public void setNumero_ticket(int numero_ticket) {
        this.numero_ticket = numero_ticket;
    }
}

