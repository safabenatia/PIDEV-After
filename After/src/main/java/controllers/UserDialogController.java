package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Admin;
import models.Users;
import models.Voyageur;
import services.ServiceUsers;
import services.EmailService;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserDialogController {

    @FXML private Label titleLabel;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField telephoneField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ImageView photoPreview;
    @FXML private Label photoFileName;
    @FXML private ToggleButton toggleEye;

    private TextInputControl currentPasswordInput;
    private Users currentUser;
    private DashboardController parent;
    private final ServiceUsers service = new ServiceUsers();
    private final EmailService emailService = new EmailService();

    private File selectedImageFile = null;
    private String currentPhotoPath = null;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList("ADMIN", "VOYAGEUR"));
        typeCombo.setValue("VOYAGEUR"); // défaut création
        currentPasswordInput = passwordField;
        toggleEye.selectedProperty().addListener((obs, oldVal, newVal) -> togglePasswordVisibility(newVal));
    }

    private void togglePasswordVisibility(boolean show) {
        if (currentPasswordInput == null || currentPasswordInput.getParent() == null) return;
        HBox container = (HBox) currentPasswordInput.getParent();
        int index = container.getChildren().indexOf(currentPasswordInput);
        if (index < 0) return;
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

    public void setUser(Users user) {
        this.currentUser = user;
        if (user == null) {
            titleLabel.setText("Ajouter un utilisateur");
            clearFields();
            passwordField.setPromptText("Mot de passe obligatoire");
            typeCombo.setDisable(false);
        } else {
            titleLabel.setText("Modifier l'utilisateur");
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            telephoneField.setText(user.getTelephone() != null ? user.getTelephone() : "");
            passwordField.setPromptText("Laissez vide pour garder l'ancien");

            String type;
            if (user instanceof Admin) {
                Admin admin = (Admin) user;
                type = "ADMIN";
                typeCombo.setDisable(true); // empêche changement type
            } else if (user instanceof Voyageur) {
                Voyageur voy = (Voyageur) user;
                type = "VOYAGEUR";
                typeCombo.setDisable(true);
            } else {
                type = "VOYAGEUR";
            }
            typeCombo.setValue(type);

            if (user.getPhotoProfilUrl() != null && !user.getPhotoProfilUrl().isBlank()) {
                currentPhotoPath = user.getPhotoProfilUrl();
                photoFileName.setText(new File(currentPhotoPath).getName());
                try {
                    photoPreview.setImage(new Image("file:" + currentPhotoPath));
                } catch (Exception ignored) {}
            }
        }
    }

    public void setParent(DashboardController parent) {
        this.parent = parent;
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
                showErrorAlert("Erreur", "Impossible d'afficher l'image");
                photoPreview.setImage(null);
            }
        } else {
            photoFileName.setText("Aucune image sélectionnée");
        }
    }

    @FXML
    private void handleSave() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = currentPasswordInput.getText().trim();
        String tel = telephoneField.getText().trim();
        String type = typeCombo.getValue();

        StringBuilder errors = new StringBuilder();

        if (nom.isEmpty()) errors.append("• Nom obligatoire\n");
        if (prenom.isEmpty()) errors.append("• Prénom obligatoire\n");
        if (email.isEmpty()) errors.append("• Email obligatoire\n");
        if (type == null) errors.append("• Type obligatoire\n");

        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("• Format email invalide\n");
        }

        boolean emailChanged = (currentUser == null) || !email.equals(currentUser.getEmail());
        if (emailChanged && service.emailExistsSafe(email)) {
            errors.append("• Cet email est déjà utilisé\n");
        }

        boolean isNewPassword = !password.isEmpty();
        if (currentUser == null && !isNewPassword) {
            errors.append("• Mot de passe obligatoire pour création\n");
        }
        if (isNewPassword && !PASSWORD_PATTERN.matcher(password).matches()) {
            errors.append("• Mot de passe trop faible (8+ car., maj, min, chiffre, spécial)\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Erreur de saisie", errors.toString());
            return;
        }

        Users userToSave;
        boolean isNew = (currentUser == null);

        if (isNew) {
            if ("ADMIN".equals(type)) {
                userToSave = new Admin();
            } else {
                userToSave = new Voyageur();
            }
        } else {
            userToSave = currentUser;
        }

        userToSave.setNom(nom);
        userToSave.setPrenom(prenom);
        userToSave.setEmail(email);
        userToSave.setTelephone(tel.isEmpty() ? null : tel);

        if (isNewPassword) {
            userToSave.setMotDePasse(password); // hashé dans service
        }

        // Photo
        if (selectedImageFile != null) {
            try {
                userToSave.setPhotoProfilUrl(saveImage(selectedImageFile));
            } catch (IOException e) {
                showErrorAlert("Erreur", "Échec sauvegarde photo");
                return;
            }
        } else if (currentPhotoPath != null) {
            userToSave.setPhotoProfilUrl(currentPhotoPath);
        }

        // DOUBLE OPT-IN : seulement pour les nouveaux utilisateurs
        if (isNew) {
            String token = UUID.randomUUID().toString();
            userToSave.setVerificationToken(token);
            userToSave.setVerificationExpiry(java.time.LocalDateTime.now().plusHours(24));
            userToSave.setVerified(false);

            // Envoi email de confirmation
            try {
                new EmailService().sendVerificationEmail(email, token);
            } catch (Exception e) {
                showErrorAlert("Attention", "Utilisateur ajouté mais email de confirmation non envoyé.");
                e.printStackTrace();
            }
        }

        // Sauvegarde
        if (isNew) {
            service.add(userToSave);
        } else {
            service.update(userToSave);
        }

        if (parent != null) {
            parent.refreshUsers();
        }

        close();
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
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        telephoneField.clear();
        typeCombo.setValue("VOYAGEUR");
        photoPreview.setImage(null);
        photoFileName.setText("Aucune image sélectionnée");
        selectedImageFile = null;
        currentPhotoPath = null;
        toggleEye.setSelected(false);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}