package fr.univcours.api.controllers;

import fr.univcours.api.models.Article;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import fr.univcours.api.models.Menu;
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
import io.javalin.http.Context;
import io.javalin.openapi.*;
import fr.univcours.api.services.ArticleService;
import java.sql.SQLException;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import java.util.List;private static final ArticleService articleService=new ArticleService();

@OpenApi(summary="Récupérer toutes les articles",operationId="getAllArticle",path="/articles",methods=HttpMethod.GET,tags={"Article"},responses={@OpenApiResponse(status="200",description="Liste des articles",content=@OpenApiContent(from=Article.class))})public static void getAllArticle(Context ctx){ctx.json(articleService.GetArticles());}

@OpenApi(summary="Trouve un article par id",operationId="getArticleById",path="/articles/{id}",methods=HttpMethod.GET,tags={"Article"},pathParams={@OpenApiParam(name="id",type=Integer.class,description="ID du article",required=true)},responses={@OpenApiResponse(status="200",description="Article trouvé",content=@OpenApiContent(from=Article.class)),@OpenApiResponse(status="404",description="Article introuvable")})public static void getArticleById(Context ctx)throws SQLException{int id=Integer.parseInt(ctx.pathParam("id"));Article rep=articleService.getArticleByid(id);if(rep==null){ctx.status(404).json("Article introuvable");}else{ctx.status(200).json(rep);}}

@OpenApi(summary="Trouve les articles pour une categorie par id",operationId="getArticleForCategorieById",path="/categories/{id}/articles",methods=HttpMethod.GET,tags={"Categorie"},pathParams={@OpenApiParam(name="id",type=Integer.class,description="ID du article",required=true)},responses={@OpenApiResponse(status="200",description="Articles trouvé",content=@OpenApiContent(from=Article.class)),@OpenApiResponse(status="404",description="Articles introuvable")})public static void getArticleForCategorieById(Context ctx)throws SQLException{int id=Integer.parseInt(ctx.pathParam("id"));List<Article>rep=articleService.getArticleForCategorie(id);if(rep==null){ctx.status(404).json("Categorie introuvable");}else{ctx.status(200).json(rep);}}

@OpenApi(summary="Récupérer les catégories d'un article",operationId="getCategoriesForArticle",path="/articles/{id}/categories",methods=HttpMethod.GET,tags={"Article"},pathParams={@OpenApiParam(name="id",type=Integer.class,description="ID de l'article",required=true)},responses={@OpenApiResponse(status="200",description="Liste des catégories de l'article"),@OpenApiResponse(status="404",description="Article introuvable")})public static void getCategoriesForArticle(Context ctx)throws SQLException{int id=Integer.parseInt(ctx.pathParam("id"));if(articleService.getArticleByid(id)==null){ctx.status(404).json("Article introuvable");return;}ctx.json(articleService.findCategoriesForArticle(id));}}
=======
=======
>>>>>>> Stashed changes

public class ArticleController {

    private static final ArticleService articleService = new ArticleService();

    @OpenApi(
            summary = "Récupérer tous les articles",
            operationId = "getAllArticle",
            path = "/articles", // Doit matcher Main.java
            methods = HttpMethod.GET,
            tags = {"Articles"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Liste des articles", content = @OpenApiContent(from = Article.class))
            })
    public static void getAllArticle(Context ctx) {
        ctx.json(articleService.GetArticles());
    }

    @OpenApi(
            summary = "Trouve un article par id",
            operationId = "getArticleById",
            path = "/articles/{id}", // Doit matcher Main.java
            methods = HttpMethod.GET,
            tags = {"Articles"},
            pathParams = {
                    @OpenApiParam(name = "id", type = Integer.class, description = "ID de l'article", required = true)
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
<<<<<<< Updated upstream
}
>>>>>>> Stashed changes
=======
}
>>>>>>> Stashed changes
