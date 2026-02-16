package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Activite;
import services.ServiceActivite;

public class ActiviteController {

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

    private ServiceActivite service = new ServiceActivite();

    // ============================
    // AJOUTER
    // ============================



    // ============================
    // AJOUTER
    // ============================
    @FXML
    public void initialize() {

        addValidation(nomField);
        addValidation(descriptionField);
        addValidation(categorieField);
        addValidation(lieuField);

        // Prix : seulement nombres
        prixField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                prixField.setText(oldValue);
            }
        });
    }

    // ============================
    // Validation bordure rouge
    // ============================
    private void addValidation(TextField field) {

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal) { // quand on quitte le champ

                if (field.getText().trim().length() < 5) {
                    field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                } else {
                    field.setStyle(null);
                }
            }
        });
    }

    // ============================
    // AJOUTER
    // ============================
    @FXML
    public void ajouter() {

        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String categorie = categorieField.getText().trim();
        String lieu = lieuField.getText().trim();
        String prixText = prixField.getText().trim();

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

        try {
            Activite a = new Activite();
            a.setNom(nom);
            a.setDescription(description);
            a.setCategorie(categorie);
            a.setLieu(lieu);
            a.setPrix(prix);

            service.add(a);

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Activité ajoutée avec succès !");
            clearFields();
            closeWindow();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }

    }



    // ============================
    // MODIFIER
    // ============================


    // ============================
    // CLEAR
    // ============================
    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        categorieField.clear();
        lieuField.clear();
        prixField.clear();

        nomField.setStyle(null);
        descriptionField.setStyle(null);
        categorieField.setStyle(null);
        lieuField.setStyle(null);
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
