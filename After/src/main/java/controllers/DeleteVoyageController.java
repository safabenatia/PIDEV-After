package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import models.voyage;
import services.ServiceVoyage;

public class DeleteVoyageController {

    @FXML private TextField idField;

    private ServiceVoyage serviceVoyage = new ServiceVoyage();

    @FXML
    public void deleteVoyage() {

        voyage v = new voyage();
        v.setIdVoyage(Integer.parseInt(idField.getText()));

        serviceVoyage.delete(v);

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
