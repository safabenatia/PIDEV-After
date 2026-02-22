package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import models.CategorieDocument;
import services.serviceCategorieDocument;

import java.util.List;

public class CategorieDocumentController {

    @FXML
    private TableView<CategorieDocument> tableCategorie;

    @FXML
    private TableColumn<CategorieDocument, Integer> colId;

    @FXML
    private TableColumn<CategorieDocument, String> colLibelle;

    @FXML
    private TableColumn<CategorieDocument, String> colDescription;

    @FXML
    private TextField txtLibelle;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtSearch;

    private final serviceCategorieDocument service = new serviceCategorieDocument();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdCategorie()).asObject());

        colLibelle.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLibelle()));
        colDescription.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        refreshTable();

        // Recherche dynamique
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> searchCategorie(newValue));

        // Click Table → Remplir champs
        tableCategorie.setOnMouseClicked((MouseEvent event) -> {
            CategorieDocument selected = tableCategorie.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtLibelle.setText(selected.getLibelle());
                txtDescription.setText(selected.getDescription());
            }
        });
    }

    private void refreshTable() {
        List<CategorieDocument> categories = service.getAll();
        ObservableList<CategorieDocument> data = FXCollections.observableArrayList(categories);
        tableCategorie.setItems(data);
    }

    private void searchCategorie(String keyword) {
        List<CategorieDocument> categories = service.getAll();
        ObservableList<CategorieDocument> filtered = FXCollections.observableArrayList();

        for (CategorieDocument cat : categories) {
            if (cat.getLibelle().toLowerCase().contains(keyword.toLowerCase()) ||
                    cat.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(cat);
            }
        }

        tableCategorie.setItems(filtered);
    }

    @FXML
    private void ajouterCategorie() {
        if (!validateFields()) return;

        if (service.existsByLibelle(txtLibelle.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Cette catégorie existe déjà !");
            return;
        }

        CategorieDocument cat = new CategorieDocument();
        cat.setLibelle(txtLibelle.getText().trim());
        cat.setDescription(txtDescription.getText().trim());

        service.add(cat);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée ✔");
        refreshTable();
        clearFields();
    }

    @FXML
    private void modifierCategorie() {
        CategorieDocument selected = tableCategorie.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez une catégorie à modifier.");
            return;
        }

        if (!validateFields()) return;

        selected.setLibelle(txtLibelle.getText().trim());
        selected.setDescription(txtDescription.getText().trim());

        service.update(selected);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie modifiée ✔");
        refreshTable();
        clearFields();
    }

    @FXML
    private void supprimerCategorie() {
        CategorieDocument selected = tableCategorie.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez une catégorie à supprimer.");
            return;
        }

        if (!service.isUsed(selected.getIdCategorie())) {
            service.delete(selected);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie supprimée ✔");
            refreshTable();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Suppression impossible", "Cette catégorie est utilisée par des documents !");
            return;
        }


    }

    private boolean validateFields() {
        String libelle = txtLibelle.getText().trim();
        String desc = txtDescription.getText().trim();

        if (libelle.isEmpty() || desc.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return false;
        }

        if (libelle.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le libellé doit contenir au moins 3 caractères.");
            return false;
        }

        if (desc.length() < 5) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La description doit contenir au moins 5 caractères.");
            return false;
        }

        if (!libelle.matches("[a-zA-ZÀ-ÿ ]+")) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le libellé doit contenir uniquement des lettres.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        txtLibelle.clear();
        txtDescription.clear();
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
