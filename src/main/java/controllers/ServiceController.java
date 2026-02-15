package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Service;
import services.ServiceService;

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

        colId.setCellValueFactory(new PropertyValueFactory<>("id_service"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom_service"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        tableService.getItems().setAll(serviceService.getAll());

        tableService.setOnMouseClicked(event -> {
            Service s = tableService.getSelectionModel().getSelectedItem();
            if (s != null) {
                nomField.setText(s.getNom_service());
                descriptionField.setText(s.getDescription());
                categorieField.setText(s.getCategorie());
            }
        });
    }

    @FXML
    public void ajouterService(ActionEvent event) {

        if (nomField.getText().isEmpty()
                || descriptionField.getText().isEmpty()
                || categorieField.getText().isEmpty()) {

            showAlert("Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        Service service = new Service();
        service.setNom_service(nomField.getText());
        service.setDescription(descriptionField.getText());
        service.setCategorie(categorieField.getText());

        serviceService.add(service);
        refreshTable();
        clearFields();
    }

    @FXML
    public void modifierService(ActionEvent event) {

        Service selected = tableService.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Erreur", "Sélectionnez un service à modifier !");
            return;
        }

        selected.setNom_service(nomField.getText());
        selected.setDescription(descriptionField.getText());
        selected.setCategorie(categorieField.getText());

        serviceService.update(selected);
        refreshTable();
        clearFields();
    }

    @FXML
    public void supprimerService(ActionEvent event) {

        Service selected = tableService.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Erreur", "Sélectionnez un service à supprimer !");
            return;
        }

        serviceService.delete(selected);
        refreshTable();
        clearFields();
    }

    private void refreshTable() {
        tableService.getItems().setAll(serviceService.getAll());
    }

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        categorieField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
