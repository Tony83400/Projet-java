package fr.univcours.api.models.pivots;

import fr.univcours.api.models.Article;

public class MenuArticle {
    private Article article;
    private int quantite;

    public MenuArticle() {}

    public MenuArticle(Article article, int quantite) {
        this.article = article;
        this.quantite = quantite;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
