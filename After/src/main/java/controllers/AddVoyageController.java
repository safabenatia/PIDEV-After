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
