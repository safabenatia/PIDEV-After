package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import models.Categorie;
import services.CategorieService;

import java.io.File;
import java.util.Optional;

public class CategorieControllerV2 {

    @FXML
    private TextField nomField;

    @FXML
    private TextField iconeField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button annulerBtn;

    @FXML
    private Button parcourirBtn;  // Bouton Parcourir pour choisir l'image

    @FXML
    private VBox categoriesContainer;

    @FXML
    private Label countLabel;

    private CategorieService categorieService;
    private ObservableList<Categorie> categoriesList;
    private Categorie categorieEnModification = null;

    @FXML
    public void initialize() {
        categorieService = new CategorieService();
        categoriesList = FXCollections.observableArrayList();

        // Charger les categories
        chargerCategories();

        // Action du bouton Ajouter
        ajouterBtn.setOnAction(event -> ajouterOuModifierCategorie());

        // Action du bouton Annuler/Reinitialiser
        annulerBtn.setOnAction(event -> {
            viderFormulaire();
            categorieEnModification = null;
            ajouterBtn.setText("➕ Ajouter la categorie");
            ajouterBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #16325c; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
        });

        // Action du bouton Parcourir (NOUVEAU)
        parcourirBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image pour la categorie");

            // Filtrer pour n'afficher que les images
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.PNG", "*.JPG", "*.JPEG"),
                    new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
            );

            // Definir le repertoire initial (le dossier du projet ou le dossier utilisateur)
            try {
                File initialDirectory = new File(System.getProperty("user.dir"));
                if (initialDirectory.exists()) {
                    fileChooser.setInitialDirectory(initialDirectory);
                }
            } catch (Exception e) {
                // Si erreur, utiliser le dossier utilisateur par defaut
                File userHome = new File(System.getProperty("user.home"));
                fileChooser.setInitialDirectory(userHome);
            }

            // Ouvrir le selecteur de fichiers
            File selectedFile = fileChooser.showOpenDialog(parcourirBtn.getScene().getWindow());

            if (selectedFile != null) {
                // Mettre le chemin absolu dans le champ texte
                iconeField.setText(selectedFile.getAbsolutePath());
                System.out.println("Image selectionnee : " + selectedFile.getAbsolutePath());
            }
        });

        // Mettre a jour le compteur
        mettreAJourCompteur();
    }
    @FXML
    private void retourDepenses() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/gestion_depenses_v2.fxml"));
            javafx.scene.Parent root = loader.load();

            // Open depenses in new maximized window
            javafx.stage.Stage newStage = new javafx.stage.Stage();
            newStage.setScene(new javafx.scene.Scene(root));
            newStage.setMaximized(true);
            newStage.setTitle("Gestion des Dépenses");
            newStage.show();

            // Close categories window
            javafx.stage.Stage currentStage = (javafx.stage.Stage) nomField.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void ajouterOuModifierCategorie() {
        // Validation
        if (nomField.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "Le nom de la categorie est obligatoire !", Alert.AlertType.ERROR);
            return;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "La description est obligatoire !", Alert.AlertType.ERROR);
            return;
        }

        // Verifier que l'image existe si un chemin est fourni
        String cheminImage = iconeField.getText().trim();
        if (!cheminImage.isEmpty()) {
            File imageFile = new File(cheminImage);
            if (!imageFile.exists()) {
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Attention");
                warning.setHeaderText("Image introuvable");
                warning.setContentText("L'image specifiee n'existe pas au chemin :\n" + cheminImage + "\n\nVoulez-vous continuer quand meme ?");

                ButtonType btnOui = new ButtonType("Oui, continuer");
                ButtonType btnNon = new ButtonType("Non, annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                warning.getButtonTypes().setAll(btnOui, btnNon);

                Optional<ButtonType> result = warning.showAndWait();
                if (result.isEmpty() || result.get() == btnNon) {
                    return; // Annuler l'ajout
                }
            }
        }

        try {
            if (categorieEnModification == null) {
                // Ajouter une nouvelle categorie
                Categorie c = new Categorie();
                c.setNomCategorie(nomField.getText().trim());
                c.setDescription(descriptionArea.getText().trim());
                c.setIconeUrl(cheminImage);

                categorieService.add(c);
                afficherAlerte("Succes", "Categorie ajoutee avec succes !", Alert.AlertType.INFORMATION);
            } else {
                // Modifier une categorie existante
                categorieEnModification.setNomCategorie(nomField.getText().trim());
                categorieEnModification.setDescription(descriptionArea.getText().trim());
                categorieEnModification.setIconeUrl(cheminImage);

                categorieService.update(categorieEnModification);
                afficherAlerte("Succes", "Categorie modifiee avec succes !", Alert.AlertType.INFORMATION);

                // Reinitialiser le mode modification
                categorieEnModification = null;
                ajouterBtn.setText("➕ Ajouter la categorie");
                ajouterBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #16325c; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
            }

            // Rafraichir et reinitialiser
            chargerCategories();
            viderFormulaire();
            mettreAJourCompteur();

        } catch (Exception e) {
            afficherAlerte("Erreur", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void chargerCategories() {
        categoriesList.clear();
        categoriesList.addAll(categorieService.getAll());

        // Vider le container
        categoriesContainer.getChildren().clear();

        // Creer une carte pour chaque categorie
        for (Categorie c : categoriesList) {
            categoriesContainer.getChildren().add(creerCarteCategorie(c));
        }
    }

    private HBox creerCarteCategorie(Categorie c) {
        HBox carte = new HBox(20);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");

        // ID
        VBox idBox = new VBox(5);
        idBox.setAlignment(Pos.CENTER);
        idBox.setStyle("-fx-background-color: #16325c; -fx-padding: 10; -fx-background-radius: 8;");
        Label idLabel = new Label("#" + c.getIdCat());
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        idBox.getChildren().add(idLabel);
        idBox.setPrefWidth(60);

        // ========== ICONE IMAGE ==========
        VBox iconeBox = new VBox(5);
        iconeBox.setAlignment(Pos.CENTER);
        iconeBox.setStyle("-fx-background-color: #F5F5DC; -fx-padding: 10; -fx-background-radius: 8;");
        iconeBox.setPrefWidth(80);
        iconeBox.setPrefHeight(80);

        if (c.getIconeUrl() != null && !c.getIconeUrl().isEmpty()) {
            try {
                // Charger l'image depuis le chemin absolu
                File imageFile = new File(c.getIconeUrl());

                if (imageFile.exists()) {
                    // L'image existe, on la charge
                    Image image = new Image(imageFile.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(55);  // Largeur de l'image
                    imageView.setFitHeight(55); // Hauteur de l'image
                    imageView.setPreserveRatio(true); // Garder les proportions

                    iconeBox.getChildren().add(imageView);
                    System.out.println("Image chargee : " + c.getIconeUrl());
                } else {
                    // L'image n'existe pas, afficher un message
                    Label errorLabel = new Label("❌");
                    errorLabel.setStyle("-fx-font-size: 28px;");
                    Label pathLabel = new Label("Image\nintrouvable");
                    pathLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: red; -fx-text-alignment: center;");
                    pathLabel.setWrapText(true);
                    iconeBox.getChildren().addAll(errorLabel, pathLabel);
                    System.err.println("Image introuvable : " + c.getIconeUrl());
                }
            } catch (Exception e) {
                // Erreur lors du chargement, afficher un emoji par defaut
                Label errorLabel = new Label("⚠️");
                errorLabel.setStyle("-fx-font-size: 28px;");
                Label msgLabel = new Label("Erreur");
                msgLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
                iconeBox.getChildren().addAll(errorLabel, msgLabel);
                System.err.println("Erreur chargement image : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Pas d'URL fournie, afficher une icone par defaut
            Label defaultLabel = new Label("📂");
            defaultLabel.setStyle("-fx-font-size: 36px;");
            iconeBox.getChildren().add(defaultLabel);
        }

        // Nom et Description
        VBox infoBox = new VBox(8);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nomLabel = new Label(c.getNomCategorie());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #16325c;");

        Label descLabel = new Label(c.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);

        infoBox.getChildren().addAll(nomLabel, descLabel);

        // Afficher le chemin de l'image (optionnel - pour debug)
        if (c.getIconeUrl() != null && !c.getIconeUrl().isEmpty()) {
            Label cheminLabel = new Label("📁 " + c.getIconeUrl());
            cheminLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999; -fx-font-style: italic;");
            cheminLabel.setWrapText(true);
            cheminLabel.setMaxWidth(400);
            infoBox.getChildren().add(cheminLabel);
        }

        // Boutons d'action
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> remplirFormulaireAvecCategorie(c));

        Button btnSupprimer = new Button("🗑️ Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> supprimerCategorie(c));

        actionsBox.getChildren().addAll(btnModifier, btnSupprimer);

        // Assembler la carte
        carte.getChildren().addAll(idBox, iconeBox, infoBox, actionsBox);

        return carte;
    }

    private void remplirFormulaireAvecCategorie(Categorie c) {
        categorieEnModification = c;
        nomField.setText(c.getNomCategorie());
        descriptionArea.setText(c.getDescription());
        iconeField.setText(c.getIconeUrl() != null ? c.getIconeUrl() : "");

        // Changer le bouton en mode "Modifier"
        ajouterBtn.setText("💾 Enregistrer les modifications");
        ajouterBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        // Scroll vers le haut
        nomField.requestFocus();
    }

    private void supprimerCategorie(Categorie c) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer cette categorie ?");
        confirmation.setContentText("Etes-vous sur de vouloir supprimer : " + c.getNomCategorie() + " ?\n\nAttention : Toutes les depenses associees seront affectees !");

        Optional<ButtonType> resultat = confirmation.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            categorieService.delete(c);
            afficherAlerte("Succes", "Categorie supprimee avec succes !", Alert.AlertType.INFORMATION);
            chargerCategories();
            mettreAJourCompteur();
        }
    }

    private void viderFormulaire() {
        nomField.clear();
        descriptionArea.clear();
        iconeField.clear();
    }

    private void mettreAJourCompteur() {
        int count = categoriesList.size();
        countLabel.setText("Total: " + count + (count > 1 ? " categories" : " categorie"));
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
