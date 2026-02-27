package GUI;

import gui.FormPaiementController;
import gui.FormReservationController;
import gui.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Reservation;
import services.ReservationService;

import java.util.List;

public class UserReservationController {

    @FXML
    private FlowPane cardsContainer;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblConfirmees;
    @FXML
    private Label lblAttente;
    @FXML
    private TextField tfUserSearch;
    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;
    @FXML
    private StackPane notifOverlay;

    private final ReservationService service = new ReservationService();
    private List<Reservation> allReservations;

    @FXML
    public void initialize() {
        charger();
        setupFilters();
    }

    private void setupFilters() {
        tfUserSearch.textProperty().addListener((o, a, b) -> appliquerFiltres());
        dpStart.valueProperty().addListener((o, a, b) -> appliquerFiltres());
        dpEnd.valueProperty().addListener((o, a, b) -> appliquerFiltres());
    }

    private void appliquerFiltres() {
        if (allReservations == null)
            return;

        String query = tfUserSearch.getText() != null ? tfUserSearch.getText().toLowerCase().trim() : "";
        java.time.LocalDate start = dpStart.getValue();
        java.time.LocalDate end = dpEnd.getValue();

        List<Reservation> filtered = allReservations.stream()
                .filter(r -> {
                    // Search query
                    boolean matchesSearch = query.isEmpty() ||
                            (r.getLieu() != null && r.getLieu().toLowerCase().contains(query)) ||
                            (r.getType() != null && r.getType().toLowerCase().contains(query)) ||
                            String.valueOf(r.getId()).contains(query);

                    // Date range
                    boolean matchesDate = true;
                    if (start != null && r.getDateReservation() != null && r.getDateReservation().isBefore(start)) {
                        matchesDate = false;
                    }
                    if (end != null && r.getDateReservation() != null && r.getDateReservation().isAfter(end)) {
                        matchesDate = false;
                    }

                    return matchesSearch && matchesDate;
                })
                .collect(java.util.stream.Collectors.toList());

        rebuildGrid(filtered);
    }

    @FXML
    private void resetFilters() {
        tfUserSearch.clear();
        dpStart.setValue(null);
        dpEnd.setValue(null);
        charger();
    }

    private void charger() {
        allReservations = service.getAll();
        rebuildGrid(allReservations);
    }

    private void rebuildGrid(List<Reservation> list) {
        cardsContainer.getChildren().clear();

        long confirmees = list.stream().filter(r -> "confirmée".equalsIgnoreCase(r.getStatut())).count();
        long attente = list.stream().filter(r -> "en attente".equalsIgnoreCase(r.getStatut())).count();

        lblTotal.setText(String.valueOf(list.size()));
        lblConfirmees.setText(String.valueOf(confirmees));
        lblAttente.setText(String.valueOf(attente));

        if (list.isEmpty()) {
            Label empty = new Label("Aucune réservation trouvée.");
            empty.setStyle("-fx-text-fill: #A79277; -fx-font-size: 15px; -fx-text-alignment: center;");
            cardsContainer.getChildren().add(empty);
            return;
        }

        for (Reservation r : list) {
            cardsContainer.getChildren().add(buildCard(r));
        }
    }

    private VBox buildCard(Reservation r) {
        VBox card = new VBox(15);
        card.setPrefWidth(300);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 25;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0, 0, 10);" +
                        "-fx-border-color: #A7927722;" +
                        "-fx-border-radius: 20;");

        // Card top: Icon + ID
        HBox top = new HBox(12);
        top.setStyle("-fx-alignment: CENTER_LEFT;");
        String iconEmoji = switch (r.getType() == null ? "" : r.getType().toLowerCase()) {
            case "hôtel" -> "🏨";
            case "chauffeur" -> "🚗";
            case "voyage" -> "✈️";
            default -> "🗓️";
        };
        Label icon = new Label(iconEmoji);
        icon.setStyle("-fx-font-size: 24px;");
        Label id = new Label("Ref #" + r.getId());
        id.setStyle("-fx-text-fill: #005082; -fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status Badge
        Label badge = new Label(r.getStatut());
        badge.setStyle(badgeStyle(r.getStatut()));

        top.getChildren().addAll(icon, id, spacer, badge);

        // Content
        VBox content = new VBox(8);
        content.getChildren().addAll(
                infoLabel("📍", "Destination", r.getLieu() == null ? "N/A" : r.getLieu()),
                infoLabel("📅", "Date", r.getDateReservation().toString()),
                infoLabel("�️", "Type", r.getType()),
                infoLabel("👥", "Voyageurs", String.valueOf(r.getNbPersonnes())));

        Label prix = new Label(String.format("%.2f TND", r.getPrixTotal()));
        prix.setStyle("-fx-text-fill: #0081C9; -fx-font-weight: 800; -fx-font-size: 18px; -fx-padding: 5 0 0 0;");

        // Action icons replacement
        HBox actions = new HBox(15);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button btnEdit = createIconButton("/edit.png", 22);
        Button btnDel = createIconButton("/delete.png", 22);
        Button btnPay = createIconButton("/pay.png", 22);

        btnEdit.setOnAction(e -> ouvrirEdition(r));
        btnDel.setOnAction(e -> {
            service.delete(r);
            showToast("Réservation supprimée.", NotificationUtil.Type.SUCCESS);
            charger();
        });

        btnPay.setOnAction(e -> {
            ouvrirPaiement(r);
        });

        // Logic for pay icon if not confirmed
        if ("en attente".equalsIgnoreCase(r.getStatut())) {
            actions.getChildren().add(btnPay);
        }

        actions.getChildren().addAll(btnEdit, btnDel);

        card.getChildren().addAll(top, content, prix, actions);
        return card;
    }

    private Button createIconButton(String iconPath, int size) {
        try {
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            Button btn = new Button("", iv);
            btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
            return btn;
        } catch (Exception e) {
            return new Button("?"); // Fallback
        }
    }

    private Label infoLabel(String emoji, String key, String val) {
        Label l = new Label(emoji + "  " + val);
        l.setStyle("-fx-text-fill: #A79277; -fx-font-size: 13px; -fx-font-weight: 500;");
        return l;
    }

    @FXML
    private void ouvrirFormulaire() {
        ouvrirEdition(null);
    }

    private void ouvrirEdition(Reservation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormReservation.fxml"));
            Parent root = loader.load();
            FormReservationController ctrl = loader.getController();
            if (r != null)
                ctrl.setReservation(r);
            ctrl.setOnSaved(() -> {
                charger();
                showToast(r == null ? "Réservation ajoutée !" : "Réservation modifiée !",
                        NotificationUtil.Type.SUCCESS);
            });
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 480, 540));
            stage.setTitle(r == null ? "Nouvelle Réservation" : "Modifier");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ouvrirPaiements() {
        navigate("/UserPaiementView.fxml", 1100, 720);
    }

    private void ouvrirPaiement(Reservation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormPaiement.fxml"));
            Parent root = loader.load();
            FormPaiementController ctrl = loader.getController();
            ctrl.setReservation(r);
            ctrl.setOnSaved(() -> {
                charger();
                showToast("Paiement enregistré !", NotificationUtil.Type.SUCCESS);
            });
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 480, 580));
            stage.setTitle("Régler mon voyage");
            stage.show();
        } catch (Exception ex) {
            showToast("Erreur ouverture paiement : " + ex.getMessage(), NotificationUtil.Type.ERROR);
        }
    }

    @FXML
    private void ouvrirAdmin() {
        navigate("/AdminReservationView.fxml", 1200, 720);
    }

    private void navigate(String fxml, int w, int h) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root, w, h));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showToast(String msg, NotificationUtil.Type type) {
        if (notifOverlay != null)
            gui.NotificationUtil.show(notifOverlay, msg, type);
    }

    private String badgeStyle(String s) {
        String color = switch (s == null ? "" : s.toLowerCase()) {
            case "confirmée" -> "#27ae60";
            case "annulée" -> "#c0392b";
            default -> "#d35400";
        };
        return "-fx-background-color:" + color + ";-fx-text-fill:white;" +
                "-fx-font-size:11px;-fx-font-weight:bold;" +
                "-fx-background-radius:20;-fx-padding:4 14 4 14;";
    }
}
