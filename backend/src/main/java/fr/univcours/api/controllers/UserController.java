package fr.univcours.api.controllers;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.models.User;
import fr.univcours.api.services.UserService;

import java.sql.SQLException;

public class UserController {

    private static final UserService userService = new UserService();

    @OpenApi(summary = "Récupérer tous les utilisateurs", operationId = "getAllUsers", path = "/users", methods = HttpMethod.GET, tags = {
            "Utilisateurs" }, responses = {
                    @OpenApiResponse(status = "200", description = "Liste des utilisateurs", content = @OpenApiContent(from = User.class))
            })
    public static void getAll(Context ctx) {
        ctx.json(userService.GetUsers());
    }

    @OpenApi(summary = "Supprimer un utilisateur", operationId = "deleteUser", path = "/users/{id}", methods = HttpMethod.DELETE, tags = {
            "Utilisateurs" }, pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'utilisateur", required = true)
            }, responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur supprimé"),
                    @OpenApiResponse(status = "400", description = "Utilisateur introuvable")
            })
    public static void delete(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean estSupprime = userService.deleteUser(id);

        if (estSupprime) {
            ctx.status(201);
        } else {
            ctx.status(400).json("Utilisateur introuvable");
        }
    }

    @OpenApi(summary = "Ajouter un nouvel utilisateur", operationId = "addUser", path = "/users", methods = HttpMethod.POST, tags = {
            "Utilisateurs" }, requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = User.class), required = true, description = "Données du nouvel utilisateur (JSON)"), responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur créé avec succès"),
                    @OpenApiResponse(status = "400", description = "Données invalides")
            })
    public static void add(Context ctx) throws SQLException {
        User newUser = ctx.bodyAsClass(User.class);
        User userRep = userService.addUser(newUser);
        if (userRep == null) {
            ctx.status(400).json("Impossible d'ajouter l'utilisateur a la base de donnée");
        }
        ctx.status(201).json(userRep);
    }

    @OpenApi(summary = "Trouve un utilisateur par id", operationId = "getUserById", path = "/users/{id}", methods = HttpMethod.GET, tags = {
            "Utilisateurs" }, pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'utilisateur", required = true)
            }, responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur trouvé"),
                    @OpenApiResponse(status = "400", description = "Utilisateur introuvable")
            })
    public static void getById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        User rep = userService.getUserByid(id);
        if (rep == null) {
            ctx.status(400).json("Utilisateur introuvable");
        }
        ctx.status(201).json(rep);
    }

    @OpenApi(summary = "Modifie un utilisateur par id", operationId = "updateUserById", path = "/users/{id}", methods = HttpMethod.PUT, tags = {
            "Utilisateurs" }, pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'utilisateur", required = true)
            }, requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = User.class), required = true, description = "Données du nouvel utilisateur (JSON)"), responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur trouvé"),
                    @OpenApiResponse(status = "400", description = "Utilisateur introuvable")
            })
    public static void updateById(Context ctx) {
        User newUser = ctx.bodyAsClass(User.class);
        int id = Integer.parseInt(ctx.pathParam("id"));
        User rep = userService.updateById(id, newUser);
        if (rep == null) {
            ctx.status(400).json("Utilisateur introuvable");
        }
        ctx.status(201).json(rep);
    }

}