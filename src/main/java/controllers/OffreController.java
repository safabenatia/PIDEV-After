package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Offre;
import models.Service;
import services.OffreService;
import services.ServiceService;
import api.MeteoAPI;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class OffreController implements Initializable {

    @FXML
    private TextField TitreOffreField;

    @FXML
    private TextField PrixOffreField;

    @FXML
    private TextField DureeOffreField;

    @FXML
    private ComboBox<Service> serviceCombo;

    @FXML
    private VBox offresVBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button closeButton;

    private OffreService offreService = new OffreService();
    private ServiceService serviceService = new ServiceService();
    private MeteoAPI meteoAPI = new MeteoAPI();
    private Offre offreSelectionne = null;
    private Button selectedButton = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupWindowButtons();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: #16325c; -fx-border-radius: 8;");
        chargerServices();
        chargerOffres();
    }

    private void setupWindowButtons() {
        if (minimizeButton != null) {
            minimizeButton.setOnAction(event -> {
                Stage stage = (Stage) minimizeButton.getScene().getWindow();
                stage.setIconified(true);
            });
        }

        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            });
        }
    }

    private void chargerServices() {
        try {
            List<Service> services = serviceService.getAll();
            if (services != null) {
                serviceCombo.setItems(javafx.collections.FXCollections.observableArrayList(services));

                serviceCombo.setCellFactory(lv -> new ListCell<Service>() {
                    @Override
                    protected void updateItem(Service service, boolean empty) {
                        super.updateItem(service, empty);
                        if (empty || service == null) {
                            setText(null);
                        } else {
                            setText(service.getNom_service() + " (ID: " + service.getId_service() + ")");
                        }
                    }
                });

                serviceCombo.setButtonCell(new ListCell<Service>() {
                    @Override
                    protected void updateItem(Service service, boolean empty) {
                        super.updateItem(service, empty);
                        if (empty || service == null) {
                            setText(null);
                        } else {
                            setText(service.getNom_service());
                        }
                    }
                });
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des services: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerOffres() {
        offresVBox.getChildren().clear();

        try {
            List<Offre> offres = offreService.getAll();

            if (offres.isEmpty()) {
                Label emptyLabel = new Label("Aucune offre disponible");
                emptyLabel.setStyle("-fx-text-fill: #16325c; -fx-font-size: 16px; -fx-padding: 20;");
                offresVBox.getChildren().add(emptyLabel);
            } else {
                for (Offre offre : offres) {
                    VBox offreCard = createOffreCard(offre);
                    offresVBox.getChildren().add(offreCard);
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des offres: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createOffreCard(Offre offre) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #16325c;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 15;" +
                        "-fx-spacing: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        card.setPrefWidth(450);

        card.setOnMouseEntered(e ->
                card.setStyle(card.getStyle() + "-fx-effect: dropshadow(gaussian, #16325c, 10, 0, 0, 5);")
        );
        card.setOnMouseExited(e ->
                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-border-color: #16325c;" +
                                "-fx-border-width: 1px;" +
                                "-fx-border-radius: 8;" +
                                "-fx-background-radius: 8;" +
                                "-fx-padding: 15;" +
                                "-fx-spacing: 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                )
        );

        // En-tête avec ID
        HBox header = new HBox();
        header.setStyle("-fx-alignment: CENTER_LEFT; -fx-spacing: 10;");

        Label idLabel = new Label("#" + offre.getId_offre());
        idLabel.setStyle(
                "-fx-background-color: #16325c;" +
                        "-fx-text-fill: #F5F5DC;" +
                        "-fx-padding: 5 10;" +
                        "-fx-background-radius: 15;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );

        Label prixLabel = new Label(String.format("%.2f DT", offre.getPrix()));
        prixLabel.setStyle(
                "-fx-background-color: #F5F5DC;" +
                        "-fx-text-fill: #16325c;" +
                        "-fx-padding: 5 10;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #16325c;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );

        Region spacer = new Region();
        spacer.setPrefWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(idLabel, spacer, prixLabel);

        // Titre
        Label titleLabel = new Label(offre.getTitre());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #16325c;");
        titleLabel.setWrapText(true);

        // Détails
        HBox detailsBox = new HBox();
        detailsBox.setStyle("-fx-spacing: 15; -fx-alignment: CENTER_LEFT;");

        Label dureeLabel = new Label("⏱️ " + offre.getDuree() + " jours");
        dureeLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        String serviceName = "Service inconnu";
        try {
            List<Service> services = serviceService.getAll();
            for (Service s : services) {
                if (s.getId_service() == offre.getServiceId()) {
                    serviceName = s.getNom_service();
                    break;
                }
            }
        } catch (Exception e) {
            serviceName = "Service inconnu";
        }

        Label serviceLabel = new Label("🏨 " + serviceName);
        serviceLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        detailsBox.getChildren().addAll(dureeLabel, serviceLabel);

        // Boutons
        HBox actions = new HBox();
        actions.setStyle("-fx-alignment: CENTER_RIGHT; -fx-spacing: 10; -fx-padding: 10 0 0 0;");

        Button selectBtn = new Button("Sélectionner");
        selectBtn.setStyle(
                "-fx-background-color: #16325c;" +
                        "-fx-text-fill: #F5F5DC;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        selectBtn.setOnAction(e -> selectOffre(offre, selectBtn));

        Button qrBtn = new Button("📱 QR Code");
        qrBtn.setStyle(
                "-fx-background-color: #9c27b0;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        qrBtn.setOnAction(e -> {
            // À implémenter plus tard
            showAlert(Alert.AlertType.INFORMATION, "QR Code", "Fonctionnalité à venir");
        });

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle(
                "-fx-background-color: #d32f2f;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        deleteBtn.setOnAction(e -> supprimerOffre(offre));

        actions.getChildren().addAll(selectBtn, qrBtn, deleteBtn);

        card.getChildren().addAll(header, titleLabel, detailsBox, actions);
        return card;
    }

    private void selectOffre(Offre offre, Button selectBtn) {
        if (selectedButton != null) {
            selectedButton.setStyle(
                    "-fx-background-color: #16325c;" +
                            "-fx-text-fill: #F5F5DC;" +
                            "-fx-padding: 8 15;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );
        }

        offreSelectionne = offre;
        selectedButton = selectBtn;
        selectBtn.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );

        TitreOffreField.setText(offre.getTitre());
        PrixOffreField.setText(String.valueOf(offre.getPrix()));
        DureeOffreField.setText(String.valueOf(offre.getDuree()));

        List<Service> services = serviceService.getAll();
        for (Service s : services) {
            if (s.getId_service() == offre.getServiceId()) {
                serviceCombo.setValue(s);
                break;
            }
        }
    }

    // NOUVELLE MÉTHODE: Suggérer offre basée sur la météo
    @FXML
    private void suggererOffreMeteo() {
        TextInputDialog dialog = new TextInputDialog("Tunis");
        dialog.setTitle("Offre Météo");
        dialog.setHeaderText("Suggestion d'offre basée sur la météo");
        dialog.setContentText("Entrez le nom de la ville:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ville -> {
            String suggestion = meteoAPI.suggererOffre(ville, "");
            String icone = meteoAPI.getIcone(ville);
            String desc = meteoAPI.getDescription(ville);
            double temp = meteoAPI.getTemperature(ville);

            TitreOffreField.setText(suggestion);

            String message = String.format(
                    "🌍 Ville: %s\n%s %s\n🌡️ Température: %.1f°C\n\n💡 Suggestion générée!",
                    ville, icone, desc, temp
            );

            showAlert(Alert.AlertType.INFORMATION, "Météo à " + ville, message);
        });
    }

    // NOUVELLE MÉTHODE: Afficher la météo
    @FXML
    private void afficherMeteo() {
        TextInputDialog dialog = new TextInputDialog("Tunis");
        dialog.setTitle("Météo en direct");
        dialog.setHeaderText("Consultez la météo pour adapter vos offres");
        dialog.setContentText("Entrez le nom de la ville:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ville -> {
            String icone = meteoAPI.getIcone(ville);
            String desc = meteoAPI.getDescription(ville);
            double temp = meteoAPI.getTemperature(ville);
            int humidite = meteoAPI.getHumidite(ville);
            double vent = meteoAPI.getVent(ville);

            String message = String.format(
                    "📍 %s\n\n%s %s\n🌡️ Température: %.1f°C\n💧 Humidité: %d%%\n💨 Vent: %.1f m/s\n\nUtilisez 'Suggérer offre météo' pour créer une offre adaptée!",
                    ville, icone, desc, temp, humidite, vent
            );

            showAlert(Alert.AlertType.INFORMATION, "Météo à " + ville, message);
        });
    }

    @FXML
    private void ajouterOffre() {
        if (!validateFields()) return;

        try {
            Offre offre = new Offre(
                    TitreOffreField.getText().trim(),
                    Double.parseDouble(PrixOffreField.getText().trim()),
                    Integer.parseInt(DureeOffreField.getText().trim()),
                    serviceCombo.getValue().getId_service()
            );

            offreService.add(offre);
            chargerOffres();
            annuler();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre ajoutée avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierOffre() {
        if (offreSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une offre à modifier.");
            return;
        }

        if (!validateFields()) return;

        try {
            offreSelectionne.setTitre(TitreOffreField.getText().trim());
            offreSelectionne.setPrix(Double.parseDouble(PrixOffreField.getText().trim()));
            offreSelectionne.setDuree(Integer.parseInt(DureeOffreField.getText().trim()));
            offreSelectionne.setServiceId(serviceCombo.getValue().getId_service());

            offreService.update(offreSelectionne);
            chargerOffres();
            annuler();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre modifiée avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void supprimerOffre(Offre offre) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'offre");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                offreService.delete(offre);
                if (offreSelectionne != null && offreSelectionne.getId_offre() == offre.getId_offre()) {
                    annuler();
                }
                chargerOffres();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée avec succès!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void supprimerOffre() {
        if (offreSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une offre à supprimer.");
            return;
        }
        supprimerOffre(offreSelectionne);
    }

    @FXML
    private void annuler() {
        TitreOffreField.clear();
        PrixOffreField.clear();
        DureeOffreField.clear();
        serviceCombo.setValue(null);
        offreSelectionne = null;
        if (selectedButton != null) {
            selectedButton.setStyle(
                    "-fx-background-color: #16325c;" +
                            "-fx-text-fill: #F5F5DC;" +
                            "-fx-padding: 8 15;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );
            selectedButton = null;
        }
    }

    @FXML
    private void handleDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            Stage stage = (Stage) TitreOffreField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le dashboard: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (TitreOffreField.getText() == null || TitreOffreField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre de l'offre est obligatoire.");
            return false;
        }
        if (PrixOffreField.getText() == null || PrixOffreField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix est obligatoire.");
            return false;
        }
        if (DureeOffreField.getText() == null || DureeOffreField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La durée est obligatoire.");
            return false;
        }
        if (serviceCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un service.");
            return false;
        }

        try {
            Double.parseDouble(PrixOffreField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre valide.");
            return false;
        }

        try {
            Integer.parseInt(DureeOffreField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La durée doit être un nombre entier.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}