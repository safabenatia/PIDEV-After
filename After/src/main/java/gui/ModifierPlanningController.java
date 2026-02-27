package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Planning;
import models.Activite;
import services.ServicePlanning;
import services.ServiceActivite;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModifierPlanningController {

    @FXML
    private ComboBox<Activite> activiteComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField heureField;

    @FXML
    private TextField dureeField;

    private Planning planning;

    private ServicePlanning servicePlanning = new ServicePlanning();
    private ServiceActivite serviceActivite = new ServiceActivite();


    @FXML
    public void initialize() {

        // Charger toutes les activités
        List<Activite> activites = serviceActivite.getAll();
        activiteComboBox.setItems(FXCollections.observableArrayList(activites));

        // 🔥 Afficher seulement le NOM dans la liste
        activiteComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Activite item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });


        activiteComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Activite item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
    }


    public void setPlanning(Planning planning) {
        this.planning = planning;

        // Sélectionner l'activité correspondante
        for (Activite a : activiteComboBox.getItems()) {
            if (a.getIdActivite() == planning.getIdActivite()) {
                activiteComboBox.setValue(a);
                break;
            }
        }

        datePicker.setValue(planning.getDateActivite());
        heureField.setText(planning.getHeureDebut().toString());
        dureeField.setText(String.valueOf(planning.getDuree()));
    }


    @FXML
    private void handleUpdate() {

        Activite selectedActivite = activiteComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String heureText = heureField.getText().trim();
        String dureeText = dureeField.getText().trim();

        if (selectedActivite == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une activité !");
            return;
        }

        if (date == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une date !");
            return;
        }

        if (heureText.isEmpty() || dureeText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        LocalTime heureDebut;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            heureDebut = LocalTime.parse(heureText, formatter);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format heure invalide (HH:mm) !");
            return;
        }

        int duree;
        try {
            duree = Integer.parseInt(dureeText);
            if (duree <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Durée invalide !");
            return;
        }

        // Mise à jour du planning
        planning.setIdActivite(selectedActivite.getIdActivite());
        planning.setDateActivite(date);
        planning.setHeureDebut(heureDebut);
        planning.setDuree(duree);

        servicePlanning.update(planning);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Planning mis à jour avec succès !");
        closeWindow();
    }


    @FXML
    private void handleCancel() {
        closeWindow();
    }


    private void closeWindow() {
        Stage stage = (Stage) activiteComboBox.getScene().getWindow();
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

