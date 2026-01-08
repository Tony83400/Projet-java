package com.example.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TransferQueue;

public class DataService {

    private static DataService instance;
<<<<<<< Updated upstream
=======
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String API_URL = "http://localhost:8080";
    private String currentLanguageId = "1";
>>>>>>> Stashed changes

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

<<<<<<< Updated upstream
    public String getHelloWorld(String URL) throws IOException {
        URL url = new URL(URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println(status);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine = "";
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        System.out.println(content);

        in.close();
        String data = content.toString();
        return data;
    }

    public Boolean deleteUser(String URL) throws IOException {
        URL url = new URL(URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        int status = con.getResponseCode();
        if (status == 204) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
=======
    public String getLanguageId() {
        return currentLanguageId;
    }

    public void setLanguageId(String id) {
        this.currentLanguageId = id;
    }
    public List<Categorie> getCategories() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/categories"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Erreur API Categories: " + response.statusCode());
        return mapper.readValue(response.body(), new TypeReference<List<Categorie>>(){});
    }

    public List<Article> getArticlesForCategory(int categoryId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/categories/" + categoryId + "/articles"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Erreur API Articles: " + response.statusCode());
        return mapper.readValue(response.body(), new TypeReference<List<Article>>(){});
    }

    public List<Menu> getMenus() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/menus"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Erreur API Menus: " + response.statusCode());
        return mapper.readValue(response.body(), new TypeReference<List<Menu>>(){});
    }

    public Commande createEmptyCommande() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/commandes"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) throw new IOException("Impossible de créer la commande");
        return mapper.readValue(response.body(), Commande.class);
    }

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
>>>>>>> Stashed changes
