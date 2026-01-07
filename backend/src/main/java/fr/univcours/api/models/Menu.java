package fr.univcours.api.models;

import java.math.BigDecimal;

public class Menu {
    private int menu_id;
    private String nom;
    private BigDecimal prix;
    private String image_url;

    public Menu() {
    }

    public Menu(int menu_id, String nom, BigDecimal prix, String imageUrl) {
        this.menu_id = menu_id;
        this.nom = nom;
        this.prix = prix;
        this.image_url = imageUrl;
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

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
