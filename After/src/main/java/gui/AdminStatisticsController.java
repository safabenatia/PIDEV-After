package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Paiement;
import model.Reservation;
import services.PaiementService;
import services.ReservationService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminStatisticsController {

    @FXML private PieChart pieResByType;
    @FXML private BarChart<String, Number> barRevenue;
    @FXML private LineChart<String, Number> lineGrowth;
    @FXML private Label lblTotalRes;
    @FXML private Label lblTotalRev;

    private final ReservationService resService = new ReservationService();
    private final PaiementService payService = new PaiementService();

    @FXML
    public void initialize() {
        loadPieChart();
        loadBarChart();
        loadLineChart();
        updateGeneralStats();
    }

    private void loadPieChart() {
        List<Reservation> list = resService.getAll();
        Map<String, Long> counts = list.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getType() == null ? "Autre" : r.getType(),
                        Collectors.counting()));

        counts.forEach((type, count) ->
                pieResByType.getData().add(new PieChart.Data(type + " (" + count + ")", count)));
    }

    private void loadBarChart() {
        List<Paiement> list = payService.afficher();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenu Mensuel (TND)");

        Map<String, Double> revenueByMonth = list.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getDatePaiement().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        Collectors.summingDouble(Paiement::getMontant)));

        revenueByMonth.forEach((month, amount) ->
                series.getData().add(new XYChart.Data<>(month, amount)));

        barRevenue.getData().add(series);
    }

    private void loadLineChart() {
        List<Reservation> list = resService.getAll();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cumul Réservations");

        Map<String, Long> daily = list.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDateReservation().toString(),
                        Collectors.counting()));

        long cumulative = 0;
        List<String> sortedDates = daily.keySet().stream().sorted().collect(Collectors.toList());
        for (String d : sortedDates) {
            cumulative += daily.get(d);
            series.getData().add(new XYChart.Data<>(d, cumulative));
        }
        lineGrowth.getData().add(series);
    }

    private void updateGeneralStats() {
        List<Reservation> resList = resService.getAll();
        List<Paiement> payList = payService.afficher();

        double totalRev = payList.stream().mapToDouble(Paiement::getMontant).sum();

        lblTotalRes.setText(String.valueOf(resList.size()));
        lblTotalRev.setText(String.format("%.2f TND", totalRev));
    }

    // ── Navigation using pieResByType to get the scene ──

    @FXML
    private void showReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminReservationView.fxml"));
            Parent root = loader.load();
            StackPane parent = (StackPane) pieResByType.getScene().lookup("#contentArea");
            if (parent != null) parent.getChildren().setAll(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void showPaiements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminPaiementView.fxml"));
            Parent root = loader.load();
            StackPane parent = (StackPane) pieResByType.getScene().lookup("#contentArea");
            if (parent != null) parent.getChildren().setAll(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void showStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminStatisticsView.fxml"));
            Parent root = loader.load();
            StackPane parent = (StackPane) pieResByType.getScene().lookup("#contentArea");
            if (parent != null) parent.getChildren().setAll(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // kept for backward compat if called from old FXML
    @FXML
    private void retourReservations() {
        showReservations();
    }

    @FXML
    private void retourPaiements() {
        showPaiements();
    }
}