package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import models.destination;
import services.ServiceDestination;

import java.util.List;

public class UpdateDestinationController {

    @FXML private TextField idField;
    @FXML private TextField paysField;
    @FXML private TextField villeField;
    @FXML private TextField continentField;

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
                break;
            }
        }
    }

    @FXML
    public void updateDestination() {

        if (!validateInputs()) return;

        destination d = new destination();

        d.setId_destination(Integer.parseInt(idField.getText()));
        d.setPays(paysField.getText());
        d.setVille(villeField.getText());
        d.setContinent(continentField.getText());

        serviceDestination.update(d);

        goBack();
    }

    private boolean validateInputs() {

        boolean valid = true;
        resetStyle();

        try {
            Integer.parseInt(idField.getText());
        } catch (Exception e) {
            idField.setStyle("-fx-border-color:red;");
            valid = false;
        }

        if (paysField.getText().isEmpty()) {
            paysField.setStyle("-fx-border-color:red;");
            valid = false;
        }

        return valid;
    }

    private void resetStyle() {
        idField.setStyle(null);
        paysField.setStyle(null);
        villeField.setStyle(null);
        continentField.setStyle(null);
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
