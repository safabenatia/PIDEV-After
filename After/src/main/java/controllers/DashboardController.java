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
import utils.Session;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label dateLabel;
    @FXML
    private Label totalServices;
    @FXML
    private Label totalOffres;
    @FXML
    private Label totalServicesCard;
    @FXML
    private Label totalOffresCard;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button closeButton;

    private ServiceService serviceService = new ServiceService();
    private OffreService offreService = new OffreService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
            dateLabel.setText(now.format(formatter).toUpperCase());
            chargerStatistiques();
            setupWindowButtons();
            System.out.println("✅ Dashboard initialisé avec succès");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoyages() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Voyages & Destinations");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Voyages & Destinations");
        }
    }

    @FXML
    private void showactivite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/activite_list.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Activite & Planning");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Activtie & Planning");
        }
    }
    @FXML
    private void showDocument() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDocument.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Documents");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Documents");
        }
    }
    @FXML
    private void showReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserReservationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Reservations");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Reservations");
        }
    }
    @FXML
    private void showDepense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_depenses_v2.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Depenses");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Depenses");
        }
    }
    @FXML
    private void handleRetourProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DashboardVoyageur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Espace Voyageur");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupWindowButtons() {
        if (minimizeButton != null) {
            minimizeButton.setOnAction(event -> {
                Stage stage = (Stage) minimizeButton.getScene().getWindow();
                stage.setIconified(true);
            });
        }
        if (maximizeButton != null) {
            maximizeButton.setOnAction(event -> {
                Stage stage = (Stage) maximizeButton.getScene().getWindow();
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                    maximizeButton.setText("□");
                } else {
                    stage.setMaximized(true);
                    maximizeButton.setText("❐");
                }
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
            if (totalServicesCard != null) totalServicesCard.setText(nbServices + " services");
            if (totalOffresCard != null) totalOffresCard.setText(nbOffres + " promotions");
            System.out.println("📊 Statistiques: " + nbServices + " services, " + nbOffres + " offres");
        } catch (Exception e) {
            totalServices.setText("0");
            totalOffres.setText("0");
            if (totalServicesCard != null) totalServicesCard.setText("0 services");
            if (totalOffresCard != null) totalOffresCard.setText("0 promotions");
            System.out.println("⚠️ Erreur chargement stats: " + e.getMessage());
        }
    }

    @FXML
    private void handleServices() {
        try {
            System.out.println("🔄 Tentative d'ouverture de service.fxml");
            URL resourceUrl = getClass().getResource("/service.fxml");
            System.out.println("URL du fichier: " + resourceUrl);
            if (resourceUrl == null) {
                showAlert("Erreur", "Fichier service.fxml introuvable dans les ressources!");
                return;
            }
            Parent root = FXMLLoader.load(resourceUrl);
            Stage stage = (Stage) totalServices.getScene().getWindow();
            stage.setScene(new Scene(root));  // ← ADDED
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("AFTER Travel - Gestion des Services");
            stage.show();
            System.out.println("✅ service.fxml chargé avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des services: " + e.getMessage());
        }
    }

    @FXML
    private void handleOffres() {
        try {
            System.out.println("🔄 Tentative d'ouverture de offre.fxml");
            URL resourceUrl = getClass().getResource("/offre.fxml");
            System.out.println("URL du fichier: " + resourceUrl);
            if (resourceUrl == null) {
                showAlert("Erreur", "Fichier offre.fxml introuvable dans les ressources!");
                return;
            }
            Parent root = FXMLLoader.load(resourceUrl);
            Stage stage = (Stage) totalOffres.getScene().getWindow();
            stage.setScene(new Scene(root));  // ← ADDED
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("AFTER Travel - Gestion des Offres");
            stage.show();
            System.out.println("✅ offre.fxml chargé avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des offres: " + e.getMessage());
        }
    }


    @FXML
    private void handleStats() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/stats.fxml"));
            Stage stage = (Stage) totalServices.getScene().getWindow();
            stage.setScene(new Scene(root));  // ← ADDED
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("AFTER Travel - Statistiques");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les statistiques");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}