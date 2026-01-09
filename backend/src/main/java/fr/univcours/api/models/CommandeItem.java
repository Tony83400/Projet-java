package fr.univcours.api.models;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("3d51e62c-241c-4225-98a7-bd7012db7cf3")
public class CommandeItem {
    @objid ("4b5d8e6d-acff-4028-b366-9de14aaddc9c")
    public int articleId;

    @objid ("f5ef3cca-ca76-45d4-8e0a-b1f6557b5c90")
    public int menuId;

    @objid ("5d4bde08-d232-4ea4-98d4-0dd12a8296fc")
    public int quantite;

    @objid ("df920f24-5fed-4d61-b091-ce0106c9a203")
    public int getArticleId() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.articleId;
    }

    @objid ("cbe53408-bfb1-4b3d-af7c-dec95a95a432")
    public void setArticleId(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.articleId = value;
    }

    @objid ("8d674f7f-59be-4032-b67d-e51c7bc03942")
    public int getMenuId() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.menuId;
    }

    @objid ("5c0c451e-7abd-448c-be71-ffd92e098369")
    public void setMenuId(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.menuId = value;
    }

    @objid ("6765ad13-c46d-4094-a349-7b4ad66ad19d")
    public int getQuantite() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.quantite;
    }

    @objid ("4427ff7d-10a0-4d72-a22a-a5c15a46e634")
    public void setQuantite(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.quantite = value;
    }

}
