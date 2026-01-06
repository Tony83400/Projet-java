package fr.univcours.api.models;

public class Menu {
    private int menu_id;
    private int article_id;
    private String nom;
    private String image_url;

    public Menu() {
    }

    public Menu(int menu_id, int article_id, String nom, String imageUrl) {
        this.menu_id = menu_id;
        this.article_id = article_id;
        this.nom = nom;
        image_url = imageUrl;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
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
