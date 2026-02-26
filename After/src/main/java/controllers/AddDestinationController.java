package controllers;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import models.destination;
import services.ServiceDestination;

public class AddDestinationController {

    @FXML private TextField paysField;
    @FXML private TextField villeField;
    @FXML private TextField continentField;

    private ServiceDestination serviceDestination = new ServiceDestination();
    @FXML
    private TextField imageField;

    private String selectedImagePath;

    @FXML
    public void addDestination() {

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
        d.setPays(paysField.getText());
        d.setVille(villeField.getText());
        d.setContinent(continentField.getText());
        d.setImage(selectedImagePath);

        serviceDestination.add(d);

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
            paysField.getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

}
