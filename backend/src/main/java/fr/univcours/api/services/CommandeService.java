package fr.univcours.api.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import fr.univcours.api.models.Commande;
import fr.univcours.api.models.CommandeItem;
import fr.univcours.api.models.LigneCommande;

@objid ("11a87015-b3ac-4398-922e-e122e25eba70")
public class CommandeService {
    @objid ("a66e35ee-92cc-42f3-a4fa-027563233e86")
    public List<Commande> GetCommandes() {
        // TODO Auto-generated return
        return null;
    }

    @objid ("4effaf29-0640-49ff-9f64-b8ceba428bd4")
    public Commande getCommandeByid(int id) throws SQLException {
        // TODO Auto-generated return
        return null;
    }

    @objid ("95019c21-55a0-4c9b-8bcd-644929822bd4")
    public List<LigneCommande> findLignesForCommande(int commandeId) throws SQLException {
        // TODO Auto-generated return
        return null;
    }

    @objid ("764e2f74-80e7-442f-996b-5365ba0d946a")
    public Commande createEmptyCommande() {
        // TODO Auto-generated return
        return null;
    }

    @objid ("31fd066f-7809-4d60-bbc8-dd8ec295bb06")
    public float calculateTotalForCommande(int commandeId) throws SQLException {
        // TODO Auto-generated return
        return 0f;
    }

    @objid ("797a70d7-f7c0-4688-a79b-87999d2c5b3c")
    public LigneCommande addLigneToCommande(int commandeId, CommandeItem item) throws SQLException {
        // TODO Auto-generated return
        return null;
    }

    @objid ("77b58a66-e918-48ed-b2c2-0e4b9dc612da")
    private int getNextNumeroTicket(Connection conn) throws SQLException {
        // TODO Auto-generated return
        return 0;
    }

}
