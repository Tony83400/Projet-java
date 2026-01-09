package fr.univcours.api.models;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("ad64ca87-e1cb-4eaf-be5e-b33c05ca080d")
public class Menu {
    @objid ("c2e2754f-9b3f-471b-bdc6-832e13550622")
    public int menu_id;

    @objid ("6f22f8fc-a490-40e2-a84d-80739fee7b81")
    public int article_id;

    @objid ("eb80e1ce-8c7e-4878-b93d-8fb2839a5421")
    public float prix;

    @objid ("673aab66-4621-4124-aaa3-50302f05a5d8")
    public String image_url;

    @objid ("10570bbc-9d1b-4d6a-bc5c-b740e439d642")
    public String nom;

    @objid ("617ff497-2951-4f48-a76a-224470aea021")
    public String description;

    @objid ("553720fc-5056-4e07-ae7b-275e612f4bff")
    public int getMenu_id() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.menu_id;
    }

    @objid ("f5352051-1f83-492b-adf7-4a335f39e9e5")
    public void setMenu_id(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.menu_id = value;
    }

    @objid ("e1a9a1ca-46be-412a-ad9f-059d44dcaaae")
    public int getArticle_id() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.article_id;
    }

    @objid ("70b44b63-4ef4-4d61-9041-2c317c0fb6b1")
    public void setArticle_id(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.article_id = value;
    }

    @objid ("8d875c65-b470-4006-9ff7-268fa7be8b2f")
    public float getPrix() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.prix;
    }

    @objid ("f1581ef9-a099-44ca-bf81-48b2fa58617c")
    public void setPrix(float value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.prix = value;
    }

    @objid ("67171e95-052c-4b8d-8cd7-fa2146c8968b")
    public String getImage_url() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.image_url;
    }

    @objid ("47418dd3-8904-4f39-bcd6-46c28700db7f")
    public void setImage_url(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.image_url = value;
    }

    @objid ("e5e04662-f48d-4452-9a64-71105b293ec0")
    public String getNom() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.nom;
    }

    @objid ("36bee53c-8f32-4997-9e23-d1ba211ffaf0")
    public void setNom(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.nom = value;
    }

    @objid ("a6c03bf5-8ea3-4317-ab03-7b8210b5d09b")
    public String getDescription() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.description;
    }

    @objid ("f0047dbe-b9d8-4f25-8f43-fe6063c49db9")
    public void setDescription(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.description = value;
    }

}
