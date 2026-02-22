package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.DateCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Document;
import models.CategorieDocument;
import services.serviceDocument;
import services.serviceCategorieDocument;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AjouterDocumentController {

    @FXML private TextField txtNom;
    @FXML private TextField txtChemin;
    @FXML private DatePicker dpAjout;
    @FXML private DatePicker dpExpiration;
    @FXML private ComboBox<CategorieDocument> cbCategorie;

    private final serviceDocument docService = new serviceDocument();
    private final serviceCategorieDocument catService = new serviceCategorieDocument();

    @FXML
    public void initialize() {

        // Charger catégories
        List<CategorieDocument> categories = catService.getAll();
        cbCategorie.getItems().addAll(categories);
        cbCategorie.setEditable(true);

        // ===============================
        // 🔹 Désactiver dates avant aujourd'hui pour date d'ajout
        // ===============================
        dpAjout.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) return;

                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // ===============================
        // 🔹 Désactiver dates avant date d'ajout pour expiration
        // ===============================
        dpExpiration.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null || dpAjout.getValue() == null) return;

                if (date.isBefore(dpAjout.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    // ===============================
    // 📂 Choisir fichier
    // ===============================
    @FXML
    private void choisirFichier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un document de voyage");

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            txtChemin.setText(file.getAbsolutePath());
        }
    }

    // ===============================
    // ➕ Ajouter document
    // ===============================
    @FXML
    private void ajouterDocument() {

        // Vérification champs obligatoires
        if (txtNom.getText().isEmpty()
                || txtChemin.getText().isEmpty()
                || dpAjout.getValue() == null) {

            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Veuillez remplir tous les champs obligatoires");
            return;
        }

        // Vérifier date ajout >= aujourd'hui
        if (dpAjout.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur Date",
                    "La date d'ajout ne peut pas être avant aujourd'hui");
            return;
        }

        // Vérifier expiration > ajout
        if (dpExpiration.getValue() != null &&
                dpExpiration.getValue().isBefore(dpAjout.getValue())) {

            showAlert(Alert.AlertType.ERROR,
                    "Erreur Date",
                    "La date d'expiration doit être après la date d'ajout");
            return;
        }

        String nomCategorie = cbCategorie.getEditor().getText().trim();

        if (nomCategorie.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Veuillez choisir une catégorie");
            return;
        }

        // Vérifier si catégorie existe
        CategorieDocument categorie = catService.getAll().stream()
                .filter(c -> c.getLibelle().equalsIgnoreCase(nomCategorie))
                .findFirst()
                .orElse(null);

        // Si catégorie n'existe pas → créer nouvelle
        if (categorie == null) {
            categorie = new CategorieDocument();
            categorie.setLibelle(nomCategorie);
            categorie.setDescription("Document de voyage");

            catService.add(categorie);
            cbCategorie.getItems().add(categorie);
        }

        // Créer document
        Document document = new Document(
                0,
                txtNom.getText(),
                txtChemin.getText(),
                Date.valueOf(dpAjout.getValue()),
                dpExpiration.getValue() != null
                        ? Date.valueOf(dpExpiration.getValue())
                        : null,
                categorie
        );

        docService.add(document);

        showAlert(Alert.AlertType.INFORMATION,
                "Succès",
                "Document ajouté avec succès ✔");

        clearFields();
    }

    // ===============================
    // 🔄 Reset champs
    // ===============================
    private void clearFields() {
        txtNom.clear();
        txtChemin.clear();
        dpAjout.setValue(null);
        dpExpiration.setValue(null);
        cbCategorie.setValue(null);
        cbCategorie.getEditor().clear();
    }

    // ===============================
    // 🔔 Alert helper
    // ===============================
    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}