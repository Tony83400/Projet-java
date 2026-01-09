package fr.univcours.api.models;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("9568b2d5-af11-4217-86e0-92383682c2ae")
public class Commande {
    @objid ("c689ccf9-7d14-4693-99be-b520a09d2022")
    public int commande_id;

    @objid ("90a2b5a2-33a0-4c9c-9299-c9526a0cc80f")
    public String statut;

    @objid ("504bb813-1041-4882-ac2a-8f8cc61873c0")
    public int numero_ticket;

    @objid ("eeb4b8df-a64b-45ff-9e20-33742e0ee885")
    public int getCommande_id() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.commande_id;
    }

    @objid ("95e20f6b-56ab-45c4-aa51-cbb0d9d46e31")
    public void setCommande_id(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.commande_id = value;
    }

    @objid ("8f94f552-64cb-4d96-97e5-af92ecebe8da")
    public String getStatut() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.statut;
    }

    @objid ("1e41c31b-175b-4fd2-8409-5f6879a6a0e8")
    public void setStatut(String value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.statut = value;
    }

    @objid ("a52b599b-108b-4ea8-a6b2-4cabe1967c70")
    public int getNumero_ticket() {
        // Automatically generated method. Please delete this comment before entering specific code.
        return this.numero_ticket;
    }

    @objid ("a2c8afe0-872b-4317-9668-b8d34bdc662c")
    public void setNumero_ticket(int value) {
        // Automatically generated method. Please delete this comment before entering specific code.
        this.numero_ticket = value;
    }

}
