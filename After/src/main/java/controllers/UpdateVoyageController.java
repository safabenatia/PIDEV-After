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
    public void setVoyage(voyage v) {
        idField.setText(String.valueOf(v.getIdVoyage()));
        titreField.setText(v.getTitre());
        descriptionField.setText(v.getDescription());
        prixField.setText(String.valueOf(v.getPrix()));
        nbPlacesField.setText(String.valueOf(v.getNbPlaces()));
        statutField.setText(v.getStatut());
        idDestinationField.setText(String.valueOf(v.getIdDestination()));
    }
    @FXML
    public void updateVoyage() {

        if (idField.getText().isEmpty()) {
            showAlert("ID vide !");
            return;
        }

        int id;
        double prix;
        int nbPlaces;
        int idDestination;

        try {
            id = Integer.parseInt(idField.getText());
        } catch (Exception e) {
            showAlert("ID doit être un nombre !");
            return;
        }

        if (titreField.getText().isEmpty()) {
            showAlert("Titre vide !");
            return;
        }

        try {
            prix = Double.parseDouble(prixField.getText());
        } catch (Exception e) {
            showAlert("Prix invalide !");
            return;
        }

        try {
            nbPlaces = Integer.parseInt(nbPlacesField.getText());
        } catch (Exception e) {
            showAlert("Nombre de places invalide !");
            return;
        }

        try {
            idDestination = Integer.parseInt(idDestinationField.getText());
        } catch (Exception e) {
            showAlert("ID Destination invalide !");
            return;
        }

        voyage v = new voyage();
        v.setIdVoyage(id);
        v.setTitre(titreField.getText());
        v.setDescription(descriptionField.getText());
        v.setPrix(prix);
        v.setNbPlaces(nbPlaces);
        v.setStatut(statutField.getText());
        v.setIdDestination(idDestination);

        v.setDateDebut(java.sql.Date.valueOf("2026-03-01"));
        v.setDateFin(java.sql.Date.valueOf("2026-03-15"));

        serviceVoyage.update(v);

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
