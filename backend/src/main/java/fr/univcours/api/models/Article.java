package fr.univcours.api.models;

public class Article {
    private int article_id;
    private String nom;
    private String description;
    private float prix;
    private int stock;
    private String image_url;


    public Article(){

    }
    public Article(int article_id, float prix, int stock, String nom, String description, String image_url) {
        this.article_id = article_id;
        this.prix = prix;
        this.stock = stock;
        this.nom = nom;
        this.description = description;
        this.image_url = image_url;
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

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
