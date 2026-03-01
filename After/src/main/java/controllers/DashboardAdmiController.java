package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Document;
import models.voyage;
import services.AdminDashboardService;
import services.serviceCategorieDocument;
import services.serviceDocument;
import utils.Session;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DashboardAdmiController {

    @FXML private HBox statsCard;
    @FXML private Label lblTotal;
    @FXML private Label lblCategories;
    @FXML private Label lblExpiringSoon;
    @FXML private Label lblExpired;
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> uploadChart;
    @FXML private ListView<String> listCategories;
    @FXML private TextField txtCategorie;
    @FXML private ListView<String> listExpires;
    @FXML private StackPane contentArea;

    private final AdminDashboardService dashboardService = new AdminDashboardService();
    private final serviceCategorieDocument categorieService = new serviceCategorieDocument();
    private final serviceDocument documentService = new serviceDocument();

    @FXML
    public void initialize() {
        refreshDashboard();
        loadCategories();
        loadExpiredDocuments();
        startAutoRefresh();
    }

    // ======================================================
    // =================== SIDEBAR NAV ======================
    // ✅ Tous les boutons retournent vers dashboardAdmin.fxml
    //    qui gère le contentArea avec ses propres méthodes
    // ======================================================

    @FXML
    private void handleShowUsers(MouseEvent event) {
        goToDashboardAdmin(event);
    }

    @FXML
    private void handleVoyages(MouseEvent event) {
        // Retourne au dashboardAdmin puis simule le clic Voyages
        // Pour l'instant on retourne juste à l'accueil admin
        goToDashboardAdmin(event);
    }

    @FXML
    private void handleReservations(MouseEvent event) {
        goToDashboardAdmin(event);
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        try {
            Session.clear();
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root));
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Méthode centrale : retourne à dashboardAdmin.fxml
    private void goToDashboardAdmin(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboardAdmin.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("After Travel - Administration");
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir : " + e.getMessage()).show();
        }
    }

    // ======================================================
    // =================== DASHBOARD ========================
    // ======================================================

    private void refreshDashboard() {
        loadStats();
        loadPieChart();
        loadBarChart();
    }

    private void loadStats() {
        lblTotal.setText(String.valueOf(documentService.getAll().size()));
        lblCategories.setText(String.valueOf(categorieService.getAll().size()));
        lblExpiringSoon.setText(String.valueOf(dashboardService.getExpiringSoonCount()));
        lblExpired.setText(String.valueOf(dashboardService.getExpiredDocumentsCount()));
    }

    private void loadPieChart() {
        categoryChart.getData().clear();
        Map<String, Integer> data = dashboardService.getDocumentsByCategory();
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        for (String key : data.keySet()) {
            list.add(new PieChart.Data(key, data.get(key)));
        }
        categoryChart.setData(list);
    }

    private void loadBarChart() {
        uploadChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Uploads");
        Map<String, Integer> data = dashboardService.getMonthlyUploads();
        for (String month : data.keySet()) {
            series.getData().add(new XYChart.Data<>(month, data.get(month)));
        }
        uploadChart.getData().add(series);
    }

    private void startAutoRefresh() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    refreshDashboard();
                    loadExpiredDocuments();
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // ======================================================
    // ================== CATEGORIES ========================
    // ======================================================

    private void loadCategories() {
        listCategories.getItems().setAll(categorieService.getAllNames());
    }

    @FXML
    private void handleVoyages() {
        try {
            services.ServiceVoyage serviceVoyage = new services.ServiceVoyage();
            services.ServiceDestination serviceDestination = new services.ServiceDestination();

            List<voyage> allVoyages = serviceVoyage.getAll();
            List<models.destination> allDestinations = serviceDestination.getAll();

            StatisticsController statsCtrl = new StatisticsController(allVoyages, allDestinations);
            VBox dashboard = statsCtrl.buildDashboard();

            contentArea.getChildren().setAll(dashboard);

        } catch (Exception e) {
            e.printStackTrace();
            Label err = new Label("Erreur: " + e.getMessage());
            err.setStyle("-fx-text-fill:red; -fx-font-size:13px;");
            contentArea.getChildren().setAll(err);
        }
    }
    @FXML
    private void handleStats() {
        try {
            Parent statsView = FXMLLoader.load(getClass().getResource("/stats.fxml"));
            contentArea.getChildren().setAll(statsView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des statistiques : " + e.getMessage());
        }
    }
    @FXML
    private void handleDashboardDocuments(javafx.scene.input.MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/DashboardAdmi.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("After Travel - Dashboard Documents");
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir : " + e.getMessage()).show();
        }
    }

    @FXML
    private void ajouterCategorie() {
        String nom = txtCategorie.getText().trim();
        if (nom.isEmpty()) { showAlert("Veuillez entrer un nom"); return; }
        categorieService.ajouterSiInexistante(nom);
        txtCategorie.clear();
        loadCategories();
        refreshDashboard();
    }

    @FXML
    private void modifierCategorie() {
        String selected = listCategories.getSelectionModel().getSelectedItem();
        String nouveau = txtCategorie.getText().trim();
        if (selected == null || nouveau.isEmpty()) {
            showAlert("Sélectionnez une catégorie et entrez un nouveau nom");
            return;
        }
        categorieService.modifierNom(selected, nouveau);
        txtCategorie.clear();
        loadCategories();
        refreshDashboard();
    }

    @FXML
    private void supprimerCategorie() {
        String selected = listCategories.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Sélectionnez une catégorie"); return; }
        boolean ok = categorieService.supprimerParLibelle(selected);
        if (!ok) { showAlert("Impossible : catégorie utilisée par des documents"); return; }
        loadCategories();
        refreshDashboard();
    }

    // ======================================================
    // ================ DOCUMENTS EXPIRES ===================
    // ======================================================

    private void loadExpiredDocuments() {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Document doc : dashboardService.getExpiredDocuments()) {
            String line = "📄 " + doc.getNomDocument()
                    + "   |   📂 " + doc.getCategorie().getLibelle()
                    + "   |   📅 Ajouté: " + doc.getDateAjout()
                    + "   |   ⚠️ Expiré: " + doc.getDateExpiration();
            items.add(line);
        }
        if (items.isEmpty()) items.add("✅ Aucun document expiré");
        listExpires.setItems(items);
    }

    @FXML
    private void supprimerExpires() {
        documentService.supprimerDocumentsExpires();
        loadExpiredDocuments();
        loadStats();
        showAlert("✅ Documents expirés supprimés !");
    }

    // ======================================================
    // ===================== UTIL ===========================
    // ======================================================

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}