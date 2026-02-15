package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    @FXML
    private Label dateLabel;

    @FXML
    private Label totalServices;

    @FXML
    private Label totalOffres;

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

            System.out.println("✅ Dashboard initialisé avec succès");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerStatistiques() {
        try {
            int nbServices = serviceService.getAll().size();
            int nbOffres = offreService.getAll().size();

            totalServices.setText(String.valueOf(nbServices));
            totalOffres.setText(String.valueOf(nbOffres));

            System.out.println("📊 Statistiques: " + nbServices + " services, " + nbOffres + " offres");
        } catch (Exception e) {
            totalServices.setText("0");
            totalOffres.setText("0");
            System.out.println("⚠️ Erreur chargement stats: " + e.getMessage());
        }
    }

    @FXML
    private void handleServices() {
        try {
            System.out.println("🔄 Tentative d'ouverture de service.fxml");

            // Vérifier que le fichier existe
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
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur inattendue: " + e.getMessage());
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
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Gestion des Offres");
            stage.show();

            System.out.println("✅ offre.fxml chargé avec succès");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des offres: " + e.getMessage());
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