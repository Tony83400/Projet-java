package fr.univcours.api.controllers;

import fr.univcours.api.models.Categorie;
import fr.univcours.api.models.Menu;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.CategorieService;
import java.sql.SQLException;

public class CategorieController {


    private static final CategorieService categorieService = new CategorieService();

    @OpenApi(
            summary = "Récupérer toutes les categories",
            operationId = "getAllCategorie",
            path = "/categories",
            methods = HttpMethod.GET,
            tags = {"Categorie"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des categories", content = @OpenApiContent(from = Categorie.class))
            })
    public static void getAllCategorie(Context ctx) {
        ctx.json(categorieService.GetCategories());
    }

    @OpenApi(
            summary = "Trouve un categorie par id",
            operationId = "getCategorieById",
            path = "/categories/{id}",
            methods = HttpMethod.GET,
            tags = {"Categorie"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du categorie", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Categorie trouvé", content = @OpenApiContent(from = Categorie.class)),
                    @OpenApiResponse(status = "404", description = "Categorie introuvable")
            })
    public static void getCategorieById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Categorie rep = categorieService.getCategorieByid(id);
        if (rep == null) {
            ctx.status(404).json("Categorie introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }
}