package com.example.frontend;

import fr.univcours.api.models.*;
import fr.univcours.api.models.Menu;
import fr.univcours.api.services.ArticleService;
import fr.univcours.api.services.CategorieService;
import fr.univcours.api.services.CommandeService;
import fr.univcours.api.services.MenuService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;
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
    @FXML private Label titleLabel;
    @FXML private Label titleLabelAccent;
    @FXML private Label totalTitle;


    private final ArticleService articleService = new ArticleService();
    private final CategorieService categorieService = new CategorieService();
    private final MenuService menuService = new MenuService();
    private final CommandeService commandeService = new CommandeService();

    private final List<CartElement> cart = new ArrayList<>();

    @FXML
    public void initialize() {
<<<<<<< Updated upstream
        loadCategoriesFromDb();
=======
        applyLanguage();
        loadCategoriesFromApi();
>>>>>>> Stashed changes
        updateCartDisplay();
        validateButton.setOnAction(e -> validateOrder());
    }

<<<<<<< Updated upstream
    private void loadCategoriesFromDb() {
=======
    private void applyLanguage() {
        boolean isEnglish = dataService.getLanguageId().equals("0");

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
>>>>>>> Stashed changes
        categoryList.getChildren().clear();

        String menuLabel = dataService.getLanguageId().equals("0") ? "MENUS" : "MENUS";
        Button menuBtn = createCategoryButton(menuLabel, null);
        categoryList.getChildren().add(menuBtn);
        List<Categorie> dbCategories = categorieService.GetCategories();
        for (Categorie cat : dbCategories) {
            categoryList.getChildren().add(createCategoryButton(cat.getNom(), cat));
        }
        selectCategory(null);
    }

    private Button createCategoryButton(String label, Categorie cat) {
        Button btn = new Button(label.toUpperCase());
        btn.getStyleClass().add("category-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> selectCategory(cat));
        return btn;
    }

    private void selectCategory(Categorie target) {
        productGrid.getChildren().clear();
<<<<<<< Updated upstream

        if (target == null) {
            List<Menu> menus = menuService.GetMenus();
            int i = 0;
            for (Menu m : menus) {
                productGrid.add(createItemCard(m.getNom(), m.getPrix(), m.getImage_url(), m), i % 3, i / 3);
                i++;
            }
        } else {
            List<Article> articles = articleService.getArticleForCategorie(target.getCategorie_id());
            int i = 0;
            for (Article a : articles) {
                productGrid.add(createItemCard(a.getNom(), a.getPrix(), a.getImage_url(), a), i % 3, i / 3);
                i++;
=======
        try {
            if (target == null) { // Catégorie "MENUS"
                List<Menu> menus = dataService.getMenus();
                int i = 0;
                for (Menu m : menus) {
                    // Création de la carte (avec description, voir étape précédente)
                    VBox card = createItemCard(m.getNom(), m.getDescription(), m.getPrix(), m.getImage_url(), m);
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
>>>>>>> Stashed changes
            }
        }
    }

<<<<<<< Updated upstream
    private VBox createItemCard(String name, double price, String imgPath, Object source) {
=======
    private VBox createItemCard(String name, String description, double price, String imgPath, Object source) {
>>>>>>> Stashed changes
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);

<<<<<<< Updated upstream
        // Chargement de l'image depuis le backend resources
=======
>>>>>>> Stashed changes
        ImageView imageView = new ImageView();
        try {
            // On cherche l'image dans le dossier images/articles du backend
            String fullPath = "/images/articles/" + imgPath.substring(imgPath.lastIndexOf("/") + 1);
            Image img = new Image(getClass().getResourceAsStream(fullPath));
            imageView.setImage(img);
        } catch (Exception e) {
            // Placeholder si image non trouvée
            imageView.setFitWidth(100);
            imageView.setFitHeight(80);
        }
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("product-name");
<<<<<<< Updated upstream
=======
        nameLabel.setWrapText(true); // Important pour les noms longs
        nameLabel.setAlignment(Pos.CENTER);

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

>>>>>>> Stashed changes
        Label priceLabel = new Label(String.format("%.2f €", price));
        priceLabel.getStyleClass().add("product-price-pill");

        card.getChildren().addAll(imageView, nameLabel, priceLabel);
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
        cartItems.getChildren().clear(); // On vide l'affichage actuel
        double total = 0;
        String qtyText = dataService.getLanguageId().equals("0") ? "Qty: " : "Qté: ";

        for (CartElement ce : cart) {
            if (ce.selected) {
                total += ce.getPrice() * ce.quantity;
            }
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
<<<<<<< Updated upstream
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT); // Pour des coins arrondis si désiré

=======
>>>>>>> Stashed changes
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("popup-root"); // Classe CSS
        root.setPrefSize(400, 300);

        // Titre de l'élément
        Label title = new Label(ce.getName());
        title.getStyleClass().add("popup-title");

        // Contrôle de quantité
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

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("delete-btn");
        btnDelete.setOnAction(e -> {
            cart.remove(ce);
            updateCartDisplay();
<<<<<<< Updated upstream
            popupStage.close();
=======
            closeOverlay(root);
>>>>>>> Stashed changes
        });

        Button btnConfirm = new Button("Valider");
        btnConfirm.getStyleClass().add("confirm-btn");
        btnConfirm.setOnAction(e -> {
            updateCartDisplay();
<<<<<<< Updated upstream
            popupStage.close();
=======
            closeOverlay(root);
>>>>>>> Stashed changes
        });

        actionButtons.getChildren().addAll(btnDelete, btnConfirm);
        root.getChildren().addAll(title, qtyContainer, actionButtons);

<<<<<<< Updated upstream
        Scene scene = new Scene(root);
        scene.setFill(null);

        String css = getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        popupStage.setScene(scene);
        popupStage.show();
=======

        openOverlay(root);
>>>>>>> Stashed changes
    }

    private void validateOrder() {
        if (cart.isEmpty()) return;

<<<<<<< Updated upstream

        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.initStyle(StageStyle.TRANSPARENT);
        boolean isEnglish = dataService.getLanguageId().equals("0");

=======
>>>>>>> Stashed changes
        VBox root = new VBox(15);
        root.getStyleClass().add("receipt-popup");
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefWidth(350);

<<<<<<< Updated upstream

        Label title = new Label("RÉCAPITULATIF");
=======
        Label title = new Label(isEnglish ? "SUMMARY" : "RÉCAPITULATIF");
>>>>>>> Stashed changes
        title.getStyleClass().add("receipt-title");

        // Liste des articles (Scrollable si commande longue)
        VBox itemsList = new VBox(8);
        itemsList.setStyle("-fx-padding: 10 0;");
        double total = 0;

        for (CartElement ce : cart) {
            if (ce.selected) {
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
        }

<<<<<<< Updated upstream
=======
        ScrollPane scrollItems = new ScrollPane(itemsList);
        scrollItems.setFitToWidth(true);
        scrollItems.setMaxHeight(300);
        scrollItems.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

>>>>>>> Stashed changes
        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.3;");

        // Total final
        HBox totalBox = new HBox();
        Label totalLabelText = new Label(isEnglish ? "FINAL TOTAL" : "TOTAL FINAL");
        Label totalAmount = new Label(String.format("%.2f €", total));
        totalLabelText.getStyleClass().add("receipt-total-text");
        totalAmount.getStyleClass().add("receipt-total-amount");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        totalBox.getChildren().addAll(totalLabelText, spacer2, totalAmount);

        // Boutons
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
<<<<<<< Updated upstream

        Button btnCancel = new Button("ANNULER");
=======
        Button btnCancel = new Button(isEnglish ? "CANCEL" : "ANNULER");
>>>>>>> Stashed changes
        btnCancel.getStyleClass().add("receipt-btn-cancel");
<<<<<<< Updated upstream
        btnCancel.setOnAction(e -> confirmStage.close());
=======
        btnCancel.setOnAction(e -> closeOverlay(root));
>>>>>>> Stashed changes

        Button btnConfirm = new Button(isEnglish ? "PAY & ORDER" : "PAYER & COMMANDER");
        btnConfirm.getStyleClass().add("receipt-btn-confirm");
        btnConfirm.setOnAction(e -> {
            try {
                Commande savedCmd = commandeService.createEmptyCommande();
                for (CartElement ce : cart) {
                    if (ce.selected) {
                        CommandeItem item = new CommandeItem();
                        item.setQuantite(ce.quantity);
                        if (ce.isArticle()) item.setArticleId(ce.getArticle().getArticle_id());
                        else item.setMenuId(ce.getMenu().getMenu_id());
                        commandeService.addLigneToCommande(savedCmd.getCommande_id(), item);
                    }
                }
                cart.clear();
                updateCartDisplay();
<<<<<<< Updated upstream
                confirmStage.close();
                new Alert(Alert.AlertType.INFORMATION, "C'est prêt ! Votre ticket est le n°" + savedCmd.getNumero_ticket()).show();
            } catch (SQLException ex) {
=======
                closeOverlay(root); // Ferme le résumé de commande


                showAlert("Success",
                        "C'est prêt ! Votre ticket est le n°" + savedCmd.getNumero_ticket(),
                        () -> goBack());


            } catch (IOException | InterruptedException ex) {
>>>>>>> Stashed changes
                ex.printStackTrace();
            }
        });

        buttons.getChildren().addAll(btnCancel, btnConfirm);
        root.getChildren().addAll(title, itemsList, sep, totalBox, buttons);

        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

<<<<<<< Updated upstream
        confirmStage.setScene(scene);
        confirmStage.show();
=======
        openOverlay(root);
>>>>>>> Stashed changes
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
            validateButton.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

<<<<<<< Updated upstream
=======

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

        btnOk.setOnAction(e -> {
            closeOverlay(root);
            if (onOkAction != null) {
                onOkAction.run();
            }
        });

        root.getChildren().addAll(title, message, btnOk);
        openOverlay(root);
    }


    private void showAlert(String titleText, String messageText) {
        showAlert(titleText, messageText, null);
    }
    private void showMenuCompositionPopup(Menu menu) {

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("popup-root"); // Fond gris foncé/arrondi (défini dans CSS)


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

        btnCancel.setOnAction(e -> closeOverlay(root));

        String priceTxt = String.format("%.2f €", menu.getPrix());
        Button btnAdd = new Button((dataService.getLanguageId().equals("2") ? "Add " : "Ajouter ") + priceTxt);
        btnAdd.getStyleClass().add("confirm-btn");
        btnAdd.setStyle("-fx-font-size: 14px; -fx-padding: 8 20;");

        btnAdd.setOnAction(e -> {
            addToCart(menu);
            closeOverlay(root);
        });

        actions.getChildren().addAll(btnCancel, btnAdd);
        root.getChildren().addAll(title, subTitle, scroll, actions);

        openOverlay(root);
    }

>>>>>>> Stashed changes
    private static class CartElement {
        Object content;
        int quantity = 1;
        boolean selected = true;

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
                Article a = (Article) o;
                return getArticle().getArticle_id() == a.getArticle_id();
            }
            if (!isArticle() && o instanceof Menu) {
                Menu m = (Menu) o;
                return getMenu().getMenu_id() == m.getMenu_id();
            }
            return false;
        }
    }
}