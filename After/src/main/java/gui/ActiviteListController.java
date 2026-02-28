package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import models.Activite;
import services.ServiceActivite;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;



import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

import services.*;
import utils.Session;


import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
public class ActiviteListController {

    @FXML
    private FlowPane activiteContainer;

    @FXML
    private Label lblTotalBudget, lblTotalActivites, lblLastUpdate;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> comboSort;

    private ServiceActivite serviceActivite = new ServiceActivite();
    private List<Activite> activiteList;
    private PdfExportService pdfExportService = new PdfExportService();
    @FXML
    public void initialize() {

        comboSort.setItems(FXCollections.observableArrayList(
                "Prix: Croissant",
                "Prix: Décroissant"
        ));

        comboSort.setOnAction(e -> applyFilterAndSort());
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilterAndSort());

        refreshData();
    }

    private void refreshData() {
        activiteList = serviceActivite.getAll();
        applyFilterAndSort();
        updateStats();
    }

    private void applyFilterAndSort() {
        if (activiteList == null) return;

        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase();

        List<Activite> filteredList = activiteList.stream()
                .filter(a ->
                        a.getNom().toLowerCase().contains(keyword) ||
                                a.getCategorie().toLowerCase().contains(keyword) ||
                                a.getLieu().toLowerCase().contains(keyword)
                )
                .sorted((a1, a2) -> {
                    if (comboSort.getValue() == null) return 0;
                    if (comboSort.getValue().equals("Prix: Croissant")) {
                        return Double.compare(a1.getPrix(), a2.getPrix());
                    } else {
                        return Double.compare(a2.getPrix(), a1.getPrix());
                    }
                })
                .collect(Collectors.toList());

        activiteContainer.getChildren().clear();

        for (Activite activite : filteredList) {
            VBox card = createActiviteCard(activite);
            activiteContainer.getChildren().add(card);
        }
    }

    private void updateStats() {
        double totalBudget = activiteList.stream().mapToDouble(Activite::getPrix).sum();
        lblTotalBudget.setText(String.format("%.2f DT", totalBudget));
        lblTotalActivites.setText(String.valueOf(activiteList.size()));
        lblLastUpdate.setText("Aujourd'hui");
    }
    @FXML
    private void handleRetourProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DashboardVoyageur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) activiteContainer.getScene().getWindow();
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
            Stage stage = (Stage) activiteContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleVoyages() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) activiteContainer.getScene().getWindow();
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
    private void showactivite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) activiteContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Activite & Planning");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Activtie & Planning");
        }
    }
    @FXML
    private void showDoc() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDocument.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) activiteContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Documents");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Documents");
        }
    }
    @FXML
    private void showReser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserReservationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) activiteContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Reservations");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Reservations");
        }
    }
    // ================= Card =================
    private VBox createActiviteCard(Activite activite) {
        VBox card = new VBox(8); // espacement vertical un peu plus serré
        card.setStyle("""
        -fx-background-color: rgba(255,255,255,0.95);
        -fx-padding: 10;          /* padding réduit */
        -fx-background-radius: 15;
        -fx-border-radius: 15;
        -fx-border-color: #E0E0E0;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);
    """);

        Label idLabel = new Label("🆔 ID: " + activite.getIdActivite());
        idLabel.setStyle("-fx-font-size: 11px;");  // police plus petite

        Label nomLabel = new Label("🏷️ Nom: " + activite.getNom());
        nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Label catLabel = new Label("📂 Catégorie: " + activite.getCategorie());
        catLabel.setStyle("-fx-font-size: 11.5px;");

        Label lieuLabel = new Label("📍 Lieu: " + activite.getLieu());
        lieuLabel.setStyle("-fx-font-size: 11.5px;");

        Label prixLabel = new Label("💰 Prix: " + activite.getPrix() + " DT");
        prixLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Label descLabel = new Label("📝 " + activite.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 11.5px;");

        // Bouton traduction
        Button btnTranslate = new Button("🌐");
        btnTranslate.setStyle("-fx-background-color: transparent; -fx-font-size: 11px;");
        btnTranslate.setOnAction(e -> showTranslationOptions(activite.getDescription(), descLabel));
        HBox descBox = new HBox(4, descLabel, btnTranslate);

        // Boutons actions
        Button btnEdit = new Button("✏️ Modifier");
        btnEdit.setStyle("""
        -fx-background-color: #F4A261;
        -fx-text-fill: white;
        -fx-background-radius: 20;
        -fx-font-weight: bold;
        -fx-font-size: 11px;
    """);

        Button btnDelete = new Button("🗑️ Supprimer");
        btnDelete.setStyle("""
        -fx-background-color: #E63946;
        -fx-text-fill: white;
        -fx-background-radius: 20;
        -fx-font-weight: bold;
        -fx-font-size: 11px;
    """);
        Button pdfBtn = new Button("📄");
        pdfBtn.setStyle("-fx-background-color:#ae4b27; -fx-text-fill:white; -fx-font-weight:bold;");

// Crée le service PDF
        PdfExportService pdfExportService = new PdfExportService();

        pdfBtn.setOnAction(e -> {
            // Choix du fichier
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            // Nom du fichier basé sur le nom de l'activité
            fileChooser.setInitialFileName(activite.getNom().replaceAll("\\s+", "_") + "_activite.pdf");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );

            java.io.File file = fileChooser.showSaveDialog(pdfBtn.getScene().getWindow());
            if (file != null) {
                // Export de l'activité dans le PDF
                pdfExportService.exportActivite(activite, file.getAbsolutePath());

                // Message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("PDF exporté avec succès !");
                alert.showAndWait();
            }
        });
        Button btnItineraire = new Button("🗺 Itinéraire");
        btnItineraire.setStyle("""
        -fx-background-color: #2A9D8F;
        -fx-text-fill: white;
        -fx-background-radius: 20;
        -fx-font-weight: bold;
        -fx-font-size: 11px;
    """);
        btnItineraire.setOnAction(e -> {
            String lieuActivite = activite.getLieu();
            MapViewController mapController = new MapViewController();
            mapController.openItineraire(lieuActivite);
        });


        btnEdit.setOnAction(e -> handleEdit(activite));
        btnDelete.setOnAction(e -> {
            serviceActivite.delete(activite);
            refreshData();
        });

        HBox buttonRow = new HBox(8, btnEdit, btnDelete,pdfBtn ,  btnItineraire);
        buttonRow.setStyle("-fx-alignment: center-right;");

        card.getChildren().addAll(
                idLabel, nomLabel, catLabel,
                lieuLabel, prixLabel, descBox,
                buttonRow
        );

        return card;
    }
    @FXML
    public void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/activite.fxml"));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une activité");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshData();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout !");
        }
    }

    private void handleEdit(Activite activite) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierActivite.fxml"));
            AnchorPane root = loader.load();

            ModifierActiviteController controller = loader.getController();
            controller.setActivite(activite);

            Stage stage = new Stage();
            stage.setTitle("Modifier une activité");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshData();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification !");
        }
    }

    @FXML
    private void handlePlanning() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planning_list.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Planning");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir le Planning !");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ================= Translation =================
    private void showTranslationOptions(String text, Label descLabel) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Anglais", "Anglais", "Arabe", "Allemand");
        dialog.setTitle("Traduction");
        dialog.setHeaderText("Choisissez la langue de traduction");
        dialog.setContentText("Langue :");

        dialog.showAndWait().ifPresent(lang -> {
            String targetLang;
            switch (lang) {
                case "Arabe": targetLang = "ar"; break;
                case "Allemand": targetLang = "de"; break;
                default: targetLang = "en";
            }

            // Appel API MyMemory
            String translated = translateTextMyMemory(text, targetLang);
            if (translated != null) {
                descLabel.setText("📝 " + translated);
            } else {
                showAlert("Erreur", "Impossible de traduire le texte !");
            }
        });
    }

    private String translateTextMyMemory(String text, String targetLang) {
        try {
            String sourceLang = "fr"; // langue source
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String urlString = "https://api.mymemory.translated.net/get?q="
                    + encodedText + "&langpair=" + sourceLang + "|" + targetLang;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "JavaFX-App");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                JSONObject json = new JSONObject(response.toString());
                return json.getJSONObject("responseData").getString("translatedText");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    @FXML
    private void openHolidayPage() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/holiday.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Jours Fériés");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}






