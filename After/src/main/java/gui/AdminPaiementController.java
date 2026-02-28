package gui;

import gui.FormPaiementController;
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
import model.Paiement;
import services.PaiementService;

public class AdminPaiementController {

    @FXML
    private TableView<Paiement> tablePaiements;
    @FXML
    private TableColumn<Paiement, Integer> colId;
    @FXML
    private TableColumn<Paiement, String> colRef;
    @FXML
    private TableColumn<Paiement, String> colMontant;
    @FXML
    private TableColumn<Paiement, String> colDevise;
    @FXML
    private TableColumn<Paiement, String> colMethode;
    @FXML
    private TableColumn<Paiement, String> colStatut;
    @FXML
    private TableColumn<Paiement, String> colDate;
    @FXML
    private TableColumn<Paiement, Integer> colIdRes;
    @FXML
    private TableColumn<Paiement, Void> colActions;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lblCount;
    @FXML
    private StackPane notifOverlay;

    private final PaiementService service = new PaiementService();
    private ObservableList<Paiement> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        setupSearch();
        charger();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colMontant.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getMontant())));
        colDevise.setCellValueFactory(new PropertyValueFactory<>("devise"));
        colMethode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMethode()));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDatePaiement()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colIdRes.setCellValueFactory(new PropertyValueFactory<>("idReservation"));

        // Method cell
        colMethode.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(methodIcon(s) + "  " + s);
                badge.setStyle(methodBadge(s));
                setGraphic(badge);
                setText(null);
            }
        });

        // Status cell
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
                badge.setStyle(statusStyle(s));
                setGraphic(badge);
                setText(null);
            }
        });

        // Actions
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️  Modifier");
            private final Button btnDel = new Button("🗑  Supprimer");
            private final HBox box = new HBox(8, btnEdit, btnDel);
            {
                btnEdit.setStyle(BTN_EDIT);
                btnDel.setStyle(BTN_DEL);
                box.setStyle("-fx-alignment: CENTER_LEFT;");
                btnEdit.setOnAction(e -> ouvrirFormulaireEdit(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Paiement p = getTableView().getItems().get(getIndex());
                    service.supprimer(p.getId());
                    AdminPaiementController.this.showToast("Paiement #" + p.getId() + " supprimé.",
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
    }

    private void setupSearch() {
        tfSearch.textProperty().addListener((obs, o, n) -> {
            String q = n == null ? "" : n.toLowerCase().trim();
            FilteredList<Paiement> filtered = new FilteredList<>(data, p -> q.isEmpty() ||
                    p.getReference().toLowerCase().contains(q) ||
                    p.getMethode().toLowerCase().contains(q) ||
                    String.valueOf(p.getId()).contains(q));
            tablePaiements.setItems(filtered);
            lblCount.setText(filtered.size() + " paiement(s) affiché(s)");
        });
    }

    @FXML
    public void charger() {
        data.setAll(service.afficher());
        tablePaiements.setItems(data);
        lblCount.setText(data.size() + " paiement(s) au total");
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

    private void ouvrirFormulaireEdit(Paiement p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormPaiement.fxml"));
            Parent root = loader.load();
            FormPaiementController ctrl = loader.getController();
            if (p != null)
                ctrl.setPaiement(p);
            ctrl.setOnSaved(() -> {
                charger();
                showToast(p == null ? "Paiement ajouté !" : "Paiement modifié !", NotificationUtil.Type.SUCCESS);
            });
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 480, 580));
            stage.setTitle(p == null ? "Nouveau Paiement" : "Modifier Paiement");
            stage.show();
        } catch (Exception ex) {
            showToast("Erreur ouverture formulaire : " + ex.getMessage(), NotificationUtil.Type.ERROR);
        }
    }

    @FXML
    private void ouvrirAdminReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminReservationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tablePaiements.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ouvrirAdminStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminStatisticsView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tablePaiements.getScene().getWindow();
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
            Stage stage = (Stage) tablePaiements.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 720));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showToast(String msg, gui.NotificationUtil.Type type) {
        if (notifOverlay != null)
            NotificationUtil.show(notifOverlay, msg, type);
    }

    private String statusStyle(String s) {
        String color = switch (s == null ? "" : s.toLowerCase()) {
            case "payé" -> "#27ae60";
            case "échoué" -> "#c0392b";
            default -> "#d35400";
        };
        return "-fx-background-color:" + color + ";-fx-text-fill:white;" +
                "-fx-font-size:11px;-fx-font-weight:bold;" +
                "-fx-background-radius:20;-fx-padding:3 12 3 12;";
    }

    private String methodIcon(String m) {
        if (m == null)
            return "?";
        return switch (m.toLowerCase()) {
            case "carte" -> "💳";
            case "virement" -> "🏦";
            default -> "💰";
        };
    }

    private String methodBadge(String m) {
        String color = switch (m == null ? "" : m.toLowerCase()) {
            case "carte" -> "#1f6feb";
            case "virement" -> "#27ae60";
            default -> "#6e40c9";
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
