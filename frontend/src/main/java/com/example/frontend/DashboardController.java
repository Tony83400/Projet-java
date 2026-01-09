package com.example.frontend;
import com.example.frontend.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
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
    @FXML
    private Label titleLabel;
    @FXML
    private Label titleLabelAccent;
    @FXML
    private Label totalTitle;
    @FXML
    private StackPane rootStackPane;

    private final DataService dataService = DataService.getInstance();
    private final List<CartElement> cart = new ArrayList<>();

    @FXML
    public void initialize() {
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
    // Affiche un noeud (le popup) par dessus tout le reste avec un fond grisé
    private void openOverlay(javafx.scene.Node content) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Fond semi-transparent
        overlay.getChildren().add(content);
        overlay.setAlignment(Pos.CENTER);

        // Optionnel : Fermer si on clique en dehors du popup (sur le gris)
        overlay.setOnMouseClicked(e -> {
            // On vérifie que le clic est bien sur le fond gris et pas sur le contenu
            if (e.getTarget() == overlay) {
                rootStackPane.getChildren().remove(overlay);
            }
        });

        rootStackPane.getChildren().add(overlay);
    }

    // Ferme l'overlay contenant le noeud spécifié
    // Version compatible Java 11
    private void closeOverlay(javafx.scene.Node content) {
        // 1. On vérifie le type
        if (content.getParent() instanceof StackPane) {
            // 2. On fait le cast explicite (conversion)
            StackPane overlay = (StackPane) content.getParent();
            // 3. On supprime
            rootStackPane.getChildren().remove(overlay);
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
                    // Création de la carte (avec description, voir étape précédente)
                    VBox card = createItemCard(m.getNom(), m.getDescription(), m.getPrix(), m.getImage_url(), m);

                    // --- MODIFICATION IMPORTANTE ---
                    // Au lieu d'ajouter au panier directement, on ouvre le popup de détails
                    card.setOnMouseClicked(e -> showMenuCompositionPopup(m));
                    // -------------------------------

                    productGrid.add(card, i % 3, i / 3);
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
                    if (newVal && placeholderUrl != null) {
                        //System.err.println(imageUrl);
                        imageView.setImage(new Image(placeholderUrl));
                    };
                });
                imageView.setImage(img);
            } else {
                if (placeholderUrl != null){
                    imageView.setImage(new Image(placeholderUrl));
                };
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
        String qtyText = dataService.getLanguageId().equals("2") ? "Qty: " : "Qté: ";

        for (CartElement ce : cart) {
            total += ce.getPrice() * ce.quantity;
            VBox itemBox = new VBox(5);
            itemBox.setAlignment(Pos.CENTER);
            itemBox.setStyle("-fx-background-color: #333; -fx-padding: 10; -fx-background-radius: 10; -fx-min-width: 120;");

            Label nameLabel = new Label(ce.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label qtyLabel = new Label(qtyText + ce.quantity);
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
        // --- 1. Construction de l'Overlay ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // Style cohérent (Gris foncé, arrondi, ombre)
        root.getStyleClass().add("popup-root");
        root.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-padding: 20;");
        root.setPrefSize(400, 300);

        // Titre
        Label title = new Label(ce.getName());
        title.getStyleClass().add("popup-title");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");

        // Contrôles Quantité (- 1 +)
        HBox qtyContainer = new HBox(20);
        qtyContainer.setAlignment(Pos.CENTER);

        Button btnMinus = new Button("-");
        btnMinus.getStyleClass().add("qty-btn"); // Assurez-vous d'avoir ce style ou utilisez un style standard
        btnMinus.setStyle("-fx-font-size: 18px; -fx-min-width: 40px;");

        Label qtyLabel = new Label(String.valueOf(ce.quantity));
        qtyLabel.getStyleClass().add("popup-qty-label");
        qtyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("qty-btn");
        btnPlus.setStyle("-fx-font-size: 18px; -fx-min-width: 40px;");

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

        // Boutons d'action (Supprimer / Valider)
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("delete-btn"); // Style rouge
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            cart.remove(ce);
            updateCartDisplay();
            closeOverlay(root); // <--- Fermeture
        });

        Button btnConfirm = new Button("Valider");
        btnConfirm.getStyleClass().add("confirm-btn"); // Style vert/orange
        btnConfirm.setOnAction(e -> {
            updateCartDisplay();
            closeOverlay(root); // <--- Fermeture
        });

        actionButtons.getChildren().addAll(btnDelete, btnConfirm);
        root.getChildren().addAll(title, qtyContainer, actionButtons);

        // --- 2. Affichage ---
        openOverlay(root);
    }

    private void validateOrder() {
        if (cart.isEmpty()) return;
        boolean isEnglish = dataService.getLanguageId().equals("2");

        // --- 1. Construction du conteneur (Overlay) ---
        VBox root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);

        // Style identique au popup Menu (Gris foncé, ombré, arrondi)
        root.getStyleClass().add("popup-root");
        root.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-padding: 20;");
        root.setPrefWidth(380);
        root.setMaxHeight(600);

        // Titre
        Label title = new Label(isEnglish ? "SUMMARY" : "RÉCAPITULATIF");
        title.getStyleClass().add("receipt-title"); // Assurez-vous que ce style existe ou utilisez le style du titre menu
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");

        // Liste des articles (dans un ScrollPane si la liste est longue)
        VBox itemsList = new VBox(8);
        itemsList.setStyle("-fx-padding: 10 0;");
        double total = 0;

        for (CartElement ce : cart) {
            HBox row = new HBox();
            Label name = new Label(ce.quantity + "x " + ce.getName());
            Label price = new Label(String.format("%.2f €", ce.getPrice() * ce.quantity));

            // Spacer pour pousser le prix à droite
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS); // <--- Vérifiez l'import de Priority

            name.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            price.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

            row.getChildren().addAll(name, spacer, price);
            itemsList.getChildren().add(row);
            total += ce.getPrice() * ce.quantity;
        }

        // Ajout d'un ScrollPane pour la liste des items (confort visuel si longue commande)
        ScrollPane scrollItems = new ScrollPane(itemsList);
        scrollItems.setFitToWidth(true);
        scrollItems.setMaxHeight(300);
        scrollItems.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Separator sep = new Separator();

        // Total
        HBox totalBox = new HBox();
        Label totalLabelText = new Label(isEnglish ? "FINAL TOTAL" : "TOTAL FINAL");
        Label totalAmount = new Label(String.format("%.2f €", total));

        totalLabelText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        totalAmount.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-font-size: 18px;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        totalBox.getChildren().addAll(totalLabelText, spacer2, totalAmount);

        // Boutons d'action
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        // Bouton ANNULER
        Button btnCancel = new Button(isEnglish ? "CANCEL" : "ANNULER");
        btnCancel.getStyleClass().add("receipt-btn-cancel");
        btnCancel.setOnAction(e -> closeOverlay(root)); // <--- Fermeture Overlay

        // Bouton PAYER
        Button btnConfirm = new Button(isEnglish ? "PAY & ORDER" : "PAYER & COMMANDER");
        btnConfirm.getStyleClass().add("receipt-btn-confirm"); // Ou "confirm-btn"
        btnConfirm.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

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
                closeOverlay(root); // Ferme le résumé de commande

                // --- MODIFICATION ICI ---
                // On affiche le succès ET on dit de revenir à l'accueil (goBack) quand on clique sur OK
                showAlert("Success",
                        "C'est prêt ! Votre ticket est le n°" + savedCmd.getNumero_ticket(),
                        () -> goBack()); // <--- L'action magique est ici
                // ------------------------

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to validate the order.");
            }
        });

        buttons.getChildren().addAll(btnCancel, btnConfirm);

        // Assemblage final
        root.getChildren().addAll(title, scrollItems, sep, totalBox, buttons);

        // --- 2. Affichage via l'Overlay ---
        openOverlay(root);
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

    // Nouvelle version qui accepte une action après le clic sur OK
    private void showAlert(String titleText, String messageText, Runnable onOkAction) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 15; -fx-border-color: #e67e22; -fx-border-width: 2; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");
        root.setMaxWidth(400);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");

        Label message = new Label(messageText);
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button btnOk = new Button("OK");
        btnOk.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 5;");

        // --- C'est ici que ça change ---
        btnOk.setOnAction(e -> {
            closeOverlay(root); // 1. On ferme le popup
            if (onOkAction != null) {
                onOkAction.run(); // 2. On exécute l'action demandée (ex: retour accueil)
            }
        });

        root.getChildren().addAll(title, message, btnOk);
        openOverlay(root);
    }

    // Surcharge pour les messages simples (Erreurs...) -> pas d'action spéciale
    private void showAlert(String titleText, String messageText) {
        showAlert(titleText, messageText, null);
    }
    private void showMenuCompositionPopup(Menu menu) {
        // --- 1. Construction du contenu du Popup (VBox) ---
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("popup-root"); // Fond gris foncé/arrondi (défini dans CSS)

        // Style spécifique pour l'overlay : limite la hauteur et ajoute une ombre
        root.setPrefWidth(380);
        root.setMaxHeight(600);
        root.setPadding(new javafx.geometry.Insets(20));
        root.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        // Titre
        Label title = new Label(menu.getNom().toUpperCase());
        title.getStyleClass().add("popup-title");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");

        // Sous-titre
        Label subTitle = new Label(dataService.getLanguageId().equals("2") ? "Contains:" : "Composition :");
        subTitle.setStyle("-fx-text-fill: #aaa; -fx-font-style: italic;");

        // Liste des articles
        VBox itemsBox = new VBox(10);
        itemsBox.setAlignment(Pos.TOP_CENTER);

        try {
            // Récupération des données
            List<DataService.MenuComposition> composition = dataService.getMenuComposition(menu.getMenu_id());

            for (DataService.MenuComposition item : composition) {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 8; -fx-background-radius: 5;");

                // Image
                ImageView img = new ImageView();
                String imgPath = item.getArticle().getImage_url();
                try {
                    img.setImage(new Image("http://localhost:8080/" + imgPath, true));
                } catch (Exception e) { /* Ignorer */ }
                img.setFitWidth(40);
                img.setFitHeight(40);
                img.setPreserveRatio(true);

                // Texte (Nom + Quantité)
                VBox textBox = new VBox(2);
                Label nameLbl = new Label(item.getArticle().getNom());
                nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                String qtyTxt = dataService.getLanguageId().equals("2") ? "Qty: " : "Qté : ";
                Label qtyLbl = new Label(qtyTxt + item.getQuantite());
                qtyLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

                textBox.getChildren().addAll(nameLbl, qtyLbl);
                row.getChildren().addAll(img, textBox);
                itemsBox.getChildren().add(row);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            itemsBox.getChildren().add(new Label("Erreur chargement composition..."));
        }

        // ScrollPane pour la liste
        ScrollPane scroll = new ScrollPane(itemsBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Boutons d'action
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);

        // Bouton ANNULER
        Button btnCancel = new Button(dataService.getLanguageId().equals("2") ? "Cancel" : "Annuler");
        btnCancel.getStyleClass().add("receipt-btn-cancel");
        // ACTION : Fermer l'overlay
        btnCancel.setOnAction(e -> closeOverlay(root));

        // Bouton AJOUTER
        String priceTxt = String.format("%.2f €", menu.getPrix());
        Button btnAdd = new Button((dataService.getLanguageId().equals("2") ? "Add " : "Ajouter ") + priceTxt);
        btnAdd.getStyleClass().add("confirm-btn");
        btnAdd.setStyle("-fx-font-size: 14px; -fx-padding: 8 20;");
        // ACTION : Ajouter au panier PUIS fermer l'overlay
        btnAdd.setOnAction(e -> {
            addToCart(menu);
            closeOverlay(root);
        });

        actions.getChildren().addAll(btnCancel, btnAdd);
        root.getChildren().addAll(title, subTitle, scroll, actions);

        // --- 2. Affichage via la méthode utilitaire ---
        openOverlay(root);
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