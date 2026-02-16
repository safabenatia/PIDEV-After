package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Activite;
import services.ServiceActivite;

public class ModifierActiviteController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField categorieField;
    @FXML
    private TextField lieuField;
    @FXML
    private TextField prixField;

    private Activite activite;
    private ServiceActivite service = new ServiceActivite();

    // ============================
    // RECEVOIR ACTIVITE SELECTIONNEE
    // ============================
    public void setActivite(Activite activite) {
        this.activite = activite;

        nomField.setText(activite.getNom());
        descriptionField.setText(activite.getDescription());
        categorieField.setText(activite.getCategorie());
        lieuField.setText(activite.getLieu());
        prixField.setText(String.valueOf(activite.getPrix()));
    }

    // ============================
    // MODIFIER
    // ============================
    @FXML
    private void handleUpdate() {

        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String categorie = categorieField.getText().trim();
        String lieu = lieuField.getText().trim();
        String prixText = prixField.getText().trim();

        // ================= CONTROLE DE SAISIE (IDENTIQUE) =================

        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Nom est obligatoire !");
            return;
        }

        if (nom.length() < 5) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Nom doit contenir au minimum 5 caractères !");
            return;
        }

        if (description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Description est obligatoire !");
            return;
        }

        if (description.length() < 5) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Description doit contenir au minimum 5 caractères !");
            return;
        }

        if (categorie.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Catégorie est obligatoire !");
            return;
        }

        if (categorie.length() < 5) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Catégorie doit contenir au minimum 5 caractères !");
            return;
        }

        if (lieu.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Lieu est obligatoire !");
            return;
        }

        if (lieu.length() < 5) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Lieu doit contenir au minimum 5 caractères !");
            return;
        }

        if (prixText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le champ Prix est obligatoire !");
            return;
        }

        double prix = Double.parseDouble(prixText);

        if (prix <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le prix doit être supérieur à 0 !");
            return;
        }

        // ================= MISE A JOUR =================

        activite.setNom(nom);
        activite.setDescription(description);
        activite.setCategorie(categorie);
        activite.setLieu(lieu);
        activite.setPrix(prix);

        service.update(activite);

        showAlert(Alert.AlertType.INFORMATION, "Succès",
                "Activité modifiée avec succès !");

        closeWindow();
    }

    // ============================
    // ANNULER
    // ============================
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    // ============================
    // ALERTES
    // ============================
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

