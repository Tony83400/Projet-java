package fr.univcours.api.models;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("5d68ed8e-f75e-46f6-a347-9504c17008fa")
public class LigneCommande {
    @objid ("ce001d61-234f-4303-b876-a1ce5ec2eac2")
    public int ligne_id;

    @objid ("2324d3a7-ef20-4188-8a07-2ae9d3a9645a")
    public int quantite;

    @objid ("a7b84c7b-b1be-4acf-a8f3-9838b58b32f5")
    public float prix_unitaire_facture;

    @objid ("058b34c5-e95e-409a-8f93-aa8c63a34d43")
    public Article article;

    @objid ("d0a7bf38-782c-4c65-9c12-f0e0ee073c50")
    public Menu menu;

    @objid ("6a1c8a10-5c92-41d3-9834-c7eaaf14ac56")
    public int getLigne_id() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.ligne_id;
    }

    @objid ("60713434-d858-42aa-8169-7fba39e3b003")
    public void setLigne_id(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.ligne_id = value;
    }

    @objid ("fba81d80-711d-47d6-98d6-b8143d275ce0")
    public int getQuantite() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.quantite;
    }

    @objid ("889975cd-e052-4079-a10e-cec8ca50c705")
    public void setQuantite(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.quantite = value;
    }

    @objid ("89e1de69-7a7a-4438-83b6-632dc19cffbb")
    public float getPrix_unitaire_facture() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.prix_unitaire_facture;
    }

    @objid ("829cee29-dd47-4d98-abcf-8ba21af099e0")
    public void setPrix_unitaire_facture(float value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.prix_unitaire_facture = value;
    }

    @objid ("4caa1c55-ca5c-48ea-8571-fc28ba1eca67")
    public Article getArticle() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.article;
    }

    @objid ("2968f9c2-9d99-4d74-9ddc-3c9c3ecf0ef2")
    public void setArticle(Article value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.article = value;
    }

    @objid ("681bbab8-a935-4cdf-9edf-0848684eba39")
    public Menu getMenu() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.menu;
    }

    @objid ("211dc946-c500-48de-86fe-3561e0478d73")
    public void setMenu(Menu value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.menu = value;
    }

}
