package com.example.frontend;

import com.example.frontend.models.CartElement;
import com.example.frontend.models.Menu;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class OverlayManager {

    private final StackPane rootStackPane;
    private final DataService dataService = DataService.getInstance();

    public OverlayManager(StackPane rootStackPane) {
        this.rootStackPane = rootStackPane;
    }

    // --- Gestion Générique des Overlays ---
    public void openOverlay(Node content) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.getChildren().add(content);
        overlay.setAlignment(Pos.CENTER);
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) closeOverlay(overlay);
        });
        rootStackPane.getChildren().add(overlay);
    }

    public void closeOverlay(Node content) {
        if (content.getParent() instanceof StackPane) {
            rootStackPane.getChildren().remove(content.getParent());
        } else if (content instanceof StackPane) { // Cas où on passe l'overlay lui-même
            rootStackPane.getChildren().remove(content);
        }
    }

    // --- Alertes ---
    public void showAlert(String titleText, String messageText, Runnable onOkAction) {
        VBox root = createBasePopup(400);
        root.setStyle(root.getStyle() + "-fx-border-color: #e67e22; -fx-border-width: 2;");

        Label title = createTitleLabel(titleText);
        Label message = new Label(messageText);
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button btnOk = new Button("OK");
        btnOk.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 5;");
        btnOk.setOnAction(e -> {
            closeOverlay(root);
            if (onOkAction != null) onOkAction.run();
        });

        root.getChildren().addAll(title, message, btnOk);
        openOverlay(root);
    }

    // --- Popup Modification Panier ---
    public void showEditPopup(CartElement ce, Runnable onUpdate, Runnable onDelete) {
        VBox root = createBasePopup(400);

        Label title = createTitleLabel(ce.getName());

        HBox qtyContainer = new HBox(20);
        qtyContainer.setAlignment(Pos.CENTER);

        Label qtyLabel = new Label(String.valueOf(ce.getQuantity()));
        qtyLabel.getStyleClass().add("popup-qty-label");
        qtyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnMinus = createQtyButton("-", e -> {
            if (ce.getQuantity() > 1) {
                ce.decrementQuantity();
                qtyLabel.setText(String.valueOf(ce.getQuantity()));
            }
        });

        Button btnPlus = createQtyButton("+", e -> {
            ce.incrementQuantity();
            qtyLabel.setText(String.valueOf(ce.getQuantity()));
        });

        qtyContainer.getChildren().addAll(btnMinus, qtyLabel, btnPlus);

        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("delete-btn");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            if (onDelete != null) onDelete.run();
            closeOverlay(root);
        });

        Button btnConfirm = new Button("Valider");
        btnConfirm.getStyleClass().add("confirm-btn");
        btnConfirm.setOnAction(e -> {
            if (onUpdate != null) onUpdate.run();
            closeOverlay(root);
        });

        actionButtons.getChildren().addAll(btnDelete, btnConfirm);
        root.getChildren().addAll(title, qtyContainer, actionButtons);

        openOverlay(root);
    }

    // --- Popup Composition Menu ---
    public void showMenuCompositionPopup(Menu menu, Consumer<Menu> onAdd) {
        VBox root = createBasePopup(380);
        root.setMaxHeight(600);

        Label title = createTitleLabel(menu.getNom().toUpperCase());
        Label subTitle = new Label(dataService.getLanguageId().equals("2") ? "Contains:" : "Composition :");
        subTitle.setStyle("-fx-text-fill: #aaa; -fx-font-style: italic;");

        VBox itemsBox = new VBox(10);
        itemsBox.setAlignment(Pos.TOP_CENTER);

        try {
            List<DataService.MenuComposition> composition = dataService.getMenuComposition(menu.getMenu_id());
            for (DataService.MenuComposition item : composition) {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 8; -fx-background-radius: 5;");

                ImageView img = new ImageView();
                try {
                    img.setImage(new Image("http://localhost:8080/" + item.getArticle().getImage_url(), true));
                } catch (Exception ignored) {}
                img.setFitWidth(40); img.setFitHeight(40); img.setPreserveRatio(true);

                VBox textBox = new VBox(2);
                Label nameLbl = new Label(item.getArticle().getNom());
                nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                Label qtyLbl = new Label((dataService.getLanguageId().equals("2") ? "Qty: " : "Qté : ") + item.getQuantite());
                qtyLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

                textBox.getChildren().addAll(nameLbl, qtyLbl);
                row.getChildren().addAll(img, textBox);
                itemsBox.getChildren().add(row);
            }
        } catch (IOException | InterruptedException e) {
            itemsBox.getChildren().add(new Label("Erreur chargement composition..."));
        }

        ScrollPane scroll = new ScrollPane(itemsBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);

        Button btnCancel = new Button(dataService.getLanguageId().equals("2") ? "Cancel" : "Annuler");
        btnCancel.getStyleClass().add("receipt-btn-cancel");
        btnCancel.setOnAction(e -> closeOverlay(root));

        Button btnAdd = new Button((dataService.getLanguageId().equals("2") ? "Add " : "Ajouter ") + String.format("%.2f €", menu.getPrix()));
        btnAdd.getStyleClass().add("confirm-btn");
        btnAdd.setStyle("-fx-font-size: 14px; -fx-padding: 8 20;");
        btnAdd.setOnAction(e -> {
            if (onAdd != null) onAdd.accept(menu);
            closeOverlay(root);
        });

        actions.getChildren().addAll(btnCancel, btnAdd);
        root.getChildren().addAll(title, subTitle, scroll, actions);

        openOverlay(root);
    }

    // --- Popup Résumé Commande ---
    public void showOrderSummaryPopup(List<CartElement> cart, Runnable onConfirmOrder) {
        boolean isEnglish = dataService.getLanguageId().equals("2");
        VBox root = createBasePopup(380);
        root.setMaxHeight(600);

        Label title = createTitleLabel(isEnglish ? "SUMMARY" : "RÉCAPITULATIF");

        VBox itemsList = new VBox(8);
        itemsList.setStyle("-fx-padding: 10 0;");
        double total = 0;

        for (CartElement ce : cart) {
            HBox row = new HBox();
            Label name = new Label(ce.getQuantity() + "x " + ce.getName());
            Label price = new Label(String.format("%.2f €", ce.getPrice() * ce.getQuantity()));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            name.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            price.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
            row.getChildren().addAll(name, spacer, price);
            itemsList.getChildren().add(row);
            total += ce.getPrice() * ce.getQuantity();
        }

        ScrollPane scrollItems = new ScrollPane(itemsList);
        scrollItems.setFitToWidth(true); scrollItems.setMaxHeight(300);
        scrollItems.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        HBox totalBox = new HBox();
        Label totalLabelText = new Label(isEnglish ? "FINAL TOTAL" : "TOTAL FINAL");
        Label totalAmount = new Label(String.format("%.2f €", total));
        totalLabelText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        totalAmount.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-font-size: 18px;");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        totalBox.getChildren().addAll(totalLabelText, spacer2, totalAmount);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button btnCancel = new Button(isEnglish ? "CANCEL" : "ANNULER");
        btnCancel.getStyleClass().add("receipt-btn-cancel");
        btnCancel.setOnAction(e -> closeOverlay(root));

        Button btnConfirm = new Button(isEnglish ? "PAY & ORDER" : "PAYER & COMMANDER");
        btnConfirm.getStyleClass().add("receipt-btn-confirm");
        btnConfirm.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnConfirm.setOnAction(e -> {
            closeOverlay(root);
            if (onConfirmOrder != null) onConfirmOrder.run();
        });

        buttons.getChildren().addAll(btnCancel, btnConfirm);
        root.getChildren().addAll(title, scrollItems, new Separator(), totalBox, buttons);

        openOverlay(root);
    }

    // --- Helpers Privés pour éviter la duplication ---
    private VBox createBasePopup(double width) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("popup-root");
        root.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-padding: 20;");
        root.setPrefWidth(width);
        return root;
    }

    private Label createTitleLabel(String text) {
        Label title = new Label(text);
        title.getStyleClass().add("popup-title");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");
        return title;
    }

    private Button createQtyButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("qty-btn");
        btn.setStyle("-fx-font-size: 18px; -fx-min-width: 40px;");
        btn.setOnAction(action);
        return btn;
    }
}