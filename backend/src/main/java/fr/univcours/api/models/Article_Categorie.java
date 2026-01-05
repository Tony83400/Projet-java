package fr.univcours.api.models;

public class Article_Categorie {
    private int article_id;
    private int categorie_id;

    public  Article_Categorie(){

    }
    public Article_Categorie(int article_id,int categorie_id){
        this.article_id = article_id;
        this.categorie_id = categorie_id;
    }
    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public int getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
        this.categorie_id = categorie_id;
    }
}
