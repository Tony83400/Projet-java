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
            path = "/menus",                // <--- Doit être strictement identique au Main
            methods = HttpMethod.GET,
            tags = {"Menus"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des menus", content = @OpenApiContent(from = Menu.class))
            })
    public static void getAllMenu(Context ctx) {
        ctx.json(menuService.GetMenus());
    }

    // DOIT MATCHER: app.get("/menus/{id}", ...)
    @OpenApi(
            summary = "Trouve un menu par id",
            operationId = "getMenuById",
            path = "/menus/{id}",           // <--- Doit être strictement identique au Main
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
            ctx.status(200).json(rep);
        }
    }

    @OpenApi(
            summary = "Récupérer la composition d'un menu",
            operationId = "getCompositionForMenu",
            path = "/menus/{id}/composition",
            methods = HttpMethod.GET,
            tags = {"Menus"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du menu", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Composition du menu"),
                    @OpenApiResponse(status = "404", description = "Menu introuvable")
            })
    public static void getCompositionForMenu(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (menuService.getMenuByid(id) == null) {
            ctx.status(404).json("Menu introuvable");
            return;
        }
        ctx.json(menuService.findCompositionForMenu(id));
    }
}