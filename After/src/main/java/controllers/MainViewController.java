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

        addBtn.setOnAction(e -> showAddForm());

        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        crudBox.getChildren().addAll(addBtn);

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

        addBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");

        addBtn.setOnAction(e -> loadCenter("/AddDestination.fxml"));

        crudBox.getChildren().addAll(addBtn);

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
                new Label("Titre: " + v.getTitre()),
                new Label("Prix: " + v.getPrix()),
                new Label("Places: " + v.getNbPlaces()),
                new Label("Statut: " + v.getStatut())
        );

        Button editBtn = new Button("✏️");
        Button deleteBtn = new Button("🗑️");

        editBtn.setStyle("-fx-background-color:#16325c; -fx-text-fill:white;");
        deleteBtn.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white;");

        editBtn.setOnAction(e -> openEditVoyage(v));
        deleteBtn.setOnAction(e -> confirmDeleteVoyage(v));

        HBox actions = new HBox(10, editBtn, deleteBtn);
        box.getChildren().add(actions);

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
                new Label("Pays: " + d.getPays()),
                new Label("Ville: " + d.getVille()),
                new Label("Continent: " + d.getContinent())
        );

        Button editBtn = new Button("✏️");
        Button deleteBtn = new Button("🗑️");

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
