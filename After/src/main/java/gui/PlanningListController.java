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


    @FXML
    public void initialize() {

        comboSortPlanning.setItems(FXCollections.observableArrayList(
                "Date Croissante",
                "Date Décroissante",
                "Durée Croissante",
                "Durée Décroissante"
        ));
        comboSortPlanning.setValue("Date Croissante");


        comboSortPlanning.valueProperty().addListener((obs, oldVal, newVal) -> applySort());

        refreshData();
    }


    private void refreshData() {
        planningList = servicePlanning.getAll();
        applySort();
        lblTotalPlanning.setText(String.valueOf(planningList.size()));
    }


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


    private VBox createPlanningCard(Planning planning) {

        VBox card = new VBox(8); // espacement vertical réduit
        card.setStyle("""
        -fx-background-color: rgba(255,255,255,0.95);
        -fx-padding: 10;            /* padding réduit */
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #E0E0E0;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 3);  /* ombre légère */
    """);

        HBox idRow = createRow("🆔", "ID Planning: " + planning.getIdPlanning(), "#FF6B6B", 11);
        HBox userRow = createRow("👤", "ID User: " + planning.getIdUser(), "#4ECDC4", 11);

        // 🔹 afficher le nom de l'activité
        Activite act = serviceActivite.getById(planning.getIdActivite());
        String activiteNom = act != null ? act.getNom() : "Inconnu";
        HBox activiteRow = createRow("🏷️", "Activité: " + activiteNom, "#FFA500", 11);

        HBox dateRow = createRow("📅", "Date: " + planning.getDateActivite(), "#2ECC71", 11);
        HBox heureRow = createRow("⏰", "Heure Début: " + planning.getHeureDebut(), "#9B59B6", 11);
        HBox dureeRow = createRow("⏳", "Durée: " + planning.getDuree(), "#ff1e5e", 11);

        HBox buttonRow = new HBox(10); // espacement réduit
        buttonRow.setStyle("-fx-alignment: center-right;");

        Button btnEdit = new Button("✏️ Modifier");
        btnEdit.setStyle("""
        -fx-background-color: #F4A261;
        -fx-text-fill: white;
        -fx-background-radius: 18;
        -fx-font-weight: bold;
        -fx-font-size: 11px;
    """);
        btnEdit.setOnAction(e -> handleEdit(planning));

        Button btnDelete = new Button("🗑️ Supprimer");
        btnDelete.setStyle("""
        -fx-background-color: #E63946;
        -fx-text-fill: white;
        -fx-background-radius: 18;
        -fx-font-weight: bold;
        -fx-font-size: 11px;
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

    // version modifiée de createRow pour réduire taille police et espacement
    private HBox createRow(String emoji, String text, String color, int fontSize) {
        HBox row = new HBox(4); // espacement réduit
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: " + (fontSize + 3) + "px; -fx-text-fill: " + color + ";");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(emojiLabel, textLabel, spacer);
        return row;
    }

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
    @FXML
    private void openCountryPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/country.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des pays");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



