package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import models.voyage;
import services.ServiceVoyage;
import javafx.scene.control.Alert;

import java.io.File;
import java.sql.Date;

public class AddVoyageController {

    @FXML private TextField titreField;
    @FXML private TextField descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField nbPlacesField;
    @FXML private TextField imageField ;
    @FXML private TextField idDestinationField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    private String selectedImagePath;
    private ServiceVoyage serviceVoyage = new ServiceVoyage();

    @FXML
    public void initialize() {
        dateDebutPicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisabled(empty || date.isBefore(java.time.LocalDate.now()));
            }
        });

        dateFinPicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisabled(empty || date.isBefore(java.time.LocalDate.now()));
            }
        });
    }

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
        v.setImage(selectedImagePath);
        v.setIdDestination(idDestination);
        if (dateDebutPicker.getValue() == null) {
            showAlert("Date début vide !");
            return;
        }
        if (dateFinPicker.getValue() == null) {
            showAlert("Date fin vide !");
            return;
        }
        if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
            showAlert("Date fin doit être après date début !");
            return;
        }

        v.setDateDebut(java.sql.Date.valueOf(dateDebutPicker.getValue()));
        v.setDateFin(java.sql.Date.valueOf(dateFinPicker.getValue()));

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
    public void chooseImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(imageField.getScene().getWindow());

        if (file != null) {
            selectedImagePath = file.toURI().toString();
            imageField.setText(file.getAbsolutePath());
        }
    }


    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            MainViewController controller = loader.getController();
            controller.showVoyages();
            titreField.getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
