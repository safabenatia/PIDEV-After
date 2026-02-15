package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Role;
import models.Users;
import services.ServiceUsers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private FlowPane usersFlowPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<Role> roleFilterCombo;
    @FXML private Label totalUsersLabel;
    @FXML
    private Button btnLogout;

    @FXML


    private final ServiceUsers service = new ServiceUsers();
    private ObservableList<Users> allUsers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Charger tous les utilisateurs
        allUsers.addAll(service.getAll());
        updateStats();
        refreshCards();

        // Rôles dans le filtre
        roleFilterCombo.setItems(FXCollections.observableArrayList(Role.values()));
        roleFilterCombo.setValue(null); // Tous par défaut

        // Recherche dynamique
        searchField.textProperty().addListener((obs, old, newVal) -> filterAndRefresh());

        // Filtre rôle
        roleFilterCombo.valueProperty().addListener((obs, old, newVal) -> filterAndRefresh());
    }

    private void updateStats() {
        totalUsersLabel.setText(String.valueOf(allUsers.size()));
    }


    private void filterAndRefresh() {
        List<Users> filtered = allUsers.stream()
                .filter(user -> {
                    String search = searchField.getText().toLowerCase();
                    return (search.isEmpty() ||
                            user.getNom().toLowerCase().contains(search) ||
                            user.getPrenom().toLowerCase().contains(search) ||
                            user.getEmail().toLowerCase().contains(search));
                })
                .filter(user -> {
                    Role selectedRole = roleFilterCombo.getValue();
                    return (selectedRole == null || user.getRole() == selectedRole);
                })
                .collect(Collectors.toList());

        usersFlowPane.getChildren().clear();
        filtered.forEach(user -> usersFlowPane.getChildren().add(createUserCard(user)));
    }

    private void refreshCards() {
        filterAndRefresh();
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        roleFilterCombo.setValue(null);
        refreshCards(); // ou filterAndRefresh() si tu utilises cette méthode
    }

    private VBox createUserCard(Users user) {
        VBox card = new VBox(16);
        card.getStyleClass().add("user-card");

        // Avatar rond
        ImageView avatar = new ImageView();
        avatar.setFitWidth(90);
        avatar.setFitHeight(90);
        avatar.setPreserveRatio(true);
        avatar.getStyleClass().add("avatar-circle");

        String photoPath = user.getPhotoProfilUrl();
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                String fullPath = "file:" + System.getProperty("user.dir") + "/src/main/resources" + photoPath;
                avatar.setImage(new Image(fullPath));
            } catch (Exception ignored) {}
        }

        // Nom + email
        Label name = new Label(user.getNom() + " " + user.getPrenom());
        name.getStyleClass().add("name-label");

        Label email = new Label(user.getEmail());
        email.getStyleClass().add("email-label");

        // Badge rôle
        Label roleBadge = new Label(user.getRole() != null ? user.getRole().toString() : "—");
        roleBadge.getStyleClass().add("role-badge");

        // Téléphone
        Label phone = new Label(user.getTelephone() != null ? user.getTelephone() : "Non renseigné");
        phone.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        // Boutons
        HBox buttons = new HBox(12);
        Button edit = new Button("Modifier");
        edit.getStyleClass().addAll("btn-small", "btn-edit");
        edit.setOnAction(e -> showUserDialog(user));

        Button delete = new Button("Supprimer");
        delete.getStyleClass().addAll("btn-small", "btn-delete");
        delete.setOnAction(e -> {
            if (confirmDelete(user)) {
                service.delete(user);
                allUsers.remove(user);
                updateStats();
                refreshCards();
            }
        });

        buttons.getChildren().addAll(edit, delete);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        // Assemblage
        VBox info = new VBox(8, name, email, roleBadge, phone);
        HBox top = new HBox(20, avatar, info);
        top.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(top, buttons);

        return card;
    }

    private boolean confirmDelete(Users user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer " + user.getNom() + " " + user.getPrenom() + " ?");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }

    private void showUserDialog(Users user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user-dialog.fxml"));
            Parent root = loader.load();

            UserDialogController ctrl = loader.getController();
            ctrl.setUser(user);
            ctrl.setParent(this);

            Stage stage = new Stage();
            stage.setTitle(user == null ? "Nouvel utilisateur" : "Modifier utilisateur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 520, 580));
            stage.setResizable(false);
            stage.showAndWait();

            // Rafraîchir après fermeture
            allUsers.clear();
            allUsers.addAll(service.getAll());
            updateStats();
            refreshCards();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshUsers() {
        allUsers.clear();
        allUsers.addAll(service.getAll());
        updateStats();
        refreshCards();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Scene scene = new Scene(loader.load());

            // Utilise n'importe quel champ @FXML déjà existant pour récupérer la Stage
            // Exemple : searchField, totalUsersLabel, roleFilterCombo, etc.
            Stage stage = (Stage) searchField.getScene().getWindow();   // ← change "searchField" par un de tes vrais fx:id

            stage.setScene(scene);
            stage.setTitle("After - Connexion");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}