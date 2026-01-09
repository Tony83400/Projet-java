package fr.univcours.api;

import fr.univcours.api.controllers.ArticleController;
import fr.univcours.api.controllers.CategorieController;
import fr.univcours.api.controllers.CommandeController;
import fr.univcours.api.controllers.MenuController;
import fr.univcours.api.database.DatabaseSetup;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location; // Import nécessaire pour les images
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration; // Attention aux imports OpenAPI qui changent souvent
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class Main {

    public static void main(String[] args) {
        // Démarrage de la base de données
        DatabaseSetup.start(); //

        Javalin app = Javalin.create(config -> {

            // 1. Configuration des fichiers statiques (IMAGES)
            // Cela permet d'accéder à http://localhost:8080/images/articles/nom_image.jpg
            config.staticFiles.add("/", Location.CLASSPATH);

            // 2. Configuration CORS (Nouvelle syntaxe pour Javalin 5.x)
            config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));

            // 3. Configuration OpenAPI (Documentation)
            config.plugins.register(new OpenApiPlugin(
                    new OpenApiPluginConfiguration()
                            .withDocumentationPath("/openapi")
                            .withDefinitionConfiguration((version, definition) -> definition
                                    .withOpenApiInfo((openApiInfo) -> {
                                        openApiInfo.setTitle("API Restaurant");
                                        openApiInfo.setVersion("1.0.0");
                                    })
                            )
            ));

            // 4. Configuration Swagger UI
            SwaggerConfiguration swaggerConfig = new SwaggerConfiguration();
            swaggerConfig.setUiPath("/"); // Doc accessible à la racine
            config.plugins.register(new SwaggerPlugin(swaggerConfig));

        }).start(8080);
        System.out.println("🚀 Serveur démarré sur http://localhost:8080");

        // --- ROUTES ---
// --- MENUS (Traduits) ---
        app.get("/menus/lang/{langue_id}", MenuController::getAllMenu);
        app.get("/menus/{id}/lang/{langue_id}", MenuController::getMenuById);
        app.get("/menus/{id}/composition/lang/{langue_id}", MenuController::getCompositionForMenu);

// --- CATÉGORIES (Traduites) ---
        app.get("/categories/lang/{langue_id}", CategorieController::getAllCategorie);
        app.get("/categories/{id}/lang/{langue_id}", CategorieController::getCategorieById);
// Récupérer les articles d'une catégorie (via ArticleController)
        app.get("/categories/{id}/articles/lang/{langue_id}", ArticleController::getArticleForCategorieById);

// --- ARTICLES (Traduits) ---
        app.get("/articles/lang/{langue_id}", ArticleController::getAllArticle);
        app.get("/articles/{id}/lang/{langue_id}", ArticleController::getArticleById);
// Récupérer les catégories d'un article
        app.get("/articles/{id}/categories/lang/{langue_id}", ArticleController::getCategoriesForArticle);

// --- COMMANDES (Standard - Pas de changement de langue appliqué) ---
        app.get("/commandes", CommandeController::getAll);
        app.post("/commandes", CommandeController::add);
        app.get("/commandes/{id}", CommandeController::getById);
        app.get("/commandes/{id}/lignes", CommandeController::getLignesForCommande);
        app.post("/commandes/{id}/lignes", CommandeController::addLigneToCommande);
        app.get("/commandes/{id}/total", CommandeController::getTotalForCommande);
    }
}