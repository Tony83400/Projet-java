package fr.univcours.api.models;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("799e287c-8b44-43ab-9551-95127c096ce9")
public class Article {
    @objid ("7509b8c1-7104-4323-96b5-935e970b5f08")
    public int article_id;

    @objid ("24f509ab-e7d9-4551-a916-2286dadbb008")
    public String nom;

    @objid ("6bfdb43d-41b9-47cd-be6b-50798cc88854")
    public String description;

    @objid ("80c1fa5d-576f-428d-a0f8-ce8f1cf46e8b")
    public float prix;

    @objid ("b48aa1e5-2bf0-4577-ae70-1107920f4646")
    public int stock;

    @objid ("746b1287-1f86-42e7-911e-0e51046e9b5c")
    public String image_url;

    @objid ("8675ca26-d83c-425b-861b-201cff3a34d2")
    public int getArticle_id() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.article_id;
    }

    @objid ("e6062c3a-ba02-4c86-97af-6c66ac108d21")
    public void setArticle_id(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.article_id = value;
    }

    @objid ("19eee917-ee5e-4f5d-bc24-e4657ce2b1ca")
    public void setNom(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.nom = value;
    }

    @objid ("53c42967-a8f1-437b-b0aa-c3488f64a0e6")
    public String getNom() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.nom;
    }

    @objid ("df5686cf-0c46-489d-9957-63be09e58e05")
    public String getDescription() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.description;
    }

    @objid ("bef2ae43-5a7a-4f5c-b62b-e8355c096c2a")
    public void setDescription(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.description = value;
    }

    @objid ("6bdd3db5-720e-4483-ab25-7acc9f28c0fe")
    public float getPrix() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.prix;
    }

    @objid ("8b601e06-cdfa-49af-bc64-9190f0b4a86c")
    public void setPrix(float value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.prix = value;
    }

    @objid ("bfc4db26-d548-4fbb-b7b3-0681b6308b6c")
    public int getStock() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.stock;
    }

    @objid ("c08f962a-3dbd-4523-8285-2b1ce723609f")
    public void setStock(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.stock = value;
    }

    @objid ("0c3a8049-2846-473e-b8b7-36690b1317aa")
    public String getImage_url() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.image_url;
    }

    @objid ("0289ddd1-1b84-49ea-8782-7a5d63a01ec6")
    public void setImage_url(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.image_url = value;
    }

}
