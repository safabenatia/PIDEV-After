package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Planning;
import models.Activite;
import services.ServicePlanning;
import services.ServiceActivite;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PlanningListController {

    @FXML
    private FlowPane planningContainer;

    @FXML
    private ComboBox<String> comboSortPlanning;

    @FXML
    private Label lblTotalPlanning;

    private ServicePlanning servicePlanning = new ServicePlanning();
    private ServiceActivite serviceActivite = new ServiceActivite();
    private List<Planning> planningList;

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        comboSortPlanning.setItems(FXCollections.observableArrayList(
                "Date Croissante",
                "Date Décroissante",
                "Durée Croissante",
                "Durée Décroissante"
        ));
        comboSortPlanning.setValue("Date Croissante");

        // 🔄 Tri dynamique
        comboSortPlanning.valueProperty().addListener((obs, oldVal, newVal) -> applySort());

        refreshData();
    }

    // ================= REFRESH =================
    private void refreshData() {
        planningList = servicePlanning.getAll();
        applySort();
        lblTotalPlanning.setText(String.valueOf(planningList.size()));
    }

    // ================= SORT =================
    private void applySort() {
        if (planningList == null) return;

        List<Planning> sorted = planningList.stream()
                .sorted((p1, p2) -> {
                    String sortValue = comboSortPlanning.getValue();
                    if (sortValue == null) return 0;

                    switch (sortValue) {
                        case "Date Croissante":
                            return p1.getDateActivite().compareTo(p2.getDateActivite());
                        case "Date Décroissante":
                            return p2.getDateActivite().compareTo(p1.getDateActivite());
                        case "Durée Croissante":
                            return Integer.compare(p1.getDuree(), p2.getDuree());
                        case "Durée Décroissante":
                            return Integer.compare(p2.getDuree(), p1.getDuree());
                    }
                    return 0;
                })
                .collect(Collectors.toList());

        planningContainer.getChildren().clear();
        for (Planning p : sorted) {
            planningContainer.getChildren().add(createPlanningCard(p));
        }
    }

    // ================= CARD =================
    private VBox createPlanningCard(Planning planning) {

        VBox card = new VBox(10);
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.95);
            -fx-padding: 15;
            -fx-background-radius: 15;
            -fx-border-radius: 15;
            -fx-border-color: #E0E0E0;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);
        """);

        HBox idRow = createRow("🆔", "ID Planning: " + planning.getIdPlanning(), "#FF6B6B");
        HBox userRow = createRow("👤", "ID User: " + planning.getIdUser(), "#4ECDC4");

        // 🔹 afficher le nom de l'activité
        Activite act = serviceActivite.getById(planning.getIdActivite());
        String activiteNom = act != null ? act.getNom() : "Inconnu";
        HBox activiteRow = createRow("🏷️", "Activité: " + activiteNom, "#FFA500");

        HBox dateRow = createRow("📅", "Date: " + planning.getDateActivite(), "#2ECC71");
        HBox heureRow = createRow("⏰", "Heure Début: " + planning.getHeureDebut(), "#9B59B6");
        HBox dureeRow = createRow("⏳", "Durée: " + planning.getDuree(), "#ff1e5e");

        HBox buttonRow = new HBox(15);
        buttonRow.setStyle("-fx-alignment: center-right;");

        Button btnEdit = new Button("✏️ Modifier");
        btnEdit.setStyle("""
            -fx-background-color: #F4A261;
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-font-weight: bold;
        """);
        btnEdit.setOnAction(e -> handleEdit(planning));

        Button btnDelete = new Button("🗑️ Supprimer");
        btnDelete.setStyle("""
            -fx-background-color: #E63946;
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-font-weight: bold;
        """);
        btnDelete.setOnAction(e -> {
            servicePlanning.delete(planning);
            refreshData();
        });

        buttonRow.getChildren().addAll(btnEdit, btnDelete);

        card.getChildren().addAll(
                idRow, userRow, activiteRow,
                dateRow, heureRow, dureeRow,
                buttonRow
        );

        return card;
    }

    // ================= ROW =================
    private HBox createRow(String emoji, String text, String color) {
        HBox row = new HBox(10);
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(emojiLabel, textLabel, spacer);
        return row;
    }

    // ================= ADD =================
    @FXML
    private void handleAddPlanning() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planning.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Planning");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshData();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire !");
        }
    }

    // ================= EDIT =================
    private void handleEdit(Planning planning) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierPlanning.fxml"));
            Parent root = loader.load();

            ModifierPlanningController controller = loader.getController();
            controller.setPlanning(planning);

            Stage stage = new Stage();
            stage.setTitle("Modifier Planning");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshData();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la modification !");
        }
    }

    // ================= ALERT =================
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/activite_list.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des activités !");
        }
    }
}



