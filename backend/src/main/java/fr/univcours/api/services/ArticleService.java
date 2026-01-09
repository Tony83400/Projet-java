package fr.univcours.api.services;

import java.util.List;
import java.util.Map;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import fr.univcours.api.models.Article;

@objid ("37eddc6a-7c29-49dc-aead-bb48177fbc02")
public class ArticleService {
    @objid ("ae6daab8-4419-4bf3-86e9-49dcba7b36e1")
    public List<Article> GetArticles(int langueId) {
        // TODO Auto-generated return
        return null;
    }

    @objid ("856c268d-9874-4b6f-9d36-c543b3ff52ac")
    public Article getArticleByid(int id, int langueId) {
        // TODO Auto-generated return
        return null;
    }

    @objid ("3c5324ad-e2ce-462c-94fd-ade898766c7b")
    public List<Article> getArticleForCategorie(int categorie_id, int langueId) {
        // TODO Auto-generated return
        return null;
    }

    @objid ("6efe93cb-6f15-4e36-9490-36f7f709a048")
    public List<Map<String, Object>> findCategoriesForArticle(int articleId, int langueId) {
        // TODO Auto-generated return
        return null;
    }

}
