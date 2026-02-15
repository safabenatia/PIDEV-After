package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Users;
import services.ServiceUsers;
import utils.PasswordUtil;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ToggleButton toggleEye;
    @FXML private Label errorLabel;
    private TextInputControl currentPasswordInput;

    private ServiceUsers service = new ServiceUsers();

    @FXML
    public void initialize() {
        currentPasswordInput = passwordField;

        toggleEye.selectedProperty().addListener((obs, oldVal, newVal) -> {
            togglePasswordVisibility(newVal);
        });
    }

    private void togglePasswordVisibility(boolean show) {
        // Sécurité 1 : champ ou parent non prêt
        if (currentPasswordInput == null || currentPasswordInput.getParent() == null) {
            System.out.println("Toggle ignoré : champ ou parent non attaché");
            return;
        }

        // Cast explicite en HBox (getChildren() devient accessible)
        HBox container = (HBox) currentPasswordInput.getParent();

        int index = container.getChildren().indexOf(currentPasswordInput);

        // Sécurité 2 : index invalide
        if (index < 0) {
            System.out.println("Le champ mot de passe n'est pas dans le container");
            return;
        }

        if (show) {
            // Montrer en clair
            TextField textField = new TextField(currentPasswordInput.getText());
            textField.setPromptText(currentPasswordInput.getPromptText());
            textField.setDisable(currentPasswordInput.isDisabled());
            textField.setEditable(currentPasswordInput.isEditable());
            container.getChildren().set(index, textField);
            currentPasswordInput = textField;  // Mise à jour référence
        } else {
            // Remasquer
            PasswordField newPassField = new PasswordField();
            newPassField.setText(currentPasswordInput.getText());
            newPassField.setPromptText(currentPasswordInput.getPromptText());
            newPassField.setDisable(currentPasswordInput.isDisabled());
            newPassField.setEditable(currentPasswordInput.isEditable());
            container.getChildren().set(index, newPassField);
            currentPasswordInput = newPassField;  // Mise à jour référence
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        Users loggedUser = service.login(email, password);

        if (loggedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardCtrl = loader.getController();
                // dashboardCtrl.setCurrentUser(loggedUser); // si tu veux passer l'utilisateur

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 800));
                stage.setTitle("After - Dashboard");
            } catch (IOException e) {
                errorLabel.setText("Erreur de chargement du dashboard");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Email ou mot de passe incorrect");
        }
    }

    @FXML
    private void goToSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 700));
            stage.setTitle("After Travel - Inscription");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}