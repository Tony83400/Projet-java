package com.example.frontend.models;

public class CommandeItem {
    private Integer articleId;
    private Integer menuId;
    private int quantite = 1;

    public CommandeItem() {
    }

    public CommandeItem(Integer articleId, Integer menuId, int quantite) {
        this.articleId = articleId;
        this.menuId = menuId;
        this.quantite = quantite;
    }

    // Getters/Setters...
    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}