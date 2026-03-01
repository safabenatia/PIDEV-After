package controllers;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.voyage;
import models.destination;
import services.*;
import services.StatisticsService;
import controllers.StatisticsController;
import utils.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainViewController {

    @FXML private StackPane mainContent;
    @FXML private HBox crudBox;
    @FXML private TextField searchField;

    private ServiceVoyage serviceVoyage = new ServiceVoyage();
    private ServiceDestination serviceDestination = new ServiceDestination();
    private List<voyage> voyages = new ArrayList<>();
    private List<destination> destinations = new ArrayList<>();
    private String currentView = "voyages";
    private RestCountriesService restCountriesService = new RestCountriesService();
    private ExchangeRateService exchangeRateService = new ExchangeRateService();
    private PdfExportService pdfExportService = new PdfExportService();
    private FavoritesService favoritesService = new FavoritesService();
    private TranslateService translateService = new TranslateService();
    private StatisticsService statisticsService = new StatisticsService();
    private PublicHolidayService publicHolidayService = new PublicHolidayService();
    private WeatherService weatherService = new WeatherService();
    private TouristAttractionService touristAttractionService = new TouristAttractionService();
    private TimezoneService timezoneService = new TimezoneService();
    private VoyageRecommendationService recommendationService = new VoyageRecommendationService();

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
        showVoyages();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NAVIGATION — exactly from your integration, untouched
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    private void handleServicesOffres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Services & Offres");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleActivitePlanning() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/activite_list.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Activite & Planning");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDocument() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDocument.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Documents");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handledepense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_depenses_v2.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Depenses");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserReservationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Reservations");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleRetourProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DashboardVoyageur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Espace Voyageur");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHOW VOYAGES
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void showVoyages() {
        currentView = "voyages";
        crudBox.getChildren().clear();

        Button addBtn = new Button("Ajouter voyage");
        addBtn.setOnAction(e -> showAddForm());
        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        voyages = serviceVoyage.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20); grid.setVgap(20); grid.setPrefColumns(3);
        for (voyage v : voyages) grid.getChildren().add(createVoyageCard(v));

        Button sortBtn = new Button("Trier par date ↑");
        sortBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        final boolean[] ascending = {true};
        sortBtn.setOnAction(e -> {
            ascending[0] = !ascending[0];
            sortBtn.setText(ascending[0] ? "Trier par date ↑" : "Trier par date ↓");
            voyages.sort((a, b) -> {
                if (a.getDateDebut() == null || b.getDateDebut() == null) return 0;
                return ascending[0] ? a.getDateDebut().compareTo(b.getDateDebut())
                        : b.getDateDebut().compareTo(a.getDateDebut());
            });
            grid.getChildren().clear();
            for (voyage vv : voyages) grid.getChildren().add(createVoyageCard(vv));
        });

        crudBox.getChildren().addAll(addBtn, sortBtn);

        Label header = new Label("Bienvenue à AFTER");
        header.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        Label subHeader = new Label("Où vos rêves deviennent réalité");
        subHeader.setStyle("-fx-font-size:16px; -fx-text-fill:#16325c;");

        VBox container = new VBox(20);
        container.setStyle("-fx-padding:20;");
        container.getChildren().addAll(header, subHeader, grid);
        mainContent.getChildren().setAll(container);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHOW DESTINATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void showDestinations() {
        currentView = "destinations";
        crudBox.getChildren().clear();

        Button addBtn = new Button("Ajouter destination");
        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        addBtn.setOnAction(e -> loadCenter("/AddDestination.fxml"));

        destinations = serviceDestination.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20); grid.setVgap(20); grid.setPrefColumns(3);
        for (destination d : destinations) grid.getChildren().add(createDestinationCard(d));

        Button sortBtn = new Button("Trier par pays ↑");
        sortBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        final boolean[] ascending = {true};
        sortBtn.setOnAction(e -> {
            ascending[0] = !ascending[0];
            sortBtn.setText(ascending[0] ? "Trier par pays ↑" : "Trier par pays ↓");
            destinations.sort((a, b) -> ascending[0] ? a.getPays().compareTo(b.getPays())
                    : b.getPays().compareTo(a.getPays()));
            grid.getChildren().clear();
            for (destination dd : destinations) grid.getChildren().add(createDestinationCard(dd));
        });

        crudBox.getChildren().addAll(addBtn, sortBtn);

        Label header = new Label("Bienvenue à AFTER");
        header.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        Label subHeader = new Label("Où vos rêves deviennent réalité");
        subHeader.setStyle("-fx-font-size:16px; -fx-text-fill:#16325c;");

        VBox container = new VBox(20);
        container.setStyle("-fx-padding:20;");
        container.getChildren().addAll(header, subHeader, grid);
        mainContent.getChildren().setAll(container);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHOW STATISTICS
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void showStatistics() {
        currentView = "statistics";
        crudBox.getChildren().clear();
        try {
            List<voyage> allVoyages = serviceVoyage.getAll();
            List<destination> allDestinations = serviceDestination.getAll();
            StatisticsController statsCtrl = new StatisticsController(allVoyages, allDestinations);
            mainContent.getChildren().setAll(statsCtrl.buildDashboard());
        } catch (Exception e) {
            e.printStackTrace();
            Label err = new Label("Erreur: " + e.getMessage());
            err.setStyle("-fx-text-fill:red; -fx-font-size:13px;");
            mainContent.getChildren().setAll(err);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHOW FAVORITES
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void showFavorites() {
        currentView = "favorites";
        crudBox.getChildren().clear();

        List<voyage> favs = favoritesService.getFavorites();
        TilePane grid = new TilePane();
        grid.setHgap(20); grid.setVgap(20); grid.setPrefColumns(3);

        if (favs.isEmpty()) {
            Label empty = new Label("Aucun voyage en favoris pour le moment 💔");
            empty.setStyle("-fx-font-size:16px; -fx-text-fill:#16325c;");
            grid.getChildren().add(empty);
        } else {
            for (voyage v : favs) grid.getChildren().add(createVoyageCard(v));
        }

        Label header = new Label("Mes Favoris ♥");
        header.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        Label subHeader = new Label("Vos voyages préférés");
        subHeader.setStyle("-fx-font-size:16px; -fx-text-fill:#16325c;");

        VBox container = new VBox(20);
        container.setStyle("-fx-padding:20;");
        container.getChildren().addAll(header, subHeader, grid);
        mainContent.getChildren().setAll(container);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHOW RECOMMENDATIONS (based on favorites — your original logic)
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void showRecommendations() {
        currentView = "recommendations";
        crudBox.getChildren().clear();

        List<voyage> favs = favoritesService.getFavorites();
        List<voyage> allVoyages = serviceVoyage.getAll();
        List<voyage> recommendations = new ArrayList<>();

        if (favs.isEmpty()) {
            allVoyages.sort((a, b) -> Double.compare(a.getPrix(), b.getPrix()));
            recommendations = allVoyages.stream().limit(3).collect(java.util.stream.Collectors.toList());
        } else {
            double avgPrix = favs.stream().mapToDouble(voyage::getPrix).average().orElse(0);
            for (voyage v : allVoyages) {
                boolean alreadyFav = favs.stream().anyMatch(f -> f.getIdVoyage() == v.getIdVoyage());
                if (!alreadyFav && Math.abs(v.getPrix() - avgPrix) <= avgPrix * 0.30)
                    recommendations.add(v);
            }
            if (recommendations.size() < 3) {
                for (voyage v : allVoyages) {
                    boolean alreadyFav = favs.stream().anyMatch(f -> f.getIdVoyage() == v.getIdVoyage());
                    boolean alreadyAdded = recommendations.stream().anyMatch(r -> r.getIdVoyage() == v.getIdVoyage());
                    if (!alreadyFav && !alreadyAdded) recommendations.add(v);
                    if (recommendations.size() >= 6) break;
                }
            }
        }

        TilePane grid = new TilePane();
        grid.setHgap(20); grid.setVgap(20); grid.setPrefColumns(3);

        if (recommendations.isEmpty()) {
            Label empty = new Label("Ajoutez des favoris pour recevoir des recommandations !");
            empty.setStyle("-fx-font-size:14px; -fx-text-fill:#16325c;");
            grid.getChildren().add(empty);
        } else {
            for (voyage v : recommendations) grid.getChildren().add(createVoyageCard(v));
        }

        Label header = new Label("Voyages Recommandes pour vous");
        header.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        Label subHeader = favs.isEmpty()
                ? new Label("Les voyages les moins chers pour commencer")
                : new Label("Bases sur vos favoris - budget moyen: "
                + Math.round(favs.stream().mapToDouble(voyage::getPrix).average().orElse(0)) + " TND");
        subHeader.setStyle("-fx-font-size:14px; -fx-text-fill:#16325c; -fx-font-style:italic;");

        VBox container = new VBox(20);
        container.setStyle("-fx-padding:20;");
        container.getChildren().addAll(header, subHeader, grid);
        mainContent.getChildren().setAll(container);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHOW ADVANCED RECOMMENDATIONS — NEW (VoyageRecommendationService)
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void showAdvancedRecommendations() {
        currentView = "advancedRec";
        crudBox.getChildren().clear();

        List<voyage> allVoyages = serviceVoyage.getAll();

        VBox container = new VBox(20);
        container.setStyle("-fx-padding:20;");

        Label header = new Label("Recommandations Intelligentes");
        header.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        // Price Analysis
        Label statsHeader = new Label("Analyse des Prix");
        statsHeader.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        Label statsLabel = new Label(recommendationService.getPriceAnalysis(allVoyages));
        statsLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#444; -fx-background-color:#f0f4ff; " +
                "-fx-padding:10; -fx-background-radius:8;");

        // Best Value
        Label bestValueHeader = new Label("Meilleurs Rapports Qualite-Prix");
        bestValueHeader.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        TilePane bestValueGrid = new TilePane();
        bestValueGrid.setHgap(15); bestValueGrid.setVgap(15); bestValueGrid.setPrefColumns(3);
        for (voyage v : recommendationService.getBestValueVoyages(allVoyages, 3))
            bestValueGrid.getChildren().add(createSmallVoyageCard(v, "BEST"));

        // Low Availability Alert
        List<voyage> lowSeats = recommendationService.getLowAvailabilityVoyages(allVoyages, 5);
        VBox urgentBox = new VBox(8);
        if (!lowSeats.isEmpty()) {
            Label urgentHeader = new Label("URGENT - Places Limitées - Réservez Vite !");
            urgentHeader.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#c0392b;");
            TilePane urgentGrid = new TilePane();
            urgentGrid.setHgap(15); urgentGrid.setVgap(15); urgentGrid.setPrefColumns(3);
            for (voyage v : lowSeats) urgentGrid.getChildren().add(createSmallVoyageCard(v, "!"));
            urgentBox.getChildren().addAll(urgentHeader, urgentGrid);
        }

        // Upcoming (next 30 days)
        Label upcomingHeader = new Label("Départs dans les 30 Prochains Jours");
        upcomingHeader.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        List<voyage> upcoming = recommendationService.getUpcomingVoyages(allVoyages, 30);
        TilePane upcomingGrid = new TilePane();
        upcomingGrid.setHgap(15); upcomingGrid.setVgap(15); upcomingGrid.setPrefColumns(3);
        if (upcoming.isEmpty()) {
            Label none = new Label("Aucun départ dans les 30 prochains jours.");
            none.setStyle("-fx-text-fill:#888; -fx-font-style:italic;");
            upcomingGrid.getChildren().add(none);
        } else {
            for (voyage v : upcoming) upcomingGrid.getChildren().add(createSmallVoyageCard(v, "DEPART"));
        }

        // Budget Filter
        Label budgetHeader = new Label("Filtrer par Budget");
        budgetHeader.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        javafx.scene.control.TextField budgetField = new javafx.scene.control.TextField();
        budgetField.setPromptText("Budget maximum (TND)");
        budgetField.setPrefWidth(200);
        TilePane budgetResultGrid = new TilePane();
        budgetResultGrid.setHgap(15); budgetResultGrid.setVgap(15); budgetResultGrid.setPrefColumns(3);
        Button budgetBtn = new Button("Rechercher");
        budgetBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        budgetBtn.setOnAction(e -> {
            try {
                double max = Double.parseDouble(budgetField.getText().trim());
                List<voyage> filtered = recommendationService.filterByBudget(allVoyages, 0, max);
                budgetResultGrid.getChildren().clear();
                if (filtered.isEmpty()) {
                    Label none = new Label("Aucun voyage dans ce budget.");
                    none.setStyle("-fx-text-fill:#888; -fx-font-style:italic;");
                    budgetResultGrid.getChildren().add(none);
                } else {
                    for (voyage v : filtered) budgetResultGrid.getChildren().add(createSmallVoyageCard(v, "TND"));
                }
            } catch (NumberFormatException ex) {
                budgetField.setStyle("-fx-border-color:red;");
            }
        });
        HBox budgetRow = new HBox(10, budgetField, budgetBtn);

        container.getChildren().addAll(header, statsHeader, statsLabel,
                new javafx.scene.control.Separator(), bestValueHeader, bestValueGrid,
                new javafx.scene.control.Separator());
        if (!lowSeats.isEmpty()) container.getChildren().add(urgentBox);
        container.getChildren().addAll(new javafx.scene.control.Separator(),
                upcomingHeader, upcomingGrid, new javafx.scene.control.Separator(),
                budgetHeader, budgetRow, budgetResultGrid);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;");
        mainContent.getChildren().setAll(scroll);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            if (currentView.equals("voyages")) showVoyages(); else showDestinations();
            return;
        }

        TilePane grid = new TilePane();
        grid.setHgap(20); grid.setVgap(20); grid.setPrefColumns(3);

        if (currentView.equals("voyages")) {
            for (voyage v : serviceVoyage.getAll()) {
                if (v.getTitre().toLowerCase().contains(keyword) ||
                        String.valueOf(v.getPrix()).contains(keyword) ||
                        String.valueOf(v.getNbPlaces()).contains(keyword) ||
                        (v.getDateDebut() != null && v.getDateDebut().toString().contains(keyword)) ||
                        (v.getDateFin() != null && v.getDateFin().toString().contains(keyword)))
                    grid.getChildren().add(createVoyageCard(v));
            }
        } else {
            for (destination d : serviceDestination.getAll()) {
                if (d.getPays().toLowerCase().contains(keyword) ||
                        d.getVille().toLowerCase().contains(keyword) ||
                        d.getContinent().toLowerCase().contains(keyword))
                    grid.getChildren().add(createDestinationCard(d));
            }
        }

        if (grid.getChildren().isEmpty()) {
            Label noResult = new Label("Aucun résultat trouvé pour : \"" + keyword + "\"");
            noResult.setStyle("-fx-font-size:16px; -fx-text-fill:#c0392b; -fx-font-style:italic;");
            VBox box = new VBox(noResult);
            box.setStyle("-fx-padding:40; -fx-alignment:center;");
            mainContent.getChildren().setAll(box);
        } else {
            mainContent.getChildren().setAll(grid);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE VOYAGE CARD — same as your integration
    // ─────────────────────────────────────────────────────────────────────────

    private VBox createVoyageCard(voyage v) {
        VBox box = new VBox(8);
        box.setPrefWidth(250);
        box.setStyle("""
        -fx-background-color:white;
        -fx-padding:15;
        -fx-background-radius:10;
        -fx-border-radius:10;
        -fx-border-color:#16325c;
        """);

        if (v.getImage() != null && !v.getImage().isEmpty()) {
            try {
                Image image = new Image(v.getImage(), 200, 150, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200); imageView.setFitHeight(150); imageView.setPreserveRatio(true);
                box.getChildren().add(imageView);
            } catch (Exception e) { System.out.println("Image load failed"); }
        }

        Label titreLabel = new Label(v.getTitre());
        titreLabel.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label descLabel = new Label(v.getDescription());
        descLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#666; -fx-wrap-text:true;");
        descLabel.setMaxWidth(220);

        Button translateBtn = new Button("FR -> EN");
        translateBtn.setStyle("-fx-background-color:#8e44ad; -fx-text-fill:white; -fx-font-size:10px;");
        final boolean[] translated = {false};
        translateBtn.setOnAction(e -> {
            if (!translated[0]) {
                translateBtn.setText("chargement..."); translateBtn.setDisable(true);
                new Thread(() -> {
                    String t = translateService.translate(v.getDescription(), "en");
                    javafx.application.Platform.runLater(() -> {
                        descLabel.setText(t); translateBtn.setText("EN -> FR");
                        translateBtn.setDisable(false); translated[0] = true;
                    });
                }).start();
            } else {
                descLabel.setText(v.getDescription()); translateBtn.setText("FR -> EN"); translated[0] = false;
            }
        });

        Label dateLabel = new Label("Date: " + v.getDateDebut() + "  ->  " + v.getDateFin());
        dateLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#888888; -fx-font-style:italic;");

        Label prixLabel = new Label("Prix: " + v.getPrix() + " TND");
        prixLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label conversionLabel = new Label("  chargement...");
        conversionLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#888;");
        new Thread(() -> {
            double[] rates = exchangeRateService.convertToEURandUSD(v.getPrix());
            javafx.application.Platform.runLater(() -> {
                if (rates[0] != 0 && rates[1] != 0)
                    conversionLabel.setText("  " + rates[0] + " EUR  |  " + rates[1] + " USD");
                else conversionLabel.setText("  conversion indisponible");
            });
        }).start();

        Label placesLabel = new Label(v.getNbPlaces() + " places disponibles");
        placesLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#555;");

        box.getChildren().addAll(titreLabel, descLabel, translateBtn, dateLabel, prixLabel, conversionLabel, placesLabel);

        Button editBtn   = new Button("✎");
        Button deleteBtn = new Button("✖");
        Button pdfBtn    = new Button("PDF");
        Button favBtn    = new Button(favoritesService.isFavorite(v) ? "❤" : "♡");
        editBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        deleteBtn.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white;");
        pdfBtn.setStyle("-fx-background-color:#27ae60; -fx-text-fill:white;");
        favBtn.setStyle("-fx-background-color:white; -fx-border-color:#c0392b; -fx-border-radius:5;");

        editBtn.setOnAction(e -> openEditVoyage(v));
        deleteBtn.setOnAction(e -> confirmDeleteVoyage(v));
        favBtn.setOnAction(e -> {
            if (favoritesService.isFavorite(v)) { favoritesService.removeFavorite(v); favBtn.setText("♡"); }
            else { favoritesService.addFavorite(v); favBtn.setText("❤"); }
        });
        pdfBtn.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Enregistrer le PDF");
            fc.setInitialFileName(v.getTitre() + "_voyage.pdf");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            java.io.File file = fc.showSaveDialog(pdfBtn.getScene().getWindow());
            if (file != null) {
                pdfExportService.exportVoyage(v, file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succes"); alert.setHeaderText(null);
                alert.setContentText("PDF exporte avec succes !"); alert.showAndWait();
            }
        });

        box.getChildren().add(new HBox(10, editBtn, deleteBtn, pdfBtn, favBtn));
        box.setOnMouseClicked(e -> showVoyageDetails(v));
        box.setStyle(box.getStyle() + "-fx-cursor:hand;");
        return box;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE DESTINATION CARD — same as your integration
    // ─────────────────────────────────────────────────────────────────────────

    private VBox createDestinationCard(destination d) {
        VBox box = new VBox(10);
        box.setPrefWidth(250);
        box.setStyle("""
        -fx-background-color:white;
        -fx-padding:15;
        -fx-background-radius:10;
        -fx-border-radius:10;
        -fx-border-color:#16325c;
        """);

        if (d.getImage() != null && !d.getImage().isEmpty()) {
            try {
                Image image = new Image(d.getImage(), 200, 150, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200); imageView.setFitHeight(150); imageView.setPreserveRatio(true);
                box.getChildren().add(imageView);
            } catch (Exception e) { System.out.println("Image load failed"); }
        }

        ImageView flagView = new ImageView();
        flagView.setFitWidth(40); flagView.setFitHeight(25);
        box.getChildren().add(flagView);

        Label paysLabel = new Label("🌍 " + d.getPays());
        paysLabel.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#16325c;");
        Label villeLabel = new Label("🏙️ " + d.getVille());
        Label continentLabel = new Label("🗺️ " + d.getContinent());

        Label capitalLabel  = new Label("🏛️ Capitale: ...");
        Label currencyLabel = new Label("💵 Devise: ...");
        Label languageLabel = new Label("🗣️ Langue: ...");
        capitalLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#555;");
        currencyLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#555;");
        languageLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#555;");
        box.getChildren().addAll(paysLabel, villeLabel, continentLabel, capitalLabel, currencyLabel, languageLabel);

        new Thread(() -> {
            String flag     = restCountriesService.getFlag(d.getPays());
            String currency = restCountriesService.getCurrency(d.getPays());
            String language = restCountriesService.getLanguage(d.getPays());
            String capital  = restCountriesService.getCapital(d.getPays());
            javafx.application.Platform.runLater(() -> {
                if (!flag.isEmpty()) {
                    try { flagView.setImage(new Image(flag, 40, 25, true, true)); }
                    catch (Exception e) { System.out.println("Flag load failed"); }
                }
                capitalLabel.setText("🏛️ Capitale: "  + (capital.isEmpty()  ? "N/A" : capital));
                currencyLabel.setText("💵 Devise: "    + (currency.isEmpty() ? "N/A" : currency));
                languageLabel.setText("🗣️ Langue: "   + (language.isEmpty() ? "N/A" : language));
            });
        }).start();

        Button editBtn = new Button("✎"); Button deleteBtn = new Button("✖");
        editBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        deleteBtn.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white;");
        editBtn.setOnAction(e -> openEditDestination(d));
        deleteBtn.setOnAction(e -> confirmDeleteDestination(d));

        box.getChildren().add(new HBox(10, editBtn, deleteBtn));
        box.setOnMouseClicked(e -> showDestinationDetails(d));
        box.setStyle(box.getStyle() + "-fx-cursor:hand;");
        return box;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE SMALL VOYAGE CARD — NEW (for advanced recommendations, with image)
    // ─────────────────────────────────────────────────────────────────────────

    private VBox createSmallVoyageCard(voyage v, String badge) {
        VBox box = new VBox(6);
        box.setPrefWidth(220);
        box.setStyle("-fx-background-color:white; -fx-padding:12; -fx-background-radius:8; " +
                "-fx-border-radius:8; -fx-border-color:#16325c;");

        if (v.getImage() != null && !v.getImage().isEmpty()) {
            try {
                Image image = new Image(v.getImage(), 196, 120, true, true);
                ImageView iv = new ImageView(image);
                iv.setFitWidth(196); iv.setFitHeight(120); iv.setPreserveRatio(true);
                box.getChildren().add(iv);
            } catch (Exception e) { System.out.println("Image load failed"); }
        }

        Label badgeLabel = new Label(badge);
        badgeLabel.setStyle("-fx-background-color:" + getBadgeColor(badge) + "; " +
                "-fx-text-fill:white; -fx-font-size:10px; -fx-font-weight:bold; " +
                "-fx-padding:2 6 2 6; -fx-background-radius:4;");

        Label title = new Label(v.getTitre());
        title.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#16325c; -fx-wrap-text:true;");
        title.setMaxWidth(200);

        Label prix = new Label(v.getPrix() + " TND");
        prix.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#27ae60;");

        Label duration = new Label("Duree: " + recommendationService.getDurationDays(v) + " jour(s)");
        duration.setStyle("-fx-font-size:11px; -fx-text-fill:#888;");

        Label places = new Label("Places: " + v.getNbPlaces());
        places.setStyle("-fx-font-size:11px; -fx-text-fill:" + (v.getNbPlaces() <= 5 ? "#c0392b;" : "#555;"));

        Label dates = new Label(v.getDateDebut() + " -> " + v.getDateFin());
        dates.setStyle("-fx-font-size:10px; -fx-text-fill:#aaa; -fx-font-style:italic;");

        box.getChildren().addAll(badgeLabel, title, prix, duration, places, dates);
        box.setOnMouseClicked(e -> showVoyageDetails(v));
        box.setStyle(box.getStyle() + "-fx-cursor:hand;");
        return box;
    }

    private String getBadgeColor(String badge) {
        switch (badge) {
            case "BEST":   return "#f39c12";
            case "!":      return "#c0392b";
            case "DEPART": return "#2980b9";
            case "TND":    return "#27ae60";
            default:       return "#16325c";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VOYAGE DETAILS POPUP — updated: added duration, weather, timezone
    // ─────────────────────────────────────────────────────────────────────────

    private void showVoyageDetails(voyage v) {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Details du Voyage");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        VBox content = new VBox(15);
        content.setStyle("-fx-padding:20; -fx-background-color:white;");
        content.setPrefWidth(450);

        if (v.getImage() != null && !v.getImage().isEmpty()) {
            try {
                Image image = new Image(v.getImage(), 400, 200, true, true);
                ImageView iv = new ImageView(image);
                iv.setFitWidth(400); iv.setFitHeight(200); iv.setPreserveRatio(true);
                content.getChildren().add(iv);
            } catch (Exception e) { System.out.println("Image load failed"); }
        }

        Label titreLabel = new Label(v.getTitre());
        titreLabel.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label descLabel = new Label("Description: " + v.getDescription());
        descLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#444; -fx-wrap-text:true;");
        descLabel.setMaxWidth(400);

        Label dateLabel = new Label("Date: " + v.getDateDebut() + "  ->  " + v.getDateFin());
        dateLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        Label prixLabel = new Label("Prix: " + v.getPrix() + " TND");
        prixLabel.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label convLabel = new Label("Conversion: chargement...");
        convLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#888;");
        new Thread(() -> {
            double[] rates = exchangeRateService.convertToEURandUSD(v.getPrix());
            javafx.application.Platform.runLater(() -> {
                if (rates[0] != 0 && rates[1] != 0)
                    convLabel.setText("Conversion: " + rates[0] + " EUR  |  " + rates[1] + " USD");
                else convLabel.setText("Conversion: indisponible");
            });
        }).start();

        Label placesLabel = new Label("Places disponibles: " + v.getNbPlaces());
        placesLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        Label favLabel = new Label(favoritesService.isFavorite(v) ? "♥ Dans vos favoris" : "♡ Pas dans vos favoris");
        favLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + (favoritesService.isFavorite(v) ? "#c0392b;" : "#888;"));

        // NEW — duration via VoyageRecommendationService
        Label durationLabel = new Label("Duree: " + recommendationService.getDurationDays(v) + " jour(s)");
        durationLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        // NEW — weather + timezone (look up destination city from DB)
        destinations = serviceDestination.getAll();
        String cityFound = destinations.stream()
                .filter(d -> d.getId_destination() == v.getIdDestination())
                .map(destination::getVille).findFirst().orElse(null);

        Label weatherLabel      = new Label("Météo: chargement...");
        Label travelAdviceLabel = new Label("");
        Label timezoneLabel     = new Label("Heure locale: chargement...");
        weatherLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        travelAdviceLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#27ae60; -fx-font-style:italic;");
        timezoneLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        if (cityFound != null) {
            final String city = cityFound;
            new Thread(() -> {
                String weather  = weatherService.getWeatherSummary(city);
                String advice   = weatherService.getTravelAdvice(city);
                String timeInfo = timezoneService.getLocalTimeSummary(city, guessTimezone(city));
                javafx.application.Platform.runLater(() -> {
                    weatherLabel.setText("🌤 Météo: " + weather);
                    travelAdviceLabel.setText(advice);
                    timezoneLabel.setText("🕐 Heure locale: " + timeInfo);
                });
            }).start();
        } else {
            weatherLabel.setText("Météo: destination inconnue");
            timezoneLabel.setText("Heure locale: destination inconnue");
        }

        content.getChildren().addAll(
                titreLabel, new javafx.scene.control.Separator(),
                descLabel, dateLabel, prixLabel, convLabel,
                placesLabel, durationLabel, favLabel,
                weatherLabel, travelAdviceLabel, timezoneLabel
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color:white;");
        dialog.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DESTINATION DETAILS POPUP — updated: fixed PublicHoliday bug + timezone
    // ─────────────────────────────────────────────────────────────────────────

    private void showDestinationDetails(destination d) {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Details de la Destination");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        VBox content = new VBox(15);
        content.setStyle("-fx-padding:20; -fx-background-color:white;");
        content.setPrefWidth(450);

        if (d.getImage() != null && !d.getImage().isEmpty()) {
            try {
                Image image = new Image(d.getImage(), 400, 200, true, true);
                ImageView iv = new ImageView(image);
                iv.setFitWidth(400); iv.setFitHeight(200); iv.setPreserveRatio(true);
                content.getChildren().add(iv);
            } catch (Exception e) { System.out.println("Image load failed"); }
        }

        Label paysLabel = new Label(d.getPays());
        paysLabel.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label villeLabel = new Label("Ville: " + d.getVille());
        villeLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        Label continentLabel = new Label("Continent: " + d.getContinent());
        continentLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        Label capitalLabel  = new Label("Capitale: chargement...");
        Label currencyLabel = new Label("Devise: chargement...");
        Label languageLabel = new Label("Langue: chargement...");
        ImageView flagView  = new ImageView();
        flagView.setFitWidth(60); flagView.setFitHeight(40);
        capitalLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        currencyLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        languageLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        new Thread(() -> {
            String flag     = restCountriesService.getFlag(d.getPays());
            String capital  = restCountriesService.getCapital(d.getPays());
            String currency = restCountriesService.getCurrency(d.getPays());
            String language = restCountriesService.getLanguage(d.getPays());
            javafx.application.Platform.runLater(() -> {
                if (!flag.isEmpty()) {
                    try { flagView.setImage(new Image(flag, 60, 40, true, true)); }
                    catch (Exception e) { System.out.println("Flag load failed"); }
                }
                capitalLabel.setText("Capitale: "  + (capital.isEmpty()  ? "N/A" : capital));
                currencyLabel.setText("Devise: "   + (currency.isEmpty() ? "N/A" : currency));
                languageLabel.setText("Langue: "   + (language.isEmpty() ? "N/A" : language));
            });
        }).start();

        content.getChildren().addAll(paysLabel, new javafx.scene.control.Separator(),
                villeLabel, continentLabel, flagView, capitalLabel, currencyLabel, languageLabel);

        // Weather — same as your integration
        Label weatherLabel = new Label("Météo: chargement...");
        weatherLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        content.getChildren().add(weatherLabel);
        new Thread(() -> {
            String weather = weatherService.getWeatherSummary(d.getVille());
            javafx.application.Platform.runLater(() -> weatherLabel.setText("🌤 " + weather));
        }).start();

        // Public Holidays — FIXED (was crashing with substring(0,2))
        Label holidayLabel = new Label("Prochain jour férié: chargement...");
        holidayLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        content.getChildren().add(holidayLabel);
        new Thread(() -> {
            String code = getCountryCode(d.getPays());
            org.json.JSONObject next = publicHolidayService.getNextHoliday(code);
            javafx.application.Platform.runLater(() -> {
                if (next != null)
                    holidayLabel.setText("🎉 Prochain férié: " + next.getString("date") + " - " + next.getString("name"));
                else
                    holidayLabel.setText("🎉 Aucun jour férié trouvé");
            });
        }).start();

        // Tourist Attractions — same as your integration
        Label attractionLabel = new Label("Attractions: chargement...");
        attractionLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        content.getChildren().add(attractionLabel);
        new Thread(() -> {
            List<String> attractions = touristAttractionService.getAttractions(d.getVille(), 5000, 3);
            javafx.application.Platform.runLater(() -> {
                if (attractions.isEmpty()) attractionLabel.setText("Aucune attraction trouvée");
                else attractionLabel.setText("🏛 Attractions: " + String.join(", ", attractions));
            });
        }).start();

        // NEW — Timezone
        Label timezoneLabel = new Label("Heure locale: chargement...");
        timezoneLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");
        content.getChildren().add(timezoneLabel);
        new Thread(() -> {
            String tz       = guessTimezone(d.getVille());
            String timeInfo = timezoneService.getLocalTimeSummary(d.getVille(), tz);
            String diff     = timezoneService.getTimeDifference("Africa/Tunis", tz);
            javafx.application.Platform.runLater(() ->
                    timezoneLabel.setText("🕐 " + timeInfo + "  (" + diff + ")")
            );
        }).start();

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color:white;");
        dialog.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EDIT / DELETE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private void openEditVoyage(voyage v) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateVoyage.fxml"));
            Node node = loader.load();
            UpdateVoyageController ctrl = loader.getController();
            ctrl.setVoyage(v);
            mainContent.getChildren().setAll(node);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openEditDestination(destination d) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDestination.fxml"));
            Node node = loader.load();
            UpdateDestinationController ctrl = loader.getController();
            ctrl.setDestination(d);
            mainContent.getChildren().setAll(node);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void confirmDeleteVoyage(voyage v) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Confirmation"); alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce voyage ?");
        ButtonType oui = new ButtonType("Oui"); ButtonType annuler = new ButtonType("Annuler");
        alert.getButtonTypes().setAll(oui, annuler);
        alert.showAndWait().ifPresent(r -> { if (r == oui) { serviceVoyage.delete(v); showVoyages(); } });
    }

    private void confirmDeleteDestination(destination d) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Confirmation"); alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette destination ?");
        ButtonType oui = new ButtonType("Oui"); ButtonType annuler = new ButtonType("Annuler");
        alert.getButtonTypes().setAll(oui, annuler);
        alert.showAndWait().ifPresent(r -> { if (r == oui) { serviceDestination.delete(d); showDestinations(); } });
    }

    @FXML public void showAddForm()    { loadCenter("/AddVoyage.fxml"); }
    @FXML public void showUpdateForm() { loadCenter("/UpdateVoyage.fxml"); }
    @FXML public void showDeleteForm() { loadCenter("/DeleteVoyage.fxml"); }

    private void loadCenter(String fxml) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxml));
            mainContent.getChildren().setAll(node);
        } catch (IOException e) { System.out.println(e.getMessage()); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COUNTRY CODE HELPER — fixes the PublicHoliday crash from your integration
    // (was: d.getPays().substring(0,2) which gave "Fr" not "FR" and crashed)
    // ─────────────────────────────────────────────────────────────────────────

    private String getCountryCode(String pays) {
        if (pays == null) return "TN";
        String p = pays.toLowerCase().trim();
        if (p.contains("tunisie") || p.contains("tunisia")) return "TN";
        if (p.contains("france")) return "FR";
        if (p.contains("allemagne") || p.contains("germany")) return "DE";
        if (p.contains("espagne") || p.contains("spain")) return "ES";
        if (p.contains("italie") || p.contains("italy")) return "IT";
        if (p.contains("royaume") || p.contains("england") || p.contains("britain")) return "GB";
        if (p.contains("etats-unis") || p.contains("usa") || p.contains("united states")) return "US";
        if (p.contains("maroc") || p.contains("morocco")) return "MA";
        if (p.contains("algerie") || p.contains("algeria")) return "DZ";
        if (p.contains("egypte") || p.contains("egypt")) return "EG";
        if (p.contains("libye") || p.contains("libya")) return "LY";
        if (p.contains("japon") || p.contains("japan")) return "JP";
        if (p.contains("chine") || p.contains("china")) return "CN";
        if (p.contains("inde") || p.contains("india")) return "IN";
        if (p.contains("bresil") || p.contains("brazil")) return "BR";
        if (p.contains("canada")) return "CA";
        if (p.contains("australie") || p.contains("australia")) return "AU";
        if (p.contains("portugal")) return "PT";
        if (p.contains("belgique") || p.contains("belgium")) return "BE";
        if (p.contains("suisse") || p.contains("switzerland")) return "CH";
        if (p.contains("pays-bas") || p.contains("netherlands")) return "NL";
        if (p.contains("suede") || p.contains("sweden")) return "SE";
        if (p.contains("turquie") || p.contains("turkey")) return "TR";
        if (p.contains("russie") || p.contains("russia")) return "RU";
        if (p.contains("arabie") || p.contains("saudi")) return "SA";
        if (p.contains("emirats") || p.contains("uae")) return "AE";
        if (p.contains("qatar")) return "QA";
        if (p.contains("singapour") || p.contains("singapore")) return "SG";
        if (p.contains("coree") || p.contains("korea")) return "KR";
        if (p.contains("afrique du sud") || p.contains("south africa")) return "ZA";
        return pays.length() >= 2 ? pays.substring(0, 2).toUpperCase() : "TN";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TIMEZONE HELPER — uses Java ZoneId, zero network calls, never fails
    // ─────────────────────────────────────────────────────────────────────────

    private String guessTimezone(String city) {
        if (city == null) return "UTC";
        String c = city.toLowerCase().trim();
        if (c.contains("paris") || c.contains("lyon") || c.contains("marseille") || c.contains("france")) return "Europe/Paris";
        if (c.contains("london") || c.contains("londres") || c.contains("manchester") || c.contains("royaume")) return "Europe/London";
        if (c.contains("rome") || c.contains("milan") || c.contains("naples") || c.contains("italie")) return "Europe/Rome";
        if (c.contains("madrid") || c.contains("barcelone") || c.contains("barcelona") || c.contains("espagne")) return "Europe/Madrid";
        if (c.contains("berlin") || c.contains("munich") || c.contains("hamburg") || c.contains("allemagne")) return "Europe/Berlin";
        if (c.contains("amsterdam") || c.contains("rotterdam") || c.contains("pays-bas") || c.contains("hollande")) return "Europe/Amsterdam";
        if (c.contains("bruxelles") || c.contains("brussels") || c.contains("belgique")) return "Europe/Brussels";
        if (c.contains("vienne") || c.contains("vienna") || c.contains("autriche")) return "Europe/Vienna";
        if (c.contains("zurich") || c.contains("geneve") || c.contains("suisse")) return "Europe/Zurich";
        if (c.contains("lisbonne") || c.contains("lisbon") || c.contains("porto") || c.contains("portugal")) return "Europe/Lisbon";
        if (c.contains("stockholm") || c.contains("suede")) return "Europe/Stockholm";
        if (c.contains("oslo") || c.contains("norvege")) return "Europe/Oslo";
        if (c.contains("copenhague") || c.contains("copenhagen") || c.contains("danemark")) return "Europe/Copenhagen";
        if (c.contains("varsovie") || c.contains("warsaw") || c.contains("pologne")) return "Europe/Warsaw";
        if (c.contains("prague") || c.contains("tcheque")) return "Europe/Prague";
        if (c.contains("budapest") || c.contains("hongrie")) return "Europe/Budapest";
        if (c.contains("athenes") || c.contains("athens") || c.contains("grece")) return "Europe/Athens";
        if (c.contains("istanbul") || c.contains("ankara") || c.contains("turquie")) return "Europe/Istanbul";
        if (c.contains("moscou") || c.contains("moscow") || c.contains("russie")) return "Europe/Moscow";
        if (c.contains("tunis") || c.contains("sfax") || c.contains("sousse") || c.contains("monastir") ||
                c.contains("bizerte") || c.contains("nabeul") || c.contains("tunisie") || c.contains("tunisia")) return "Africa/Tunis";
        if (c.contains("casablanca") || c.contains("marrakech") || c.contains("rabat") ||
                c.contains("fes") || c.contains("tanger") || c.contains("agadir") || c.contains("maroc")) return "Africa/Casablanca";
        if (c.contains("alger") || c.contains("oran") || c.contains("constantine") || c.contains("algerie")) return "Africa/Algiers";
        if (c.contains("cairo") || c.contains("le caire") || c.contains("alexandrie") || c.contains("egypte")) return "Africa/Cairo";
        if (c.contains("tripoli") || c.contains("libye")) return "Africa/Tripoli";
        if (c.contains("lagos") || c.contains("abuja") || c.contains("nigeria")) return "Africa/Lagos";
        if (c.contains("nairobi") || c.contains("kenya")) return "Africa/Nairobi";
        if (c.contains("johannesburg") || c.contains("cape town") || c.contains("afrique du sud")) return "Africa/Johannesburg";
        if (c.contains("dubai") || c.contains("abu dhabi") || c.contains("emirats")) return "Asia/Dubai";
        if (c.contains("riyad") || c.contains("riyadh") || c.contains("arabie")) return "Asia/Riyadh";
        if (c.contains("doha") || c.contains("qatar")) return "Asia/Qatar";
        if (c.contains("beyrouth") || c.contains("beirut") || c.contains("liban")) return "Asia/Beirut";
        if (c.contains("tokyo") || c.contains("osaka") || c.contains("kyoto") || c.contains("japon")) return "Asia/Tokyo";
        if (c.contains("beijing") || c.contains("shanghai") || c.contains("chine")) return "Asia/Shanghai";
        if (c.contains("seoul") || c.contains("coree")) return "Asia/Seoul";
        if (c.contains("singapore") || c.contains("singapour")) return "Asia/Singapore";
        if (c.contains("bangkok") || c.contains("thai")) return "Asia/Bangkok";
        if (c.contains("jakarta") || c.contains("bali") || c.contains("indonesie")) return "Asia/Jakarta";
        if (c.contains("mumbai") || c.contains("delhi") || c.contains("bangalore") || c.contains("inde")) return "Asia/Kolkata";
        if (c.contains("hong kong")) return "Asia/Hong_Kong";
        if (c.contains("new york") || c.contains("boston") || c.contains("miami") || c.contains("washington")) return "America/New_York";
        if (c.contains("los angeles") || c.contains("san francisco") || c.contains("seattle") || c.contains("las vegas")) return "America/Los_Angeles";
        if (c.contains("toronto") || c.contains("montreal") || c.contains("canada")) return "America/Toronto";
        if (c.contains("sao paulo") || c.contains("rio") || c.contains("bresil")) return "America/Sao_Paulo";
        if (c.contains("buenos aires") || c.contains("argentine")) return "America/Argentina/Buenos_Aires";
        if (c.contains("sydney") || c.contains("melbourne") || c.contains("australie")) return "Australia/Sydney";
        return "UTC";
    }
}