package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Admin;
import models.Users;
import services.ServiceUsers;
import utils.JwtUtil;
import utils.Session;
import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ToggleButton toggleEye;
    @FXML private Label errorLabel;

    private TextInputControl currentPasswordInput;
    private final ServiceUsers service = new ServiceUsers();

    @FXML
    public void initialize() {
        currentPasswordInput = passwordField;
        toggleEye.selectedProperty().addListener((obs, oldVal, newVal) -> {
            togglePasswordVisibility(newVal);
        });
    }

    private void togglePasswordVisibility(boolean show) {
        if (currentPasswordInput == null || currentPasswordInput.getParent() == null) {
            System.out.println("Toggle ignoré : champ ou parent non prêt");
            return;
        }

        HBox container = (HBox) currentPasswordInput.getParent();
        int index = container.getChildren().indexOf(currentPasswordInput);
        if (index < 0) {
            System.out.println("Index invalide");
            return;
        }

        if (show) {
            TextField textField = new TextField(currentPasswordInput.getText());
            textField.setPromptText(currentPasswordInput.getPromptText());
            textField.setDisable(currentPasswordInput.isDisabled());
            textField.setEditable(currentPasswordInput.isEditable());
            container.getChildren().set(index, textField);
            currentPasswordInput = textField;
        } else {
            PasswordField passField = new PasswordField();
            passField.setText(currentPasswordInput.getText());
            passField.setPromptText(currentPasswordInput.getPromptText());
            passField.setDisable(currentPasswordInput.isDisabled());
            passField.setEditable(currentPasswordInput.isEditable());
            container.getChildren().set(index, passField);
            currentPasswordInput = passField;
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = currentPasswordInput.getText().trim();

        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: red;");

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            Users loggedUser = service.login(email, password);

            System.out.println("Login réussi ! Utilisateur : " + loggedUser.getEmail() +
                    " | ID = " + loggedUser.getId() +
                    " | Rôle = " + (loggedUser instanceof Admin ? "ADMIN" : "VOYAGEUR") +
                    " | Verified = " + loggedUser.isVerified());

            String role = (loggedUser instanceof Admin) ? "ADMIN" : "VOYAGEUR";
            String token = JwtUtil.generateToken(loggedUser.getEmail(), role, loggedUser.getId());

            System.out.println("JWT généré : " + token.substring(0, Math.min(60, token.length())) + "...");

            Session.setCurrentUser(loggedUser);
            Session.setJwtToken(token);

            // ← ONLY CHANGE: added /views/ to the path
            String fxmlPath;
            String title;

            if (loggedUser instanceof Admin) {
                fxmlPath = "/DashboardAdmin.fxml";
                title = "After Travel - Administration";
            } else {
                fxmlPath = "/views/DashboardVoyageur.fxml";
                title = "After Travel - Espace Voyageur";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(title);

        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("non vérifié")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Compte en attente de validation");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Votre compte n'est pas encore activé.\n\n" +
                                "→ Consultez votre boîte email (" + email + ")\n" +
                                "→ Cliquez sur le lien de confirmation reçu\n\n" +
                                "Le lien est valide 24 heures."
                );
                alert.showAndWait();
            } else {
                showSimpleError("Erreur", ex.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showSimpleError("Échec de connexion", ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    private void showSimpleError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 700));
            stage.setTitle("After Travel - Inscription");
        } catch (Exception ex) {
            ex.printStackTrace();
            showSimpleError("Échec de connexion", ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName());
        }
    }
}