package fr.univcours.api.controllers;

import fr.univcours.api.models.Commande;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.CommandeService;

import java.sql.SQLException;

public class CommandeController {

    private static final CommandeService commandeService = new CommandeService();

    @OpenApi(summary = "Récupérer tous les utilisateurs", operationId = "getAllCommandes", path = "/commandes", methods = HttpMethod.GET, tags = {
            "Utilisateurs" }, responses = {
                    @OpenApiResponse(status = "200", description = "Liste des utilisateurs", content = @OpenApiContent(from = Commande.class))
            })
    public static void getAll(Context ctx) {
        ctx.json(commandeService.GetCommandes());
    }

    @OpenApi(summary = "Supprimer un utilisateur", operationId = "deleteCommande", path = "/commandes/{id}", methods = HttpMethod.DELETE, tags = {
            "Utilisateurs" }, pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'utilisateur", required = true)
            }, responses = {
                    @OpenApiResponse(status = "204", description = "Utilisateur supprimé"),
                    @OpenApiResponse(status = "404", description = "Utilisateur introuvable")
            })
    public static void delete(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean estSupprime = commandeService.deleteCommande(id);

        if (estSupprime) {
            ctx.status(204); 
        } else {
            ctx.status(404).json("Utilisateur introuvable");
        }
    }

    @OpenApi(summary = "Ajouter un nouvel utilisateur", operationId = "addCommande", path = "/commandes", methods = HttpMethod.POST, tags = {
            "Utilisateurs" }, requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Commande.class), required = true, description = "Données du nouvel utilisateur (JSON)"), responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur créé avec succès"),
                    @OpenApiResponse(status = "400", description = "Données invalides")
            })
    public static void add(Context ctx) throws SQLException {
        Commande newCommande = ctx.bodyAsClass(Commande.class);
        Commande commandeRep = commandeService.addCommande(newCommande);
        if (commandeRep == null) {
            ctx.status(400).json("Impossible d'ajouter l'utilisateur a la base de donnée");
        } else {
            ctx.status(201).json(commandeRep);
        }
    }

    @OpenApi(summary = "Trouve un utilisateur par id", operationId = "getCommandeById", path = "/commandes/{id}", methods = HttpMethod.GET, tags = {
            "Utilisateurs" }, pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'utilisateur", required = true)
            }, responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur trouvé"),
                    @OpenApiResponse(status = "404", description = "Utilisateur introuvable")
            })
    public static void getById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Commande rep = commandeService.getCommandeByid(id);
        if (rep == null) {
            ctx.status(404).json("Utilisateur introuvable");
        } else {
            ctx.status(201).json(rep);
        }
    }

    @OpenApi(summary = "Modifie un utilisateur par id", operationId = "updateCommandeById", path = "/commandes/{id}", methods = HttpMethod.PUT, tags = {
            "Utilisateurs" }, pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'utilisateur", required = true)
            }, requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Commande.class), required = true, description = "Données du nouvel utilisateur (JSON)"), responses = {
                    @OpenApiResponse(status = "201", description = "Utilisateur trouvé"),
                    @OpenApiResponse(status = "404", description = "Utilisateur introuvable")
            })
    public static void updateById(Context ctx) {
        Commande newCommande = ctx.bodyAsClass(Commande.class);
        int id = Integer.parseInt(ctx.pathParam("id"));
        Commande rep = commandeService.updateById(id, newCommande);
        if (rep == null) {
            ctx.status(404).json("Utilisateur introuvable");
        } else {
            ctx.status(201).json(rep);
        }
    }

}