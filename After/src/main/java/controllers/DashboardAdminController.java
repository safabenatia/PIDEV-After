package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import models.Admin;
import models.Users;
import models.Voyageur;
import services.ServiceUsers;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;




import java.net.URL;                       // ← pour URL et MalformedURLException
import java.net.MalformedURLException;      // ← si tu utilises catch(MalformedURLException)
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import com.opencsv.CSVWriter;


import java.io.FileWriter;

import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import utils.JwtUtil;
import utils.Session;


public class DashboardAdminController {

    @FXML private FlowPane usersFlowPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilterCombo;   // ← ComboBox pour type (pas Role)
    @FXML private Label totalUsersLabel;
    @FXML private Button btnLogout;

    private final ServiceUsers service = new ServiceUsers();
    private ObservableList<Users> allUsers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
       /*if (Session.getJwtToken() == null || !JwtUtil.validateToken(Session.getJwtToken())) {
            handleLogout();
            return;
        }*/
        try {
            List<Users> usersFromDb = service.getAll();
            System.out.println("Utilisateurs chargés depuis la BDD : " + usersFromDb.size());
            allUsers.addAll(usersFromDb);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
            e.printStackTrace();
            // Optionnel : montrer une alerte à l'utilisateur
            new Alert(Alert.AlertType.ERROR, "Impossible de charger les utilisateurs").show();
        }

        updateStats();
        refreshCards();

        // ComboBox (avec protection)
        if (typeFilterCombo != null) {
            typeFilterCombo.setItems(FXCollections.observableArrayList("Tous", "ADMIN", "VOYAGEUR"));
            typeFilterCombo.setValue("Tous");
            typeFilterCombo.valueProperty().addListener((obs, old, newVal) -> filterAndRefresh());
        } else {
            System.err.println("ERREUR : typeFilterCombo est null → fx:id incorrect dans FXML ?");
        }

        searchField.textProperty().addListener((obs, old, newVal) -> filterAndRefresh());
    }

    private void updateStats() {
        totalUsersLabel.setText(String.valueOf(allUsers.size()));
    }

    private void filterAndRefresh() {
        String search = (searchField.getText() != null)
                ? searchField.getText().toLowerCase()
                : "";

        // On lit la valeur UNE SEULE FOIS, et on ne la modifie plus
        final String selected = (typeFilterCombo != null && typeFilterCombo.getValue() != null)
                ? typeFilterCombo.getValue()
                : "Tous";

        List<Users> filtered = allUsers.stream()
                .filter(u -> search.isEmpty() ||
                        u.getNom().toLowerCase().contains(search) ||
                        u.getPrenom().toLowerCase().contains(search) ||
                        u.getEmail().toLowerCase().contains(search))
                .filter(u -> "Tous".equals(selected) ||
                        ("ADMIN".equals(selected) && u instanceof Admin) ||
                        ("VOYAGEUR".equals(selected) && u instanceof Voyageur))
                .collect(Collectors.toList());






        usersFlowPane.getChildren().clear();

        if (filtered.isEmpty()) {
            Label emptyLabel = new Label("Aucun utilisateur trouvé");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b; -fx-padding: 40;");
            usersFlowPane.getChildren().add(emptyLabel);
        } else {
            filtered.forEach(u -> usersFlowPane.getChildren().add(createUserCard(u)));
        }
    }

    private void refreshCards() {
        filterAndRefresh();
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        typeFilterCombo.setValue("Tous");
        refreshCards();
    }

    private void startTokenExpirationChecker() {
        // Vérifie toutes les 30 secondes (ajuste à 60000 pour 1 min)
        PauseTransition checker = new PauseTransition(Duration.seconds(30));
        checker.setOnFinished(event -> {
            if (!JwtUtil.validateToken(Session.getJwtToken())) {
                System.out.println("Token expiré → déconnexion automatique");
                Session.clear();
                showAlert("Session expirée" + "Votre session de 24 heures est terminée. Veuillez vous reconnecter.");
                handleLogout();
            } else {
                checker.playFromStart(); // relance le timer
            }
        });
        checker.play(); // démarre immédiatement
    }

    private VBox createUserCard(Users user) {
        VBox card = new VBox(16);
        card.getStyleClass().add("user-card");

        // Avatar
        ImageView avatar = new ImageView();
        avatar.setFitWidth(90);
        avatar.setFitHeight(90);
        avatar.setPreserveRatio(true);
        avatar.getStyleClass().add("avatar-circle");

        String photo = user.getPhotoProfilUrl();
        if (photo != null && !photo.isBlank() && !photo.equals("null")) {
            try {
                String path = "file:" + System.getProperty("user.dir") + "/src/main/resources" + photo;
                avatar.setImage(new Image(path));
            } catch (Exception ignored) {}
        }

        // Infos principales
        Label name = new Label(user.getNom() + " " + user.getPrenom());
        name.getStyleClass().add("name-label");

        Label email = new Label(user.getEmail());
        email.getStyleClass().add("email-label");

        // Type (ADMIN / VOYAGEUR)
        String typeText = (user instanceof Admin) ? "ADMIN" : "VOYAGEUR";
        Label typeBadge = new Label(typeText);
        typeBadge.getStyleClass().add("role-badge");
        if (user instanceof Admin) {
            typeBadge.getStyleClass().add("admin-badge");   // tu peux styliser différemment
        }

        Label phone = new Label(user.getTelephone() != null && !user.getTelephone().isBlank()
                ? user.getTelephone() : "Non renseigné");
        phone.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        // Boutons
        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().addAll("btn-small", "btn-edit");
        editBtn.setOnAction(e -> showUserDialog(user));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().addAll("btn-small", "btn-delete");
        deleteBtn.setOnAction(e -> {
            if (confirmDelete(user)) {
                service.delete(user);
                allUsers.remove(user);
                updateStats();
                refreshCards();
            }
        });

        buttons.getChildren().addAll(editBtn, deleteBtn);

        // ────────────────────────────────────────────────
        // EXEMPLE DE DOWNCAST demandé par ton prof
        // Bouton visible UNIQUEMENT pour les admins
        // ────────────────────────────────────────────────
        if (user instanceof Admin) {
            Button adminActionsBtn = new Button("Actions Admin");
            adminActionsBtn.getStyleClass().addAll("btn-small", "btn-admin-special");
            adminActionsBtn.setOnAction(e -> {
                // DOWNCAST ici (sécurisé grâce au instanceof)
                Admin admin = (Admin) user;
                // Exemple d'utilisation : on pourrait appeler une méthode spécifique
                // admin.gererSignalements();   ← si tu ajoutes cette méthode dans Admin
                showAlert("Actions admin pour : " + admin.getNom() + " " + admin.getPrenom());
            });
            buttons.getChildren().add(adminActionsBtn);
        }
        // ────────────────────────────────────────────────

        // Assemblage final
        VBox infoBox = new VBox(8, name, email, typeBadge, phone);
        HBox topBox = new HBox(20, avatar, infoBox);
        topBox.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(topBox, buttons);
        return card;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
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
    @FXML
    private void handleExportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les utilisateurs en CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                // En-tête
                writer.writeNext(new String[]{"ID", "Nom", "Prénom", "Email", "Téléphone", "Type", "Date création"});

                // Données (utilise ta liste actuelle ou recharge depuis la BDD)
                List<Users> users = service.getAll(); // ou filtered si tu veux exporter la vue actuelle

                for (Users user : users) {
                    String type = (user instanceof Admin) ? "ADMIN" : "VOYAGEUR";
                    writer.writeNext(new String[]{
                            String.valueOf(user.getId()),
                            user.getNom(),
                            user.getPrenom(),
                            user.getEmail(),
                            user.getTelephone() != null ? user.getTelephone() : "",
                            type,
                            "2025-01-01" // remplace par vraie date si tu as le champ
                    });
                }

                showAlert("Succès" + "Export CSV terminé : " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Erreur" +  "Impossible d'exporter le fichier : " + e.getMessage());
            }
        }
    }
    @FXML
    private void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la liste des utilisateurs avec photos en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("utilisateurs_" + java.time.LocalDate.now() + ".pdf");

        File file = fileChooser.showSaveDialog(usersFlowPane.getScene().getWindow());
        if (file == null) return;

        try {
            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Liste des utilisateurs - After Travel")
                    .setBold().setFontSize(20).setMarginBottom(20));

            float[] columnWidths = {1.5f, 1, 2.5f, 2.5f, 5, 2.5f, 2};
            Table table = new Table(UnitValue.createPointArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Photo").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Nom").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Prénom").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Téléphone").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Type").setBold()));

            List<Users> usersToExport = service.getAll();

            for (Users user : usersToExport) {
                String type = (user instanceof Admin) ? "ADMIN" : "VOYAGEUR";

                Cell photoCell = new Cell();
                String photoPath = user.getPhotoProfilUrl();

                if (photoPath != null && !photoPath.trim().isEmpty() && !"null".equals(photoPath)) {
                    try {
                        URL resource = getClass().getResource(photoPath);
                        ImageData imageData;
                        if (resource != null) {
                            imageData = ImageDataFactory.create(resource);
                        } else {
                            String fullPath = photoPath.startsWith("/")
                                    ? System.getProperty("user.dir") + "/src/main/resources" + photoPath
                                    : photoPath;
                            imageData = ImageDataFactory.create(fullPath);
                        }

                        com.itextpdf.layout.element.Image img = new com.itextpdf.layout.element.Image(imageData);
                        img.scaleToFit(60, 60);
                        img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        photoCell.add(img);
                    } catch (Exception e) {
                        photoCell.add(new Paragraph("Photo introuvable")
                                .setFontSize(8).setItalic());
                    }
                } else {
                    photoCell.add(new Paragraph("Aucune")
                            .setFontSize(8).setItalic());
                }

                table.addCell(photoCell);
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getNom() != null ? user.getNom() : "-");
                table.addCell(user.getPrenom() != null ? user.getPrenom() : "-");
                table.addCell(user.getEmail() != null ? user.getEmail() : "-");
                table.addCell(user.getTelephone() != null ? user.getTelephone() : "-");
                table.addCell(type);
            }

            document.add(table);
            document.close();

            showAlert("Succès"+ "PDF généré :\n" + file.getAbsolutePath());
        } catch (Exception e) {
            showAlert("Erreur"+ "Échec PDF :\n" + e.getMessage());
            e.printStackTrace();
        }
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

            refreshUsers();
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
            Stage stage = (Stage) totalUsersLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("After Travel - Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}