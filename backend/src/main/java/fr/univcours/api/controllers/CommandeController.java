package fr.univcours.api.controllers;

import fr.univcours.api.models.Commande;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.CommandeService;
import java.sql.SQLException;

public class CommandeController {

    private static final CommandeService commandeService = new CommandeService();

    @OpenApi(
            summary = "Récupérer toutes les commandes",
            operationId = "getAllCommandes",
            path = "/commandes",
            methods = HttpMethod.GET,
            tags = {"Commandes"}, // <--- Correction du tag
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des commandes", content = @OpenApiContent(from = Commande.class))
            })
    public static void getAll(Context ctx) {
        ctx.json(commandeService.GetCommandes());
    }

    @OpenApi(
            summary = "Supprimer une commande",
            operationId = "deleteCommande",
            path = "/commandes/{id}",
            methods = HttpMethod.DELETE,
            tags = {"Commandes"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la commande", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "204", description = "Commande supprimée"),
                    @OpenApiResponse(status = "404", description = "Commande introuvable")
            })
    public static void delete(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean estSupprime = commandeService.deleteCommande(id);
        if (estSupprime) {
            ctx.status(204);
        } else {
            ctx.status(404).json("Commande introuvable");
        }
    }

    @OpenApi(
            summary = "Ajouter une nouvelle commande",
            operationId = "addCommande",
            path = "/commandes",
            methods = HttpMethod.POST,
            tags = {"Commandes"},
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Commande.class), required = true, description = "Données de la commande"),
            responses = {
                    @OpenApiResponse(status = "201", description = "Commande créée avec succès"),
                    @OpenApiResponse(status = "400", description = "Données invalides")
            })
    public static void add(Context ctx) throws SQLException {
        Commande newCommande = ctx.bodyAsClass(Commande.class);
        Commande commandeRep = commandeService.addCommande(newCommande);
        if (commandeRep == null) {
            ctx.status(400).json("Impossible d'ajouter la commande");
        } else {
            ctx.status(201).json(commandeRep);
        }
    }

    @OpenApi(
            summary = "Trouve une commande par id",
            operationId = "getCommandeById",
            path = "/commandes/{id}",
            methods = HttpMethod.GET,
            tags = {"Commandes"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la commande", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Commande trouvée", content = @OpenApiContent(from = Commande.class)),
                    @OpenApiResponse(status = "404", description = "Commande introuvable")
            })
    public static void getById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Commande rep = commandeService.getCommandeByid(id);
        if (rep == null) {
            ctx.status(404).json("Commande introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }

    @OpenApi(
            summary = "Modifie une commande par id",
            operationId = "updateCommandeById",
            path = "/commandes/{id}",
            methods = HttpMethod.PUT,
            tags = {"Commandes"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la commande", required = true)
            },
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Commande.class), required = true, description = "Données mises à jour"),
            responses = {
                    @OpenApiResponse(status = "200", description = "Commande mise à jour", content = @OpenApiContent(from = Commande.class)),
                    @OpenApiResponse(status = "404", description = "Commande introuvable")
            })
    public static void updateById(Context ctx) {
        Commande newCommande = ctx.bodyAsClass(Commande.class);
        int id = Integer.parseInt(ctx.pathParam("id"));
        Commande rep = commandeService.updateById(id, newCommande);
        if (rep == null) {
            ctx.status(404).json("Commande introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }
}