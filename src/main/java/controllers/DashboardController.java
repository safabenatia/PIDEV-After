package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.OffreService;
import services.ServiceService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label totalServices;

    @FXML
    private Label totalOffres;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button closeButton;

    private ServiceService serviceService = new ServiceService();
    private OffreService offreService = new OffreService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerStatistiques();
        setupWindowButtons();
    }

    private void setupWindowButtons() {
        if (minimizeButton != null) {
            minimizeButton.setOnAction(event -> {
                Stage stage = (Stage) minimizeButton.getScene().getWindow();
                stage.setIconified(true);
            });
        }

        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            });
        }
    }

    private void chargerStatistiques() {
        try {
            int nbServices = serviceService.getAll().size();
            int nbOffres = offreService.getAll().size();
            totalServices.setText(String.valueOf(nbServices));
            totalOffres.setText(String.valueOf(nbOffres));
        } catch (Exception e) {
            totalServices.setText("0");
            totalOffres.setText("0");
        }
    }

    @FXML
    private void handleServices() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/service.fxml"));
            Stage stage = (Stage) totalServices.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOffres() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/offre.fxml"));
            Stage stage = (Stage) totalOffres.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}