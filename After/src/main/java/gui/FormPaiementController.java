package gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.Paiement;
import services.*;
//import services.StripeService;
import model.Reservation;


public class FormPaiementController {

    @FXML
    private TextField tfReference;
    @FXML
    private TextField tfMontant;
    @FXML
    private TextField tfDevise;
    @FXML
    private ComboBox<String> cbMethode;
    @FXML
    private ComboBox<String> cbStatut;
    @FXML
    private TextField tfIdReservation;
    @FXML
    private DatePicker dpDate;
    @FXML
    private Label lblTitre;
    @FXML
    private StackPane notifOverlay;

    private final PaiementService service = new PaiementService();
    private final ReservationService resService = new ReservationService();
    private final StripeService stripeService = new StripeService();
    private final PdfServiceReservation pdfService = new PdfServiceReservation();  // ✅

    private Paiement paiementEdit = null;
    private Runnable onSaved;

    public void setOnSaved(Runnable callback) {
        this.onSaved = callback;
    }

    @FXML
    public void initialize() {
        cbMethode.getItems().addAll("Carte", "Virement", "Espèces");
        cbStatut.getItems().addAll("en attente", "payé", "échoué");
        cbStatut.setValue("payé");
        dpDate.setValue(java.time.LocalDate.now());

        // Live clear-error listeners
        tfReference.textProperty().addListener((o, a, b) -> clearError(tfReference));
        tfMontant.textProperty().addListener((o, a, b) -> clearError(tfMontant));
        tfDevise.textProperty().addListener((o, a, b) -> clearError(tfDevise));
        cbMethode.valueProperty().addListener((o, a, b) -> clearError(cbMethode));
        cbStatut.valueProperty().addListener((o, a, b) -> clearError(cbStatut));
        tfIdReservation.textProperty().addListener((o, a, b) -> clearError(tfIdReservation));
        dpDate.valueProperty().addListener((o, a, b) -> clearError(dpDate));
    }

    public void setReservation(Reservation r) {
        this.tfIdReservation.setText(String.valueOf(r.getId()));
        this.tfMontant.setText(String.valueOf(r.getPrixTotal()));
        this.tfDevise.setText("TND");
        this.tfIdReservation.setDisable(true);
        this.tfMontant.setDisable(true);
    }

    public void setPaiement(Paiement p) {
        this.paiementEdit = p;
        lblTitre.setText("Modifier Paiement #" + p.getId());
        tfReference.setText(p.getReference());
        tfMontant.setText(String.valueOf(p.getMontant()));
        tfDevise.setText(p.getDevise());
        cbMethode.setValue(p.getMethode());
        cbStatut.setValue(p.getStatut());
        tfIdReservation.setText(String.valueOf(p.getIdReservation()));
        dpDate.setValue(p.getDatePaiement().toLocalDate());
    }

    @FXML
    private void enregistrer() {
        boolean ok = true;

        String ref = tfReference.getText().trim();
        if (ref.isEmpty()) {
            markError(tfReference);
            ok = false;
        }

        double montant = 0;
        try {
            montant = Double.parseDouble(tfMontant.getText());
            clearError(tfMontant);
        } catch (Exception e) {
            markError(tfMontant);
            ok = false;
        }

        String devise = tfDevise.getText().trim();
        if (devise.isEmpty()) {
            markError(tfDevise);
            ok = false;
        }

        int idRes = 0;
        try {
            idRes = Integer.parseInt(tfIdReservation.getText());
            clearError(tfIdReservation);
        } catch (Exception e) {
            markError(tfIdReservation);
            ok = false;
        }

        if (cbMethode.getValue() == null) {
            markError(cbMethode);
            ok = false;
        }
        if (cbStatut.getValue() == null) {
            markError(cbStatut);
            ok = false;
        }
        if (dpDate.getValue() == null) {
            markError(dpDate);
            ok = false;
        }

        if (!ok) {
            showToast("Corrigez les champs en rouge.", gui.NotificationUtil.Type.ERROR);
            return;
        }

        try {
            java.time.LocalDateTime dt = dpDate.getValue().atStartOfDay();
            Paiement p;
            if (paiementEdit == null) {
                p = new Paiement(ref, montant, cbMethode.getValue(), idRes, cbStatut.getValue(), dt, devise);
                service.ajouter(p);
            } else {
                paiementEdit.setReference(ref);
                paiementEdit.setMontant(montant);
                paiementEdit.setMethode(cbMethode.getValue());
                paiementEdit.setIdReservation(idRes);
                paiementEdit.setStatut(cbStatut.getValue());
                paiementEdit.setDatePaiement(dt);
                paiementEdit.setDevise(devise);
                service.modifier(paiementEdit);
                p = paiementEdit;
            }

            // --- STRIPE & PDF LOGIC ---
            String methode = cbMethode.getValue();
            final int targetIdRes = idRes;
            Reservation res = resService.getAll().stream()
                    .filter(r -> r.getId() == targetIdRes).findFirst().orElse(null);

            if (res != null) {
                // Generate PDF
                String pdfPath = pdfService.generateReceipt(res, p);
                System.out.println("📄 Reçu généré: " + pdfPath);

                // Stripe Checkout if Carte: open in WebView and handle success in-app
                if ("Carte".equalsIgnoreCase(methode)) {
                    com.stripe.model.checkout.Session session = stripeService.createCheckoutSession(res, p);
                    p.setStatut("en attente");
                    service.modifier(p);

                    final String stripeUrl = session.getUrl();
                    final Stage formStage = (Stage) tfReference.getScene().getWindow();
                    openStripeWebViewAndHandleSuccess(stripeUrl, p, res, formStage);
                    showToast("Completez le paiement dans la fenêtre Stripe...", NotificationUtil.Type.SUCCESS);
                    // Form and list refresh happen when success URL is detected in WebView
                    return;
                } else {
                    showToast("Paiement enregistré et reçu généré !", gui.NotificationUtil.Type.SUCCESS);
                }
            }

            if (onSaved != null)
                onSaved.run();
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.millis(800));
            pause.setOnFinished(e -> ((Stage) tfReference.getScene().getWindow()).close());
            pause.play();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Erreur: " + e.getMessage(), gui.NotificationUtil.Type.ERROR);
        }
    }

    @FXML
    private void annuler() {
        ((Stage) tfReference.getScene().getWindow()).close();
    }

    /**
     * Opens Stripe Checkout in an in-app WebView. When the user is redirected to the success URL,
     * validates the session, marks payment and reservation as confirmed, then closes the window and refreshes.
     */
    private void openStripeWebViewAndHandleSuccess(String stripeUrl, Paiement p, Reservation res, Stage formStage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        Stage stripeStage = new Stage();
        stripeStage.setTitle("Paiement Stripe");
        stripeStage.setScene(new javafx.scene.Scene(webView, 500, 700));
        stripeStage.setOnCloseRequest(e -> {
            // If user closes without paying, keep form open so they can retry or cancel
        });

        engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            if (newLoc == null || !newLoc.contains("success") || !newLoc.contains("session_id="))
                return;
            // Extract session_id from URL (e.g. https://example.com/success?session_id=cs_test_xxx)
            int idx = newLoc.indexOf("session_id=");
            if (idx < 0) return;
            String sessionId = newLoc.substring(idx + 11);
            int amp = sessionId.indexOf('&');
            if (amp > 0) sessionId = sessionId.substring(0, amp);

            final String sid = sessionId;
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    com.stripe.model.checkout.Session session = stripeService.retrieveSession(sid);
                    if (session != null && "complete".equalsIgnoreCase(session.getStatus())
                            && "paid".equalsIgnoreCase(session.getPaymentStatus())) {
                        Platform.runLater(() -> {
                            p.setStatut("payé");
                            service.modifier(p);
                            res.setStatut("confirmée");
                            resService.update(res);
                            engine.loadContent(
                                "<!DOCTYPE html><html><body style='font-family:sans-serif;text-align:center;padding:40px;'>" +
                                "<h1 style='color:#27ae60;'>✓ Paiement réussi !</h1>" +
                                "<p>Réservation confirmée. Fermeture...</p></body></html>");
                            stripeStage.close();
                            showToast("Paiement réussi ! Réservation confirmée.", NotificationUtil.Type.SUCCESS);
                            if (onSaved != null)
                                onSaved.run();
                            formStage.close();
                        });
                    }
                    return null;
                }
            };
            task.setOnFailed(e -> Platform.runLater(() -> {
                showToast("Erreur vérification Stripe: " + (task.getException() != null ? task.getException().getMessage() : ""), NotificationUtil.Type.ERROR);
            }));
            new Thread(task).start();
        });

        engine.load(stripeUrl);
        stripeStage.show();
    }

    private void showToast(String msg, gui.NotificationUtil.Type type) {
        if (notifOverlay != null)
            gui.NotificationUtil.show(notifOverlay, msg, type);
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
