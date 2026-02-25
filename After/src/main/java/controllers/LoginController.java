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
import utils.JwtUtil;      // ← importe ta classe JwtUtil
import utils.Session;      // ← la classe Session que je t'ai donnée
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
        String password = currentPasswordInput.getText().trim();  // .trim() aussi ici

        // Reset du message d'erreur au début
        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: red;");  // couleur par défaut erreur

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            Users loggedUser = service.login(email, password);

            // Si on arrive ici → login OK + compte VERIFIED
            System.out.println("Login réussi ! Utilisateur : " + loggedUser.getEmail() +
                    " | ID = " + loggedUser.getId() +
                    " | Rôle = " + (loggedUser instanceof Admin ? "ADMIN" : "VOYAGEUR") +
                    " | Verified = " + loggedUser.isVerified());

            String role = (loggedUser instanceof Admin) ? "ADMIN" : "VOYAGEUR";
            String token = JwtUtil.generateToken(loggedUser.getEmail(), role, loggedUser.getId());

            System.out.println("JWT généré : " + token.substring(0, Math.min(60, token.length())) + "...");

            // Stockage de la session
            Session.setCurrentUser(loggedUser);
            Session.setJwtToken(token);

            // Chargement du dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("After Travel - Dashboard");

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
            showSimpleError("Échec de connexion", "Email ou mot de passe incorrect");
        }
    }
// Méthode helper (ajoute-la dans la classe)
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}