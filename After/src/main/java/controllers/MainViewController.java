package controllers;
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
import models.voyage;
import models.destination;
import services.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainViewController {

    @FXML
    private StackPane mainContent;

    @FXML
    private HBox crudBox;


    @FXML
    private TextField searchField;

    private ServiceVoyage serviceVoyage = new ServiceVoyage();
    private ServiceDestination serviceDestination = new ServiceDestination();
    private List<voyage> voyages = new ArrayList<>();
    private List<destination> destinations = new ArrayList<>();
    private String currentView = "voyages";
    private RestCountriesService restCountriesService = new RestCountriesService();
    private ExchangeRateService exchangeRateService = new ExchangeRateService();
    private PdfExportService pdfExportService = new PdfExportService();
    private FavoritesService favoritesService = new FavoritesService();

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch();
        });
        showVoyages();
    }
    @FXML
    public void showVoyages() {
        currentView = "voyages";
        crudBox.getChildren().clear();

        Button addBtn = new Button("Ajouter voyage");
        addBtn.setOnAction(e -> showAddForm());
        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        voyages = serviceVoyage.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        for (voyage v : voyages) {
            grid.getChildren().add(createVoyageCard(v));
        }

        Button sortBtn = new Button("Trier par date ↑");
        sortBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        final boolean[] ascending = {true};
        sortBtn.setOnAction(e -> {
            ascending[0] = !ascending[0];
            sortBtn.setText(ascending[0] ? "Trier par date ↑" : "Trier par date ↓");
            voyages.sort((a, b) -> {
                if (a.getDateDebut() == null || b.getDateDebut() == null) return 0;
                return ascending[0]
                        ? a.getDateDebut().compareTo(b.getDateDebut())
                        : b.getDateDebut().compareTo(a.getDateDebut());
            });
            grid.getChildren().clear();
            for (voyage vv : voyages) grid.getChildren().add(createVoyageCard(vv));
        });

        crudBox.getChildren().addAll(addBtn, sortBtn); // only ONE addAll

        Label header = new Label("Bienvenue à AFTER");
        header.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label subHeader = new Label("Où vos rêves deviennent réalité");
        subHeader.setStyle("-fx-font-size:16px; -fx-text-fill:#16325c;");

        VBox container = new VBox(20);
        container.setStyle("-fx-padding:20;");
        container.getChildren().addAll(header, subHeader, grid);

        mainContent.getChildren().setAll(container);
    }

    @FXML
    public void showDestinations() {
        currentView = "destinations";
        crudBox.getChildren().clear();

        Button addBtn = new Button("Ajouter destination");
        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        addBtn.setOnAction(e -> loadCenter("/AddDestination.fxml"));

        destinations = serviceDestination.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        for (destination d : destinations) {
            grid.getChildren().add(createDestinationCard(d));
        }

        Button sortBtn = new Button("Trier par pays ↑");
        sortBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        final boolean[] ascending = {true};
        sortBtn.setOnAction(e -> {
            ascending[0] = !ascending[0];
            sortBtn.setText(ascending[0] ? "Trier par pays ↑" : "Trier par pays ↓");
            destinations.sort((a, b) -> ascending[0]
                    ? a.getPays().compareTo(b.getPays())
                    : b.getPays().compareTo(a.getPays()));
            grid.getChildren().clear();
            for (destination dd : destinations) grid.getChildren().add(createDestinationCard(dd));
        });

        crudBox.getChildren().addAll(addBtn, sortBtn); // only ONE addAll

        Label header = new Label("Bienvenue à AFTER");
        header.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label subHeader = new Label("Où vos rêves deviennent réalité");
        subHeader.setStyle("-fx-font-size:16px; -fx-text-fill:#16325c;");

        VBox container = new VBox(20);
        container.setStyle("-fx-padding:20;");
        container.getChildren().addAll(header, subHeader, grid);

        mainContent.getChildren().setAll(container);
    }
    private void openEditVoyage(voyage v) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateVoyage.fxml"));
            Node node = loader.load();
            UpdateVoyageController ctrl = loader.getController();
            ctrl.setVoyage(v); // we'll pre-fill the form
            mainContent.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditDestination(destination d) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateDestination.fxml"));
            Node node = loader.load();
            UpdateDestinationController ctrl = loader.getController();
            ctrl.setDestination(d); // we'll pre-fill the form
            mainContent.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmDeleteVoyage(voyage v) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce voyage ?");

        ButtonType oui = new ButtonType("Oui");
        ButtonType annuler = new ButtonType("Annuler");
        alert.getButtonTypes().setAll(oui, annuler);

        alert.showAndWait().ifPresent(response -> {
            if (response == oui) {
                serviceVoyage.delete(v);
                showVoyages();
            }
        });
    }

    private void confirmDeleteDestination(destination d) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette destination ?");

        ButtonType oui = new ButtonType("Oui");
        ButtonType annuler = new ButtonType("Annuler");
        alert.getButtonTypes().setAll(oui, annuler);

        alert.showAndWait().ifPresent(response -> {
            if (response == oui) {
                serviceDestination.delete(d);
                showDestinations();
            }
        });
    }
    @FXML
    public void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            if (currentView.equals("voyages")) showVoyages();
            else showDestinations();
            return;
        }

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        if (currentView.equals("voyages")) {
            List<voyage> allVoyages = serviceVoyage.getAll();
            for (voyage v : allVoyages) {
                if (v.getTitre().toLowerCase().contains(keyword) ||
                        String.valueOf(v.getPrix()).contains(keyword) ||
                        String.valueOf(v.getNbPlaces()).contains(keyword) ||
                        (v.getDateDebut() != null && v.getDateDebut().toString().contains(keyword)) ||
                        (v.getDateFin() != null && v.getDateFin().toString().contains(keyword))) {
                    grid.getChildren().add(createVoyageCard(v));
                }
            }
        } else {
            List<destination> allDestinations = serviceDestination.getAll();
            for (destination d : allDestinations) {
                if (d.getPays().toLowerCase().contains(keyword) ||
                        d.getVille().toLowerCase().contains(keyword) ||
                        d.getContinent().toLowerCase().contains(keyword)) {
                    grid.getChildren().add(createDestinationCard(d));
                }
            }
        }

        mainContent.getChildren().setAll(grid);
    }

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
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                box.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Image load failed");
            }
        }

        Label titreLabel = new Label(v.getTitre());
        titreLabel.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label dateLabel = new Label("📅 " + v.getDateDebut() + "  →  " + v.getDateFin());
        dateLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#888888; -fx-font-style:italic;");

        Label prixLabel = new Label("💰 " + v.getPrix() + " TND");
        prixLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label conversionLabel = new Label("  ≈ chargement...");
        conversionLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#888;");

        Label placesLabel = new Label(v.getNbPlaces() + " places disponibles");
        placesLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#555;");

        box.getChildren().addAll(titreLabel, dateLabel, prixLabel, conversionLabel, placesLabel);

        // fetch conversion in background
        new Thread(() -> {
            double eur = exchangeRateService.convertToEUR(v.getPrix());
            double usd = exchangeRateService.convertToUSD(v.getPrix());
            javafx.application.Platform.runLater(() -> {
                if (eur != 0 && usd != 0) {
                    conversionLabel.setText("  ≈ " + eur + " EUR  |  " + usd + " USD");
                } else {
                    conversionLabel.setText("  ≈ conversion indisponible");
                }
            });
        }).start();

        Button editBtn = new Button("✎");
        Button deleteBtn = new Button("✖");
        Button pdfBtn = new Button("⬇ PDF");
        Button favBtn = new Button(favoritesService.isFavorite(v) ? "❤" : "♡");

        editBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        deleteBtn.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white;");
        pdfBtn.setStyle("-fx-background-color:#27ae60; -fx-text-fill:white;");
        favBtn.setStyle("-fx-background-color:white; -fx-border-color:#c0392b; -fx-border-radius:5;");

        editBtn.setOnAction(e -> openEditVoyage(v));
        deleteBtn.setOnAction(e -> confirmDeleteVoyage(v));

        favBtn.setOnAction(e -> {
            if (favoritesService.isFavorite(v)) {
                favoritesService.removeFavorite(v);
                favBtn.setText("♡");
            } else {
                favoritesService.addFavorite(v);
                favBtn.setText("❤");
            }
        });

        pdfBtn.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.setInitialFileName(v.getTitre() + "_voyage.pdf");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            java.io.File file = fileChooser.showSaveDialog(pdfBtn.getScene().getWindow());
            if (file != null) {
                pdfExportService.exportVoyage(v, file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("PDF exporté avec succès !");
                alert.showAndWait();
            }
        });

        HBox actions = new HBox(10, editBtn, deleteBtn, pdfBtn, favBtn);
        box.getChildren().add(actions);

        return box;
    }
    @FXML
    public void showFavorites() {
        currentView = "favorites";
        crudBox.getChildren().clear();

        List<voyage> favs = favoritesService.getFavorites();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        if (favs.isEmpty()) {
            Label empty = new Label("Aucun voyage en favoris pour le moment 💔");
            empty.setStyle("-fx-font-size:16px; -fx-text-fill:#16325c;");
            grid.getChildren().add(empty);
        } else {
            for (voyage v : favs) {
                grid.getChildren().add(createVoyageCard(v));
            }
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

        // destination image
        if (d.getImage() != null && !d.getImage().isEmpty()) {
            try {
                Image image = new Image(d.getImage(), 200, 150, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                box.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Image load failed");
            }
        }

        // flag placeholder — will be filled by API
        ImageView flagView = new ImageView();
        flagView.setFitWidth(40);
        flagView.setFitHeight(25);
        box.getChildren().add(flagView);

        Label paysLabel = new Label("🌍 " + d.getPays());
        paysLabel.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#16325c;");

        Label villeLabel = new Label("🏙️ " + d.getVille());
        Label continentLabel = new Label("🗺️ " + d.getContinent());

        // placeholders for API data
        Label capitalLabel = new Label("🏛️ Capitale: ...");
        Label currencyLabel = new Label("💵 Devise: ...");
        Label languageLabel = new Label("🗣️ Langue: ...");
        capitalLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#555;");
        currencyLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#555;");
        languageLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#555;");

        box.getChildren().addAll(paysLabel, villeLabel, continentLabel,
                capitalLabel, currencyLabel, languageLabel);

        // fetch API in background thread
        new Thread(() -> {
            String flag = restCountriesService.getFlag(d.getPays());
            String currency = restCountriesService.getCurrency(d.getPays());
            String language = restCountriesService.getLanguage(d.getPays());
            String capital = restCountriesService.getCapital(d.getPays());

            javafx.application.Platform.runLater(() -> {
                if (!flag.isEmpty()) {
                    try {
                        flagView.setImage(new Image(flag, 40, 25, true, true));
                    } catch (Exception e) {
                        System.out.println("Flag load failed");
                    }
                }
                capitalLabel.setText("🏛️ Capitale: " + (capital.isEmpty() ? "N/A" : capital));
                currencyLabel.setText("💵 Devise: " + (currency.isEmpty() ? "N/A" : currency));
                languageLabel.setText("🗣️ Langue: " + (language.isEmpty() ? "N/A" : language));
            });
        }).start();

        Button editBtn = new Button("✎");
        Button deleteBtn = new Button("✖");
        editBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        deleteBtn.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white;");
        editBtn.setOnAction(e -> openEditDestination(d));
        deleteBtn.setOnAction(e -> confirmDeleteDestination(d));

        HBox actions = new HBox(10, editBtn, deleteBtn);
        box.getChildren().add(actions);

        return box;
    }
    @FXML
    public void showAddForm() {
        loadCenter("/AddVoyage.fxml");
    }

    @FXML
    public void showUpdateForm() {
        loadCenter("/UpdateVoyage.fxml");
    }

    @FXML
    public void showDeleteForm() {
        loadCenter("/DeleteVoyage.fxml");
    }

    private void loadCenter(String fxml) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxml));
            mainContent.getChildren().setAll(node);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
