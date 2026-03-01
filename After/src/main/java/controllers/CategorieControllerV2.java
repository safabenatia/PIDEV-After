package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import models.Categorie;
import models.depense;
import services.CategorieService;
import service.serviceDeppense;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CategorieControllerV2 {

    // ========== CHAMPS DU FORMULAIRE ==========
    @FXML private TextField nomField;
    @FXML private TextField iconeField;
    @FXML private TextArea descriptionArea;
    @FXML private Button ajouterBtn;
    @FXML private Button annulerBtn;
    @FXML private Button parcourirBtn;
    @FXML private Button btnActualiserStats;

    // ========== CONTENEURS ==========
    @FXML private VBox categoriesContainer;
    @FXML private Label countLabel;

    // ========== LABELS DE STATISTIQUES ==========
    @FXML private Label statTotalCategories;
    @FXML private Label statPlusUtilisee;
    @FXML private Label statPlusUtiliseeNb;
    @FXML private Label statPlusDepensee;
    @FXML private Label statPlusDepenseeMontant;

    // ========== GRAPHIQUES ==========
    @FXML private PieChart pieChartCategories;
    @FXML private BarChart<String, Number> barChartDepenses;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // ========== SCROLLPANE ==========
    @FXML private ScrollPane mainScrollPane;

    private CategorieService categorieService;
    private serviceDeppense depenseService;
    private ObservableList<Categorie> categoriesList;
    private Categorie categorieEnModification = null;

    @FXML
    public void initialize() {
        System.out.println("=== INITIALISATION DU CONTROLEUR ===");

        try {
            categorieService = new CategorieService();
            depenseService = new serviceDeppense();
            categoriesList = FXCollections.observableArrayList();

            // Vérification des composants FXML
            verifierComposants();

            // Configuration
            configurerScrollPane();

            // Chargement initial des données
            chargerCategories();
            chargerStatistiques();
            initialiserGraphiques();

            // Configuration des boutons
            configurerBoutons();

            System.out.println("=== INITIALISATION TERMINÉE AVEC SUCCÈS ===\n");

        } catch (Exception e) {
            System.err.println("ERREUR D'INITIALISATION: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verifierComposants() {
        System.out.println("Vérification des composants FXML:");
        System.out.println("  - nomField: " + (nomField != null ? "OK" : "NULL"));
        System.out.println("  - iconeField: " + (iconeField != null ? "OK" : "NULL"));
        System.out.println("  - descriptionArea: " + (descriptionArea != null ? "OK" : "NULL"));
        System.out.println("  - categoriesContainer: " + (categoriesContainer != null ? "OK" : "NULL"));
        System.out.println("  - countLabel: " + (countLabel != null ? "OK" : "NULL"));
        System.out.println("  - statTotalCategories: " + (statTotalCategories != null ? "OK" : "NULL"));
    }

    private void configurerScrollPane() {
        if (mainScrollPane != null) {
            mainScrollPane.setFitToWidth(true);
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
    }

    private void configurerBoutons() {
        // Bouton Ajouter/Modifier
        ajouterBtn.setOnAction(event -> ajouterOuModifierCategorie());

        // Bouton Annuler
        annulerBtn.setOnAction(event -> {
            viderFormulaire();
            categorieEnModification = null;
            ajouterBtn.setText("➕ Ajouter la catégorie");
            ajouterBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #16325c; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
        });

        // Bouton Parcourir
        parcourirBtn.setOnAction(event -> choisirImage());

        // Bouton Actualiser
        if (btnActualiserStats != null) {
            btnActualiserStats.setOnAction(event -> {
                chargerStatistiques();
                initialiserGraphiques();
            });
        }
    }

    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image pour la catégorie");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(parcourirBtn.getScene().getWindow());
        if (selectedFile != null) {
            iconeField.setText(selectedFile.getAbsolutePath());
            System.out.println("Image sélectionnée : " + selectedFile.getAbsolutePath());
        }
    }

    // ========== GESTION DES CATÉGORIES ==========

    private void chargerCategories() {
        try {
            // Récupérer toutes les catégories
            List<Categorie> categoriesFromDB = categorieService.getAll();

            // Mettre à jour la liste observable
            categoriesList.clear();
            categoriesList.addAll(categoriesFromDB);

            // Mettre à jour l'affichage
            afficherCategories();

            // Debug
            System.out.println("=== CHARGEMENT CATÉGORIES ===");
            System.out.println("Catégories en DB: " + categoriesFromDB.size());
            System.out.println("Catégories dans liste: " + categoriesList.size());
            for (Categorie c : categoriesList) {
                System.out.println("  - ID: " + c.getIdCat() + ", Nom: " + c.getNomCategorie());
            }
            System.out.println("==============================");

        } catch (Exception e) {
            System.err.println("Erreur chargement catégories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherCategories() {
        // Vider le conteneur
        categoriesContainer.getChildren().clear();

        if (categoriesList.isEmpty()) {
            // Afficher un message si aucune catégorie
            Label emptyLabel = new Label("Aucune catégorie trouvée");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50;");
            emptyLabel.setAlignment(Pos.CENTER);
            categoriesContainer.getChildren().add(emptyLabel);
        } else {
            // Créer une carte pour chaque catégorie
            for (Categorie c : categoriesList) {
                HBox carte = creerCarteCategorie(c);
                categoriesContainer.getChildren().add(carte);
            }
        }

        // Mettre à jour le compteur
        mettreAJourCompteur();
    }

    private HBox creerCarteCategorie(Categorie c) {
        HBox carte = new HBox(20);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        carte.setPrefHeight(120);

        // ID
        VBox idBox = new VBox(5);
        idBox.setAlignment(Pos.CENTER);
        idBox.setStyle("-fx-background-color: #16325c; -fx-padding: 10; -fx-background-radius: 8;");
        idBox.setPrefWidth(60);
        Label idLabel = new Label("#" + c.getIdCat());
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        idBox.getChildren().add(idLabel);

        // Icône
        VBox iconeBox = new VBox(5);
        iconeBox.setAlignment(Pos.CENTER);
        iconeBox.setStyle("-fx-background-color: #F5F5DC; -fx-padding: 10; -fx-background-radius: 8;");
        iconeBox.setPrefWidth(80);
        iconeBox.setPrefHeight(80);

        if (c.getIconeUrl() != null && !c.getIconeUrl().isEmpty()) {
            try {
                File imageFile = new File(c.getIconeUrl());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(55);
                    imageView.setFitHeight(55);
                    imageView.setPreserveRatio(true);
                    iconeBox.getChildren().add(imageView);
                } else {
                    Label errorLabel = new Label("❌");
                    errorLabel.setStyle("-fx-font-size: 28px;");
                    iconeBox.getChildren().add(errorLabel);
                }
            } catch (Exception e) {
                Label errorLabel = new Label("⚠️");
                errorLabel.setStyle("-fx-font-size: 28px;");
                iconeBox.getChildren().add(errorLabel);
            }
        } else {
            Label defaultLabel = new Label("📂");
            defaultLabel.setStyle("-fx-font-size: 36px;");
            iconeBox.getChildren().add(defaultLabel);
        }

        // Informations
        VBox infoBox = new VBox(8);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nomLabel = new Label(c.getNomCategorie());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #16325c;");

        Label descLabel = new Label(c.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);

        infoBox.getChildren().addAll(nomLabel, descLabel);

        // Actions
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> remplirFormulaireAvecCategorie(c));

        Button btnSupprimer = new Button("🗑️ Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> supprimerCategorie(c));

        actionsBox.getChildren().addAll(btnModifier, btnSupprimer);

        carte.getChildren().addAll(idBox, iconeBox, infoBox, actionsBox);
        return carte;
    }

    private void ajouterOuModifierCategorie() {
        if (nomField.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "Le nom de la catégorie est obligatoire !", Alert.AlertType.ERROR);
            return;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "La description est obligatoire !", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (categorieEnModification == null) {
                // Ajout
                Categorie c = new Categorie();
                c.setNomCategorie(nomField.getText().trim());
                c.setDescription(descriptionArea.getText().trim());
                c.setIconeUrl(iconeField.getText().trim());

                categorieService.add(c);
                afficherAlerte("Succès", "Catégorie ajoutée avec succès !", Alert.AlertType.INFORMATION);
            } else {
                // Modification
                categorieEnModification.setNomCategorie(nomField.getText().trim());
                categorieEnModification.setDescription(descriptionArea.getText().trim());
                categorieEnModification.setIconeUrl(iconeField.getText().trim());

                categorieService.update(categorieEnModification);
                afficherAlerte("Succès", "Catégorie modifiée avec succès !", Alert.AlertType.INFORMATION);

                // Reset du mode modification
                categorieEnModification = null;
                ajouterBtn.setText("➕ Ajouter la catégorie");
                ajouterBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #16325c; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
            }

            // Recharger tout
            chargerCategories();
            viderFormulaire();
            chargerStatistiques();
            initialiserGraphiques();

        } catch (Exception e) {
            afficherAlerte("Erreur", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void supprimerCategorie(Categorie c) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer cette catégorie ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer : " + c.getNomCategorie() + " ?");

        Optional<ButtonType> resultat = confirmation.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            try {
                categorieService.delete(c);
                afficherAlerte("Succès", "Catégorie supprimée avec succès !", Alert.AlertType.INFORMATION);

                // Recharger tout
                chargerCategories();
                chargerStatistiques();
                initialiserGraphiques();

            } catch (Exception e) {
                afficherAlerte("Erreur", "Erreur lors de la suppression : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void remplirFormulaireAvecCategorie(Categorie c) {
        categorieEnModification = c;
        nomField.setText(c.getNomCategorie());
        descriptionArea.setText(c.getDescription());
        iconeField.setText(c.getIconeUrl() != null ? c.getIconeUrl() : "");

        ajouterBtn.setText("💾 Enregistrer les modifications");
        ajouterBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        nomField.requestFocus();
    }

    private void viderFormulaire() {
        nomField.clear();
        descriptionArea.clear();
        iconeField.clear();
    }

    private void mettreAJourCompteur() {
        int count = categoriesList.size();
        countLabel.setText("Total: " + count + (count > 1 ? " catégories" : " catégorie"));
        System.out.println("Compteur mis à jour: " + count + " catégories");
    }

    // ========== STATISTIQUES ==========

    private void chargerStatistiques() {
        try {
            List<Categorie> categories = categorieService.getAll();
            List<depense> depenses = depenseService.getAll();

            // Total catégories
            if (statTotalCategories != null) {
                statTotalCategories.setText(String.valueOf(categories.size()));
            }

            calculerCategoriePlusUtilisee(categories, depenses);
            calculerCategoriePlusDepensee(categories, depenses);

            System.out.println("Statistiques actualisées");

        } catch (Exception e) {
            System.err.println("Erreur chargement stats: " + e.getMessage());
        }
    }

    private void calculerCategoriePlusUtilisee(List<Categorie> categories, List<depense> depenses) {
        if (categories.isEmpty() || depenses.isEmpty()) {
            statPlusUtilisee.setText("---");
            statPlusUtiliseeNb.setText("(0 dépenses)");
            return;
        }

        Map<Integer, Long> comptesParCategorie = depenses.stream()
                .collect(Collectors.groupingBy(depense::getIdCategorie, Collectors.counting()));

        Optional<Map.Entry<Integer, Long>> maxEntry = comptesParCategorie.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            int idCategorie = maxEntry.get().getKey();
            long nombreDepenses = maxEntry.get().getValue();

            categories.stream()
                    .filter(c -> c.getIdCat() == idCategorie)
                    .findFirst()
                    .ifPresent(cat -> {
                        statPlusUtilisee.setText(cat.getNomCategorie());
                        statPlusUtiliseeNb.setText("(" + nombreDepenses + " dépense" + (nombreDepenses > 1 ? "s" : "") + ")");
                    });
        }
    }

    private void calculerCategoriePlusDepensee(List<Categorie> categories, List<depense> depenses) {
        if (categories.isEmpty() || depenses.isEmpty()) {
            statPlusDepensee.setText("---");
            statPlusDepenseeMontant.setText("(0.00 DT)");
            return;
        }

        Map<Integer, Double> totauxParCategorie = depenses.stream()
                .collect(Collectors.groupingBy(
                        depense::getIdCategorie,
                        Collectors.summingDouble(depense::getMontant)
                ));

        Optional<Map.Entry<Integer, Double>> maxEntry = totauxParCategorie.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            int idCategorie = maxEntry.get().getKey();
            double montantTotal = maxEntry.get().getValue();

            categories.stream()
                    .filter(c -> c.getIdCat() == idCategorie)
                    .findFirst()
                    .ifPresent(cat -> {
                        statPlusDepensee.setText(cat.getNomCategorie());
                        statPlusDepenseeMontant.setText(String.format("(%.2f DT)", montantTotal));
                    });
        }
    }

    // ========== GRAPHIQUES ==========

    private void initialiserGraphiques() {
        try {
            List<Categorie> categories = categorieService.getAll();
            List<depense> depenses = depenseService.getAll();

            // Pie Chart
            if (pieChartCategories != null) {
                pieChartCategories.setTitle("Répartition des dépenses par catégorie");
                pieChartCategories.setLabelsVisible(true);
                pieChartCategories.setLegendVisible(true);

                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

                Map<Integer, Double> totauxParCategorie = depenses.stream()
                        .collect(Collectors.groupingBy(
                                depense::getIdCategorie,
                                Collectors.summingDouble(depense::getMontant)
                        ));

                for (Categorie cat : categories) {
                    double montant = totauxParCategorie.getOrDefault(cat.getIdCat(), 0.0);
                    if (montant > 0) {
                        pieData.add(new PieChart.Data(cat.getNomCategorie() + "\n(" + String.format("%.2f DT)", montant), montant));
                    }
                }

                if (pieData.isEmpty()) {
                    pieData.add(new PieChart.Data("Aucune dépense", 1));
                }

                pieChartCategories.setData(pieData);
            }

            // Bar Chart
            if (barChartDepenses != null) {
                barChartDepenses.setTitle("Top 5 des catégories par montant");
                if (xAxis != null) xAxis.setLabel("Catégories");
                if (yAxis != null) yAxis.setLabel("Montant (DT)");

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Montant des dépenses");

                Map<Integer, Double> totauxParCategorie = depenses.stream()
                        .collect(Collectors.groupingBy(
                                depense::getIdCategorie,
                                Collectors.summingDouble(depense::getMontant)
                        ));

                List<Map.Entry<Categorie, Double>> listeTriee = new ArrayList<>();
                for (Categorie cat : categories) {
                    double montant = totauxParCategorie.getOrDefault(cat.getIdCat(), 0.0);
                    listeTriee.add(new AbstractMap.SimpleEntry<>(cat, montant));
                }

                listeTriee.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

                int limite = Math.min(5, listeTriee.size());
                for (int i = 0; i < limite; i++) {
                    Map.Entry<Categorie, Double> entry = listeTriee.get(i);
                    String nomCategorie = entry.getKey().getNomCategorie();
                    if (nomCategorie.length() > 15) {
                        nomCategorie = nomCategorie.substring(0, 12) + "...";
                    }
                    series.getData().add(new XYChart.Data<>(nomCategorie, entry.getValue()));
                }

                barChartDepenses.getData().clear();
                if (!series.getData().isEmpty()) {
                    barChartDepenses.getData().add(series);
                }
            }

            System.out.println("Graphiques initialisés");

        } catch (Exception e) {
            System.err.println("Erreur initialisation graphiques: " + e.getMessage());
        }
    }

    // ========== UTILITAIRES ==========

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}