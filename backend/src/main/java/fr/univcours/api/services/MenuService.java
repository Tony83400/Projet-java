package fr.univcours.api.services;

import java.util.List;
import java.util.Map;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import fr.univcours.api.models.Menu;

@objid ("162769ce-b4a0-4377-85d9-a7c1c103e696")
public class MenuService {
    @objid ("2d9fb782-97a8-4aac-8844-dcf88c602213")
    public List<Menu> GetMenus(int langueId) {
        // TODO Auto-generated return
        return null;
    }

    @objid ("3f62f359-de0b-4b0d-a5b1-c40618a0eb9c")
    public Menu getMenuByid(int id, int langueId) {
        // TODO Auto-generated return
        return null;
    }

    @objid ("eac16a59-aa90-4564-a437-08544a39e37f")
    public List<Map<String, Object>> findCompositionForMenu(int menuId, int langueId) {
        // TODO Auto-generated return
        return null;
    }

}
