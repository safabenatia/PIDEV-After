package GUI;

import gui.FormReservationController;
import gui.NotificationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Reservation;
import services.ReservationService;

public class AdminReservationController {

    @FXML
    private TableView<Reservation> tableReservations;
    @FXML
    private TableColumn<Reservation, Integer> colId;
    @FXML
    private TableColumn<Reservation, String> colType;
    @FXML
    private TableColumn<Reservation, String> colLieu;
    @FXML
    private TableColumn<Reservation, Integer> colVoyage;
    @FXML
    private TableColumn<Reservation, Integer> colUser;
    @FXML
    private TableColumn<Reservation, String> colDate;
    @FXML
    private TableColumn<Reservation, String> colStatut;
    @FXML
    private TableColumn<Reservation, Integer> colNb;
    @FXML
    private TableColumn<Reservation, Double> colPrix;
    @FXML
    private TableColumn<Reservation, Void> colActions;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lblCount;
    @FXML
    private StackPane notifOverlay;

    private final ReservationService service = new ReservationService();
    private ObservableList<Reservation> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        setupSearch();
        charger();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colVoyage.setCellValueFactory(new PropertyValueFactory<>("idVoyage"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDateReservation().toString()));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut()));
        colNb.setCellValueFactory(new PropertyValueFactory<>("nbPersonnes"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));

        // Status badge cell
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(s);
                badge.setStyle(badgeStyle(s));
                setGraphic(badge);
                setText(null);
            }
        });

        // Actions column
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️  Modifier");
            private final Button btnDel = new Button("🗑  Supprimer");
            private final HBox box = new HBox(8, btnEdit, btnDel);
            {
                btnEdit.setStyle(BTN_EDIT);
                btnDel.setStyle(BTN_DEL);
                box.setStyle("-fx-alignment: CENTER_LEFT;");

                btnEdit.setOnAction(e -> {
                    Reservation r = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireEdit(r);
                });
                btnDel.setOnAction(e -> {
                    Reservation r = getTableView().getItems().get(getIndex());
                    service.delete(r);
                    AdminReservationController.this.showToast("Réservation #" + r.getId() + " supprimée.",
                            NotificationUtil.Type.SUCCESS);
                    charger();
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Column style
        tableReservations.setStyle(
                "-fx-background-color: #161b22;" +
                        "-fx-text-fill: #e6edf3;" +
                        "-fx-border-color: transparent;");
    }

    private void setupSearch() {
        tfSearch.textProperty().addListener((obs, o, n) -> {
            String q = n == null ? "" : n.toLowerCase().trim();
            FilteredList<Reservation> filtered = new FilteredList<>(data, r -> {
                if (q.isEmpty())
                    return true;
                String d = r.getDateReservation() != null ? r.getDateReservation().toString() : "";
                String l = r.getLieu() != null ? r.getLieu().toLowerCase() : "";
                String t = r.getType() != null ? r.getType().toLowerCase() : "";
                String s = r.getStatut() != null ? r.getStatut().toLowerCase() : "";

                return l.contains(q) || t.contains(q) || d.contains(q) || s.contains(q)
                        || String.valueOf(r.getId()).contains(q);
            });
            tableReservations.setItems(filtered);
            lblCount.setText(filtered.size() + " réservation(s) affichée(s)");
        });
    }

    @FXML
    public void charger() {
        data.setAll(service.getAll());
        tableReservations.setItems(data);
        lblCount.setText(data.size() + " réservation(s) au total");
    }

    @FXML
    private void rafraichir() {
        charger();
        showToast("Liste actualisée.", NotificationUtil.Type.INFO);
    }

    @FXML
    private void ouvrirFormulaire() {
        ouvrirFormulaireEdit(null);
    }

    private void ouvrirFormulaireEdit(Reservation r) {
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
            stage.setScene(new Scene(root, 480, 560));
            stage.setTitle(r == null ? "Nouvelle Réservation" : "Modifier Réservation");
            stage.show();
        } catch (Exception ex) {
            showToast("Erreur ouverture formulaire : " + ex.getMessage(), NotificationUtil.Type.ERROR);
        }
    }

    @FXML
    private void ouvrirAdminStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminStatisticsView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ouvrirAdminPaiement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminPaiementView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ouvrirVueUtilisateur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserReservationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 720));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showToast(String msg, gui.NotificationUtil.Type type) {
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
                "-fx-background-radius:20;-fx-padding:3 12 3 12;";
    }

    private static final String BTN_EDIT = "-fx-background-color:#0081C9;-fx-text-fill:white;-fx-font-size:11px;" +
            "-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:5 12 5 12;";
    private static final String BTN_DEL = "-fx-background-color:#c0392b;-fx-text-fill:white;-fx-font-size:11px;"
            +
            "-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:5 12 5 12;";
}
