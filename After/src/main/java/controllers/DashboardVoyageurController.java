package controllers;

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
import models.Users;
import services.ServiceUsers;
import utils.PasswordUtil;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

public class DashboardVoyageurController {

    @FXML private Label welcomeLabel;
    @FXML private Label emailStatLabel;
    @FXML private Label phoneStatLabel;

    // Profil fields
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private PasswordField passwordField;
    @FXML private ToggleButton toggleEye;
    @FXML private ImageView photoPreview;
    @FXML private Label photoFileName;
    @FXML private Label feedbackLabel;

    private TextInputControl currentPasswordInput;
    private File selectedImageFile = null;
    private final ServiceUsers service = new ServiceUsers();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

    @FXML
    public void initialize() {
        currentPasswordInput = passwordField;

        Users user = Session.getCurrentUser();
        if (user == null) {
            handleLogout();
            return;
        }

        // Populate header
        welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
        emailStatLabel.setText(user.getEmail());
        phoneStatLabel.setText(user.getTelephone() != null && !user.getTelephone().isBlank()
                ? user.getTelephone() : "Non renseigné");

        // Populate form
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getTelephone() != null ? user.getTelephone() : "");

        // Photo
        String photo = user.getPhotoProfilUrl();
        if (photo != null && !photo.isBlank() && !photo.equals("null")) {
            try {
                String path = "file:" + System.getProperty("user.dir") + "/src/main/resources" + photo;
                photoPreview.setImage(new Image(path));
                photoFileName.setText(new File(photo).getName());
            } catch (Exception ignored) {}
        }

        // Password toggle
        toggleEye.selectedProperty().addListener((obs, oldVal, newVal) ->
                togglePasswordVisibility(newVal));
    }

    private void togglePasswordVisibility(boolean show) {
        if (currentPasswordInput == null || currentPasswordInput.getParent() == null) return;
        HBox container = (HBox) currentPasswordInput.getParent();
        int index = container.getChildren().indexOf(currentPasswordInput);
        if (index < 0) return;

        if (show) {
            TextField textField = new TextField(currentPasswordInput.getText());
            textField.setPromptText(currentPasswordInput.getPromptText());
            textField.getStyleClass().add("search-box");
            HBox.setHgrow(textField, javafx.scene.layout.Priority.ALWAYS);
            container.getChildren().set(index, textField);
            currentPasswordInput = textField;
        } else {
            PasswordField passField = new PasswordField();
            passField.setText(currentPasswordInput.getText());
            passField.setPromptText(currentPasswordInput.getPromptText());
            passField.getStyleClass().add("search-box");
            HBox.setHgrow(passField, javafx.scene.layout.Priority.ALWAYS);
            container.getChildren().set(index, passField);
            currentPasswordInput = passField;
        }
    }
    @FXML
    private void showServicesOffres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("After Travel - Services & Offres");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le module services.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleChoosePhoto() {
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
                showAlert("Erreur", "Impossible d'afficher l'image.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSaveProfil() {
        Users user = Session.getCurrentUser();
        if (user == null) return;

        String nom     = nomField.getText().trim();
        String prenom  = prenomField.getText().trim();
        String email   = emailField.getText().trim();
        String tel     = telephoneField.getText().trim();
        String password = currentPasswordInput.getText().trim();

        StringBuilder errors = new StringBuilder();

        if (nom.isEmpty())    errors.append("• Nom obligatoire\n");
        if (prenom.isEmpty()) errors.append("• Prénom obligatoire\n");
        if (email.isEmpty())  errors.append("• Email obligatoire\n");

        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches())
            errors.append("• Format email invalide\n");

        boolean emailChanged = !email.equals(user.getEmail());
        if (emailChanged && service.emailExistsSafe(email))
            errors.append("• Cet email est déjà utilisé\n");

        boolean changePassword = !password.isEmpty();
        if (changePassword && !PASSWORD_PATTERN.matcher(password).matches())
            errors.append("• Mot de passe trop faible (8+ car., maj, min, chiffre, spécial)\n");

        if (errors.length() > 0) {
            showAlert("Erreur de saisie", errors.toString(), Alert.AlertType.ERROR);
            return;
        }

        // Apply changes
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setTelephone(tel.isEmpty() ? null : tel);

        if (changePassword) {
            user.setMotDePasse(password); // hashed inside service.update()
        } else {
            user.setMotDePasse(null); // signal: don't change password
        }

        // Photo
        if (selectedImageFile != null) {
            try {
                user.setPhotoProfilUrl(saveImage(selectedImageFile));
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de sauvegarder la photo.", Alert.AlertType.ERROR);
                return;
            }
        }

        service.update(user);
        Session.setCurrentUser(user);

        // Refresh header stats
        welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
        emailStatLabel.setText(user.getEmail());
        phoneStatLabel.setText(user.getTelephone() != null && !user.getTelephone().isBlank()
                ? user.getTelephone() : "Non renseigné");

        feedbackLabel.setText("✓ Profil mis à jour avec succès !");
        feedbackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #16a34a;");
    }
    @FXML
    private void showVoyages() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent voyageView = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(voyageView, stage.getWidth(), stage.getHeight()));
            stage.setMaximized(true);
            stage.setTitle("After Travel - Voyages & Destinations");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le module voyage.", Alert.AlertType.ERROR);
        }
    }
    // Sidebar navigation — only profil for now
    @FXML
    private void showProfil() {
        // Already visible by default, nothing to switch
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
    private void handleLogout() {
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("After Travel - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}