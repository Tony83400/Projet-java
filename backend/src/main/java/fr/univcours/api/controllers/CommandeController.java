package fr.univcours.api.controllers;

import fr.univcours.api.models.Commande;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.CommandeService;
import java.sql.SQLException;


import fr.univcours.api.models.CommandeRequest;

public class CommandeController {

    private static final CommandeService commandeService = new CommandeService();

    @OpenApi(
            summary = "Récupérer toutes les commandes",
            operationId = "getAllCommandes",
            path = "/commandes",
            methods = HttpMethod.GET,
            tags = {"Commandes"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des commandes", content = @OpenApiContent(from = Commande.class))
            })
    public static void getAll(Context ctx) {
        ctx.json(commandeService.GetCommandes());
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
            summary = "Ajouter une nouvelle commande",
            operationId = "addCommande",
            path = "/commandes",
            methods = HttpMethod.POST,
            tags = {"Commandes"},
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = CommandeRequest.class), required = true, description = "Données de la commande"),
            responses = {
                    @OpenApiResponse(status = "201", description = "Commande créée avec succès", content = @OpenApiContent(from = Commande.class)),
                    @OpenApiResponse(status = "400", description = "Données invalides")
            })
    public static void add(Context ctx) {
        try {
            CommandeRequest newCommandeRequest = ctx.bodyAsClass(CommandeRequest.class);
            Commande commandeRep = commandeService.addCommande(newCommandeRequest);
            ctx.status(201).json(commandeRep);
        } catch (Exception e) {
            ctx.status(400).json("Impossible d'ajouter la commande: " + e.getMessage());
        }
    }

    @OpenApi(
            summary = "Récupérer les lignes d'une commande",
            operationId = "getLignesForCommande",
            path = "/commandes/{id}/lignes",
            methods = HttpMethod.GET,
            tags = {"Commandes"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la commande", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des lignes de la commande"),
                    @OpenApiResponse(status = "404", description = "Commande introuvable")
            })
    public static void getLignesForCommande(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (commandeService.getCommandeByid(id) == null) {
            ctx.status(404).json("Commande introuvable");
            return;
        }
        ctx.json(commandeService.findLignesForCommande(id));
    }

    @OpenApi(
            summary = "Récupérer le prix total d'une commande",
            operationId = "getTotalForCommande",
            path = "/commandes/{id}/total",
            methods = HttpMethod.GET,
            tags = {"Commandes"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la commande", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Prix total de la commande", content = @OpenApiContent(from = java.math.BigDecimal.class)),
                    @OpenApiResponse(status = "404", description = "Commande introuvable")
            })
    public static void getTotalForCommande(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (commandeService.getCommandeByid(id) == null) {
            ctx.status(404).json("Commande introuvable");
            return;
        }
        java.math.BigDecimal total = commandeService.calculateTotalForCommande(id);
        ctx.json(java.util.Collections.singletonMap("total", total));
    }
}