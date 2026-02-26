package controllers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.destination;
import models.voyage;
import services.StatisticsService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsController {

    private static final String DARK_BLUE = "#16325c";
    private static final String ACCENT    = "#2980b9";
    private static final String GREEN     = "#27ae60";
    private static final String ORANGE    = "#e67e22";
    private static final String RED       = "#c0392b";
    private static final String PURPLE    = "#8e44ad";

    private static final Color[] CHART_COLORS = {
            Color.web("#2980b9"), Color.web("#27ae60"), Color.web("#e67e22"),
            Color.web("#c0392b"), Color.web("#8e44ad"), Color.web("#16a085"),
            Color.web("#d35400"), Color.web("#2c3e50")
    };

    // ── Data is passed in from MainViewController — no new DB connections ──
    private final List<voyage>      voyages;
    private final List<destination> destinations;
    private final StatisticsService ss = new StatisticsService();

    public StatisticsController(List<voyage> voyages, List<destination> destinations) {
        this.voyages      = voyages;
        this.destinations = destinations;
    }

    /**
     * Builds and returns the full dashboard as a VBox.
     * MainViewController puts this directly into mainContent (StackPane).
     */
    public VBox buildDashboard() {
        VBox root = new VBox(28);
        root.setStyle("-fx-background-color:#F5F5DC; -fx-padding:30;");

        // header
        root.getChildren().add(buildHeader());

        // KPI cards
        root.getChildren().add(buildKpiRow());

        // row 1 — bar charts
        HBox row1 = new HBox(24);
        row1.getChildren().addAll(
                buildCard("📅 Voyages par mois",
                        buildBarChart(ss.voyagesParMois(voyages), 460, 220, ACCENT), 500),
                buildCard("💰 Voyages par tranche de prix",
                        buildBarChart(ss.voyagesParTranchePrix(voyages), 360, 220, GREEN), 400)
        );
        root.getChildren().add(row1);

        // row 2 — horizontal bar + pie
        HBox row2 = new HBox(24);
        row2.getChildren().addAll(
                buildCard("🏆 Top destinations (nb voyages)",
                        buildHorizontalBarChart(
                                ss.voyagesParDestination(voyages, destinations, 6), 440, 220), 480),
                buildCard("🌍 Destinations par continent",
                        buildPieChart(ss.destinationsParContinent(destinations), 260, 200), 320)
        );
        root.getChildren().add(row2);

        // row 3 — sparkline
        root.getChildren().add(
                buildCard("📈 Répartition des prix (tous les voyages)",
                        buildSparkline(880, 130), 940)
        );

        return root;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HEADER
    // ══════════════════════════════════════════════════════════════════════
    private VBox buildHeader() {
        Label title = new Label("📊  Tableau de Bord Statistiques");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill:" + DARK_BLUE + ";");

        Label sub = new Label(voyages.size() + " voyages  •  "
                + destinations.size() + " destinations  •  Mis à jour maintenant");
        sub.setStyle("-fx-text-fill:#777; -fx-font-size:13px;");

        VBox box = new VBox(4, title, sub);
        box.setStyle("-fx-border-color:#c8c4a0; -fx-border-width:0 0 1 0; -fx-padding:0 0 16 0;");
        return box;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  KPI ROW
    // ══════════════════════════════════════════════════════════════════════
    private HBox buildKpiRow() {
        HBox row = new HBox(14);
        row.getChildren().addAll(
                kpi("🧳 Total voyages",  String.valueOf(voyages.size()),              DARK_BLUE),
                kpi("💵 Prix moyen",     Math.round(ss.prixMoyen(voyages)) + " TND",  ACCENT),
                kpi("⬇️ Prix min",       Math.round(ss.prixMin(voyages))   + " TND",  GREEN),
                kpi("⬆️ Prix max",       Math.round(ss.prixMax(voyages))   + " TND",  ORANGE),
                kpi("💺 Places totales", String.valueOf(ss.totalPlaces(voyages)),      PURPLE),
                kpi("🔜 À venir",        String.valueOf(ss.voyagesAVenir(voyages)),    ACCENT),
                kpi("🔥 Urgents <7j",    String.valueOf(ss.voyagesUrgents(voyages)),   RED)
        );
        return row;
    }

    private VBox kpi(String label, String value, String color) {
        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        val.setStyle("-fx-text-fill:" + color + ";");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#666; -fx-font-size:11px;");

        VBox box = new VBox(4, val, lbl);
        box.setStyle(
                "-fx-background-color:white;"
                        + "-fx-padding:14 18 14 18;"
                        + "-fx-background-radius:12;"
                        + "-fx-border-color:#c8c4a0;"
                        + "-fx-border-radius:12;"
                        + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),8,0,0,2);"
        );
        box.setPrefWidth(128);
        return box;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CARD WRAPPER
    // ══════════════════════════════════════════════════════════════════════
    private VBox buildCard(String title, javafx.scene.Node chart, double prefW) {
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setStyle("-fx-text-fill:" + DARK_BLUE + "; -fx-padding:0 0 8 0;");

        VBox card = new VBox(8, lbl, chart);
        card.setStyle(
                "-fx-background-color:white;"
                        + "-fx-padding:18;"
                        + "-fx-background-radius:14;"
                        + "-fx-border-color:#c8c4a0;"
                        + "-fx-border-radius:14;"
                        + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.07),10,0,0,3);"
        );
        card.setPrefWidth(prefW);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  VERTICAL BAR CHART
    // ══════════════════════════════════════════════════════════════════════
    private Canvas buildBarChart(Map<String, Long> data, double w, double h, String hexColor) {
        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (data == null || data.isEmpty()) { drawEmpty(gc, w, h); return canvas; }

        long max = data.values().stream().mapToLong(Long::longValue).max().orElse(1);
        if (max == 0) max = 1;

        double padL = 28, padR = 10, padT = 14, padB = 38;
        double plotW = w - padL - padR;
        double plotH = h - padT - padB;
        int    n     = data.size();
        double barW  = (plotW / n) * 0.58;
        double gap   = (plotW / n) * 0.42;

        // grid lines
        gc.setStroke(Color.web("#eef0f5"));
        gc.setLineWidth(1);
        for (int i = 0; i <= 4; i++) {
            double y = padT + plotH - (plotH * i / 4.0);
            gc.strokeLine(padL, y, padL + plotW, y);
        }

        Color barColor = Color.web(hexColor);
        int idx = 0;
        for (Map.Entry<String, Long> e : data.entrySet()) {
            double x    = padL + idx * (barW + gap) + gap / 2.0;
            double barH = plotH * e.getValue() / (double) max;
            double y    = padT + plotH - barH;

            LinearGradient grad = new LinearGradient(0, y, 0, y + barH, false,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web(hexColor).brighter()),
                    new Stop(1, barColor));
            gc.setFill(grad);
            gc.fillRoundRect(x, y, barW, barH, 6, 6);

            if (e.getValue() > 0) {
                gc.setFill(Color.web(DARK_BLUE));
                gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
                gc.fillText(String.valueOf(e.getValue()), x + barW / 2 - 4, y - 4);
            }

            gc.setFill(Color.web("#666"));
            gc.setFont(Font.font("Segoe UI", 10));
            String key = e.getKey();
            gc.fillText(key, x + barW / 2 - key.length() * 2.8, padT + plotH + 15);
            idx++;
        }

        gc.setStroke(Color.web("#dde3ee"));
        gc.setLineWidth(1.5);
        gc.strokeLine(padL, padT, padL, padT + plotH);
        gc.strokeLine(padL, padT + plotH, padL + plotW, padT + plotH);

        return canvas;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HORIZONTAL BAR CHART
    // ══════════════════════════════════════════════════════════════════════
    private Canvas buildHorizontalBarChart(Map<String, Long> data, double w, double h) {
        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (data == null || data.isEmpty()) { drawEmpty(gc, w, h); return canvas; }

        long max = data.values().stream().mapToLong(Long::longValue).max().orElse(1);

        double padL = 155, padR = 40, padT = 8, padB = 8;
        double plotW = w - padL - padR;
        double plotH = h - padT - padB;
        int    n     = data.size();
        double barH  = (plotH / n) * 0.55;
        double gapH  = (plotH / n) * 0.45;

        int idx = 0;
        for (Map.Entry<String, Long> e : data.entrySet()) {
            double y    = padT + idx * (barH + gapH) + gapH / 2.0;
            double barW = plotW * e.getValue() / (double) max;
            Color  col  = CHART_COLORS[idx % CHART_COLORS.length];

            gc.setFill(Color.web(DARK_BLUE));
            gc.setFont(Font.font("Segoe UI", 11));
            String lbl = e.getKey().length() > 20
                    ? e.getKey().substring(0, 18) + "…" : e.getKey();
            gc.fillText(lbl, 2, y + barH * 0.72);

            LinearGradient grad = new LinearGradient(padL, 0, padL + barW, 0, false,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, col), new Stop(1, col.brighter()));
            gc.setFill(grad);
            gc.fillRoundRect(padL, y, Math.max(barW, 4), barH, 5, 5);

            gc.setFill(Color.web(DARK_BLUE));
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            gc.fillText(String.valueOf(e.getValue()), padL + barW + 6, y + barH * 0.72);
            idx++;
        }
        return canvas;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PIE CHART
    // ══════════════════════════════════════════════════════════════════════
    private VBox buildPieChart(Map<String, Long> data, double w, double h) {
        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (data == null || data.isEmpty()) { drawEmpty(gc, w, h); return new VBox(canvas); }

        long   total  = data.values().stream().mapToLong(Long::longValue).sum();
        double cx     = w / 2, cy = h / 2;
        double radius = Math.min(w, h) / 2 - 12;
        double angle  = -90;
        int    idx    = 0;

        for (Map.Entry<String, Long> e : data.entrySet()) {
            double sweep = 360.0 * e.getValue() / total;
            gc.setFill(CHART_COLORS[idx % CHART_COLORS.length]);
            gc.fillArc(cx - radius, cy - radius, radius * 2, radius * 2,
                    angle, sweep, javafx.scene.shape.ArcType.ROUND);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeArc(cx - radius, cy - radius, radius * 2, radius * 2,
                    angle, sweep, javafx.scene.shape.ArcType.ROUND);
            angle += sweep;
            idx++;
        }

        VBox legend = new VBox(5);
        idx = 0;
        for (Map.Entry<String, Long> e : data.entrySet()) {
            Color col = CHART_COLORS[idx % CHART_COLORS.length];
            long  pct = Math.round(100.0 * e.getValue() / total);

            Canvas dot = new Canvas(12, 12);
            dot.getGraphicsContext2D().setFill(col);
            dot.getGraphicsContext2D().fillRoundRect(0, 0, 12, 12, 4, 4);

            Label lbl = new Label(e.getKey() + "  " + pct + "%");
            lbl.setStyle("-fx-font-size:11px; -fx-text-fill:#444;");

            HBox row = new HBox(6, dot, lbl);
            row.setStyle("-fx-alignment:center-left;");
            legend.getChildren().add(row);
            idx++;
        }

        return new VBox(10, canvas, legend);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PRICE SPARKLINE
    // ══════════════════════════════════════════════════════════════════════
    private Canvas buildSparkline(double w, double h) {
        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (voyages.isEmpty()) { drawEmpty(gc, w, h); return canvas; }

        List<Double> prices = voyages.stream()
                .map(voyage::getPrix)
                .sorted()
                .collect(Collectors.toList());

        double padL = 40, padR = 20, padT = 14, padB = 28;
        double plotW = w - padL - padR;
        double plotH = h - padT - padB;
        double minP  = prices.get(0);
        double maxP  = prices.get(prices.size() - 1);
        double range = (maxP - minP == 0) ? 1 : maxP - minP;
        int    n     = prices.size();

        double[] xs = new double[n + 2];
        double[] ys = new double[n + 2];
        for (int i = 0; i < n; i++) {
            xs[i] = padL + i * (plotW / Math.max(n - 1, 1));
            ys[i] = padT + plotH - plotH * (prices.get(i) - minP) / range;
        }
        xs[n] = xs[n - 1]; ys[n] = padT + plotH;
        xs[n + 1] = xs[0]; ys[n + 1] = padT + plotH;

        LinearGradient areaGrad = new LinearGradient(0, padT, 0, padT + plotH, false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(ACCENT, 0.22)),
                new Stop(1, Color.web(ACCENT, 0.02)));
        gc.setFill(areaGrad);
        gc.fillPolygon(xs, ys, n + 2);

        gc.setStroke(Color.web(ACCENT));
        gc.setLineWidth(2.5);
        gc.beginPath();
        for (int i = 0; i < n; i++) {
            if (i == 0) gc.moveTo(xs[i], ys[i]);
            else        gc.lineTo(xs[i], ys[i]);
        }
        gc.stroke();

        for (int i = 0; i < n; i++) {
            gc.setFill(Color.web(ACCENT));
            gc.fillOval(xs[i] - 3.5, ys[i] - 3.5, 7, 7);
            gc.setFill(Color.WHITE);
            gc.fillOval(xs[i] - 1.5, ys[i] - 1.5, 3, 3);
        }

        gc.setFill(Color.web("#888"));
        gc.setFont(Font.font("Segoe UI", 10));
        gc.fillText(Math.round(minP) + " TND", 2, padT + plotH);
        gc.fillText(Math.round(maxP) + " TND", 2, padT + 10);
        gc.fillText("Voyages triés par prix  →", padL, padT + plotH + 18);

        return canvas;
    }

    // ── EMPTY STATE ────────────────────────────────────────────────────────
    private void drawEmpty(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.web("#bbb"));
        gc.setFont(Font.font("Segoe UI", 13));
        gc.fillText("Aucune donnée disponible", w / 2 - 80, h / 2);
    }
}