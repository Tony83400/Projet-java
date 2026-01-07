package fr.univcours.api.models;

import java.math.BigDecimal;

public class LigneCommande {
    private int ligne_id;
    private int quantite;
    private BigDecimal prix_unitaire_facture;
    private Article article; // Can be null
    private Menu menu;       // Can be null

    public LigneCommande() {
    }

    public int getLigne_id() {
        return ligne_id;
    }

    public void setLigne_id(int ligne_id) {
        this.ligne_id = ligne_id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrix_unitaire_facture() {
        return prix_unitaire_facture;
    }

    public void setPrix_unitaire_facture(BigDecimal prix_unitaire_facture) {
        this.prix_unitaire_facture = prix_unitaire_facture;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
