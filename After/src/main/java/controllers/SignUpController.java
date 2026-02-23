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
import models.Voyageur;
import services.ServiceUsers;
import services.EmailService; // ← importe-le ici
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
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
    @FXML private Label errorLabel;

    private File selectedImageFile = null;
    private TextInputControl currentPasswordInput;
    private final ServiceUsers service = new ServiceUsers();
    private final EmailService emailService = new EmailService(); // ← instance

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

    @FXML
    public void initialize() {
        currentPasswordInput = passwordField;
        toggleEye.selectedProperty().addListener((obs, old, selected) -> togglePasswordVisibility(selected));
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        Stage stage = (Stage) photoPreview.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);
        if (selectedImageFile != null) {
            photoFileName.setText(selectedImageFile.getName());
            try {
                photoPreview.setImage(new Image(selectedImageFile.toURI().toString()));
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'afficher l'image", Alert.AlertType.ERROR);
                photoPreview.setImage(null);
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

        if (nom.isEmpty()) errors.append("• Le nom est obligatoire\n");
        if (prenom.isEmpty()) errors.append("• Le prénom est obligatoire\n");
        if (email.isEmpty()) errors.append("• L'email est obligatoire\n");
        if (password.isEmpty()) errors.append("• Le mot de passe est obligatoire\n");

        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("• Format d'email invalide\n");
        }

        if (service.emailExistsSafe(email)) {
            errors.append("• Cet email est déjà utilisé\n");
        }

        if (!password.isEmpty() && !PASSWORD_PATTERN.matcher(password).matches()) {
            errors.append("• Mot de passe trop faible :\n");
            errors.append("  - ≥ 8 caractères\n");
            errors.append("  - ≥ 1 majuscule\n");
            errors.append("  - ≥ 1 minuscule\n");
            errors.append("  - ≥ 1 chiffre\n");
            errors.append("  - ≥ 1 caractère spécial\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreur d'inscription", errors.toString(), Alert.AlertType.ERROR);
            errorLabel.setText("Vérifiez les erreurs ci-dessus");
            return;
        }

        Voyageur newUser = new Voyageur();
        newUser.setNom(nom);
        newUser.setPrenom(prenom);
        newUser.setEmail(email);
        newUser.setMotDePasse(password); // sera hashé dans service.add()
        newUser.setTelephone(tel.isEmpty() ? null : tel);

        // Photo
        if (selectedImageFile != null) {
            try {
                newUser.setPhotoProfilUrl(saveImage(selectedImageFile));
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de sauvegarder la photo", Alert.AlertType.ERROR);
                return;
            }
        } else {
            newUser.setPhotoProfilUrl("/public/profiles/default.jpg");
        }

        // Génération token de vérification
        String token = UUID.randomUUID().toString();
        newUser.setVerificationToken(token);
        newUser.setVerificationExpiry(java.time.LocalDateTime.now().plusHours(24));

        // Enregistrement
        service.add(newUser);

        // Envoi email de confirmation
        try {
            new EmailService().sendVerificationEmail(email, token);
            showAlert(
                    "Inscription réussie",
                    "Votre compte a été créé avec succès !\n\n" +
                            "Pour finaliser votre inscription :\n" +
                            "→ Consultez votre boîte email (" + email + ")\n" +
                            "→ Cliquez sur le lien de confirmation reçu\n\n" +
                            "Le lien est valide 24 heures.",
                    Alert.AlertType.INFORMATION
            );
        } catch (Exception e) {
            showAlert("Attention", "Compte créé mais email de confirmation non envoyé.", Alert.AlertType.WARNING);
            e.printStackTrace();
        }

        goToLogin();
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
            stage.setScene(new Scene(root));
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de connexion", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}