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

    // ================= INITIALISATION =================
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

    // ================= Rafraîchir =================
    private void refreshData() {
        activiteList = serviceActivite.getAll();
        applyFilterAndSort();
        updateStats();
    }

    // ================= Recherche + Tri =================
    private void applyFilterAndSort() {

        if (activiteList == null) return;

        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase();

        List<Activite> filteredList = activiteList.stream()

                // 🔎 Recherche
                .filter(a ->
                        a.getNom().toLowerCase().contains(keyword) ||
                                a.getCategorie().toLowerCase().contains(keyword) ||
                                a.getLieu().toLowerCase().contains(keyword)
                )

                // 🔽 Tri
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

    // ================= Statistiques =================
    private void updateStats() {

        double totalBudget = 0;

        for (Activite activite : activiteList) {
            totalBudget += activite.getPrix();
        }

        lblTotalBudget.setText(String.format("%.2f DT", totalBudget));
        lblTotalActivites.setText(String.valueOf(activiteList.size()));
        lblLastUpdate.setText("Aujourd'hui");
    }

    // ================= Card =================
    private VBox createActiviteCard(Activite activite) {

        VBox card = new VBox(12);
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.95);
            -fx-padding: 20;
            -fx-background-radius: 15;
            -fx-border-radius: 15;
            -fx-border-color: #E0E0E0;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);
        """);

        Label idLabel = new Label("🆔 ID: " + activite.getIdActivite());
        Label nomLabel = new Label("🏷️ Nom: " + activite.getNom());
        Label catLabel = new Label("📂 Catégorie: " + activite.getCategorie());
        Label lieuLabel = new Label("📍 Lieu: " + activite.getLieu());
        Label prixLabel = new Label("💰 Prix: " + activite.getPrix() + " DT");
        Label descLabel = new Label("📝 " + activite.getDescription());

        descLabel.setWrapText(true);
        prixLabel.setStyle("-fx-font-weight: bold;");

        Button btnEdit = new Button("✏️ Modifier");
        btnEdit.setStyle("""
            -fx-background-color: #F4A261;
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-font-weight: bold;
        """);

        Button btnDelete = new Button("🗑️ Supprimer");
        btnDelete.setStyle("""
            -fx-background-color: #E63946;
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-font-weight: bold;
        """);

        btnEdit.setOnAction(e -> handleEdit(activite));

        btnDelete.setOnAction(e -> {
            serviceActivite.delete(activite);
            refreshData();
        });

        HBox buttonRow = new HBox(15, btnEdit, btnDelete);
        buttonRow.setStyle("-fx-alignment: center-right;");

        card.getChildren().addAll(
                idLabel, nomLabel, catLabel,
                lieuLabel, prixLabel, descLabel,
                buttonRow
        );

        return card;
    }

    // ================= Ajouter =================
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

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout !");
        }
    }

    // ================= Modifier =================
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

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification !");
        }
    }

    // ================= Planning =================
    @FXML
    private void handlePlanning() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planning_list.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Planning");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le Planning !");
        }
    }

    // ================= Alert =================
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


