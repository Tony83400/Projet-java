package fr.univcours.api;

import fr.univcours.api.controllers.UserController;
import fr.univcours.api.database.DatabaseSetup;
import fr.univcours.api.models.User;
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
            openApiConfig.getInfo().setTitle("API Users");
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
        app.get("/users", UserController::getAll);
        app.delete("/users/{id}", UserController::delete);
        app.post("/users", UserController::add);
        app.get("/users/{id}", UserController::getById);
        app.put("users/{id}", UserController::updateById);

    }
}