package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import models.destination;
import services.ServiceDestination;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;

import java.util.List;

public class UpdateDestinationController {

    @FXML private TextField idField;
    @FXML private TextField paysField;
    @FXML private TextField villeField;
    @FXML private TextField continentField;
    @FXML
    private TextField imageField;

    private String selectedImagePath;

    private ServiceDestination serviceDestination = new ServiceDestination();

    @FXML
    public void loadDestination() {

        int id = Integer.parseInt(idField.getText());
        List<destination> list = serviceDestination.getAll();

        for (destination d : list) {
            if (d.getId_destination() == id) {
                paysField.setText(d.getPays());
                villeField.setText(d.getVille());
                continentField.setText(d.getContinent());
                selectedImagePath = d.getImage();
                imageField.setText(d.getImage());

                break;
            }
        }
    }

    @FXML
    public void updateDestination() {

        if (idField.getText().isEmpty()) {
            showAlert("ID vide !");
            return;
        }

        int id;

        try {
            id = Integer.parseInt(idField.getText());
        } catch (Exception e) {
            showAlert("ID doit être un nombre !");
            return;
        }

        if (paysField.getText().isEmpty()) {
            showAlert("Pays vide !");
            return;
        }

        if (villeField.getText().isEmpty()) {
            showAlert("Ville vide !");
            return;
        }

        if (continentField.getText().isEmpty()) {
            showAlert("Continent vide !");
            return;
        }


        destination d = new destination();
        d.setId_destination(id);
        d.setPays(paysField.getText());
        d.setVille(villeField.getText());
        d.setContinent(continentField.getText());
        if (selectedImagePath == null) {
            d.setImage(imageField.getText());
        } else {
            d.setImage(selectedImagePath);
        }


        serviceDestination.update(d);

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
            controller.showDestinations();

            StackPane mainContent =
                    (StackPane) paysField.getScene().lookup("#mainContent");

            mainContent.getChildren().setAll(root.lookup("#mainContent"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
