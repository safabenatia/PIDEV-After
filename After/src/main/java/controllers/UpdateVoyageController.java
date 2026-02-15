package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import models.voyage;
import services.ServiceVoyage;

import java.sql.Date;
import java.util.List;

public class UpdateVoyageController {

    @FXML
    private TextField idField;
    @FXML
    private TextField titreField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField nbPlacesField;
    @FXML
    private TextField statutField;
    @FXML
    private TextField idDestinationField;

    private ServiceVoyage serviceVoyage = new ServiceVoyage();

    @FXML
    public void loadVoyage() {

        int id = Integer.parseInt(idField.getText());
        List<voyage> voyages = serviceVoyage.getAll();

        for (voyage v : voyages) {
            if (v.getIdVoyage() == id) {

                titreField.setText(v.getTitre());
                descriptionField.setText(v.getDescription());
                prixField.setText(String.valueOf(v.getPrix()));
                nbPlacesField.setText(String.valueOf(v.getNbPlaces()));
                statutField.setText(v.getStatut());
                idDestinationField.setText(String.valueOf(v.getIdDestination()));
                break;
            }
        }
    }

    @FXML
    public void updateVoyage() {

        voyage v = new voyage();

        v.setIdVoyage(Integer.parseInt(idField.getText()));
        v.setTitre(titreField.getText());
        v.setDescription(descriptionField.getText());
        v.setPrix(Double.parseDouble(prixField.getText()));
        v.setNbPlaces(Integer.parseInt(nbPlacesField.getText()));
        v.setStatut(statutField.getText());
        v.setIdDestination(Integer.parseInt(idDestinationField.getText()));

        // 🔥 IMPORTANT FIX FOR DATE
        v.setDateDebut(Date.valueOf("2026-03-01"));
        v.setDateFin(Date.valueOf("2026-03-15"));

        serviceVoyage.update(v);

        goBack();
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            StackPane mainContent =
                    (StackPane) idField.getScene().lookup("#mainContent");

            mainContent.getChildren().clear();
            mainContent.getChildren().add(root.lookup("#mainContent"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
