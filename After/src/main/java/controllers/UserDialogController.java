package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Role;
import models.Users;
import services.ServiceUsers;
import utils.PasswordUtil;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

public class UserDialogController {

    @FXML private Label titleLabel;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField telephoneField;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private ImageView photoPreview;
    @FXML private Label photoFileName;
    @FXML private ToggleButton toggleEye;
    private TextInputControl currentPasswordInput;

    private Users user;
    private DashboardController parent;
    private final ServiceUsers service = new ServiceUsers();

    private File selectedImageFile = null;
    private String currentPhotoPath = null;

    // Regex pour email (plus permissive mais réaliste)
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Regex mot de passe : min 8, maj, min, chiffre, caractère spécial
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(Role.values()));

        // Initialisation cruciale
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

    public void setUser(Users user) {
        this.user = user;
        if (user == null) {
            titleLabel.setText("Ajouter un utilisateur");
            clearFields();
            passwordField.setPromptText("Mot de passe obligatoire");
        } else {
            titleLabel.setText("Modifier l'utilisateur");
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            passwordField.setPromptText("Laissez vide pour garder l'ancien");
            telephoneField.setText(user.getTelephone());
            roleCombo.setValue(user.getRole());

            if (user.getPhotoProfilUrl() != null && !user.getPhotoProfilUrl().isEmpty()) {
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
            showErrorAlert("Erreur", "Impossible de récupérer la fenêtre actuelle : " + e.getMessage());
            return;
        }

        if (stage == null) {
            showErrorAlert("Erreur", "La fenêtre parent est introuvable");
            return;
        }

        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            photoFileName.setText(selectedImageFile.getName());
            try {
                photoPreview.setImage(new Image(selectedImageFile.toURI().toString()));
                showErrorAlert("Succès", "Image chargée : " + selectedImageFile.getName());
            } catch (Exception e) {
                showErrorAlert("Erreur de chargement", "Impossible d'afficher l'image : " + e.getMessage());
                photoPreview.setImage(null);
                photoFileName.setText("Erreur de chargement");
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
        String password = currentPasswordInput.getText(); // ← IMPORTANT : toujours current !
        String tel = telephoneField.getText().trim();
        Role role = roleCombo.getValue();

        StringBuilder errors = new StringBuilder();

        // Contrôles obligatoires
        if (nom.isEmpty()) errors.append("• Nom obligatoire\n");
        if (prenom.isEmpty()) errors.append("• Prénom obligatoire\n");
        if (email.isEmpty()) errors.append("• Email obligatoire\n");
        if (role == null) errors.append("• Rôle obligatoire\n");

        // Contrôle format email
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("• Format d'email invalide (ex: nom@domaine.com)\n");
        }

        // Contrôle unicité email
        boolean emailChanged = (user == null) || !email.equals(user.getEmail());
        if (emailChanged && service.emailExistsSafe(email)) {
            errors.append("• Cet email est déjà utilisé\n");
        }

        // Contrôle mot de passe
        boolean isNewPassword = !password.trim().isEmpty();
        if (user == null && !isNewPassword) {
            errors.append("• Mot de passe obligatoire pour un nouvel utilisateur\n");
        }
        if (isNewPassword && !PASSWORD_PATTERN.matcher(password).matches()) {
            errors.append("• Mot de passe trop faible :\n");
            errors.append("  - Minimum 8 caractères\n");
            errors.append("  - Au moins 1 majuscule\n");
            errors.append("  - Au moins 1 minuscule\n");
            errors.append("  - Au moins 1 chiffre\n");
            errors.append("  - Au moins 1 caractère spécial (!@#$%^&* etc.)\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Erreur de saisie", errors.toString());
            return;
        }

        // Tout OK → sauvegarde
        boolean isNew = (user == null);
        if (isNew) user = new Users();

        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setTelephone(tel);
        user.setRole(role);

        // Mot de passe : seulement si saisi
        if (isNewPassword) {
            user.setMotDePasse(password); // sera hashé dans ServiceUsers
        } // sinon → modification + vide = on garde l'ancien

        // Photo
        if (selectedImageFile != null) {
            try {
                user.setPhotoProfilUrl(saveImage(selectedImageFile));
            } catch (IOException e) {
                showErrorAlert("Erreur", "Impossible de sauvegarder la photo");
                return;
            }
        } else if (currentPhotoPath != null) {
            user.setPhotoProfilUrl(currentPhotoPath);
        }

        if (isNew) {
            service.add(user);
        } else {
            service.update(user);
        }

        if (parent != null) parent.refreshUsers();
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

    @FXML private void handleCancel() { close(); }

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
        roleCombo.setValue(null);
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