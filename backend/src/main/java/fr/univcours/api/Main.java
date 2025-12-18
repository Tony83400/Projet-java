package fr.univcours.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.javalin.Javalin;

/**
 * Classe principale qui démarre le serveur API
 */
public class Main {

    private static final UserServiceImpl userService = new UserServiceImpl();

    public static void main(String[] args) {
        // Créer et configurer l'application Javalin
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
        }).start(7000);

        // Message de démarrage
        System.out.println("🚀 Serveur démarré sur http://localhost:7000");
        System.out.println("📋 Essayez : http://localhost:7000/users");

        // Route GET /users - Récupère tous les utilisateurs
        app.get("/users", ctx -> {
            ctx.json(userService.GetUsers());
        });

        // Route GET /users/:id - Récupère un utilisateur par ID
        app.get("/users/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            userService.getUserById(id)
                    .ifPresentOrElse(
                            user -> ctx.json(user),
                            () -> ctx.status(404).result("Utilisateur non trouvé"));
        });

        // Route POST /users - Ajoute un utilisateur
        app.post("/users", ctx -> {
            User newUser = ctx.bodyAsClass(User.class);
            if (newUser.getAge() < 0) {
                ctx.status(400).json("L'age doit être positif");
            }
            User created = userService.addUser(newUser);
            ctx.status(201).json(created);
        });

        // Route GET / - Page d'accueil
        app.get("/", ctx -> {
            ctx.html(getWelcomeHTML());
        });

        app.put("/users/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));

            User editedUser = ctx.bodyAsClass(User.class);
            Optional<User> newUser = userService.editUser(id, editedUser);
            if (newUser.isPresent()) {
                ctx.status(201).json(newUser);
            } else {
                ctx.status(400).json("Utilisateur introuvable");
            }
        });

        app.delete("/users/{id}", (ctx) -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Optional<User> deleteUser = userService.deleteUser(id);
            if (deleteUser.isPresent()) {
                ctx.status(201).json(deleteUser);
            } else {
                ctx.status(400).json("Utilisateur introuvable");
            }
        });

        app.get("/users/{name}", ctx -> {
            String name = ctx.pathParam("name");
            Optional<User> user = userService.getUserByName(name);
            if (user.isPresent()) {
                ctx.status(201).json(user);
            } else {
                ctx.status(400).json("Utilisateur introuvable");
            }

        });
    }

    /**
     * Charge la page HTML d'accueil depuis les ressources
     */
    private static String getWelcomeHTML() {
        try {
            InputStream inputStream = Main.class.getClassLoader()
                    .getResourceAsStream("welcome.html");

            if (inputStream == null) {
                return "<h1>Erreur : Page non trouvée</h1>";
            }

            return new String(inputStream.readAllBytes(),
                    StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return "<h1>Erreur de chargement</h1>";
        }
    }
}
