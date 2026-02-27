package controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.DateCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import models.Document;
import models.CategorieDocument;

import services.*;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;

public class AjouterDocumentController {

    @FXML private TextField txtNom;
    @FXML private TextField txtChemin;
    @FXML private DatePicker dpAjout;
    @FXML private DatePicker dpExpiration;

    private final CloudinaryService cloudinaryService = new CloudinaryService();
    private File selectedFile; // ← fichier sélectionné

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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.jpg", "*.png", "*.jpeg")
        );
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            txtChemin.setText(selectedFile.getName()); // afficher nom fichier
        }
    }

    @FXML
    private void ajouterDocument() {

        if (txtNom.getText().isEmpty()
                || selectedFile == null
                || dpAjout.getValue() == null) {

            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Veuillez remplir tous les champs et choisir un fichier");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                try {
                    // ===== UPLOAD CLOUDINARY =====
                    String cloudinaryUrl = cloudinaryService.uploadFile(selectedFile);

                    if (cloudinaryUrl == null) {
                        throw new RuntimeException("Erreur upload Cloudinary");
                    }

                    // ===== OCR (sur le fichier local avant upload) =====
                    String texteOCR = ocrService.extractTextFromFile(selectedFile.getAbsolutePath());

                    if (texteOCR == null || texteOCR.isEmpty()) {
                        throw new RuntimeException("OCR vide");
                    }

                    // ===== DETECTION CATEGORIE =====
                    String nomCategorie = classifier.detectCategory(texteOCR);

                    // ===== RECUPERATION OU CREATION CATEGORIE =====
                    CategorieDocument categorie = catService.getAll().stream()
                            .filter(c -> c.getLibelle().equalsIgnoreCase(nomCategorie))
                            .findFirst()
                            .orElse(null);

                    if (categorie == null) {
                        categorie = new CategorieDocument();
                        categorie.setLibelle(nomCategorie);
                        categorie.setDescription("Auto détectée par OCR");
                        catService.add(categorie);

                        categorie = catService.getAll().stream()
                                .filter(c -> c.getLibelle().equalsIgnoreCase(nomCategorie))
                                .findFirst()
                                .orElse(categorie);
                    }

                    // ===== CREATION DOCUMENT avec URL Cloudinary =====
                    Document document = new Document(
                            0,
                            txtNom.getText(),
                            cloudinaryUrl, // ← URL Cloudinary au lieu du chemin local
                            Date.valueOf(dpAjout.getValue()),
                            dpExpiration.getValue() != null
                                    ? Date.valueOf(dpExpiration.getValue())
                                    : null,
                            categorie
                    );

                    docService.add(document);

                    // ===== EMAIL =====
                    mailService.sendMail(
                            "mahdi.bribech12@gmail.com",
                            "Nouveau document ajouté ✈",
                            "Le document \"" + txtNom.getText() + "\" a été ajouté.\n" +
                                    "🔗 Lien: " + cloudinaryUrl
                    );

                    // ===== RAPPEL CALENDRIER =====
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
                    "Document ajouté ✔\n" +
                            "Catégorie détectée automatiquement 🤖\n" +
                            "Fichier stocké sur Cloudinary ☁️");
            clearFields();
        });

        task.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Impossible d'ajouter le document : " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void clearFields() {
        txtNom.clear();
        txtChemin.clear();
        selectedFile = null;
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