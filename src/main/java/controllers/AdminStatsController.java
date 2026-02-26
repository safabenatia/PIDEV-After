package controllers;

import api.StatsAPI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Offre;
import models.Service;
import services.OffreService;
import services.ServiceService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminStatsController implements Initializable {

    // ==================== KPIs ====================
    @FXML private Label totalServicesLabel;
    @FXML private Label totalOffresLabel;
    @FXML private Label prixMoyenLabel;
    @FXML private Label servicePopulaireLabel;
    @FXML private Label offrePlusChereLabel;
    @FXML private Label offreMoinsChereLabel;

    // ==================== GRAPHIQUES ====================
    @FXML private BarChart<String, Number> offresParServiceChart;
    @FXML private PieChart categoriesChart;
    @FXML private LineChart<String, Number> prixOffresChart;

    // ==================== TABLEAU ====================
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Integer> colId;
    @FXML private TableColumn<Service, String> colNom;
    @FXML private TableColumn<Service, String> colDesc;
    @FXML private TableColumn<Service, String> colCat;
    @FXML private TableColumn<Service, Number> colNbOffres;

    // ==================== BOUTONS ====================
    @FXML private Button minimizeButton;
    @FXML private Button maximizeButton;
    @FXML private Button closeButton;
    @FXML private Button exportExcelButton;
    @FXML private Button refreshButton;

    // ==================== SERVICES ====================
    private StatsAPI statsAPI;
    private ServiceService serviceService = new ServiceService();
    private OffreService offreService = new OffreService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🔄 Initialisation du dashboard admin...");
        statsAPI = new StatsAPI();
        setupWindowButtons();
        chargerDonnees();
        configurerTableau();
        chargerGraphiques();
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
            // KPIs principaux
            totalServicesLabel.setText(String.valueOf(statsAPI.getTotalServices()));
            totalOffresLabel.setText(String.valueOf(statsAPI.getTotalOffres()));
            prixMoyenLabel.setText(String.format("%.2f DT", statsAPI.getPrixMoyenOffres()));

            // Service le plus populaire
            servicePopulaireLabel.setText(statsAPI.getServiceLePlusPopulaire());

            // Offre la plus chère et la moins chère
            Offre plusChere = statsAPI.getOffreLaPlusChere();
            Offre moinsChere = statsAPI.getOffreLaMoinsChere();

            if (plusChere != null) {
                offrePlusChereLabel.setText(plusChere.getTitre() + " (" + plusChere.getPrix() + " DT)");
            }
            if (moinsChere != null) {
                offreMoinsChereLabel.setText(moinsChere.getTitre() + " (" + moinsChere.getPrix() + " DT)");
            }

            System.out.println("✅ Données chargées: " + statsAPI.getTotalServices() +
                    " services, " + statsAPI.getTotalOffres() + " offres");
        } catch (Exception e) {
            System.out.println("❌ Erreur chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurerTableau() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id_service"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom_service"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        // Colonne personnalisée pour le nombre d'offres
        colNbOffres.setCellValueFactory(cellData -> {
            Service service = cellData.getValue();
            long nbOffres = offreService.getAll().stream()
                    .filter(o -> o.getServiceId() == service.getId_service())
                    .count();
            return new javafx.beans.property.SimpleIntegerProperty((int) nbOffres);
        });

        refreshTable();
    }

    private void refreshTable() {
        ObservableList<Service> services = FXCollections.observableArrayList(serviceService.getAll());
        servicesTable.setItems(services);
    }

    private void chargerGraphiques() {
        // BarChart : Offres par service
        offresParServiceChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'offres");

        Map<String, Integer> offresParService = statsAPI.getOffresParService();
        for (Map.Entry<String, Integer> entry : offresParService.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        offresParServiceChart.getData().add(series);

        // PieChart : Catégories
        categoriesChart.getData().clear();
        Map<String, Integer> categories = statsAPI.getServicesParCategorie();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : categories.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
        categoriesChart.setData(pieData);

        // LineChart : Prix des offres
        prixOffresChart.getData().clear();
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Prix des offres");

        Map<Integer, Double> prixOffres = statsAPI.getPrixOffresParId();
        for (Map.Entry<Integer, Double> entry : prixOffres.entrySet()) {
            lineSeries.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
        }
        prixOffresChart.getData().add(lineSeries);
    }

    @FXML
    private void exporterExcel() {
        try {
            // Créer le classeur Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Services");

            // Style pour les en-têtes
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // En-têtes
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Nom", "Description", "Catégorie", "Nombre d'offres"};

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données
            List<Service> services = serviceService.getAll();
            int rowNum = 1;

            for (Service s : services) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(s.getId_service());
                row.createCell(1).setCellValue(s.getNom_service());
                row.createCell(2).setCellValue(s.getDescription());
                row.createCell(3).setCellValue(s.getCategorie());

                long nbOffres = offreService.getAll().stream()
                        .filter(o -> o.getServiceId() == s.getId_service())
                        .count();
                row.createCell(4).setCellValue(nbOffres);
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sauvegarder le fichier
            String fileName = "statistiques_services_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

            try (FileOutputStream out = new FileOutputStream(fileName)) {
                workbook.write(out);
            }

            workbook.close();

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Fichier Excel généré : " + fileName);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'export : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshData() {
        chargerDonnees();
        refreshTable();
        chargerGraphiques();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Données actualisées !");
    }

    @FXML
    private void handleDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard_admin.fxml"));
            Stage stage = (Stage) totalServicesLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Dashboard Admin");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}