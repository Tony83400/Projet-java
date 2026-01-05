package fr.univcours.api.models;

public class Categorie {
    private int categorie_id;
    private String nom;
    private String description;

    public Categorie(){
    }
    public  Categorie(int categorie_id, String nom,String description){
        this.categorie_id = categorie_id;
        this.nom = nom;
        this.description = description;
    }

    public int getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
        this.categorie_id = categorie_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
