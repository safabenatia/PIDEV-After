package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import utils.Session;

import static utils.Session.getCurrentUser;

public class GestionDocumentController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    public void initialize() {

        try {

            if (Session.getCurrentUser().getTypeUtilisateur().equalsIgnoreCase("admin")) {

                Parent root = FXMLLoader.load(
                        getClass().getResource("/DashboardAdmi.fxml")
                );

                mainPane.getChildren().setAll(root);

            } else {

                Parent root = FXMLLoader.load(
                        getClass().getResource("/AfficherDocument.fxml")
                );

                mainPane.getChildren().setAll(root);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}