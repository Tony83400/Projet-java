package fr.univcours.api.models;

public class Commande {
    private int commande_id;
    private int article_id;
    private int quantite_article;
    private int numero_commande;
    private int niveau_epice;

    public Commande() {
    }

    public Commande(int commande_id, int article_id, int quantite_article, int numero_commande,int niveau_epice) {
        this.commande_id = commande_id;
        this.article_id = article_id;
        this.quantite_article = quantite_article;
        this.numero_commande = numero_commande;
        this.niveau_epice = niveau_epice;
    }

    public int getCommande_id() {
        return commande_id;
    }

    public void setCommande_id(int commande_id) {
        this.commande_id = commande_id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public int getQuantite_article() {
        return quantite_article;
    }

    public void setQuantite_article(int quantite_article) {
        this.quantite_article = quantite_article;
    }

    public int getNumero_commande() {
        return numero_commande;
    }

    public void setNumero_commande(int numero_commande) {
        this.numero_commande = numero_commande;
    }

    public int getNiveau_epice() {
        return niveau_epice;
    }

    public void setNiveau_epice(int niveau_epice) {
        this.niveau_epice = niveau_epice;
    }
}
