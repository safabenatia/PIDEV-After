package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Categorie;
import models.depense;
import services.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DepenseControllerV2 {

    @FXML private TextField titreField;
    @FXML private TextField montantField;
    @FXML private ComboBox<String> deviseSourceCombo;
    @FXML private ComboBox<String> deviseCibleCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private Button ajouterBtn;
    @FXML private Button camembertBtn;
    @FXML private ComboBox<String> triCombo;
    @FXML private Button verifierBudgetBtn;
    @FXML private MenuItem rapportTexteItem;
    @FXML private MenuItem rapportPDFItem;
    @FXML private Label conversionLabel;
    @FXML private VBox depensesContainer;
    @FXML private Label totalLabel;

    private service.serviceDeppense depenseService;
    private CategorieService categorieService;
    private service.ConvertisseurDeviseService convertisseurService;
    private RapportService rapportService;
    private ObservableList<depense> depensesList;
    private ObservableList<Categorie> categoriesList;
    private depense depenseEnModification = null;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        depenseService = new service.serviceDeppense();
        categorieService = new CategorieService();
        convertisseurService = new service.ConvertisseurDeviseService();
        rapportService = new RapportService();
        depensesList = FXCollections.observableArrayList();
        categoriesList = FXCollections.observableArrayList();

        deviseSourceCombo.setItems(FXCollections.observableArrayList("TND", "EUR", "USD"));
        deviseCibleCombo.setItems(FXCollections.observableArrayList("TND", "EUR", "USD"));
        deviseSourceCombo.setValue("TND");
        deviseCibleCombo.setValue("TND");

        triCombo.setItems(FXCollections.observableArrayList(
                "Date (recent → ancien)",
                "Date (ancien → recent)",
                "Montant (↑ croissant)",
                "Montant (↓ decroissant)"
        ));

        chargerCategoriesDepuisBD();
        datePicker.setValue(LocalDate.now());
        chargerDepenses();

        ajouterBtn.setOnAction(event -> ajouterOuModifierDepense());
        triCombo.setOnAction(event -> appliquerTri());
        verifierBudgetBtn.setOnAction(event -> {
            VerificationBudgetDialog dialog = new VerificationBudgetDialog();
            dialog.afficher((Stage) verifierBudgetBtn.getScene().getWindow());
        });
        rapportTexteItem.setOnAction(event -> genererRapportTexte());
        rapportPDFItem.setOnAction(event -> genererRapportPDF());
        camembertBtn.setOnAction(event -> afficherCamembert());
        deviseSourceCombo.setOnAction(event -> afficherConversion());
        deviseCibleCombo.setOnAction(event -> afficherConversion());
        montantField.textProperty().addListener((obs, old, nouveau) -> afficherConversion());

        calculerTotal();
    }

    private void chargerCategoriesDepuisBD() {
        categoriesList.clear();
        categoriesList.addAll(categorieService.getAll());
        categorieCombo.setItems(categoriesList);

        categorieCombo.setCellFactory(param -> new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomCategorie());
            }
        });

        categorieCombo.setButtonCell(new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomCategorie());
            }
        });
    }

    private void ajouterOuModifierDepense() {
        if (titreField.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "Le titre est obligatoire !", Alert.AlertType.ERROR);
            return;
        }
        if (montantField.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "Le montant est obligatoire !", Alert.AlertType.ERROR);
            return;
        }
        if (datePicker.getValue() == null) {
            afficherAlerte("Erreur", "La date est obligatoire !", Alert.AlertType.ERROR);
            return;
        }
        if (categorieCombo.getValue() == null) {
            afficherAlerte("Erreur", "La categorie est obligatoire !", Alert.AlertType.ERROR);
            return;
        }

        try {
            double montant = Double.parseDouble(montantField.getText().trim());
            String deviseSource = deviseSourceCombo.getValue();
            String deviseCible = deviseCibleCombo.getValue();

            double montantFinal = montant;
            if (!deviseSource.equals(deviseCible)) {
                montantFinal = convertisseurService.convertirDevise(montant, deviseSource, deviseCible);
            }

            double montantTND = montantFinal;
            if (!deviseCible.equals("TND")) {
                montantTND = convertisseurService.convertirDevise(montantFinal, deviseCible, "TND");
            }

            if (depenseEnModification == null) {
                depense d = new depense();
                d.setTitre(titreField.getText().trim());
                d.setMontant(montantTND);
                d.setDateDepense(Date.valueOf(datePicker.getValue()));
                d.setIdCategorie(categorieCombo.getValue().getIdCat());
                depenseService.add(d);
                afficherAlerte("Succes", "Depense ajoutee avec succes !", Alert.AlertType.INFORMATION);
            } else {
                depenseEnModification.setTitre(titreField.getText().trim());
                depenseEnModification.setMontant(montantTND);
                depenseEnModification.setDateDepense(Date.valueOf(datePicker.getValue()));
                depenseEnModification.setIdCategorie(categorieCombo.getValue().getIdCat());
                depenseService.update(depenseEnModification);
                afficherAlerte("Succes", "Depense modifiee avec succes !", Alert.AlertType.INFORMATION);
                depenseEnModification = null;
                ajouterBtn.setText("➕ Ajouter la depense");
                ajouterBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #16325c; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 35; -fx-background-radius: 8; -fx-cursor: hand;");
            }

            chargerDepenses();
            viderFormulaire();
            calculerTotal();

        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le montant doit etre un nombre valide !", Alert.AlertType.ERROR);
        }
    }

    private void chargerDepenses() {
        chargerDepenses(depenseService.getAll());
    }

    private void chargerDepenses(List<depense> depenses) {
        depensesList.clear();
        depensesList.addAll(depenses);
        depensesContainer.getChildren().clear();
        for (depense d : depensesList) {
            depensesContainer.getChildren().add(creerCarteDepense(d));
        }
    }

    private HBox creerCarteDepense(depense d) {
        HBox carte = new HBox(20);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");

        VBox idBox = new VBox(5);
        idBox.setAlignment(Pos.CENTER);
        idBox.setStyle("-fx-background-color: #16325c; -fx-padding: 10; -fx-background-radius: 8;");
        Label idLabel = new Label("#" + d.getIdDep());
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        idBox.getChildren().add(idLabel);
        idBox.setPrefWidth(60);

        VBox iconeBox = new VBox(5);
        iconeBox.setAlignment(Pos.CENTER);
        iconeBox.setStyle("-fx-background-color: #F5F5DC; -fx-padding: 10; -fx-background-radius: 8;");
        iconeBox.setPrefWidth(70);
        iconeBox.setPrefHeight(70);

        Categorie cat = categorieService.getById(d.getIdCategorie());

        if (cat != null && cat.getIconeUrl() != null && !cat.getIconeUrl().isEmpty()) {
            try {
                File imageFile = new File(cat.getIconeUrl());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setPreserveRatio(true);
                    iconeBox.getChildren().add(imageView);
                } else {
                    Label defaultIcon = new Label("📂");
                    defaultIcon.setStyle("-fx-font-size: 32px;");
                    iconeBox.getChildren().add(defaultIcon);
                }
            } catch (Exception e) {
                Label defaultIcon = new Label("📂");
                defaultIcon.setStyle("-fx-font-size: 32px;");
                iconeBox.getChildren().add(defaultIcon);
            }
        } else {
            Label defaultIcon = new Label("📂");
            defaultIcon.setStyle("-fx-font-size: 32px;");
            iconeBox.getChildren().add(defaultIcon);
        }

        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label titreLabel = new Label(d.getTitre());
        titreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #16325c;");
        String nomCat = cat != null ? cat.getNomCategorie() : "Categorie inconnue";
        Label categorieLabel = new Label("📁 " + nomCat);
        categorieLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        infoBox.getChildren().addAll(titreLabel, categorieLabel);

        VBox montantBox = new VBox(3);
        montantBox.setAlignment(Pos.CENTER_RIGHT);
        montantBox.setPrefWidth(150);
        Label montantLabel = new Label(String.format("%.2f DT", d.getMontant()));
        montantLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        montantBox.getChildren().add(montantLabel);

        VBox dateBox = new VBox(5);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPrefWidth(120);
        LocalDate localDate;
        if (d.getDateDepense() instanceof Date) {
            localDate = ((Date) d.getDateDepense()).toLocalDate();
        } else {
            localDate = new Date(d.getDateDepense().getTime()).toLocalDate();
        }
        Label dateLabel = new Label("📅 " + localDate.format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        dateBox.getChildren().add(dateLabel);

        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnModifier = new Button("✏️");
        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 10 15; -fx-background-radius: 5; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> remplirFormulaireAvecDepense(d));
        Button btnSupprimer = new Button("🗑️");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 10 15; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> supprimerDepense(d));
        actionsBox.getChildren().addAll(btnModifier, btnSupprimer);

        carte.getChildren().addAll(idBox, iconeBox, infoBox, montantBox, dateBox, actionsBox);
        return carte;
    }

    private void appliquerTri() {
        String choix = triCombo.getValue();
        if (choix == null) return;
        List<depense> depensesTriees;
        switch (choix) {
            case "Date (recent → ancien)": depensesTriees = depenseService.trierParDate(false); break;
            case "Date (ancien → recent)": depensesTriees = depenseService.trierParDate(true); break;
            case "Montant (↑ croissant)": depensesTriees = depenseService.trierParMontant(true); break;
            case "Montant (↓ decroissant)": depensesTriees = depenseService.trierParMontant(false); break;
            default: depensesTriees = depenseService.getAll();
        }
        chargerDepenses(depensesTriees);
    }

    private void genererRapportTexte() {
        String rapport = rapportService.genererRapportMoisCourant();
        TextArea textArea = new TextArea(rapport);
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        Stage stage = new Stage();
        stage.setTitle("📊 Rapport Mensuel - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        stage.setScene(new Scene(new VBox(textArea), 800, 600));
        stage.show();
    }

    private void genererRapportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.setInitialFileName("Rapport_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(rapportPDFItem.getParentPopup().getOwnerWindow());
        if (file != null) {
            LocalDate now = LocalDate.now();
            String chemin = rapportService.exporterRapportPDF(now.getMonthValue(), now.getYear(), file.getAbsolutePath());
            if (chemin != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succes");
                alert.setHeaderText("Rapport PDF genere !");
                alert.setContentText("Le rapport a ete enregistre dans :\n" + chemin);
                alert.showAndWait();
            } else {
                afficherAlerte("Erreur", "Impossible de generer le PDF", Alert.AlertType.ERROR);
            }
        }
    }

    private void afficherConversion() {
        try {
            String deviseSource = deviseSourceCombo.getValue();
            String deviseCible = deviseCibleCombo.getValue();
            if (deviseSource == null || deviseCible == null) { conversionLabel.setText(""); return; }
            if (deviseSource.equals(deviseCible)) { conversionLabel.setText("(Même devise)"); return; }
            if (montantField.getText().trim().isEmpty()) { conversionLabel.setText(""); return; }
            double montant = Double.parseDouble(montantField.getText().trim());
            double montantConverti = convertisseurService.convertirDevise(montant, deviseSource, deviseCible);
            conversionLabel.setText(String.format("≈ %.2f %s", montantConverti, deviseCible));
        } catch (NumberFormatException e) {
            conversionLabel.setText("");
        }
    }

    // ========== CAMEMBERT ==========

    private void afficherCamembert() {
        Map<String, Double> totauxParCategorie = new HashMap<>();
        for (depense d : depensesList) {
            Categorie cat = categorieService.getById(d.getIdCategorie());
            String nomCat = cat != null ? cat.getNomCategorie() : "Autres";
            totauxParCategorie.merge(nomCat, d.getMontant(), Double::sum);
        }

        if (totauxParCategorie.isEmpty()) {
            afficherAlerte("Camembert", "Aucune depense a afficher !", Alert.AlertType.INFORMATION);
            return;
        }

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        totauxParCategorie.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Repartition des depenses par categorie",
                dataset, true, true, false
        );

        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("Aucune depense enregistree");
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        chart.setBackgroundPaint(java.awt.Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 450));
        chartPanel.setMouseWheelEnabled(true);

        JFrame frame = new JFrame("Camembert des depenses");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ========== UTILITAIRES ==========
    @FXML
    private void ouvrirCategories() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/gestion_categories_v2.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Catégories");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setMaximized(true);  // ← fullscreen
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible d'ouvrir les catégories : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void remplirFormulaireAvecDepense(depense d) {
        depenseEnModification = d;
        titreField.setText(d.getTitre());
        montantField.setText(String.valueOf(d.getMontant()));
        deviseSourceCombo.setValue("TND");
        deviseCibleCombo.setValue("TND");
        if (d.getDateDepense() != null) {
            if (d.getDateDepense() instanceof Date) {
                datePicker.setValue(((Date) d.getDateDepense()).toLocalDate());
            } else {
                datePicker.setValue(new Date(d.getDateDepense().getTime()).toLocalDate());
            }
        }
        for (Categorie cat : categoriesList) {
            if (cat.getIdCat() == d.getIdCategorie()) {
                categorieCombo.setValue(cat);
                break;
            }
        }
        ajouterBtn.setText("💾 Enregistrer");
        ajouterBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 35; -fx-background-radius: 8; -fx-cursor: hand;");
        titreField.requestFocus();
    }

    private void supprimerDepense(depense d) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer cette depense ?");
        confirmation.setContentText("Etes-vous sur de vouloir supprimer : " + d.getTitre() + " ?");
        Optional<ButtonType> resultat = confirmation.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            depenseService.delete(d);
            afficherAlerte("Succes", "Depense supprimee avec succes !", Alert.AlertType.INFORMATION);
            chargerDepenses();
            calculerTotal();
        }
    }

    private void viderFormulaire() {
        titreField.clear();
        montantField.clear();
        deviseSourceCombo.setValue("TND");
        deviseCibleCombo.setValue("TND");
        datePicker.setValue(LocalDate.now());
        categorieCombo.getSelectionModel().clearSelection();
        conversionLabel.setText("");
    }

    private void calculerTotal() {
        double total = depensesList.stream().mapToDouble(depense::getMontant).sum();
        totalLabel.setText(String.format("Total: %.2f DT", total));
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}