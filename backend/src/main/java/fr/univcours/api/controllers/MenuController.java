package fr.univcours.api.controllers;

import fr.univcours.api.impl.MenuServiceImpl;
import fr.univcours.api.models.Menu;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import java.sql.SQLException;

public class MenuController {

    private static final MenuServiceImpl menuService = new MenuServiceImpl();


    @OpenApi(
            summary = "Récupérer tous les menus dans une langue donnée",
            operationId = "getAllMenu",
            path = "/menus/lang/{langue_id}", // Nouveau chemin avec langue
            methods = HttpMethod.GET,
            tags = {"Menus"},
            pathParams = {
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des menus", content = @OpenApiContent(from = Menu.class))
            })
    public static void getAllMenu(Context ctx) {
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));
        ctx.json(menuService.GetMenus(langueId));
    }

    @OpenApi(
            summary = "Trouve un menu par id et langue",
            operationId = "getMenuById",
            path = "/menus/{id}/lang/{langue_id}", // Nouveau chemin combiné
            methods = HttpMethod.GET,
            tags = {"Menus"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du menu", required = true),
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Menu trouvé", content = @OpenApiContent(from = Menu.class)),
                    @OpenApiResponse(status = "404", description = "Menu introuvable")
            })
    public static void getMenuById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));

        Menu rep = menuService.getMenuByid(id, langueId);
        if (rep == null) {
            ctx.status(404).json("Menu introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }

    @OpenApi(
            summary = "Récupérer la composition d'un menu avec langue",
            operationId = "getCompositionForMenu",
            path = "/menus/{id}/composition/lang/{langue_id}",
            methods = HttpMethod.GET,
            tags = {"Menus"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du menu", required = true),
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Composition du menu"),
                    @OpenApiResponse(status = "404", description = "Menu introuvable")
            })
    public static void getCompositionForMenu(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));

        // On vérifie d'abord si le menu existe
        if (menuService.getMenuByid(id, langueId) == null) {
            ctx.status(404).json("Menu introuvable");
            return;
        }
        ctx.json(menuService.findCompositionForMenu(id, langueId));
    }
}