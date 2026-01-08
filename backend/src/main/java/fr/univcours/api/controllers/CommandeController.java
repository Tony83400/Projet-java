package fr.univcours.api.controllers;

import fr.univcours.api.impl.CommandeServiceImpl;
import fr.univcours.api.models.Commande;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.CommandeService;
import java.sql.SQLException;
import java.util.Collections; // Ajouté pour singletonMap

import fr.univcours.api.models.CommandeItem;
import fr.univcours.api.models.LigneCommande;

public class CommandeController {

    private static final CommandeServiceImpl commandeService = new CommandeServiceImpl();

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
            summary = "Initialiser une nouvelle commande vide",
            operationId = "initCommande",
            path = "/commandes",
            methods = HttpMethod.POST,
            tags = {"Commandes"},
            responses = {
                    @OpenApiResponse(status = "201", description = "Commande initialisée avec succès", content = @OpenApiContent(from = Commande.class)),
                    @OpenApiResponse(status = "400", description = "Erreur lors de l'initialisation")
            })
    public static void add(Context ctx) {
        try {
            Commande commandeRep = commandeService.createEmptyCommande();
            ctx.status(201).json(commandeRep);
        } catch (Exception e) {
            ctx.status(400).json("Impossible d'initialiser la commande: " + e.getMessage());
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

    // --- C'est ici que se trouve le changement principal ---
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
                    // CORRECTION : from = Float.class (et non java.math.float)
                    @OpenApiResponse(status = "200", description = "Prix total de la commande", content = @OpenApiContent(from = Float.class)),
                    @OpenApiResponse(status = "404", description = "Commande introuvable")
            })
    public static void getTotalForCommande(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (commandeService.getCommandeByid(id) == null) {
            ctx.status(404).json("Commande introuvable");
            return;
        }

        // CORRECTION : float primitif simple
        float total = commandeService.calculateTotalForCommande(id);

        // Java va automatiquement transformer le float en Float pour la map
        ctx.json(Collections.singletonMap("total", total));
    }
    // ------------------------------------------------------

    @OpenApi(
            summary = "Ajouter un article à une commande existante",
            operationId = "addLigneToCommande",
            path = "/commandes/{id}/lignes",
            methods = HttpMethod.POST,
            tags = {"Commandes"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la commande à modifier", required = true)
            },
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = CommandeItem.class), required = true, description = "Article ou menu à ajouter"),
            responses = {
                    @OpenApiResponse(status = "201", description = "Article ajouté avec succès", content = @OpenApiContent(from = LigneCommande.class)),
                    @OpenApiResponse(status = "400", description = "Données invalides"),
                    @OpenApiResponse(status = "404", description = "Commande introuvable")
            })
    public static void addLigneToCommande(Context ctx) {
        try {
            int commandeId = Integer.parseInt(ctx.pathParam("id"));
            if (commandeService.getCommandeByid(commandeId) == null) {
                ctx.status(404).json("Commande introuvable");
                return;
            }
            CommandeItem newItem = ctx.bodyAsClass(CommandeItem.class);
            LigneCommande nouvelleLigne = commandeService.addLigneToCommande(commandeId, newItem);
            if (nouvelleLigne != null) {
                ctx.status(201).json(nouvelleLigne);
            } else {
                ctx.status(400).json("Impossible d'ajouter l'article à la commande.");
            }
        } catch (Exception e) {
            ctx.status(400).json("Impossible d'ajouter l'article: " + e.getMessage());
        }
    }
}