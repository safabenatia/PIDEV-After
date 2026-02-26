package controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Categorie;
import service.CategorieService;
import service.serviceDeppense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fenêtre popup pour vérifier le budget de plusieurs catégories
 */
public class VerificationBudgetDialog {

    private Stage dialogStage;
    private CategorieService categorieService;
    private serviceDeppense depenseService;
    private Map<Categorie, TextField> budgetFields;
    private Map<Categorie, CheckBox> checkBoxes;

    public VerificationBudgetDialog() {
        categorieService = new CategorieService();
        depenseService = new serviceDeppense();
        budgetFields = new HashMap<>();
        checkBoxes = new HashMap<>();
    }

    /**
     * Affiche la fenêtre de vérification budget
     */
    public void afficher(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("💰 Vérification Budget par Catégorie");

        // Container principal
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #F5F5DC;");

        // Titre
        Label titre = new Label("💰 Vérifiez vos budgets par catégorie");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #16325c;");

        Label instruction = new Label("Cochez les catégories à vérifier et entrez la limite de budget pour chacune :");
        instruction.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        instruction.setWrapText(true);

        // Zone scrollable pour la liste des catégories
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(350);

        VBox categoriesContainer = new VBox(10);
        categoriesContainer.setPadding(new Insets(10));

        // Récupérer toutes les catégories
        List<Categorie> categories = categorieService.getAll();

        for (Categorie cat : categories) {
            HBox categorieRow = creerLigneCategorie(cat);
            categoriesContainer.getChildren().add(categorieRow);
        }

        scrollPane.setContent(categoriesContainer);

        // Boutons d'action
        HBox boutonsBox = new HBox(15);
        boutonsBox.setAlignment(Pos.CENTER);

        Button btnVerifier = new Button("✓ Vérifier les budgets");
        btnVerifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
        btnVerifier.setOnAction(e -> verifierBudgets());

        Button btnToutSelectionner = new Button("☑ Tout sélectionner");
        btnToutSelectionner.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 13px; " +
                "-fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        btnToutSelectionner.setOnAction(e -> toutSelectionner(true));

        Button btnToutDeselectionner = new Button("☐ Tout désélectionner");
        btnToutDeselectionner.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 13px; " +
                "-fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        btnToutDeselectionner.setOnAction(e -> toutSelectionner(false));

        Button btnAnnuler = new Button("✕ Fermer");
        btnAnnuler.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-size: 13px; " +
                "-fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        btnAnnuler.setOnAction(e -> dialogStage.close());

        boutonsBox.getChildren().addAll(btnToutSelectionner, btnToutDeselectionner, btnVerifier, btnAnnuler);

        // Assembler
        root.getChildren().addAll(titre, instruction, scrollPane, boutonsBox);

        Scene scene = new Scene(root, 700, 600);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Crée une ligne pour une catégorie avec checkbox et champ budget
     */
    private HBox creerLigneCategorie(Categorie categorie) {
        HBox row = new HBox(20);
        row.setPadding(new Insets(15));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Checkbox
        CheckBox checkBox = new CheckBox();
        checkBox.setStyle("-fx-font-size: 14px;");
        checkBoxes.put(categorie, checkBox);

        // Nom de la catégorie
        Label nomLabel = new Label(categorie.getNomCategorie());
        nomLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #16325c;");
        nomLabel.setPrefWidth(180);

        // Total actuel des dépenses
        double totalActuel = depenseService.getTotalParCategorie(categorie.getIdCat());
        Label totalLabel = new Label(String.format("Dépensé: %.2f DT", totalActuel));
        totalLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        totalLabel.setPrefWidth(150);

        // Champ pour le budget limite
        VBox budgetBox = new VBox(5);
        Label budgetLabelTitle = new Label("Budget max (DT):");
        budgetLabelTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");

        TextField budgetField = new TextField();
        budgetField.setPromptText("Ex: 500");
        budgetField.setPrefWidth(120);
        budgetField.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        budgetField.setDisable(true);

        budgetBox.getChildren().addAll(budgetLabelTitle, budgetField);
        budgetFields.put(categorie, budgetField);

        // Activer/désactiver le champ selon la checkbox
        checkBox.selectedProperty().addListener((obs, old, nouveau) -> {
            budgetField.setDisable(!nouveau);
            if (nouveau) {
                budgetField.requestFocus();
            }
        });

        // Indicateur visuel
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(checkBox, nomLabel, totalLabel, spacer, budgetBox);

        return row;
    }

    /**
     * Vérifie les budgets des catégories sélectionnées
     */
    private void verifierBudgets() {
        List<String> resultats = new ArrayList<>();
        int nbCategoriesSelectionnees = 0;
        int nbDepassements = 0;

        for (Map.Entry<Categorie, CheckBox> entry : checkBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                nbCategoriesSelectionnees++;
                Categorie categorie = entry.getKey();
                TextField budgetField = budgetFields.get(categorie);

                try {
                    double budgetMax = Double.parseDouble(budgetField.getText().trim());
                    double totalDepense = depenseService.getTotalParCategorie(categorie.getIdCat());

                    boolean depasse = totalDepense > budgetMax;
                    if (depasse) {
                        nbDepassements++;
                    }

                    String statut = depasse ? "❌ DÉPASSÉ" : "✅ OK";
                    String differenceText = depasse ?
                            String.format("Dépassement: %.2f DT", totalDepense - budgetMax) :
                            String.format("Restant: %.2f DT", budgetMax - totalDepense);

                    resultats.add(String.format("%s %s\n   Total: %.2f DT / Budget: %.2f DT\n   %s\n",
                            statut, categorie.getNomCategorie(), totalDepense, budgetMax, differenceText));

                } catch (NumberFormatException e) {
                    resultats.add("⚠️ " + categorie.getNomCategorie() + " : Budget invalide\n");
                }
            }
        }

        if (nbCategoriesSelectionnees == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune catégorie sélectionnée");
            alert.setHeaderText("Veuillez sélectionner au moins une catégorie");
            alert.setContentText("Cochez les catégories que vous souhaitez vérifier.");
            alert.showAndWait();
            return;
        }

        // Afficher les résultats
        Alert resultatAlert = new Alert(
                nbDepassements > 0 ? Alert.AlertType.WARNING : Alert.AlertType.INFORMATION
        );
        resultatAlert.setTitle("Résultats de vérification");
        resultatAlert.setHeaderText(
                nbDepassements > 0 ?
                        "⚠️ " + nbDepassements + " catégorie(s) en dépassement !" :
                        "✅ Tous les budgets sont respectés"
        );

        TextArea textArea = new TextArea(String.join("\n", resultats));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(300);
        textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        resultatAlert.getDialogPane().setContent(textArea);
        resultatAlert.getDialogPane().setPrefWidth(500);
        resultatAlert.showAndWait();
    }

    /**
     * Sélectionne ou désélectionne toutes les catégories
     */
    private void toutSelectionner(boolean selectionner) {
        for (CheckBox checkBox : checkBoxes.values()) {
            checkBox.setSelected(selectionner);
        }
    }
}