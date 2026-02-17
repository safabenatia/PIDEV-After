package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import models.voyage;
import services.ServiceVoyage;
import javafx.scene.control.Alert;

import java.sql.Date;

public class AddVoyageController {

    @FXML private TextField titreField;
    @FXML private TextField descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField nbPlacesField;
    @FXML private TextField statutField;
    @FXML private TextField idDestinationField;

    private ServiceVoyage serviceVoyage = new ServiceVoyage();

    @FXML
    public void addVoyage() {

        if (titreField.getText().isEmpty()) {
            showAlert("Titre vide !");
            return;
        }

        if (descriptionField.getText().isEmpty()) {
            showAlert("Description vide !");
            return;
        }

        double prix;
        int nbPlaces;
        int idDestination;

        try {
            prix = Double.parseDouble(prixField.getText());
        } catch (Exception e) {
            showAlert("Prix doit être un nombre !");
            return;
        }

        try {
            nbPlaces = Integer.parseInt(nbPlacesField.getText());
        } catch (Exception e) {
            showAlert("Nombre de places doit être un entier !");
            return;
        }

        try {
            idDestination = Integer.parseInt(idDestinationField.getText());
        } catch (Exception e) {
            showAlert("ID Destination doit être un nombre !");
            return;
        }

        voyage v = new voyage();
        v.setTitre(titreField.getText());
        v.setDescription(descriptionField.getText());
        v.setPrix(prix);
        v.setNbPlaces(nbPlaces);
        v.setStatut(statutField.getText());
        v.setIdDestination(idDestination);

        v.setDateDebut(new java.sql.Date(System.currentTimeMillis()));
        v.setDateFin(new java.sql.Date(System.currentTimeMillis()));

        serviceVoyage.add(v);

        goBack();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    public void goBack() {
        try {
            StackPane mainContent = (StackPane) titreField.getScene().lookup("#mainContent");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            mainContent.getChildren().clear();
            mainContent.getChildren().add(root.lookup("#mainContent"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
