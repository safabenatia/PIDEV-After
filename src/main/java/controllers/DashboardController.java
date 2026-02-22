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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ==================== LABELS STATISTIQUES ====================
    @FXML
    private Label dateLabel;

    @FXML
    private Label totalServices;

    @FXML
    private Label totalOffres;

    // ==================== LABELS DES CARTES ====================
    @FXML
    private Label totalServicesCard;  // Pour la carte Services

    @FXML
    private Label totalOffresCard;    // Pour la carte Offres

    // ==================== BOUTONS DE FENÊTRE ====================
    @FXML
    private Button minimizeButton;

    @FXML
    private Button maximizeButton;

    @FXML
    private Button closeButton;

    // ==================== SERVICES ====================
    private ServiceService serviceService = new ServiceService();
    private OffreService offreService = new OffreService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Afficher la date actuelle
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
            dateLabel.setText(now.format(formatter).toUpperCase());

            // Charger les statistiques
            chargerStatistiques();

            // Configurer les boutons de fenêtre
            setupWindowButtons();

            System.out.println("✅ Dashboard initialisé avec succès");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configure les boutons de la barre de titre
     */
    private void setupWindowButtons() {
        // Bouton MINIMISER
        if (minimizeButton != null) {
            minimizeButton.setOnAction(event -> {
                Stage stage = (Stage) minimizeButton.getScene().getWindow();
                stage.setIconified(true);
            });
        }

        // Bouton MAXIMISER / RESTAURER
        if (maximizeButton != null) {
            maximizeButton.setOnAction(event -> {
                Stage stage = (Stage) maximizeButton.getScene().getWindow();
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                    maximizeButton.setText("□");  // Symbole agrandir
                } else {
                    stage.setMaximized(true);
                    maximizeButton.setText("❐");  // Symbole restaurer
                }
            });
        }

        // Bouton FERMER
        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            });
        }
    }

    /**
     * Charge les statistiques depuis la base de données
     */
    private void chargerStatistiques() {
        try {
            int nbServices = serviceService.getAll().size();
            int nbOffres = offreService.getAll().size();

            // Mise à jour des labels principaux
            totalServices.setText(String.valueOf(nbServices));
            totalOffres.setText(String.valueOf(nbOffres));

            // Mise à jour des labels dans les cartes
            if (totalServicesCard != null) {
                totalServicesCard.setText(nbServices + " services");
            }
            if (totalOffresCard != null) {
                totalOffresCard.setText(nbOffres + " promotions");
            }

            System.out.println("📊 Statistiques: " + nbServices + " services, " + nbOffres + " offres");
        } catch (Exception e) {
            totalServices.setText("0");
            totalOffres.setText("0");
            if (totalServicesCard != null) totalServicesCard.setText("0 services");
            if (totalOffresCard != null) totalOffresCard.setText("0 promotions");
            System.out.println("⚠️ Erreur chargement stats: " + e.getMessage());
        }
    }

    /**
     * Ouvre la gestion des services
     */
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
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Gestion des Services");
            stage.show();

            System.out.println("✅ service.fxml chargé avec succès");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des services: " + e.getMessage());
        }
    }

    /**
     * Ouvre la gestion des offres
     */
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
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Gestion des Offres");
            stage.show();

            System.out.println("✅ offre.fxml chargé avec succès");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des offres: " + e.getMessage());
        }
    }

    /**
     * Ouvre les statistiques
     */
    @FXML
    private void handleStats() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/stats.fxml"));
            Stage stage = (Stage) totalServices.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Statistiques");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les statistiques");
        }
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}