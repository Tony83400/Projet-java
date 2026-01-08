package fr.univcours.api;

import fr.univcours.api.controllers.ArticleController;
<<<<<<< Updated upstream
import fr.univcours.api.controllers.CategorieController;
=======
import fr.univcours.api.controllers.MenuController;
//import fr.univcours.api.controllers.CommandeController;
>>>>>>> Stashed changes
import fr.univcours.api.controllers.CommandeController;
import fr.univcours.api.controllers.MenuController;
import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.Article;
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

        // Menus
        app.get("/menus", MenuController::getAllMenu);
        app.get("/menus/{id}", MenuController::getMenuById);
        app.get("/menus/{id}/composition", MenuController::getCompositionForMenu);

<<<<<<< Updated upstream
        // Catégories
        app.get("/categories", CategorieController::getAllCategorie);
        app.get("/categories/{id}", CategorieController::getCategorieById);
        app.get("/categories/{id}/articles", ArticleController::getArticleForCategorieById);

        // Commandes
=======
        app.get("/articles", ArticleController::getAllArticle);
        app.get("/articles/{id}", ArticleController::getArticleById);

>>>>>>> Stashed changes
        app.get("/commandes", CommandeController::getAll);
        app.post("/commandes", CommandeController::add);
        app.get("/commandes/{id}", CommandeController::getById);
        app.get("/commandes/{id}/lignes", CommandeController::getLignesForCommande);
        app.post("/commandes/{id}/lignes", CommandeController::addLigneToCommande);
        app.get("/commandes/{id}/total", CommandeController::getTotalForCommande);

        // Articles
        app.get("/articles", ArticleController::getAllArticle);
        app.get("/articles/{id}", ArticleController::getArticleById);
        app.get("/articles/{id}/categories", ArticleController::getCategoriesForArticle);
    }
}