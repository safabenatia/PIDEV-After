package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Activite;
import models.Planning;
import services.ServiceActivite;
import services.ServicePlanning;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlanningController {

    @FXML
    private ComboBox<Activite> activiteComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField heureField;

    @FXML
    private TextField dureeField;

    private ServicePlanning servicePlanning = new ServicePlanning();
    private ServiceActivite serviceActivite = new ServiceActivite();

    private int fixedUserId=1;


    private Planning editingPlanning = null;

    public void setFixedUserId(int id) {
        this.fixedUserId = id;
    }

    @FXML
    public void initialize() {
        loadActivites();


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

    private void loadActivites() {
        List<Activite> activites = serviceActivite.getAll();
        ObservableList<Activite> obsList = FXCollections.observableArrayList(activites);
        activiteComboBox.setItems(obsList);
    }


    public void loadPlanning(Planning planning) {
        this.editingPlanning = planning;

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
    public void ajouter() {

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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Heure invalide (format HH:mm) !");
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


        if (editingPlanning != null) {

            editingPlanning.setIdActivite(selectedActivite.getIdActivite());
            editingPlanning.setDateActivite(date);
            editingPlanning.setHeureDebut(heureDebut);
            editingPlanning.setDuree(duree);

            servicePlanning.update(editingPlanning);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Planning modifié avec succès !");

        } else {


            Planning p = new Planning(
                    fixedUserId,
                    selectedActivite.getIdActivite(),
                    date,
                    heureDebut,
                    duree
            );

            servicePlanning.add(p);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Planning ajouté avec succès !");
        }

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




