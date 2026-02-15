package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import models.destination;
import services.ServiceDestination;

public class DeleteDestinationController {

    @FXML private TextField idField;

    private ServiceDestination serviceDestination = new ServiceDestination();

    @FXML
    public void deleteDestination() {

        try {
            int id = Integer.parseInt(idField.getText());

            destination d = new destination();
            d.setId_destination(id);

            serviceDestination.delete(d);

            goBack();

        } catch (Exception e) {
            idField.setStyle("-fx-border-color:red;");
        }
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
