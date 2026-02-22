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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherDocumentController {

    @FXML private VBox containerDocuments;
    @FXML private TextField txtSearch;

    private final serviceDocument service = new serviceDocument();
    private List<Document> documents;

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

    private void afficherCartes(List<Document> docs) {

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

            btnMod.setOnAction(e -> modifierDocument(doc));
            btnSup.setOnAction(e -> {
                service.delete(doc);
                loadDocuments();
            });

            actions.getChildren().addAll(btnMod, btnSup);

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
    private void modifierDocument(Document documentSelectionne) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ModifierDocument.fxml")
            );

            Parent root = loader.load();

            // 🔥 RÉCUPÉRER LE CONTROLLER
            ModifierDocumentController controller = loader.getController();

            // 🔥 PASSER LE DOCUMENT AU CONTROLLER
            controller.setDocument(documentSelectionne);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Document");
            stage.showAndWait();

            afficherCartes(); // refresh après modification

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherCartes() {
    }

    public void refresh() {
        loadDocuments();
    }
}