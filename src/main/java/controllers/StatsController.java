package controllers;

import api.StatsAPI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StatsController implements Initializable {

    @FXML private Label totalServicesLabel;
    @FXML private Label totalOffresLabel;
    @FXML private Label prixMoyenLabel;
    @FXML private Label topServiceLabel;
    @FXML private Button minimizeButton;
    @FXML private Button maximizeButton;
    @FXML private Button closeButton;

    private StatsAPI statsAPI;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🔄 Initialisation de StatsController...");
        statsAPI = new StatsAPI();
        setupWindowButtons();
        chargerDonnees();
    }

    private void setupWindowButtons() {
        if (minimizeButton != null) {
            minimizeButton.setOnAction(e -> ((Stage) minimizeButton.getScene().getWindow()).setIconified(true));
        }
        if (maximizeButton != null) {
            maximizeButton.setOnAction(e -> {
                Stage stage = (Stage) maximizeButton.getScene().getWindow();
                stage.setMaximized(!stage.isMaximized());
                maximizeButton.setText(stage.isMaximized() ? "❐" : "□");
            });
        }
        if (closeButton != null) {
            closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
        }
    }

    private void chargerDonnees() {
        try {
            totalServicesLabel.setText(String.valueOf(statsAPI.getTotalServices()));
            totalOffresLabel.setText(String.valueOf(statsAPI.getTotalOffres()));
            System.out.println("✅ Données chargées: " + statsAPI.getTotalServices() + " services, " + statsAPI.getTotalOffres() + " offres");
        } catch (Exception e) {
            System.out.println("❌ Erreur chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            ((Stage) totalServicesLabel.getScene().getWindow()).setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}