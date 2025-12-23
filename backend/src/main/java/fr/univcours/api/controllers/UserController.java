package fr.univcours.api.controllers;


import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.models.User;
import fr.univcours.api.services.UserService;

import java.sql.SQLException;

public class UserController {

    private static final UserService userService = new UserService();

    @OpenApi(
            summary = "Récupérer tous les utilisateurs",
            operationId = "getAllUsers",
            path = "/users",
            methods = HttpMethod.GET,
            tags = {"Utilisateurs"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des utilisateurs", content = @OpenApiContent(from = User.class))
            }
    )
    public static void getAll(Context ctx) {
        ctx.json(userService.GetUsers());
    }

    @OpenApi(
            summary = "Supprimer un utilisateur",
            operationId = "deleteUser",
            path = "/users/{id}",
            methods = HttpMethod.DELETE,
            tags = {"Utilisateurs"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'utilisateur",required = true)
            },
            responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur supprimé"),
                    @OpenApiResponse(status = "400", description = "Utilisateur introuvable")
            }
    )
    public static void delete(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean estSupprime = userService.deleteUser(id);

        if (estSupprime) {
            ctx.status(201);
        } else {
            ctx.status(400).json("Utilisateur introuvable");
        }
    }
}