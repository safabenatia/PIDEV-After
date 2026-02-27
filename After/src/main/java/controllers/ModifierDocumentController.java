package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.DateCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.CategorieDocument;
import models.Document;
import services.serviceCategorieDocument;
import services.serviceDocument;
import services.MailService;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ModifierDocumentController {

    @FXML private TextField txtNom;
    @FXML private TextField txtChemin;
    @FXML private DatePicker dpAjout;
    @FXML private DatePicker dpExpiration;
    @FXML private ComboBox<CategorieDocument> cbCategorie;

    private final serviceDocument docService = new serviceDocument();
    private final serviceCategorieDocument catService = new serviceCategorieDocument();
    private final MailService mailService = new MailService();

    private Document document;

    @FXML
    public void initialize() {

        // Charger catégories
        List<CategorieDocument> categories = catService.getAll();
        cbCategorie.getItems().addAll(categories);
        cbCategorie.setEditable(true);

        // ❌ Interdire date ajout avant aujourd’hui
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

        // ❌ Interdire expiration avant date ajout
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

    public void setDocument(Document document) {
        this.document = document;

        txtNom.setText(document.getNomDocument());
        txtChemin.setText(document.getCheminFichier());

        if (document.getDateAjout() != null)
            dpAjout.setValue(document.getDateAjout().toLocalDate());

        if (document.getDateExpiration() != null)
            dpExpiration.setValue(document.getDateExpiration().toLocalDate());

        cbCategorie.setValue(document.getCategorie());
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
    private void modifierDocument() {

        if (txtNom.getText().isEmpty()
                || txtChemin.getText().isEmpty()
                || dpAjout.getValue() == null) {

            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Veuillez remplir tous les champs obligatoires");
            return;
        }

        // ❌ Date ajout < aujourd’hui
        if (dpAjout.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur Date",
                    "La date d'ajout ne peut pas être avant aujourd'hui");
            return;
        }

        // ❌ Expiration avant ajout
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

        // Si catégorie nouvelle → créer
        if (categorie == null) {
            categorie = new CategorieDocument();
            categorie.setLibelle(nomCategorie);
            categorie.setDescription("Document de voyage");
            catService.add(categorie);
            cbCategorie.getItems().add(categorie);
        }

        // Modifier document
        document.setNomDocument(txtNom.getText());
        document.setCheminFichier(txtChemin.getText());
        document.setDateAjout(Date.valueOf(dpAjout.getValue()));
        document.setDateExpiration(
                dpExpiration.getValue() != null
                        ? Date.valueOf(dpExpiration.getValue())
                        : null
        );
        document.setCategorie(categorie);

        docService.update(document);

        // ✅ Envoi email après modification
        mailService.sendMail(
                "mahdi.bribech12@gmail.com",
                "Document modifié ✏",
                "Le document \"" + txtNom.getText() + "\" a été modifié avec succès."
        );

        showAlert(Alert.AlertType.INFORMATION,
                "Succès",
                "Document modifié avec succès ✔\nEmail envoyé 📩");

        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}