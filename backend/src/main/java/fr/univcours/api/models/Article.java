package fr.univcours.api.models;

public class Article {
    private int article_id;
    private String nom;
    private String description;
    private int prix;
    private int stock;


    public Article(){

    }
    public Article(int article_id, int prix, int stock, String nom,String description) {
        this.article_id = article_id;
        this.prix = prix;
        this.stock = stock;
        this.nom = nom;
        this.description = description;
    }
    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
