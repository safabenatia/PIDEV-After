package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import models.voyage;
import services.ServiceVoyage;

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

        if (!validateInputs()) return;

        voyage v = new voyage();

        v.setTitre(titreField.getText());
        v.setDescription(descriptionField.getText());
        v.setPrix(Double.parseDouble(prixField.getText()));
        v.setNbPlaces(Integer.parseInt(nbPlacesField.getText()));
        v.setStatut(statutField.getText());
        v.setIdDestination(Integer.parseInt(idDestinationField.getText()));

        v.setDateDebut(new Date(System.currentTimeMillis()));
        v.setDateFin(new Date(System.currentTimeMillis()));

        serviceVoyage.add(v);

        goBack();
    }

    private void resetStyle() {
        titreField.setStyle(null);
        descriptionField.setStyle(null);
        prixField.setStyle(null);
        nbPlacesField.setStyle(null);
        statutField.setStyle(null);
        idDestinationField.setStyle(null);
    }

    private boolean validateInputs() {

        boolean valid = true;

        resetStyle();

        try {
            Double.parseDouble(prixField.getText());
        } catch (Exception e) {
            prixField.setStyle("-fx-border-color:red;");
            valid = false;
        }

        try {
            Integer.parseInt(nbPlacesField.getText());
        } catch (Exception e) {
            nbPlacesField.setStyle("-fx-border-color:red;");
            valid = false;
        }

        try {
            Integer.parseInt(idDestinationField.getText());
        } catch (Exception e) {
            idDestinationField.setStyle("-fx-border-color:red;");
            valid = false;
        }

        if (titreField.getText().isEmpty()) {
            titreField.setStyle("-fx-border-color:red;");
            valid = false;
        }

        if (descriptionField.getText().isEmpty()) {
            descriptionField.setStyle("-fx-border-color:red;");
            valid = false;
        }

        return valid;
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
