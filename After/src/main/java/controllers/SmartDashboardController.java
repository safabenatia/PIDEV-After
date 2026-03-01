package controllers;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import models.Activite;
import models.Planning;

public class SmartDashboardController {

    // ── Palette claire style "Tableau de Bord Statistiques" ─────────────────
    private static final String BG_PAGE     = "#F5F0E8";  // beige doux (fond page)
    private static final String BG_CARD     = "#FFFFFF";  // blanc pur (cards)
    private static final String BORDER_CARD = "#E8E0D0";  // bordure beige clair
    private static final String TEXT_TITLE  = "#1A2F4A";  // bleu marine foncé
    private static final String TEXT_SUB    = "#7A8A9A";  // gris bleuté
    private static final String TEXT_LABEL  = "#4A5568";  // gris anthracite

    // Couleurs vives des valeurs KPI
    private static final String VAL_DARK    = "#4A5568";
    private static final String VAL_BLUE    = "#2196F3";
    private static final String VAL_GREEN   = "#4CAF50";
    private static final String VAL_ORANGE  = "#FF9800";
    private static final String VAL_PURPLE  = "#9C27B0";
    private static final String VAL_TEAL    = "#00BCD4";
    private static final String VAL_RED     = "#F44336";

    private List<Activite> allActivites;
    private List<Planning> allPlannings;

    public SmartDashboardController(List<Activite> allActivites, List<Planning> allPlannings) {
        this.allActivites = allActivites;
        this.allPlannings = allPlannings;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  POINT D'ENTRÉE
    // ════════════════════════════════════════════════════════════════════════
    public VBox buildDashboard() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24, 28, 28, 28));
        root.setStyle("-fx-background-color:" + BG_PAGE + ";");

        root.getChildren().add(buildHeader());
        root.getChildren().add(buildDivider());
        root.getChildren().add(buildKpiRow());
        root.getChildren().add(buildChartsRow());

        return root;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  EN-TÊTE
    // ════════════════════════════════════════════════════════════════════════
    private VBox buildHeader() {
        VBox header = new VBox(4);

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("⬛");
        icon.setStyle("-fx-font-size:22px; -fx-text-fill:" + TEXT_TITLE + ";");

        Label title = new Label("Smart Dashboard");
        title.setStyle(
                "-fx-font-size:24px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + TEXT_TITLE + ";" +
                        "-fx-font-family:'Segoe UI Semibold','Segoe UI',sans-serif;"
        );

        titleRow.getChildren().addAll(icon, title);

        String subText = allActivites.size() + " activités  •  " +
                allPlannings.size() + " plannings  •  Mis à jour maintenant";
        Label sub = new Label(subText);
        sub.setStyle(
                "-fx-font-size:13px;" +
                        "-fx-text-fill:" + TEXT_SUB + ";" +
                        "-fx-font-family:'Segoe UI',sans-serif;" +
                        "-fx-padding:0 0 0 4;"
        );

        header.getChildren().addAll(titleRow, sub);
        return header;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DIVIDER
    // ════════════════════════════════════════════════════════════════════════
    private Region buildDivider() {
        Region div = new Region();
        div.setPrefHeight(1.5);
        div.setStyle("-fx-background-color:#D4C9B5;");
        return div;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  RANGÉE KPI
    // ════════════════════════════════════════════════════════════════════════
    private HBox buildKpiRow() {
        double totalPrix = allActivites.stream().mapToDouble(Activite::getPrix).sum();
        double prixMoyen = allActivites.isEmpty() ? 0 : totalPrix / allActivites.size();
        double prixMin   = allActivites.stream().mapToDouble(Activite::getPrix).min().orElse(0);
        double prixMax   = allActivites.stream().mapToDouble(Activite::getPrix).max().orElse(0);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox[] cards = {
                buildKpiCard(String.valueOf(allActivites.size()),        "Total activités",   VAL_DARK,   "☐"),
                buildKpiCard(String.format("%.0f DT", prixMoyen),       "Prix moyen",        VAL_BLUE,   "⊟"),
                buildKpiCard(String.format("%.0f DT", prixMin),         "Prix min",          VAL_GREEN,  "↓"),
                buildKpiCard(String.format("%.0f DT", prixMax),         "Prix max",          VAL_ORANGE, "↑"),
                buildKpiCard(String.format("%.0f DT", totalPrix),       "Prix total",        VAL_PURPLE, "Σ"),
                buildKpiCard(String.valueOf(allPlannings.size()),        "Total plannings",   VAL_TEAL,   "≡"),
                buildKpiCard(String.valueOf(allActivites.size()),        "Activités actives", VAL_RED,    "●"),
        };

        for (VBox card : cards) {
            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().add(card);
        }

        return row;
    }

    private VBox buildKpiCard(String value, String label, String valueColor, String iconStr) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14, 12, 12, 14));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
                "-fx-background-color:" + BG_CARD + ";" +
                        "-fx-background-radius:12;" +
                        "-fx-border-color:" + BORDER_CARD + ";" +
                        "-fx-border-radius:12;" +
                        "-fx-border-width:1.2;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.07));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        card.setEffect(shadow);

        Label valueLbl = new Label(value);
        valueLbl.setStyle(
                "-fx-font-size:22px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + valueColor + ";" +
                        "-fx-font-family:'Segoe UI Semibold','Segoe UI',sans-serif;"
        );

        HBox labelRow = new HBox(5);
        labelRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLbl = new Label(iconStr);
        iconLbl.setStyle("-fx-font-size:11px; -fx-text-fill:" + TEXT_SUB + ";");

        Label labelLbl = new Label(label);
        labelLbl.setWrapText(true);
        labelLbl.setStyle(
                "-fx-font-size:11px;" +
                        "-fx-text-fill:" + TEXT_LABEL + ";" +
                        "-fx-font-family:'Segoe UI',sans-serif;"
        );
        labelLbl.setMaxWidth(Double.MAX_VALUE);

        labelRow.getChildren().addAll(iconLbl, labelLbl);
        card.getChildren().addAll(valueLbl, labelRow);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  RANGÉE GRAPHIQUES
    // ════════════════════════════════════════════════════════════════════════
    private HBox buildChartsRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.TOP_LEFT);

        VBox left  = buildChartCard("📊  Activités par catégorie", buildPieChart());
        VBox right = buildChartCard("📅  Plannings par mois",      buildBarChart());

        HBox.setHgrow(left,  Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        row.getChildren().addAll(left, right);
        return row;
    }

    private VBox buildChartCard(String title, javafx.scene.Node chart) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18, 18, 14, 18));
        card.setStyle(
                "-fx-background-color:" + BG_CARD + ";" +
                        "-fx-background-radius:14;" +
                        "-fx-border-color:" + BORDER_CARD + ";" +
                        "-fx-border-radius:14;" +
                        "-fx-border-width:1.2;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        shadow.setRadius(12);
        shadow.setOffsetY(4);
        card.setEffect(shadow);

        Label titleLbl = new Label(title);
        titleLbl.setStyle(
                "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + TEXT_TITLE + ";" +
                        "-fx-font-family:'Segoe UI Semibold','Segoe UI',sans-serif;"
        );

        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:" + BORDER_CARD + ";");

        VBox.setVgrow(chart, Priority.ALWAYS);
        card.getChildren().addAll(titleLbl, sep, chart);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PIE CHART
    // ════════════════════════════════════════════════════════════════════════
    private PieChart buildPieChart() {
        PieChart pie = new PieChart();
        pie.setLegendVisible(true);
        pie.setLabelsVisible(true);
        pie.setPrefSize(420, 300);
        pie.setStyle("-fx-background-color:transparent;");

        Map<String, Long> byCategorie = allActivites.stream()
                .collect(Collectors.groupingBy(Activite::getCategorie, Collectors.counting()));

        byCategorie.forEach((cat, count) ->
                pie.getData().add(new PieChart.Data(cat + "  (" + count + ")", count)));

        return pie;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  BAR CHART
    // ════════════════════════════════════════════════════════════════════════
    private BarChart<String, Number> buildBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();

        xAxis.setTickLabelFill(Color.web(TEXT_SUB));
        yAxis.setTickLabelFill(Color.web(TEXT_SUB));

        BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);
        bar.setLegendVisible(false);
        bar.setPrefSize(420, 280);
        bar.setBarGap(3);
        bar.setCategoryGap(14);
        bar.setAnimated(true);
        bar.setStyle("-fx-background-color:transparent;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Plannings");

        String[] mois = {"Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"};

        Map<String, Long> byMonth = allPlannings.stream()
                .collect(Collectors.groupingBy(
                        p -> mois[p.getDateActivite().getMonthValue() - 1],
                        Collectors.counting()
                ));

        for (String m : mois) {
            series.getData().add(new XYChart.Data<>(m, byMonth.getOrDefault(m, 0L)));
        }

        bar.getData().add(series);
        return bar;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LINE CHART (évolution)
    // ════════════════════════════════════════════════════════════════════════
    private LineChart<String, Number> buildLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();

        xAxis.setTickLabelFill(Color.web(TEXT_SUB));
        yAxis.setTickLabelFill(Color.web(TEXT_SUB));

        LineChart<String, Number> line = new LineChart<>(xAxis, yAxis);
        line.setLegendVisible(false);
        line.setPrefSize(860, 260);
        line.setCreateSymbols(true);
        line.setAnimated(true);
        line.setStyle("-fx-background-color:transparent;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Plannings");

        Map<LocalDate, Long> byDate = allPlannings.stream()
                .collect(Collectors.groupingBy(Planning::getDateActivite, Collectors.counting()));

        byDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> series.getData().add(
                        new XYChart.Data<>(e.getKey().toString(), e.getValue())));

        line.getData().add(series);
        return line;
    }}
