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
            MainViewController controller = loader.getController();
            controller.showVoyages();
            idField.getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

}
