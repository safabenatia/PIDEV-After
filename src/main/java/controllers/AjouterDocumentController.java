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
import services.MailService;

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
    private final MailService mailService = new MailService();

    @FXML
    public void initialize() {

        List<CategorieDocument> categories = catService.getAll();
        cbCategorie.getItems().addAll(categories);
        cbCategorie.setEditable(true);

        // Désactiver dates avant aujourd'hui
        dpAjout.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) return;
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        // Désactiver expiration avant date ajout
        dpExpiration.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null || dpAjout.getValue() == null) return;
                if (date.isBefore(dpAjout.getValue())) {
                    setDisable(true);
                }
            }
        });
    }

    @FXML
    private void choisirFichier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un document");

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            txtChemin.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void ajouterDocument() {

        if (txtNom.getText().isEmpty()
                || txtChemin.getText().isEmpty()
                || dpAjout.getValue() == null) {

            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Veuillez remplir tous les champs obligatoires");
            return;
        }

        if (dpAjout.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur Date",
                    "La date d'ajout ne peut pas être avant aujourd'hui");
            return;
        }

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

        CategorieDocument categorie = catService.getAll().stream()
                .filter(c -> c.getLibelle().equalsIgnoreCase(nomCategorie))
                .findFirst()
                .orElse(null);

        if (categorie == null) {
            categorie = new CategorieDocument();
            categorie.setLibelle(nomCategorie);
            categorie.setDescription("Document de voyage");
            catService.add(categorie);
            cbCategorie.getItems().add(categorie);
        }

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

        // ✅ Envoi Email
        mailService.sendMail(
                "mahdi.bribech12@gmail.com",
                "Nouveau document ajouté ✈",
                "Le document \"" + txtNom.getText() + "\" a été ajouté avec succès."
        );

        showAlert(Alert.AlertType.INFORMATION,
                "Succès",
                "Document ajouté avec succès ✔\nEmail envoyé 📩");

        clearFields();
    }

    private void clearFields() {
        txtNom.clear();
        txtChemin.clear();
        dpAjout.setValue(null);
        dpExpiration.setValue(null);
        cbCategorie.setValue(null);
        cbCategorie.getEditor().clear();
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}