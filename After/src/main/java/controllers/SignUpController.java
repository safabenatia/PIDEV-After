package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Role;
import models.Users;
import services.ServiceUsers;
import utils.PasswordUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField telephoneField;
    @FXML private ImageView photoPreview;
    @FXML private Label photoFileName;
    @FXML private ToggleButton toggleEye;
    @FXML private Label errorLabel;  // on garde pour info en bas, mais l'Alert sera prioritaire

    private File selectedImageFile = null;
    private TextInputControl currentPasswordInput;

    private final ServiceUsers service = new ServiceUsers();

    // Regex email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Regex mot de passe fort
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

    @FXML
    public void initialize() {
        // Initialisation toggle mot de passe
        currentPasswordInput = passwordField;

        toggleEye.selectedProperty().addListener((obs, old, selected) -> {
            togglePasswordVisibility(selected);
        });
    }

    private void togglePasswordVisibility(boolean show) {
        if (currentPasswordInput == null || currentPasswordInput.getParent() == null) return;

        HBox container = (HBox) currentPasswordInput.getParent();
        int index = container.getChildren().indexOf(currentPasswordInput);
        if (index < 0) return;

        if (show) {
            TextField textField = new TextField(currentPasswordInput.getText());
            textField.setPromptText(currentPasswordInput.getPromptText());
            container.getChildren().set(index, textField);
            currentPasswordInput = textField;
        } else {
            PasswordField passField = new PasswordField();
            passField.setText(currentPasswordInput.getText());
            passField.setPromptText(currentPasswordInput.getPromptText());
            container.getChildren().set(index, passField);
            currentPasswordInput = passField;
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");

        // Filtres d'extension (images courantes)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        // Essaie d'ouvrir le dialogue
        Stage stage = null;
        try {
            stage = (Stage) photoPreview.getScene().getWindow();  // ou nomField.getScene().getWindow()
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de récupérer la fenêtre actuelle : " + e.getMessage());
            return;
        }

        if (stage == null) {
            showAlert("Erreur", "La fenêtre parent est introuvable");
            return;
        }

        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            photoFileName.setText(selectedImageFile.getName());
            try {
                photoPreview.setImage(new Image(selectedImageFile.toURI().toString()));
                showAlert("Succès", "Image chargée : " + selectedImageFile.getName());
            } catch (Exception e) {
                showAlert("Erreur de chargement", "Impossible d'afficher l'image : " + e.getMessage());
                photoPreview.setImage(null);
                photoFileName.setText("Erreur de chargement");
            }
        } else {
            photoFileName.setText("Aucune image sélectionnée");
        }
    }

    @FXML
    private void handleSignUp() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = currentPasswordInput.getText().trim();
        String tel = telephoneField.getText().trim();

        StringBuilder errors = new StringBuilder();

        // Contrôles obligatoires
        if (nom.isEmpty()) errors.append("• Le nom est obligatoire\n");
        if (prenom.isEmpty()) errors.append("• Le prénom est obligatoire\n");
        if (email.isEmpty()) errors.append("• L'email est obligatoire\n");
        if (password.isEmpty()) errors.append("• Le mot de passe est obligatoire\n");

        // Format email
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("• Le format de l'email est invalide (ex: nom@domaine.com)\n");
        }

        // Unicité email
        if (service.emailExistsSafe(email)) {
            errors.append("• Cet email est déjà utilisé par un autre compte\n");
        }

        // Complexité mot de passe
        if (!password.isEmpty() && !PASSWORD_PATTERN.matcher(password).matches()) {
            errors.append("• Le mot de passe ne respecte pas les exigences :\n");
            errors.append("  - Au moins 8 caractères\n");
            errors.append("  - Au moins 1 lettre majuscule\n");
            errors.append("  - Au moins 1 lettre minuscule\n");
            errors.append("  - Au moins 1 chiffre\n");
            errors.append("  - Au moins 1 caractère spécial (!@#$%^&* etc.)\n");
        }

        // Affichage d'une Alert si erreurs
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'inscription");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes :");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            errorLabel.setText("Vérifiez les erreurs ci-dessus"); // optionnel : petit message en bas
            return;
        }

        // Tout est correct → création de l'utilisateur
        Users newUser = new Users();
        newUser.setNom(nom);
        newUser.setPrenom(prenom);
        newUser.setEmail(email);
        newUser.setMotDePasse(password); // sera hashé dans service.add()
        newUser.setTelephone(tel);
        newUser.setRole(Role.VOYAGEUR); // rôle forcé

        // Photo
        if (selectedImageFile != null) {
            try {
                newUser.setPhotoProfilUrl(saveImage(selectedImageFile));
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur photo");
                alert.setContentText("Impossible de sauvegarder la photo : " + e.getMessage());
                alert.showAndWait();
                return;
            }
        } else {
            newUser.setPhotoProfilUrl("default.jpg");
        }

        // Enregistrement
        service.add(newUser);

        // Succès → Alert de confirmation + redirection
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Inscription réussie");
        successAlert.setHeaderText("Bienvenue sur After !");
        successAlert.setContentText("Votre compte a été créé avec succès.\nVous allez être redirigé vers la page de connexion.");
        successAlert.showAndWait();

        // Redirection vers login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setContentText("Impossible de charger la page de connexion");
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    private String saveImage(File source) throws IOException {
        String dir = "src/main/resources/public/profiles/";
        Path dirPath = Path.of(dir);
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

        String ext = source.getName().substring(source.getName().lastIndexOf("."));
        String name = System.currentTimeMillis() + ext;
        Path dest = Path.of(dir + name);
        Files.copy(source.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        return "/public/profiles/" + name;
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}