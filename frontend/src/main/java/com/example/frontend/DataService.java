package com.example.frontend;

import com.example.frontend.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DataService {

    private static DataService instance;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String API_URL = "http://localhost:8080";
    private String currentLanguageId = "1";

    public String getLanguageId() {
        return currentLanguageId;
    }

    public void setLanguageId(String id) {
        this.currentLanguageId = id;
    }

    private DataService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }



    // --- GET Categories ---
    public List<Categorie> getCategories() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/categories/lang/" + currentLanguageId ))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Erreur API Categories: " + response.statusCode());
        return mapper.readValue(response.body(), new TypeReference<List<Categorie>>(){});
    }

    // --- GET Articles par Catégorie ---
    public List<Article> getArticlesForCategory(int categoryId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/categories/" + categoryId + "/articles/lang/" + currentLanguageId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Erreur API Articles: " + response.statusCode());
        return mapper.readValue(response.body(), new TypeReference<List<Article>>(){});
    }

    // --- GET Menus ---
    public List<Menu> getMenus() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/menus/lang/" + currentLanguageId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Erreur API Menus: " + response.statusCode());
        return mapper.readValue(response.body(), new TypeReference<List<Menu>>(){});
    }

    // --- POST Créer commande vide ---
    public Commande createEmptyCommande() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/commandes"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) throw new IOException("Impossible de créer la commande");
        return mapper.readValue(response.body(), Commande.class);
    }

    // --- POST Ajouter ligne à la commande (Update pour accepter CommandeItem) ---
    public void addLigneToCommande(int commandeId, CommandeItem item) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(item);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/commandes/" + commandeId + "/lignes"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) throw new IOException("Erreur ajout ligne: " + response.statusCode());
    }
}