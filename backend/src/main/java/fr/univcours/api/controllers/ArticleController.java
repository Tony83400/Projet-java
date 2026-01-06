package fr.univcours.api.controllers;

import fr.univcours.api.models.Article;
import fr.univcours.api.models.Menu;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.ArticleService;
import java.sql.SQLException;
import java.util.List;

public class ArticleController {

    private static final ArticleService articleService = new ArticleService();

    @OpenApi(
            summary = "Récupérer toutes les articles",
            operationId = "getAllArticle",
            path = "/articles",
            methods = HttpMethod.GET,
            tags = {"Article"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des articles", content = @OpenApiContent(from = Article.class))
            })
    public static void getAllArticle(Context ctx) {
        ctx.json(articleService.GetArticles());
    }

    @OpenApi(
            summary = "Trouve un article par id",
            operationId = "getArticleById",
            path = "/articles/{id}",
            methods = HttpMethod.GET,
            tags = {"Article"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du article", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Article trouvé", content = @OpenApiContent(from = Article.class)),
                    @OpenApiResponse(status = "404", description = "Article introuvable")
            })
    public static void getArticleById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Article rep = articleService.getArticleByid(id);
        if (rep == null) {
            ctx.status(404).json("Article introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }

    @OpenApi(
            summary = "Trouve les articles pour un menu par id",
            operationId = "getArticleForMenuById",
            path = "/articlesForMenuById/{id}",
            methods = HttpMethod.GET,
            tags = {"Article"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du article", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Articles trouvé", content = @OpenApiContent(from = Article.class)),
                    @OpenApiResponse(status = "404", description = "Articles introuvable")
            })
    public static void getArticleForMenuById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        List<Article> rep = articleService.getArticlesForMenu(id);
        if (rep == null) {
            ctx.status(404).json("Menu introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }
    @OpenApi(
            summary = "Trouve les articles pour une categorie par id",
            operationId = "getArticleForCategorieById",
            path = "/articlesForCategorieById/{id}",
            methods = HttpMethod.GET,
            tags = {"Article"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID du article", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Articles trouvé", content = @OpenApiContent(from = Article.class)),
                    @OpenApiResponse(status = "404", description = "Articles introuvable")
            })
    public static void getArticleForCategorieById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        List<Article> rep = articleService.getArticleForCategorie(id);
        if (rep == null) {
            ctx.status(404).json("Categorie introuvable");
        } else {
            ctx.status(200).json(rep);
        }
    }
}
