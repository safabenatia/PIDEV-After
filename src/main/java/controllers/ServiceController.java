package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Service;
import services.ServiceService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ServiceController implements Initializable {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField categorieField;

    @FXML
    private VBox servicesVBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button closeButton;

    private ServiceService serviceService = new ServiceService();
    private Service serviceSelectionne = null;
    private Button selectedButton = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurer les boutons de fenêtre
        setupWindowButtons();

        // Configuration du ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: #16325c; -fx-border-radius: 8;");

        // Charger les données
        refreshList();
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

    private void refreshList() {
        servicesVBox.getChildren().clear();

        List<Service> services = serviceService.getAll();

        if (services.isEmpty()) {
            Label emptyLabel = new Label("Aucun service disponible");
            emptyLabel.setStyle("-fx-text-fill: #16325c; -fx-font-size: 16px; -fx-padding: 20;");
            servicesVBox.getChildren().add(emptyLabel);
        } else {
            for (Service service : services) {
                VBox serviceCard = createServiceCard(service);
                servicesVBox.getChildren().add(serviceCard);
            }
        }
    }

    private VBox createServiceCard(Service service) {
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

        // En-tête
        HBox header = new HBox();
        header.setStyle("-fx-alignment: CENTER_LEFT; -fx-spacing: 10;");

        Label idLabel = new Label("#" + service.getId_service());
        idLabel.setStyle(
                "-fx-background-color: #16325c;" +
                        "-fx-text-fill: #F5F5DC;" +
                        "-fx-padding: 5 10;" +
                        "-fx-background-radius: 15;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );

        Label categoryLabel = new Label(service.getCategorie());
        categoryLabel.setStyle(
                "-fx-background-color: #F5F5DC;" +
                        "-fx-text-fill: #16325c;" +
                        "-fx-padding: 5 10;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #16325c;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15;" +
                        "-fx-font-size: 12px;"
        );

        Region spacer = new Region();
        spacer.setPrefWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(idLabel, spacer, categoryLabel);

        // Titre
        Label titleLabel = new Label(service.getNom_service());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #16325c;");

        // Description
        Label descLabel = new Label(service.getDescription());
        descLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");
        descLabel.setWrapText(true);

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
        selectBtn.setOnAction(e -> selectService(service, selectBtn));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle(
                "-fx-background-color: #d32f2f;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        deleteBtn.setOnAction(e -> deleteService(service));

        actions.getChildren().addAll(selectBtn, deleteBtn);

        card.getChildren().addAll(header, titleLabel, descLabel, actions);
        return card;
    }

    private void selectService(Service service, Button selectBtn) {
        if (selectedButton != null) {
            selectedButton.setStyle(
                    "-fx-background-color: #16325c;" +
                            "-fx-text-fill: #F5F5DC;" +
                            "-fx-padding: 8 15;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );
        }

        serviceSelectionne = service;
        selectedButton = selectBtn;
        selectBtn.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );

        nomField.setText(service.getNom_service());
        descriptionField.setText(service.getDescription());
        categorieField.setText(service.getCategorie());
    }

    private void deleteService(Service service) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le service");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer ce service ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                serviceService.delete(service);
                if (serviceSelectionne != null && serviceSelectionne.getId_service() == service.getId_service()) {
                    clearFields();
                }
                refreshList();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé avec succès!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void ajouterService(ActionEvent event) {
        if (!validateFields()) return;

        try {
            Service service = new Service();
            service.setNom_service(nomField.getText().trim());
            service.setDescription(descriptionField.getText().trim());
            service.setCategorie(categorieField.getText().trim());

            serviceService.add(service);
            refreshList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void modifierService(ActionEvent event) {
        if (serviceSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un service à modifier !");
            return;
        }

        if (!validateFields()) return;

        try {
            serviceSelectionne.setNom_service(nomField.getText().trim());
            serviceSelectionne.setDescription(descriptionField.getText().trim());
            serviceSelectionne.setCategorie(categorieField.getText().trim());

            serviceService.update(serviceSelectionne);
            refreshList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service modifié avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void supprimerService(ActionEvent event) {
        if (serviceSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un service à supprimer !");
            return;
        }
        deleteService(serviceSelectionne);
    }

    @FXML
    public void annuler(ActionEvent event) {
        clearFields();
    }

    @FXML
    public void handleDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le dashboard: " + e.getMessage());
        }
    }

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        categorieField.clear();
        serviceSelectionne = null;
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

    private boolean validateFields() {
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du service est obligatoire.");
            return false;
        }
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La description est obligatoire.");
            return false;
        }
        if (categorieField.getText() == null || categorieField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La catégorie est obligatoire.");
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