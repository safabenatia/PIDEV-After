package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import models.voyage;
import models.destination;
import services.ServiceVoyage;
import services.ServiceDestination;

import java.io.IOException;
import java.util.List;

public class MainViewController {

    @FXML
    private StackPane mainContent;

    @FXML
    private TextField searchField;

    private ServiceVoyage serviceVoyage = new ServiceVoyage();
    private ServiceDestination serviceDestination = new ServiceDestination();


    @FXML
    public void initialize() {
        showVoyages();
    }
    // DISPLAY VOYAGES IN GRID
    // ===============================

    @FXML
    public void showVoyages() {
        List<voyage> voyages = serviceVoyage.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        for (voyage v : voyages) {
            grid.getChildren().add(createVoyageCard(v));
        }

        mainContent.getChildren().setAll(grid);
    }

    // ===============================
    // DISPLAY DESTINATIONS IN GRID
    // ===============================

    @FXML
    public void showDestinations() {
        List<destination> destinations = serviceDestination.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        for (destination d : destinations) {
            grid.getChildren().add(createDestinationCard(d));
        }

        mainContent.getChildren().setAll(grid);
    }

    // ===============================
    // SEARCH VOYAGE BY TITRE
    // ===============================

    @FXML
    public void handleSearch() {

        String keyword = searchField.getText().toLowerCase();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        // SEARCH VOYAGES BY TITRE
        List<voyage> voyages = serviceVoyage.getAll();
        for (voyage v : voyages) {
            if (v.getTitre().toLowerCase().contains(keyword)) {
                grid.getChildren().add(createVoyageCard(v));
            }
        }

        // SEARCH DESTINATIONS BY ID
        List<destination> destinations = serviceDestination.getAll();
        for (destination d : destinations) {
            if (String.valueOf(d.getId_destination()).contains(keyword)) {
                grid.getChildren().add(createDestinationCard(d));
            }
        }

        mainContent.getChildren().setAll(grid);
    }



    // ===============================
    // CARD CREATION
    // ===============================

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

        box.getChildren().addAll(
                new Label("ID: " + v.getIdVoyage()),
                new Label("Titre: " + v.getTitre()),
                new Label("Prix: " + v.getPrix()),
                new Label("Places: " + v.getNbPlaces()),
                new Label("Statut: " + v.getStatut())
        );

        return box;
    }

    private VBox createDestinationCard(destination d) {
        VBox box = new VBox(8);
        box.setPrefWidth(250);
        box.setStyle("""
                -fx-background-color:white;
                -fx-padding:15;
                -fx-background-radius:10;
                -fx-border-radius:10;
                -fx-border-color:#16325c;
                """);

        box.getChildren().addAll(
                new Label("ID: " + d.getId_destination()),
                new Label("Pays: " + d.getPays()),
                new Label("Ville: " + d.getVille()),
                new Label("Continent: " + d.getContinent())
        );

        return box;
    }

    // ===============================
    // LOAD FORMS INSIDE CENTER
    // ===============================

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
