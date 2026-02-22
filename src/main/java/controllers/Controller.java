package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private Button ajouterButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        ajouterButton.setOnAction(event -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Champs incomplets");
                alert.setContentText("Veuillez remplir tous les champs !");
                alert.showAndWait();
            } else {

                messageLabel.setText("✓ Personne ajoutée : " + prenom + " " + nom);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Personne ajoutée avec succès");
                alert.setContentText("Nom : " + nom + "\nPrénom : " + prenom);
                alert.showAndWait();

                nomField.clear();
                prenomField.clear();
            }
        });
    }
}