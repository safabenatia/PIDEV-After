package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Service;
import services.ServiceService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ServiceController implements Initializable {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField categorieField;

    @FXML
    private TableView<Service> tableService;

    @FXML
    private TableColumn<Service, Integer> colId;

    @FXML
    private TableColumn<Service, String> colNom;

    @FXML
    private TableColumn<Service, String> colDesc;

    @FXML
    private TableColumn<Service, String> colCat;

    private ServiceService serviceService = new ServiceService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id_service"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom_service"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        // Charger les données
        refreshTable();

        // Remplir le formulaire lors de la sélection
        tableService.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        nomField.setText(newSelection.getNom_service());
                        descriptionField.setText(newSelection.getDescription());
                        categorieField.setText(newSelection.getCategorie());
                    }
                }
        );
    }

    @FXML
    public void ajouterService(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        try {
            Service service = new Service();
            service.setNom_service(nomField.getText().trim());
            service.setDescription(descriptionField.getText().trim());
            service.setCategorie(categorieField.getText().trim());

            serviceService.add(service);
            refreshTable();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void modifierService(ActionEvent event) {
        Service selected = tableService.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un service à modifier !");
            return;
        }

        if (!validateFields()) {
            return;
        }

        try {
            selected.setNom_service(nomField.getText().trim());
            selected.setDescription(descriptionField.getText().trim());
            selected.setCategorie(categorieField.getText().trim());

            serviceService.update(selected);
            refreshTable();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service modifié avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void supprimerService(ActionEvent event) {
        Service selected = tableService.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un service à supprimer !");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le service");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer ce service ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                serviceService.delete(selected);
                refreshTable();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé avec succès!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
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

    private void refreshTable() {
        tableService.getItems().setAll(serviceService.getAll());
    }

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        categorieField.clear();
        tableService.getSelectionModel().clearSelection();
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