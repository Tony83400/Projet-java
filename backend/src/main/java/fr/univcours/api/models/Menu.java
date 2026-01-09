package fr.univcours.api.models;


public class Menu {
    private int menu_id;
    private String nom;
    private float prix;
    private String image_url;
    private String description;
    public Menu() {
    }

    public Menu(int menu_id, String nom, float prix, String imageUrl, String description) {
        this.menu_id = menu_id;
        this.nom = nom;
        this.prix = prix;
        this.image_url = imageUrl;
        this.description = description;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
