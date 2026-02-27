package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
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

    @FXML
    private PieChart pieResByType;
    @FXML
    private BarChart<String, Number> barRevenue;
    @FXML
    private LineChart<String, Number> lineGrowth;
    @FXML
    private Label lblTotalRes;
    @FXML
    private Label lblTotalRev;

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
        List<Reservation> list = resService.afficher();
        Map<String, Long> counts = list.stream()
                .collect(
                        Collectors.groupingBy(r -> r.getType() == null ? "Autre" : r.getType(), Collectors.counting()));

        counts.forEach((type, count) -> {
            pieResByType.getData().add(new PieChart.Data(type + " (" + count + ")", count));
        });
    }

    private void loadBarChart() {
        List<Paiement> list = payService.afficher();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenu Mensuel (TND)");

        // Group by month
        Map<String, Double> revenueByMonth = list.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getDatePaiement().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        Collectors.summingDouble(Paiement::getMontant)));

        revenueByMonth.forEach((month, amount) -> series.getData().add(new XYChart.Data<>(month, amount)));
        barRevenue.getData().add(series);
    }

    private void loadLineChart() {
        List<Reservation> list = resService.afficher();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cumul Réservations");

        Map<String, Long> daily = list.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDateReservation().toString(),
                        Collectors.counting()));

        long cumulative = 0;
        // Sort keys by date string
        List<String> sortedDates = daily.keySet().stream().sorted().collect(Collectors.toList());
        for (String d : sortedDates) {
            cumulative += daily.get(d);
            series.getData().add(new XYChart.Data<>(d, cumulative));
        }
        lineGrowth.getData().add(series);
    }

    private void updateGeneralStats() {
        List<Reservation> resList = resService.afficher();
        List<Paiement> payList = payService.afficher();

        double totalRev = payList.stream().mapToDouble(Paiement::getMontant).sum();

        lblTotalRes.setText(String.valueOf(resList.size()));
        lblTotalRev.setText(String.format("%.2f TND", totalRev));
    }

    @FXML
    private void retourReservations() {
        navigate("/AdminReservationView.fxml");
    }

    @FXML
    private void retourPaiements() {
        navigate("/AdminPaiementView.fxml");
    }

    private void navigate(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) pieResByType.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
