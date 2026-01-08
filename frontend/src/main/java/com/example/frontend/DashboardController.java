package com.example.frontend;

import com.example.frontend.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML
    private VBox categoryList;
    @FXML
    private GridPane productGrid;
    @FXML
    private HBox cartItems;
    @FXML
    private Button validateButton;
    @FXML
    private Label totalLabel;

    private final DataService dataService = DataService.getInstance();
    private final List<CartElement> cart = new ArrayList<>();

    @FXML
    public void initialize() {
        loadCategoriesFromApi();
        updateCartDisplay();
        validateButton.setOnAction(e -> validateOrder());
    }

    private void loadCategoriesFromApi() {
        categoryList.getChildren().clear();

        Button menuBtn = createCategoryButton("MENUS", null);
        categoryList.getChildren().add(menuBtn);

        try {
            List<Categorie> dbCategories = dataService.getCategories();
            for (Categorie cat : dbCategories) {
                categoryList.getChildren().add(createCategoryButton(cat.getNom(), cat));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load categories from the server.");
        }

        // MODIFICATION : On simule un clic sur le premier bouton (MENUS) pour l'activer visuellement dès le début
        selectCategory(null, menuBtn);
    }

    private Button createCategoryButton(String label, Categorie cat) {
        Button btn = new Button(label.toUpperCase());
        btn.getStyleClass().add("category-button");
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setOnAction(e -> selectCategory(cat, btn));

        return btn;
    }

    // MODIFICATION DE LA SIGNATURE : ajout du paramètre 'activeButton'
    private void selectCategory(Categorie target, Button activeButton) {
        // 1. GESTION VISUELLE
        categoryList.getChildren().forEach(node -> {
            node.getStyleClass().remove("active");
        });
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }

        // 2. LOGIQUE MÉTIER
        productGrid.getChildren().clear();
        try {
            if (target == null) { // Catégorie "MENUS"
                List<Menu> menus = dataService.getMenus();
                int i = 0;
                for (Menu m : menus) {
                    // MODIFICATION : on passe "" pour la description des menus
                    productGrid.add(createItemCard(m.getNom(), "", m.getPrix(), m.getImage_url(), m), i % 3, i / 3);
                    i++;
                }
            } else {
                List<Article> articles = dataService.getArticlesForCategory(target.getCategorie_id());
                int i = 0;
                for (Article a : articles) {
                    // MODIFICATION : on passe a.getDescription()
                    productGrid.add(createItemCard(a.getNom(), a.getDescription(), a.getPrix(), a.getImage_url(), a), i % 3, i / 3);
                    i++;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load items from the server.");
        }
    }

    // Modifiez la signature pour accepter 'description'
    private VBox createItemCard(String name, String description, double price, String imgPath, Object source) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);

        // --- Image (Code existant conservé) ---
        ImageView imageView = new ImageView();
        String placeholderUrl = getClass().getResource("/com/example/frontend/img/placeholder.png") != null
                ? getClass().getResource("/com/example/frontend/img/placeholder.png").toExternalForm()
                : null;
        try {
            if (imgPath != null && !imgPath.isEmpty()) {
                String imageUrl = "http://localhost:8080/" + imgPath;
                Image img = new Image(imageUrl, true);
                img.errorProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal && placeholderUrl != null) imageView.setImage(new Image(placeholderUrl));
                });
                imageView.setImage(img);
            } else {
                if (placeholderUrl != null) imageView.setImage(new Image(placeholderUrl));
            }
        } catch (Exception e) {
            if (placeholderUrl != null) imageView.setImage(new Image(placeholderUrl));
        }
        imageView.setFitWidth(120);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        // --- Nom ---
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true); // Important pour les noms longs
        nameLabel.setAlignment(Pos.CENTER);

        // --- NOUVEAU : Description ---
        // On n'affiche le label que si une description existe
        if (description != null && !description.isEmpty()) {
            Label descLabel = new Label(description);
            descLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10px; -fx-font-style: italic;");
            descLabel.setWrapText(true);
            descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            descLabel.setMaxWidth(120); // Largeur alignée avec l'image
            descLabel.setMaxHeight(40); // Limite la hauteur (environ 2-3 lignes)
            card.getChildren().add(imageView);
            card.getChildren().add(nameLabel);
            card.getChildren().add(descLabel); // Ajout description
        } else {
            card.getChildren().add(imageView);
            card.getChildren().add(nameLabel);
        }
        // -----------------------------

        Label priceLabel = new Label(String.format("%.2f €", price));
        priceLabel.getStyleClass().add("product-price-pill");

        card.getChildren().add(priceLabel);
        card.setOnMouseClicked(e -> addToCart(source));

        return card;
    }

    private void addToCart(Object item) {
        boolean found = false;
        for (CartElement ce : cart) {
            if (ce.isSameAs(item)) {
                ce.quantity++;
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

        for (CartElement ce : cart) {
            total += ce.getPrice() * ce.quantity;
            VBox itemBox = new VBox(5);
            itemBox.setAlignment(Pos.CENTER);
            itemBox.setStyle("-fx-background-color: #333; -fx-padding: 10; -fx-background-radius: 10; -fx-min-width: 120;");

            Label nameLabel = new Label(ce.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label qtyLabel = new Label("Qté: " + ce.quantity);
            qtyLabel.setStyle("-fx-text-fill: #ccc;");

            Label priceLabel = new Label(String.format("%.2f €", ce.getPrice() * ce.quantity));
            priceLabel.setStyle("-fx-text-fill: #e67e22;");

            itemBox.getChildren().addAll(nameLabel, qtyLabel, priceLabel);
            itemBox.setOnMouseClicked(e -> showEditPopup(ce));

            cartItems.getChildren().add(itemBox);
        }

        totalLabel.setText(String.format("%.2f €", total));
    }

    private void showEditPopup(CartElement ce) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("popup-root");
        root.setPrefSize(400, 300);

        Label title = new Label(ce.getName());
        title.getStyleClass().add("popup-title");

        HBox qtyContainer = new HBox(20);
        qtyContainer.setAlignment(Pos.CENTER);
        Button btnMinus = new Button("-");
        btnMinus.getStyleClass().add("qty-btn");
        Label qtyLabel = new Label(String.valueOf(ce.quantity));
        qtyLabel.getStyleClass().add("popup-qty-label");
        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("qty-btn");

        btnMinus.setOnAction(e -> {
            if (ce.quantity > 1) {
                ce.quantity--;
                qtyLabel.setText(String.valueOf(ce.quantity));
            }
        });
        btnPlus.setOnAction(e -> {
            ce.quantity++;
            qtyLabel.setText(String.valueOf(ce.quantity));
        });

        qtyContainer.getChildren().addAll(btnMinus, qtyLabel, btnPlus);

        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);
        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("delete-btn");
        btnDelete.setOnAction(e -> {
            cart.remove(ce);
            updateCartDisplay();
            popupStage.close();
        });

        Button btnConfirm = new Button("Valider");
        btnConfirm.getStyleClass().add("confirm-btn");
        btnConfirm.setOnAction(e -> {
            updateCartDisplay();
            popupStage.close();
        });

        actionButtons.getChildren().addAll(btnDelete, btnConfirm);
        root.getChildren().addAll(title, qtyContainer, actionButtons);

        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void validateOrder() {
        if (cart.isEmpty()) return;

        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(15);
        root.getStyleClass().add("receipt-popup");
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefWidth(350);

        Label title = new Label("RÉCAPITULATIF");
        title.getStyleClass().add("receipt-title");

        VBox itemsList = new VBox(8);
        itemsList.setStyle("-fx-padding: 10 0;");
        double total = 0;

        for (CartElement ce : cart) {
            HBox row = new HBox();
            Label name = new Label(ce.quantity + "x " + ce.getName());
            Label price = new Label(String.format("%.2f €", ce.getPrice() * ce.quantity));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            name.setStyle("-fx-text-fill: white;");
            price.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
            row.getChildren().addAll(name, spacer, price);
            itemsList.getChildren().add(row);
            total += ce.getPrice() * ce.quantity;
        }

        Separator sep = new Separator();
        HBox totalBox = new HBox();
        Label totalLabelText = new Label("TOTAL FINAL");
        Label totalAmount = new Label(String.format("%.2f €", total));
        totalLabelText.getStyleClass().add("receipt-total-text");
        totalAmount.getStyleClass().add("receipt-total-amount");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        totalBox.getChildren().addAll(totalLabelText, spacer2, totalAmount);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        Button btnCancel = new Button("ANNULER");
        btnCancel.getStyleClass().add("receipt-btn-cancel");
        btnCancel.setOnAction(e -> confirmStage.close());

        Button btnConfirm = new Button("PAYER & COMMANDER");
        btnConfirm.getStyleClass().add("receipt-btn-confirm");
        btnConfirm.setOnAction(e -> {
            try {
                Commande savedCmd = dataService.createEmptyCommande();
                for (CartElement ce : cart) {
                    CommandeItem item = new CommandeItem();
                    item.setQuantite(ce.quantity);
                    if (ce.isArticle()) {
                        item.setArticleId(ce.getArticle().getArticle_id());
                    } else {
                        item.setMenuId(ce.getMenu().getMenu_id());
                    }
                    dataService.addLigneToCommande(savedCmd.getCommande_id(), item);
                }
                cart.clear();
                updateCartDisplay();
                confirmStage.close();
                showAlert("Success", "C'est prêt ! Votre ticket est le n°" + savedCmd.getNumero_ticket());
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to validate the order.");
            }
        });

        buttons.getChildren().addAll(btnCancel, btnConfirm);
        root.getChildren().addAll(title, itemsList, sep, totalBox, buttons);

        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        confirmStage.setScene(scene);
        confirmStage.show();
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private static class CartElement {
        Object content;
        int quantity = 1;

        CartElement(Object c) {
            this.content = c;
        }

        boolean isArticle() {
            return content instanceof Article;
        }

        Article getArticle() {
            return (Article) content;
        }

        Menu getMenu() {
            return (Menu) content;
        }

        String getName() {
            return isArticle() ? getArticle().getNom() : getMenu().getNom();
        }

        double getPrice() {
            return isArticle() ? getArticle().getPrix() : getMenu().getPrix();
        }

        boolean isSameAs(Object o) {
            if (isArticle() && o instanceof Article) {
                return getArticle().getArticle_id() == ((Article) o).getArticle_id();
            }
            if (!isArticle() && o instanceof Menu) {
                return getMenu().getMenu_id() == ((Menu) o).getMenu_id();
            }
            return false;
        }
    }
}
