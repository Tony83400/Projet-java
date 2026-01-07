package com.example.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML private VBox categoryList;
    @FXML private GridPane productGrid;
    @FXML private HBox cartItems;
    @FXML private Button validateButton;
    @FXML private ScrollPane cartScrollPane;

    // Label pour afficher le coût total sous le bouton commander
    @FXML private Label totalLabel;

    // Données de l'application
    private List<CartItem> cart = new ArrayList<>();
    private String[] categories = {"Entrées", "Plats", "Desserts", "Boissons", "Menus"};
    private String selectedCategory = "Entrées";

    private String[][] products = {
            {"Nems", "Rouleaux de printemps", "Soupe miso", "Salade de choux", "Raviolis vapeur", "Edamame"},
            {"Riz cantonais", "Poulet Kung Pao", "Boeuf sauté", "Canard laqué", "Nouilles sautées", "Porc aigre-doux"},
            {"Perles de coco", "Mochi", "Beignets banane", "Litchis", "Gâteau thé vert", "Glace sésame"},
            {"Thé jasmin", "Coca-Cola", "Eau", "Bière Tsingtao", "Jus de mangue", "Sake"},
            {"Menu A", "Menu B", "Menu C", "Menu Enfant", "Menu Duo", "Menu Famille"}
    };

    private double[][] prices = {
            {4.50, 5.00, 3.50, 3.00, 6.00, 4.00},
            {8.50, 12.00, 13.50, 15.00, 10.00, 11.50},
            {4.00, 4.50, 3.50, 3.00, 5.00, 4.50},
            {2.50, 2.50, 1.50, 4.00, 3.00, 6.00},
            {15.00, 18.00, 22.00, 9.00, 28.00, 45.00}
    };

    @FXML
    public void initialize() {
        setupCategories();
        loadProducts(0);
        updateCartDisplay();
        setupValidateButton();
    }

    private void setupCategories() {
        categoryList.getChildren().clear();
        for (int i = 0; i < categories.length; i++) {
            final int index = i;
            Button categoryBtn = new Button(categories[i]);
            categoryBtn.getStyleClass().add("category-button");

            if (categories[i].equals(selectedCategory)) {
                categoryBtn.getStyleClass().add("active");
            }

            categoryBtn.setOnAction(e -> {
                selectedCategory = categories[index];
                loadProducts(index);
                updateCategoryStyles();
            });

            categoryList.getChildren().add(categoryBtn);
        }
    }

    private void updateCategoryStyles() {
        for (int i = 0; i < categoryList.getChildren().size(); i++) {
            Button btn = (Button) categoryList.getChildren().get(i);
            btn.getStyleClass().remove("active");
            if (categories[i].equals(selectedCategory)) {
                btn.getStyleClass().add("active");
            }
        }
    }

    private void loadProducts(int categoryIndex) {
        productGrid.getChildren().clear();
        productGrid.setHgap(20);
        productGrid.setVgap(20);
        productGrid.setPadding(new Insets(20));

        String[] categoryProducts = products[categoryIndex];
        double[] categoryPrices = prices[categoryIndex];

        int col = 0;
        int row = 0;

        for (int i = 0; i < categoryProducts.length; i++) {
            VBox productCard = createProductCard(categoryProducts[i], categoryPrices[i]);
            productGrid.add(productCard, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(String name, double price) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(220);
        card.setPrefHeight(240);

        Region imagePlaceholder = new Region();
        imagePlaceholder.setPrefSize(100, 100);
        imagePlaceholder.getStyleClass().add("product-image-placeholder");
        imagePlaceholder.setStyle("-fx-background-color: #f4f4f9; -fx-background-radius: 8;");

        Label nameLabel = new Label(name);
        nameLabel.setWrapText(true);
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("%.2f €", price));
        priceLabel.getStyleClass().add("product-price-pill");

        card.getChildren().addAll(imagePlaceholder, nameLabel, priceLabel);
        card.setOnMouseClicked(e -> addToCart(name, price));

        return card;
    }

    private void addToCart(String name, double price) {
        for (CartItem item : cart) {
            if (item.name.equals(name)) {
                item.quantity++;
                updateCartDisplay();
                return;
            }
        }
        cart.add(new CartItem(name, price, 1));
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartItems.getChildren().clear();
        double total = 0;

        for (CartItem item : cart) {
            HBox itemBox = createCartItemBox(item);
            cartItems.getChildren().add(itemBox);
            if (item.selected) {
                total += item.price * item.quantity;
            }
        }

        if (totalLabel != null) {
            totalLabel.setText(String.format("%.2f €", total));
        }
    }

    private HBox createCartItemBox(CartItem item) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 15, 10, 15));
        box.setMinWidth(250);
        box.getStyleClass().add("cart-item-box");

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(item.selected);
        checkBox.setStyle("-fx-accent: #d20202;");
        checkBox.setOnAction(e -> {
            item.selected = checkBox.isSelected();
            updateCartDisplay();
        });

        VBox details = new VBox(2);
        Label nameLabel = new Label(item.name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e272e;");

        Label unitPriceLabel = new Label("Qté: " + item.quantity);
        unitPriceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        details.getChildren().addAll(nameLabel, unitPriceLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label priceLabel = new Label(String.format("%.2f €", item.price * item.quantity));
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #d20202;");

        box.getChildren().addAll(checkBox, details, spacer, priceLabel);
        box.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                showEditPopup(item);
            }
        });

        return box;
    }

    private void showEditPopup(CartItem item) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("popup-root");

        Label titleLabel = new Label("Modifier " + item.name);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D3436;");

        HBox quantityBox = new HBox(20);
        quantityBox.setAlignment(Pos.CENTER);

        // Bouton moins
        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().add("circle-button");
        minusBtn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2D3436;");

        Label quantityLabel = new Label(String.valueOf(item.quantity));
        quantityLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Bouton plus
        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("circle-button");
        plusBtn.setStyle("-fx-background-color: #fec938; -fx-text-fill: #2D3436;");

        minusBtn.setOnAction(e -> {
            if (item.quantity > 1) {
                item.quantity--;
                quantityLabel.setText(String.valueOf(item.quantity));
            }
        });

        plusBtn.setOnAction(e -> {
            item.quantity++;
            quantityLabel.setText(String.valueOf(item.quantity));
        });

        quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button deleteBtn = new Button("Retirer");
        deleteBtn.setStyle("-fx-background-color: white; -fx-text-fill: #d20202; -fx-border-color: #d20202; -fx-border-radius: 8;");

        Button confirmBtn = new Button("Valider");
        confirmBtn.setStyle("-fx-background-color: #d20202; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;");

        deleteBtn.setOnAction(e -> {
            cart.remove(item);
            updateCartDisplay();
            popup.close();
        });

        confirmBtn.setOnAction(e -> {
            updateCartDisplay();
            popup.close();
        });

        buttonBox.getChildren().addAll(deleteBtn, confirmBtn);
        root.getChildren().addAll(titleLabel, quantityBox, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(null);
        // On charge le CSS aussi pour la popup
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void setupValidateButton() {
        validateButton.setOnAction(e -> validateOrder());
    }

    private void validateOrder() {
        double total = 0;
        StringBuilder orderSummary = new StringBuilder();

        for (CartItem item : cart) {
            if (item.selected) {
                total += item.price * item.quantity;
                orderSummary.append("• ").append(item.name).append(" x").append(item.quantity).append("\n");
            }
        }

        if (total == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Panier");
            alert.setHeaderText(null);
            alert.setContentText("Votre panier est vide ou aucun article n'est sélectionné.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de commande");
        alert.setHeaderText("Montant total : " + String.format("%.2f €", total));
        alert.setContentText("Voulez-vous valider les articles suivants ?\n\n" + orderSummary.toString());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cart.clear();
            updateCartDisplay();

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("Votre commande a été envoyée en cuisine !");
            success.showAndWait();
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) categoryList.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class CartItem {
        String name;
        double price;
        int quantity;
        boolean selected;

        CartItem(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.selected = true;
        }
    }
}