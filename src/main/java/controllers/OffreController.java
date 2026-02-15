package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import models.Offre;
import models.Service;
import services.OffreService;
import services.ServiceService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OffreController implements Initializable {

    @FXML
    private TextField TitreOffreField;

    @FXML
    private TextField PrixOffreField;

    @FXML
    private TextField DureeOffreField;

    @FXML
    private ComboBox<Service> serviceCombo;

    @FXML
    private TableView<Offre> tableOffres;

    @FXML
    private TableColumn<Offre, Integer> colId;

    @FXML
    private TableColumn<Offre, String> colTitre;

    @FXML
    private TableColumn<Offre, Double> colPrix;

    @FXML
    private TableColumn<Offre, Integer> colDuree;

    @FXML
    private TableColumn<Offre, String> colService;

    private ObservableList<Offre> offreList = FXCollections.observableArrayList();
    private OffreService offreService = new OffreService();
    private ServiceService serviceService = new ServiceService();
    private Offre offreSelectionne = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id_offre"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));

        // Configuration pour afficher le nom du service
        colService.setCellValueFactory(cellData -> {
            Offre offre = cellData.getValue();
            if (offre != null) {
                List<Service> services = serviceService.getAll();
                for (Service s : services) {
                    if (s.getId_service() == offre.getServiceId()) {
                        return new SimpleStringProperty(s.getNom_service());
                    }
                }
                return new SimpleStringProperty("Service inconnu (ID: " + offre.getServiceId() + ")");
            }
            return new SimpleStringProperty("");
        });

        // Charger les services dans le ComboBox
        chargerServices();

        // Charger les offres
        chargerOffres();

        // Remplir le formulaire lors de la sélection
        tableOffres.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        remplirFormulaire(newSelection);
                    }
                }
        );
    }

    private void chargerServices() {
        try {
            List<Service> services = serviceService.getAll();
            if (services != null) {
                serviceCombo.setItems(FXCollections.observableArrayList(services));

                // Personnaliser l'affichage
                serviceCombo.setCellFactory(lv -> new ListCell<Service>() {
                    @Override
                    protected void updateItem(Service service, boolean empty) {
                        super.updateItem(service, empty);
                        if (empty || service == null) {
                            setText(null);
                        } else {
                            setText(service.getNom_service() + " (ID: " + service.getId_service() + ")");
                        }
                    }
                });

                serviceCombo.setButtonCell(new ListCell<Service>() {
                    @Override
                    protected void updateItem(Service service, boolean empty) {
                        super.updateItem(service, empty);
                        if (empty || service == null) {
                            setText(null);
                        } else {
                            setText(service.getNom_service());
                        }
                    }
                });
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des services: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerOffres() {
        try {
            offreList.clear();
            List<Offre> offres = offreService.getAll();
            if (offres != null) {
                offreList.addAll(offres);
            }
            tableOffres.setItems(offreList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des offres: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void remplirFormulaire(Offre offre) {
        if (offre != null) {
            offreSelectionne = offre;
            TitreOffreField.setText(offre.getTitre());
            PrixOffreField.setText(String.valueOf(offre.getPrix()));
            DureeOffreField.setText(String.valueOf(offre.getDuree()));

            // Trouver et sélectionner le service
            List<Service> services = serviceService.getAll();
            for (Service s : services) {
                if (s.getId_service() == offre.getServiceId()) {
                    serviceCombo.setValue(s);
                    break;
                }
            }
        }
    }

    @FXML
    private void ajouterOffre() {
        if (!validateFields()) {
            return;
        }

        try {
            Offre offre = new Offre(
                    TitreOffreField.getText().trim(),
                    Double.parseDouble(PrixOffreField.getText().trim()),
                    Integer.parseInt(DureeOffreField.getText().trim()),
                    serviceCombo.getValue().getId_service()
            );

            offreService.add(offre);
            chargerOffres();
            annuler();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre ajoutée avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierOffre() {
        if (offreSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une offre à modifier.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        try {
            offreSelectionne.setTitre(TitreOffreField.getText().trim());
            offreSelectionne.setPrix(Double.parseDouble(PrixOffreField.getText().trim()));
            offreSelectionne.setDuree(Integer.parseInt(DureeOffreField.getText().trim()));
            offreSelectionne.setServiceId(serviceCombo.getValue().getId_service());

            offreService.update(offreSelectionne);
            chargerOffres();
            annuler();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre modifiée avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerOffre() {
        if (offreSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une offre à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'offre");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                offreService.delete(offreSelectionne);
                chargerOffres();
                annuler();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée avec succès!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void annuler() {
        TitreOffreField.clear();
        PrixOffreField.clear();
        DureeOffreField.clear();
        serviceCombo.setValue(null);
        offreSelectionne = null;
        tableOffres.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            Stage stage = (Stage) TitreOffreField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AFTER Travel - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le dashboard: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (TitreOffreField.getText() == null || TitreOffreField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre de l'offre est obligatoire.");
            return false;
        }
        if (PrixOffreField.getText() == null || PrixOffreField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix est obligatoire.");
            return false;
        }
        if (DureeOffreField.getText() == null || DureeOffreField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La durée est obligatoire.");
            return false;
        }
        if (serviceCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un service.");
            return false;
        }

        try {
            Double.parseDouble(PrixOffreField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre valide.");
            return false;
        }

        try {
            Integer.parseInt(DureeOffreField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La durée doit être un nombre entier.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}