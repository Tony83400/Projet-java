package fr.univcours.api;

import fr.univcours.api.controllers.ArticleController;
import fr.univcours.api.controllers.MenuController;
//import fr.univcours.api.controllers.CommandeController;
import fr.univcours.api.controllers.CommandeController;
import fr.univcours.api.controllers.CategorieController;
import fr.univcours.api.database.DatabaseSetup;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            // Configuration de la Db

            DatabaseSetup.start();
            // 1. Configuration CORS
            config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));

            // 2. Configuration OpenAPI
            OpenApiConfiguration openApiConfig = new OpenApiConfiguration();
            openApiConfig.getInfo().setTitle("API Commandes");
            openApiConfig.getInfo().setVersion("1.0.0");
            openApiConfig.getInfo().setDescription("Documentation de l'API");

            // On enregistre le plugin avec la config qu'on vient de créer
            config.plugins.register(new OpenApiPlugin(openApiConfig));

            // 3. Configuration Swagger UI (Visuel)
            SwaggerConfiguration swaggerConfig = new SwaggerConfiguration();
            swaggerConfig.setUiPath("/"); // L'URL pour accéder à la doc

            // On enregistre le plugin Swagger
            config.plugins.register(new SwaggerPlugin(swaggerConfig));

        }).start(7000);

        System.out.println("🚀 Serveur démarré sur http://localhost:7000");

        // Routes
        app.get("/menus", MenuController::getAllMenu);
        app.get("/menus/{id}", MenuController::getMenuById);

        app.get("/categories", CategorieController::getAllCategorie);
        app.get("/categories/{id}", CategorieController::getCategorieById);

        app.get("/commandes", CommandeController::getAll);
        app.post("/commandes", CommandeController::add);
        app.get("/commandes/{id}", CommandeController::getById);
        app.delete("/commandes/{id}", CommandeController::delete);
        app.put("/commandes/{id}", CommandeController::updateById);
        app.get("/nextCommandeId",CommandeController::getNextCommandeId);
        app.get("/commandes/{commande_id}/total", CommandeController::getTotal);
        app.get("/commandes/{commande_id}/commandes",CommandeController::getByNumero);

        app.get("/articles", ArticleController::getAllArticle);
        app.get("/articles/{id}",ArticleController::getArticleById);
        app.get("/menus/{id}/articles",ArticleController::getArticleForMenuById);
        app.get("/categories/{id}/articles",ArticleController::getArticleForCategorieById);

    }
}