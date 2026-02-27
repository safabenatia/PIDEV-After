package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Reservation;
import services.ReservationService;

public class FormReservationController {

    @FXML
    private TextField tfIdVoyage;
    @FXML
    private TextField tfIdUser;
    @FXML
    private DatePicker dpDate;
    @FXML
    private ComboBox<String> cbType;
    @FXML
    private TextField tfLieu;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<String> cbStatut;
    @FXML
    private TextField tfNbPersonnes;
    @FXML
    private TextField tfPrixTotal;
    @FXML
    private Label lblTitre;
    @FXML
    private StackPane notifOverlay;
    @FXML
    private VBox statutBox;

    private final ReservationService service = new ReservationService();
    private final services.MailService mailService = new services.MailService();
    private Reservation reservationEdit = null;
    private Runnable onSaved;

    public void setOnSaved(Runnable callback) {
        this.onSaved = callback;
    }

    @FXML
    public void initialize() {
        cbStatut.getItems().addAll("en attente", "confirmée", "annulée");
        // No default statut when adding — backend uses "en attente" for new reservations; when user pays it becomes "confirmée"

        cbType.getItems().addAll("Voyage", "Hôtel", "Chauffeur", "Circuit", "Autre");
        cbType.setValue("Voyage");

        // Live clear-error listeners
        tfIdVoyage.textProperty().addListener((o, a, b) -> clearError(tfIdVoyage));
        tfIdUser.textProperty().addListener((o, a, b) -> clearError(tfIdUser));
        dpDate.valueProperty().addListener((o, a, b) -> clearError(dpDate));
        cbStatut.valueProperty().addListener((o, a, b) -> clearError(cbStatut));
        cbType.valueProperty().addListener((o, a, b) -> clearError(cbType));
        tfLieu.textProperty().addListener((o, a, b) -> clearError(tfLieu));
        taDescription.textProperty().addListener((o, a, b) -> clearError(taDescription));
        tfNbPersonnes.textProperty().addListener((o, a, b) -> clearError(tfNbPersonnes));
        tfPrixTotal.textProperty().addListener((o, a, b) -> clearError(tfPrixTotal));
    }

    public void setReservation(Reservation r) {
        this.reservationEdit = r;
        lblTitre.setText("Modifier Réservation #" + r.getId());
        statutBox.setVisible(true);
        statutBox.setManaged(true);
        tfIdVoyage.setText(String.valueOf(r.getIdVoyage()));
        tfIdUser.setText(String.valueOf(r.getIdUtilisateur()));
        dpDate.setValue(r.getDateReservation());
        cbStatut.setValue(r.getStatut());
        cbType.setValue(r.getType());
        tfLieu.setText(r.getLieu());
        taDescription.setText(r.getDescription());
        tfNbPersonnes.setText(String.valueOf(r.getNbPersonnes()));
        tfPrixTotal.setText(String.valueOf(r.getPrixTotal()));
    }

    @FXML
    private void enregistrer() {
        boolean ok = true;

        int idV = 0, idU = 0, nb = 0;
        double prix = 0;

        try {
            idV = Integer.parseInt(tfIdVoyage.getText());
            clearError(tfIdVoyage);
        } catch (Exception e) {
            markError(tfIdVoyage);
            ok = false;
        }
        try {
            idU = Integer.parseInt(tfIdUser.getText());
            clearError(tfIdUser);
        } catch (Exception e) {
            markError(tfIdUser);
            ok = false;
        }
        try {
            nb = Integer.parseInt(tfNbPersonnes.getText());
            clearError(tfNbPersonnes);
        } catch (Exception e) {
            markError(tfNbPersonnes);
            ok = false;
        }
        try {
            prix = Double.parseDouble(tfPrixTotal.getText());
            clearError(tfPrixTotal);
        } catch (Exception e) {
            markError(tfPrixTotal);
            ok = false;
        }

        if (dpDate.getValue() == null) {
            markError(dpDate);
            ok = false;
        }
        if (cbStatut.getValue() == null && reservationEdit != null) {
            markError(cbStatut);
            ok = false;
        }
        if (cbType.getValue() == null) {
            markError(cbType);
            ok = false;
        }
        if (tfLieu.getText().isEmpty()) {
            markError(tfLieu);
            ok = false;
        }
        if (taDescription.getText().isEmpty()) {
            markError(taDescription);
            ok = false;
        }

        if (!ok) {
            showToast("Corrigez les champs en rouge.", NotificationUtil.Type.ERROR);
            return;
        }

        try {
            String typeStr = cbType.getValue();
            String lieuStr = tfLieu.getText();
            String descStr = taDescription.getText();
            // When adding, statut is not shown as default in form; we set "en attente" here. When user pays, it becomes "confirmée".
            String statutStr = (reservationEdit != null) ? cbStatut.getValue() : "en attente";

            if (reservationEdit == null) {
                Reservation newRes = new Reservation(idV, idU, dpDate.getValue(), statutStr, nb, prix,
                        typeStr,
                        lieuStr, descStr);
                service.ajouter(newRes);
                // Send confirmation email (don't fail reservation if email fails)
                boolean emailSent = mailService.sendConfirmationEmail("asmaantri032@gmail.com", newRes);
                if (!emailSent) {
                    showToast("Réservation ajoutée ! (E-mail non envoyé)", NotificationUtil.Type.SUCCESS);
                    if (onSaved != null) onSaved.run();
                    javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                    pt.setOnFinished(e -> ((Stage) dpDate.getScene().getWindow()).close());
                    pt.play();
                    return;
                }
            } else {
                reservationEdit.setIdVoyage(idV);
                reservationEdit.setIdUtilisateur(idU);
                reservationEdit.setDateReservation(dpDate.getValue());
                reservationEdit.setStatut(cbStatut.getValue());
                reservationEdit.setNbPersonnes(nb);
                reservationEdit.setPrixTotal(prix);
                reservationEdit.setType(typeStr);
                reservationEdit.setLieu(lieuStr);
                reservationEdit.setDescription(descStr);
                service.modifier(reservationEdit);
            }
            if (onSaved != null)
                onSaved.run();
            // Short delay then close
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.millis(200));
            pause.setOnFinished(e -> ((Stage) dpDate.getScene().getWindow()).close());
            pause.play();
        } catch (Exception e) {
            showToast("Erreur : " + e.getMessage(), NotificationUtil.Type.ERROR);
        }
    }

    @FXML
    private void annuler() {
        ((Stage) dpDate.getScene().getWindow()).close();
    }

    private void showToast(String msg, NotificationUtil.Type type) {
        if (notifOverlay != null)
            NotificationUtil.show(notifOverlay, msg, type);
    }

    private static final String BASE = "-fx-background-color: #FEFAF6;" +
            "-fx-border-color: #A7927744;" +
            "-fx-border-radius: 10;-fx-background-radius: 10;" +
            "-fx-text-fill: #005082;-fx-prompt-text-fill: #A79277;" +
            "-fx-padding: 10;";
    private static final String ERR = "-fx-background-color: #FEFAF6;" +
            "-fx-border-color: #c0392b;-fx-border-width: 2;" +
            "-fx-border-radius: 10;-fx-background-radius: 10;" +
            "-fx-text-fill: #005082;-fx-prompt-text-fill: #c0392b88;" +
            "-fx-padding: 10;";

    private void markError(Control c) {
        c.setStyle(ERR);
    }

    private void clearError(Control c) {
        c.setStyle(BASE);
    }
}
