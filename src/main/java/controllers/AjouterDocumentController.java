package controllers;

import javafx.concurrent.Task;
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
import services.GoogleCalendarService;
import services.OcrService;
import services.DocumentClassifier;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;

public class AjouterDocumentController {

    @FXML private TextField txtNom;
    @FXML private TextField txtChemin;
    @FXML private DatePicker dpAjout;
    @FXML private DatePicker dpExpiration;

    private final serviceDocument docService = new serviceDocument();
    private final serviceCategorieDocument catService = new serviceCategorieDocument();
    private final MailService mailService = new MailService();
    private final OcrService ocrService = new OcrService();
    private final DocumentClassifier classifier = new DocumentClassifier();

    @FXML
    public void initialize() {

        dpAjout.setValue(LocalDate.now());

        dpAjout.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (!empty && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        dpExpiration.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (!empty && dpAjout.getValue() != null &&
                        date.isBefore(dpAjout.getValue())) {
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
        if (file != null) txtChemin.setText(file.getAbsolutePath());
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

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                try {
                    // ===== OCR =====
                    String texteOCR = ocrService.extractTextFromFile(txtChemin.getText());

                    if (texteOCR == null || texteOCR.isEmpty()) {
                        throw new RuntimeException("OCR vide");
                    }

                    // ===== détection catégorie =====
                    String nomCategorie = classifier.detectCategory(texteOCR);

                    // ===== récupération ou création catégorie =====
                    CategorieDocument categorie = catService.getAll().stream()
                            .filter(c -> c.getLibelle().equalsIgnoreCase(nomCategorie))
                            .findFirst()
                            .orElse(null);

                    if (categorie == null) {
                        categorie = new CategorieDocument();
                        categorie.setLibelle(nomCategorie);
                        categorie.setDescription("Auto détectée par OCR");
                        catService.add(categorie);

                        // récupérer avec ID
                        categorie = catService.getAll().stream()
                                .filter(c -> c.getLibelle().equalsIgnoreCase(nomCategorie))
                                .findFirst()
                                .orElse(categorie);
                    }

                    // ===== création document =====
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

                    // ===== email =====
                    mailService.sendMail(
                            "mahdi.bribech12@gmail.com",
                            "Nouveau document ajouté ✈",
                            "Le document \"" + txtNom.getText() + "\" a été ajouté automatiquement."
                    );

                    // ===== rappel calendrier =====
                    if (dpExpiration.getValue() != null) {
                        GoogleCalendarService.ajouterRappelExpiration(
                                txtNom.getText(),
                                dpExpiration.getValue()
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert(Alert.AlertType.INFORMATION,
                    "Succès",
                    "Document ajouté ✔\nCatégorie détectée automatiquement 🤖");
            clearFields();
        });

        task.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur OCR",
                    "Impossible d'analyser le document");
        });

        new Thread(task).start();
    }

    private void clearFields() {
        txtNom.clear();
        txtChemin.clear();
        dpAjout.setValue(LocalDate.now());
        dpExpiration.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}