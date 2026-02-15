package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import models.Service;
import models.Offre;
import services.ServiceService;
import services.OffreService;

import java.util.List;

public class OffreController {

    @FXML
    private TextField TitreOffreField;

    @FXML
    private TextField PrixOffreField;

    @FXML
    private TextField DureeOffreField;

    @FXML
    private ComboBox<Service> serviceCombo;

    private ServiceService serviceService = new ServiceService();
    private OffreService offreService = new OffreService();

    @FXML
    public void initialize() {

        List<Service> list = serviceService.getAll();

        ObservableList<Service> observableList =
                FXCollections.observableArrayList(list);

        serviceCombo.setItems(observableList);
    }

    @FXML
    public void AddOffre() {

        if (TitreOffreField.getText().isEmpty()
                || PrixOffreField.getText().isEmpty()
                || DureeOffreField.getText().isEmpty()
                || serviceCombo.getSelectionModel().getSelectedItem() == null) {

            showAlert("Veuillez remplir tous les champs !");
            return;
        }

        try {
            String titre = TitreOffreField.getText();
            double prix = Double.parseDouble(PrixOffreField.getText());
            int duree = Integer.parseInt(DureeOffreField.getText());
            int serviceId = serviceCombo.getSelectionModel().getSelectedItem().getId_service();

            Offre offre = new Offre(titre, prix, duree, serviceId);

            offreService.add(offre);

            clearFields();

            showAlert("Offre ajoutée avec succès !");

        } catch (NumberFormatException e) {
            showAlert("Prix et durée doivent être numériques !");
        }
    }

    private void clearFields() {
        TitreOffreField.clear();
        PrixOffreField.clear();
        DureeOffreField.clear();
        serviceCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
