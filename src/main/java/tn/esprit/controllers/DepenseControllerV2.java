package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.Categorie;
import models.depense;
import service.CategorieService;
import service.serviceDeppense;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DepenseControllerV2 {

    @FXML
    private TextField titreField;
    
    @FXML
    private TextField montantField;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ComboBox<Categorie> categorieCombo;
    
    @FXML
    private Button ajouterBtn;
    
    @FXML
    private VBox depensesContainer;
    
    @FXML
    private Label totalLabel;
    
    private serviceDeppense depenseService;
    private CategorieService categorieService;
    private ObservableList<depense> depensesList;
    private ObservableList<Categorie> categoriesList;
    private depense depenseEnModification = null;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        depenseService = new serviceDeppense();
        categorieService = new CategorieService();
        depensesList = FXCollections.observableArrayList();
        categoriesList = FXCollections.observableArrayList();
        
        chargerCategoriesDepuisBD();
        
        datePicker.setValue(LocalDate.now());
        
        chargerDepenses();
        
        ajouterBtn.setOnAction(event -> ajouterOuModifierDepense());
        
        calculerTotal();
    }
    
    private void chargerCategoriesDepuisBD() {
        categoriesList.clear();
        categoriesList.addAll(categorieService.getAll());
        categorieCombo.setItems(categoriesList);
        
        categorieCombo.setCellFactory(param -> new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomCategorie());
            }
        });
        
        categorieCombo.setButtonCell(new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomCategorie());
            }
        });
    }
    
    private void ajouterOuModifierDepense() {
        if (titreField.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "Le titre est obligatoire !", Alert.AlertType.ERROR);
            return;
        }
        
        if (montantField.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "Le montant est obligatoire !", Alert.AlertType.ERROR);
            return;
        }
        
        if (datePicker.getValue() == null) {
            afficherAlerte("Erreur", "La date est obligatoire !", Alert.AlertType.ERROR);
            return;
        }
        
        if (categorieCombo.getValue() == null) {
            afficherAlerte("Erreur", "La catégorie est obligatoire !", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            double montant = Double.parseDouble(montantField.getText().trim());
            
            if (depenseEnModification == null) {
                depense d = new depense();
                d.setTitre(titreField.getText().trim());
                d.setMontant(montant);
                d.setDateDepense(Date.valueOf(datePicker.getValue()));
                d.setIdCategorie(categorieCombo.getValue().getIdCat());
                
                depenseService.add(d);
                afficherAlerte("Succès", "Dépense ajoutée avec succès !", Alert.AlertType.INFORMATION);
            } else {
                depenseEnModification.setTitre(titreField.getText().trim());
                depenseEnModification.setMontant(montant);
                depenseEnModification.setDateDepense(Date.valueOf(datePicker.getValue()));
                depenseEnModification.setIdCategorie(categorieCombo.getValue().getIdCat());
                
                depenseService.update(depenseEnModification);
                afficherAlerte("Succès", "Dépense modifiée avec succès !", Alert.AlertType.INFORMATION);
                
                depenseEnModification = null;
                ajouterBtn.setText("➕ Ajouter");
                ajouterBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #16325c; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
            }
            
            chargerDepenses();
            viderFormulaire();
            calculerTotal();
            
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le montant doit être un nombre valide !", Alert.AlertType.ERROR);
        }
    }
    
    private void chargerDepenses() {
        depensesList.clear();
        depensesList.addAll(depenseService.getAll());
        
        depensesContainer.getChildren().clear();
        
        for (depense d : depensesList) {
            depensesContainer.getChildren().add(creerCarteDepense(d));
        }
    }
    
    private HBox creerCarteDepense(depense d) {
        HBox carte = new HBox(20);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        VBox idBox = new VBox(5);
        idBox.setAlignment(Pos.CENTER);
        idBox.setStyle("-fx-background-color: #16325c; -fx-padding: 10; -fx-background-radius: 8;");
        Label idLabel = new Label("#" + d.getIdDep());
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        idBox.getChildren().add(idLabel);
        idBox.setPrefWidth(60);
        
        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        Label titreLabel = new Label(d.getTitre());
        titreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #16325c;");
        
        Categorie cat = categorieService.getById(d.getIdCategorie());
        String nomCat = cat != null ? cat.getNomCategorie() : "Catégorie inconnue";
        Label categorieLabel = new Label("📁 " + nomCat);
        categorieLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        infoBox.getChildren().addAll(titreLabel, categorieLabel);
        

        VBox montantBox = new VBox(3);
        montantBox.setAlignment(Pos.CENTER_RIGHT);
        montantBox.setPrefWidth(150);
        
        Label montantLabel = new Label(String.format("%.2f DT", d.getMontant()));
        montantLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        
        montantBox.getChildren().add(montantLabel);
        
        VBox dateBox = new VBox(5);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPrefWidth(120);
        
        LocalDate localDate;
        if (d.getDateDepense() instanceof Date) {
            localDate = ((Date) d.getDateDepense()).toLocalDate();
        } else {
            localDate = new Date(d.getDateDepense().getTime()).toLocalDate();
        }
        
        Label dateLabel = new Label("📅 " + localDate.format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        dateBox.getChildren().add(dateLabel);
        
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; " +
                           "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> remplirFormulaireAvecDepense(d));
        
        Button btnSupprimer = new Button("🗑️ Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; " +
                            "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> supprimerDepense(d));
        
        actionsBox.getChildren().addAll(btnModifier, btnSupprimer);
        
        carte.getChildren().addAll(idBox, infoBox, montantBox, dateBox, actionsBox);
        
        return carte;
    }
    
    private void remplirFormulaireAvecDepense(depense d) {
        depenseEnModification = d;
        titreField.setText(d.getTitre());
        montantField.setText(String.valueOf(d.getMontant()));
        
        if (d.getDateDepense() != null) {
            if (d.getDateDepense() instanceof Date) {
                datePicker.setValue(((Date) d.getDateDepense()).toLocalDate());
            } else {
                datePicker.setValue(new Date(d.getDateDepense().getTime()).toLocalDate());
            }
        }
        
        for (Categorie cat : categoriesList) {
            if (cat.getIdCat() == d.getIdCategorie()) {
                categorieCombo.setValue(cat);
                break;
            }
        }
        
        ajouterBtn.setText("💾 Enregistrer");
        ajouterBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
        
        titreField.requestFocus();
    }
    
    private void supprimerDepense(depense d) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer cette dépense ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer : " + d.getTitre() + " ?");
        
        Optional<ButtonType> resultat = confirmation.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            depenseService.delete(d);
            afficherAlerte("Succès", "Dépense supprimée avec succès !", Alert.AlertType.INFORMATION);
            chargerDepenses();
            calculerTotal();
        }
    }
    
    private void viderFormulaire() {
        titreField.clear();
        montantField.clear();
        datePicker.setValue(LocalDate.now());
        categorieCombo.getSelectionModel().clearSelection();
    }
    
    private void calculerTotal() {
        double total = depensesList.stream()
                .mapToDouble(depense::getMontant)
                .sum();
        totalLabel.setText(String.format("Total: %.2f DT", total));
    }
    
    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
