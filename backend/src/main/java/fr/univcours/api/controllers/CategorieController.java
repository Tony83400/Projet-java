package fr.univcours.api.controllers;

import fr.univcours.api.impl.CategorieServiceImpl;
import fr.univcours.api.models.Categorie;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import java.sql.SQLException;

public class CategorieController {

    private static final CategorieServiceImpl categorieService = new CategorieServiceImpl();

    @OpenApi(
            summary = "Récupérer toutes les categories dans une langue",
            operationId = "getAllCategorie",
            path = "/categories/lang/{langue_id}",
            methods = HttpMethod.GET,
            tags = {"Categorie"},
            pathParams = {
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des categories", content = @OpenApiContent(from = Categorie.class))
            })
    public static void getAllCategorie(Context ctx) {
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));
        ctx.json(categorieService.GetCategories(langueId));
    }

    @OpenApi(
            summary = "Trouve une categorie par id et langue",
            operationId = "getCategorieById",
            path = "/categories/{id}/lang/{langue_id}",
            methods = HttpMethod.GET,
            tags = {"Categorie"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la categorie", required = true),
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Categorie trouvée", content = @OpenApiContent(from = Categorie.class)),
                    @OpenApiResponse(status = "404", description = "Categorie introuvable")
            })
    public static void getCategorieById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));

        Categorie rep = categorieService.getCategorieByid(id, langueId);
        if (rep == null) {
            ctx.status(404).json("Categorie introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }
}