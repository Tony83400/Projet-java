package fr.univcours.api.models;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("8820fe7b-2ebc-44fd-9afe-0a99f9756476")
public class MenuArticle {
    @objid ("5ce99a99-e767-4488-80e4-e86b6901f75d")
    public Article article;

    @objid ("bb9ce97f-d403-4292-8ea2-58d9eb2f8ca1")
    public String quantite;

    @objid ("6ba41e87-7601-4dcc-acaa-b6bc2e68ed95")
    public String getQuantite() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.quantite;
    }

    @objid ("f706affe-498c-4b7e-9e65-d76a60312966")
    public void setQuantite(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.quantite = value;
    }

    @objid ("872432cd-e48d-4ae6-bd4e-f489556f03e4")
    public Article getArticle() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.article;
    }

    @objid ("04c40802-70d3-4605-802b-d73613496ded")
    public void setArticle(Article value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.article = value;
    }

}
