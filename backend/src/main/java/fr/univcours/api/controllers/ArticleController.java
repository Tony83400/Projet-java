package fr.univcours.api.controllers;

import fr.univcours.api.impl.ArticleServiceImpl;
import fr.univcours.api.models.Article;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import java.sql.SQLException;
import java.util.List;

public class ArticleController {

    private static final ArticleServiceImpl articleService = new ArticleServiceImpl();

    @OpenApi(
            summary = "Récupérer toutes les articles avec langue",
            operationId = "getAllArticle",
            path = "/articles/lang/{langue_id}",
            methods = HttpMethod.GET,
            tags = {"Article"},
            pathParams = {
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des articles", content = @OpenApiContent(from = Article.class))
            })
    public static void getAllArticle(Context ctx) {
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));
        ctx.json(articleService.GetArticles(langueId));
    }

    @OpenApi(
            summary = "Trouve un article par id et langue",
            operationId = "getArticleById",
            path = "/articles/{id}/lang/{langue_id}",
            methods = HttpMethod.GET,
            tags = {"Article"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'article", required = true),
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Article trouvé", content = @OpenApiContent(from = Article.class)),
                    @OpenApiResponse(status = "404", description = "Article introuvable")
            })
    public static void getArticleById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));

        Article rep = articleService.getArticleByid(id, langueId);
        if (rep == null) {
            ctx.status(404).json("Article introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }

    @OpenApi(
            summary = "Trouve les articles pour une categorie par id et langue",
            operationId = "getArticleForCategorieById",
            path = "/categories/{id}/articles/lang/{langue_id}",
            methods = HttpMethod.GET,
            tags = {"Categorie"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de la catégorie", required = true),
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Articles trouvés", content = @OpenApiContent(from = Article.class)),
                    @OpenApiResponse(status = "404", description = "Catégorie introuvable")
            })
    public static void getArticleForCategorieById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));

        List<Article> rep = articleService.getArticleForCategorie(id, langueId);
        if (rep == null) {
            ctx.status(404).json("Categorie introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }

    @OpenApi(
            summary = "Récupérer les catégories d'un article avec langue",
            operationId = "getCategoriesForArticle",
            path = "/articles/{id}/categories/lang/{langue_id}",
            methods = HttpMethod.GET,
            tags = {"Article"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'article", required = true),
                    @OpenApiParam(name = "langue_id", type = Integer.class, description = "ID langue (1=FR, 2=EN)", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des catégories de l'article"),
                    @OpenApiResponse(status = "404", description = "Article introuvable")
            })
    public static void getCategoriesForArticle(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        int langueId = Integer.parseInt(ctx.pathParam("langue_id"));

        // Petite vérif si l'article existe (optionnel mais recommandé)
        if (articleService.getArticleByid(id, langueId) == null) {
            ctx.status(404).json("Article introuvable");
            return;
        }
        ctx.json(articleService.findCategoriesForArticle(id, langueId));
    }
}