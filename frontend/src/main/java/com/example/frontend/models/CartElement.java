package com.example.frontend.models;

public class CartElement {
    private Object content;
    private int quantity = 1;

    public CartElement(Object c) {
        this.content = c;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void incrementQuantity() { this.quantity++; }
    public void decrementQuantity() { if(this.quantity > 0) this.quantity--; }

    public boolean isArticle() {
        return content instanceof Article;
    }

    public Article getArticle() {
        return (Article) content;
    }

    public Menu getMenu() {
        return (Menu) content;
    }

    public String getName() {
        return isArticle() ? getArticle().getNom() : getMenu().getNom();
    }

    public double getPrice() {
        return isArticle() ? getArticle().getPrix() : getMenu().getPrix();
    }

    public boolean isSameAs(Object o) {
        if (isArticle() && o instanceof Article) {
            return getArticle().getArticle_id() == ((Article) o).getArticle_id();
        }
        if (!isArticle() && o instanceof Menu) {
            return getMenu().getMenu_id() == ((Menu) o).getMenu_id();
        }
        return false;
    }
}