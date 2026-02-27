package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Document;
import services.serviceDocument;
import java.awt.Desktop;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;
import java.io.File;
import services.PdfService;
import models.Document;
import utils.Session;

public class AfficherDocumentController {

    @FXML private VBox containerDocuments;
    @FXML private TextField txtSearch;

    private final serviceDocument service = new serviceDocument();
    private List<Document> documents;
    private Document docSelectionne;

    @FXML
    public void initialize() {
        loadDocuments();

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            afficherCartes(
                    documents.stream()
                            .filter(d ->
                                    d.getNomDocument().toLowerCase().contains(newVal.toLowerCase())
                                            || d.getCheminFichier().toLowerCase().contains(newVal.toLowerCase())
                            )
                            .collect(Collectors.toList())
            );
        });
    }

    private void loadDocuments() {
        documents = service.getAll();
        afficherCartes(documents);
    }
    @FXML
    private void handleRetourProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DashboardVoyageur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) containerDocuments.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Espace Voyageur");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) containerDocuments.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleVoyages() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) containerDocuments.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Voyages & Destinations");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Voyages & Destinations");
        }
    }
    @FXML
    private void showser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) containerDocuments.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - service et offre ");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir service et offre");
        }
    }
    @FXML
    private void showact() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/activite_list.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) containerDocuments.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Documents");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Documents");
        }
    }

    void afficherCartes(List<Document> docs) {

        containerDocuments.getChildren().clear();

        for (Document doc : docs) {

            VBox card = new VBox();
            card.setSpacing(12);
            card.setPrefWidth(900);

            card.setStyle("""
                -fx-background-color: white;
                -fx-padding: 25;
                -fx-background-radius: 18;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20,0,0,6);
            """);

            // ===== HEADER =====
            HBox header = new HBox();
            header.setSpacing(10);

            Label lblNom = new Label("✈ " + doc.getNomDocument());
            lblNom.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label badgeCat = new Label(doc.getCategorie().getLibelle());
            badgeCat.setStyle("""
                -fx-background-color:#e8f0ff;
                -fx-text-fill:#1e3c72;
                -fx-padding:5 12;
                -fx-background-radius:20;
                -fx-font-weight:bold;
            """);

            header.getChildren().addAll(lblNom, spacer, badgeCat);

            // ===== INFOS =====
            Label lblAjout = new Label("Ajouté le : " + doc.getDateAjout());
            lblAjout.setStyle("-fx-text-fill:#555;");

            Label lblExp = new Label(
                    doc.getDateExpiration() != null ?
                            "Expire le : " + doc.getDateExpiration()
                            : "Pas de date d'expiration"
            );

            if (doc.getDateExpiration() != null &&
                    doc.getDateExpiration().toLocalDate().isBefore(LocalDate.now())) {
                lblExp.setStyle("-fx-text-fill:#e63946; -fx-font-weight:bold;");
            } else {
                lblExp.setStyle("-fx-text-fill:#555;");
            }

            // ===== ACTIONS =====
            HBox actions = new HBox(15);

            Button btnMod = new Button("✏ Modifier");
            btnMod.setStyle("""
                -fx-background-color:#1e3c72;
                -fx-text-fill:white;
                -fx-background-radius:25;
                -fx-padding:6 18;
            """);

            Button btnSup = new Button("🗑 Supprimer");
            btnSup.setStyle("""
                -fx-background-color:#e63946;
                -fx-text-fill:white;
                -fx-background-radius:25;
                -fx-padding:6 18;
            """);
            // ===== ACTIONS =====


            Button btnPdf = new Button("📄 Télécharger PDF");
            btnPdf.setStyle("""
                -fx-background-color:#2a9d8f;
                -fx-text-fill:white;
                -fx-background-radius:25;
                -fx-padding:6 18;
            """);

            btnPdf.setOnAction(e -> telechargerPDF(doc));
            btnMod.setOnAction(e -> modifierDocument(doc));
            btnSup.setOnAction(e -> {
                service.delete(doc);
                loadDocuments();
            });

            actions.getChildren().addAll(btnPdf,btnMod, btnSup);

            card.getChildren().addAll(header, lblAjout, lblExp, actions);

            containerDocuments.getChildren().add(card);
        }
    }

    @FXML
    private void ouvrirAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDocument.fxml"));
            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(loader.load()));
            stage.showAndWait();
            loadDocuments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategoriesView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            CategoriesController controller = loader.getController();
            controller.setParent(this);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void afficherParCategorie(int idCategorie) {
        List<Document> docs = service.getByCategorie(idCategorie);

        containerDocuments.getChildren().clear();

        if (docs.isEmpty()) {
            Label empty = new Label("Aucun document dans cette catégorie");
            empty.setStyle("-fx-text-fill:#777; -fx-font-size:16px;");
            containerDocuments.getChildren().add(empty);
            return;
        }

        afficherCartes(docs);
    }
    @FXML
    private void exporterTousDocuments() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter tous les documents");
        chooser.setInitialFileName("documents_AFTER.pdf");

        File file = chooser.showSaveDialog(containerDocuments.getScene().getWindow());

        if (file != null) {
            new PdfService().exporterListeDocumentsPDF(documents, file.getAbsolutePath());
            partagerPDF(file);
        }
    }
    @FXML
    private void telechargerPDF(Document doc) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Télécharger PDF");
        chooser.setInitialFileName("document_" + doc.getIdDocument() + ".pdf");

        File file = chooser.showSaveDialog(containerDocuments.getScene().getWindow());

        if (file != null) {
            new PdfService().exporterDocumentPDF(doc, file.getAbsolutePath());
            partagerPDF(file); // ✅ ouverture automatique
        }
    }
    private void partagerPDF(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @FXML
    private void modifierDocument(Document documentSelectionne) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ModifierDocument.fxml")
            );

            Parent root = loader.load();

            ModifierDocumentController controller = loader.getController();
            controller.setDocument(documentSelectionne);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Document");
            stage.showAndWait();

            loadDocuments(); // ✅ refresh

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherCartes() {
        loadDocuments();
    }

    public void refresh() {
        loadDocuments();
    }
}