package controllers;

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
import services.ServiceVoyage;
import services.ServiceDestination;

import java.io.IOException;
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


    @FXML
    public void initialize() {
        showVoyages();
    }


    @FXML
    public void showVoyages() {

        crudBox.getChildren().clear();

        Button addBtn = new Button("Ajouter voyage");
        Button updateBtn = new Button("Modifier voyage");
        Button deleteBtn = new Button("Supprimer voyage");

        addBtn.setOnAction(e -> showAddForm());
        updateBtn.setOnAction(e -> showUpdateForm());
        deleteBtn.setOnAction(e -> showDeleteForm());

        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        updateBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        deleteBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        crudBox.getChildren().addAll(addBtn, updateBtn, deleteBtn);

        List<voyage> voyages = serviceVoyage.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        for (voyage v : voyages) {
            grid.getChildren().add(createVoyageCard(v));
        }

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

        crudBox.getChildren().clear();

        Button addBtn = new Button("Ajouter destination");
        Button updateBtn = new Button("Modifier destination");
        Button deleteBtn = new Button("Supprimer destination");

        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        updateBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        deleteBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        addBtn.setOnAction(e -> loadCenter("/AddDestination.fxml"));
        updateBtn.setOnAction(e -> loadCenter("/UpdateDestination.fxml"));
        deleteBtn.setOnAction(e -> loadCenter("/DeleteDestination.fxml"));

        crudBox.getChildren().addAll(addBtn, updateBtn, deleteBtn);

        List<destination> destinations = serviceDestination.getAll();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        for (destination d : destinations) {
            grid.getChildren().add(createDestinationCard(d));
        }

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
    public void handleSearch() {

        String keyword = searchField.getText().toLowerCase();

        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(3);

        List<voyage> voyages = serviceVoyage.getAll();
        for (voyage v : voyages) {
            if (v.getTitre().toLowerCase().contains(keyword)) {
                grid.getChildren().add(createVoyageCard(v));
            }
        }

        List<destination> destinations = serviceDestination.getAll();
        for (destination d : destinations) {
            if (String.valueOf(d.getId_destination()).contains(keyword)) {
                grid.getChildren().add(createDestinationCard(d));
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

        VBox box = new VBox(10);
        box.setPrefWidth(250);
        box.setStyle("""
            -fx-background-color:white;
            -fx-padding:15;
            -fx-background-radius:10;
            -fx-border-radius:10;
            -fx-border-color:#16325c;
            """);

        // IMAGE
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

        box.getChildren().addAll(
                new Label("ID: " + d.getId_destination()),
                new Label("Pays: " + d.getPays()),
                new Label("Ville: " + d.getVille()),
                new Label("Continent: " + d.getContinent())
        );

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
