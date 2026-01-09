package com.example.frontend;

import com.example.frontend.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML private VBox categoryList;
    @FXML private GridPane productGrid;
    @FXML private HBox cartItems;
    @FXML private Button validateButton;
    @FXML private Label totalLabel;
    @FXML private Label titleLabel;
    @FXML private Label titleLabelAccent;
    @FXML private Label totalTitle;
    @FXML private StackPane rootStackPane;

    private final DataService dataService = DataService.getInstance();
    private final List<CartElement> cart = new ArrayList<>();
    private OverlayManager overlayManager; // Notre nouveau gestionnaire

    @FXML
    public void initialize() {
        // On initialise le manager avec le StackPane
        this.overlayManager = new OverlayManager(rootStackPane);

        applyLanguage();
        loadCategoriesFromApi();
        updateCartDisplay();
        validateButton.setOnAction(e -> validateOrder());
    }

    private void applyLanguage() {
        boolean isEnglish = dataService.getLanguageId().equals("2");
        if (isEnglish) {
            titleLabel.setText("KIOSK");
            titleLabelAccent.setText("ORDER");
            totalTitle.setText("TOTAL TO PAY");
            validateButton.setText("ORDER");
        } else {
            titleLabel.setText("BORNE");
            titleLabelAccent.setText("COMMANDE");
            totalTitle.setText("TOTAL A PAYER");
            validateButton.setText("COMMANDER");
        }
    }

    private void loadCategoriesFromApi() {
        categoryList.getChildren().clear();
        String menuLabel = dataService.getLanguageId().equals("2") ? "MENUS" : "MENUS";
        Button menuBtn = createCategoryButton(menuLabel, null);
        categoryList.getChildren().add(menuBtn);

        try {
            List<Categorie> dbCategories = dataService.getCategories();
            for (Categorie cat : dbCategories) {
                categoryList.getChildren().add(createCategoryButton(cat.getNom(), cat));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            overlayManager.showAlert("Error", "Failed to load categories from the server.", null);
        }
        selectCategory(null, menuBtn);
    }

    private Button createCategoryButton(String label, Categorie cat) {
        Button btn = new Button(label.toUpperCase());
        btn.getStyleClass().add("category-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> selectCategory(cat, btn));
        return btn;
    }

    private void selectCategory(Categorie target, Button activeButton) {
        categoryList.getChildren().forEach(node -> node.getStyleClass().remove("active"));
        if (activeButton != null) activeButton.getStyleClass().add("active");

        productGrid.getChildren().clear();
        try {
            if (target == null) { // Menus
                List<Menu> menus = dataService.getMenus();
                int i = 0;
                for (Menu m : menus) {
                    VBox card = createItemCard(m.getNom(), m.getDescription(), m.getPrix(), m.getImage_url(), m);
                    // On utilise l'overlayManager
                    card.setOnMouseClicked(e -> overlayManager.showMenuCompositionPopup(m, this::addToCart));
                    productGrid.add(card, i % 3, i / 3);
                    i++;
                }
            } else { // Articles
                List<Article> articles = dataService.getArticlesForCategory(target.getCategorie_id());
                int i = 0;
                for (Article a : articles) {
                    VBox card = createItemCard(a.getNom(), a.getDescription(), a.getPrix(), a.getImage_url(), a);
                    card.setOnMouseClicked(e -> addToCart(a));
                    productGrid.add(card, i % 3, i / 3);
                    i++;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            overlayManager.showAlert("Error", "Failed to load items.", null);
        }
    }

    private void addToCart(Object item) {
        boolean found = false;
        for (CartElement ce : cart) {
            if (ce.isSameAs(item)) {
                ce.incrementQuantity();
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartElement(item));
        }
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartItems.getChildren().clear();
        double total = 0;
        String qtyText = dataService.getLanguageId().equals("2") ? "Qty: " : "Qté: ";

        for (CartElement ce : cart) {
            total += ce.getPrice() * ce.getQuantity();
            VBox itemBox = new VBox(5);
            itemBox.setAlignment(Pos.CENTER);
            itemBox.setStyle("-fx-background-color: #333; -fx-padding: 10; -fx-background-radius: 10; -fx-min-width: 120;");

            Label nameLabel = new Label(ce.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label qtyLabel = new Label(qtyText + ce.getQuantity());
            qtyLabel.setStyle("-fx-text-fill: #ccc;");
            Label priceLabel = new Label(String.format("%.2f €", ce.getPrice() * ce.getQuantity()));
            priceLabel.setStyle("-fx-text-fill: #e67e22;");

            itemBox.getChildren().addAll(nameLabel, qtyLabel, priceLabel);

            // On délègue l'édition à l'OverlayManager en passant les actions à effectuer
            itemBox.setOnMouseClicked(e -> overlayManager.showEditPopup(ce,
                    this::updateCartDisplay,
                    () -> { cart.remove(ce); updateCartDisplay(); }
            ));

            cartItems.getChildren().add(itemBox);
        }
        totalLabel.setText(String.format("%.2f €", total));
    }

    private void validateOrder() {
        if (cart.isEmpty()) return;

        // On affiche le résumé, et on fournit la logique de "Confirmation" en lambda
        overlayManager.showOrderSummaryPopup(cart, () -> {
            try {
                Commande savedCmd = dataService.createEmptyCommande();
                for (CartElement ce : cart) {
                    CommandeItem item = new CommandeItem();
                    item.setQuantite(ce.getQuantity());
                    if (ce.isArticle()) item.setArticleId(ce.getArticle().getArticle_id());
                    else item.setMenuId(ce.getMenu().getMenu_id());
                    dataService.addLigneToCommande(savedCmd.getCommande_id(), item);
                }
                cart.clear();
                updateCartDisplay();

                // Message de succès avec retour accueil
                overlayManager.showAlert("Success",
                        "C'est prêt ! Votre ticket est le n°" + savedCmd.getNumero_ticket(),
                        this::goBack);

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                overlayManager.showAlert("Error", "Failed to validate order.", null);
            }
        });
    }

    private VBox createItemCard(String name, String description, double price, String imgPath, Object source) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        String placeholderUrl = getClass().getResource("/com/example/frontend/img/placeholder.png") != null
                ? getClass().getResource("/com/example/frontend/img/placeholder.png").toExternalForm() : null;
        try {
            if (imgPath != null && !imgPath.isEmpty()) {
                imageView.setImage(new Image("http://localhost:8080/" + imgPath, true));
            } else if (placeholderUrl != null) {
                imageView.setImage(new Image(placeholderUrl));
            }
        } catch (Exception e) {
            if (placeholderUrl != null) imageView.setImage(new Image(placeholderUrl));
        }
        imageView.setFitWidth(120); imageView.setFitHeight(100); imageView.setPreserveRatio(true);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true); nameLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, nameLabel);

        if (description != null && !description.isEmpty()) {
            Label descLabel = new Label(description);
            descLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10px; -fx-font-style: italic;");
            descLabel.setWrapText(true); descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            descLabel.setMaxWidth(120); descLabel.setMaxHeight(40);
            card.getChildren().add(descLabel);
        }

        Label priceLabel = new Label(String.format("%.2f €", price));
        priceLabel.getStyleClass().add("product-price-pill");
        card.getChildren().add(priceLabel);

        return card;
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            validateButton.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}