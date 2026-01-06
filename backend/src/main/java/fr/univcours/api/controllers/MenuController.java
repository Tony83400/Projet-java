package fr.univcours.api.controllers;

import fr.univcours.api.models.Menu;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.MenuService;
import java.sql.SQLException;

public class MenuController {

    private static final MenuService menuService = new MenuService();


    @OpenApi(
            summary = "Récupérer tous les menus",
            operationId = "getAllMenu",
            path = "/menus", // <--- ICI C'ÉTAIT "/menu"
            methods = HttpMethod.GET,
            tags = {"Menus"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des menus", content = @OpenApiContent(from = Menu.class))
            })
    public static void getAllMenu(Context ctx) {
        ctx.json(menuService.GetMenus());
    }

    // CORRECTION : path = "/menus/{id}"
    @OpenApi(
            summary = "Trouve un menu par id",
            operationId = "getMenuById",
            path = "/menus/{id}", // <--- ICI C'ÉTAIT "/menu/{id}"
            methods = HttpMethod.GET,
            tags = {"Menus"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du menu", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Menu trouvé", content = @OpenApiContent(from = Menu.class)),
                    @OpenApiResponse(status = "404", description = "Menu introuvable")
            })
    public static void getMenuById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Menu rep = menuService.getMenuByid(id);
        if (rep == null) {
            ctx.status(404).json("Menu introuvable");
        } else {
            ctx.status(200).json(rep); // J'ai mis 200 au lieu de 201 (201 c'est pour la création)
        }
    }
}