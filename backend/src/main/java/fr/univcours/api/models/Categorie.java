package fr.univcours.api.models;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("1a69f495-6420-416e-ac4d-255c4f896d30")
public class Categorie {
    @objid ("43160ac5-4689-46fe-a339-5316d53c5030")
    public int categorie_id;

    @objid ("c4354838-5d0c-47f4-aca8-6ffe55b5998f")
    public String nom;

    @objid ("49e8db43-0864-4d93-9997-d6b4c1058b7e")
    public String description;

    @objid ("17be5982-c364-4e09-ac30-c1ea6bb3d68f")
    public int getCategorie_id() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.categorie_id;
    }

    @objid ("c5fb5ab7-1dde-4fa1-bbaa-86db502d570f")
    public void setCategorie_id(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.categorie_id = value;
    }

    @objid ("63c2ee7e-9358-41b9-a2ff-8b5bd56ddd44")
    public String getNom() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.nom;
    }

    @objid ("df036b2c-f222-4b99-a11c-9fc17322dd0e")
    public void setNom(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.nom = value;
    }

    @objid ("92934452-d145-4525-a2e7-4f1a5e010bff")
    public String getDescription() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.description;
    }

    @objid ("95c15574-e34a-4559-b886-5aa90247dee3")
    public void setDescription(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.description = value;
    }

}
