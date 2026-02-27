package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Paiement;
import services.PaiementService;
import services.StripeService;
import services.ReservationService;
import services.PdfService;
import model.Reservation;

import java.awt.*;
import java.io.File;
import java.util.List;

public class UserPaiementController {

    @FXML
    private VBox cardsContainer;
    @FXML
    private Label lblTotalPay;
    @FXML
    private Label lblMontantTotal;
    @FXML
    private StackPane notifOverlay;

    private final PaiementService service = new PaiementService();
    private final StripeService stripeService = new StripeService();
    private final ReservationService resService = new ReservationService();
    private final PdfService pdfService = new PdfService();

    @FXML
    public void initialize() {
        charger();
    }

    private void charger() {
        cardsContainer.getChildren().clear();
        List<Paiement> list = service.afficher();

        double totalMontant = list.stream().mapToDouble(Paiement::getMontant).sum();
        lblTotalPay.setText(String.valueOf(list.size()));
        lblMontantTotal.setText(String.format("%.2f TND", totalMontant));

        if (list.isEmpty()) {
            Label empty = new Label("Aucun paiement trouvé.");
            empty.setStyle("-fx-text-fill: #A79277; -fx-font-size: 15px;");
            cardsContainer.getChildren().add(empty);
            return;
        }

        for (Paiement p : list) {
            cardsContainer.getChildren().add(buildReceiptCard(p));
        }
    }

    private HBox buildReceiptCard(Paiement p) {
        HBox card = new HBox(20);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-padding: 20 28 20 28;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 15, 0, 0, 8);" +
                        "-fx-border-color: #A7927722;" +
                        "-fx-border-radius: 18;");

        // Method icon circle
        Label methodIcon = new Label(methodIcon(p.getMethode()));
        methodIcon.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-background-color: #FEFAF6;" +
                        "-fx-background-radius: 50;" +
                        "-fx-padding: 12 14 12 14;" +
                        "-fx-border-color: #A7927722;" +
                        "-fx-border-radius: 50;");

        // Info block
        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label ref = new Label("Réf : " + p.getReference());
        ref.setStyle("-fx-text-fill: #005082; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox meta = new HBox(12);
        Label method = new Label("Par " + p.getMethode() + "   ·   Voyage #" + p.getIdReservation());
        method.setStyle("-fx-text-fill: #A79277; -fx-font-size: 12px; -fx-font-weight: 500;");
        Label status = new Label(p.getStatut());
        status.setStyle(statusStyle(p.getStatut()));
        meta.getChildren().addAll(method, status);

        info.getChildren().addAll(ref, meta);

        // Right side: amount + actions
        VBox right = new VBox(10);
        right.setStyle("-fx-alignment: CENTER_RIGHT;");
        Label montant = new Label(String.format("%.2f %s", p.getMontant(), p.getDevise()));
        montant.setStyle(
                "-fx-background-color: #0081C9;" +
                        "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 20; -fx-padding: 6 18 6 18;");

        HBox btns = new HBox(12);
        btns.setStyle("-fx-alignment: CENTER_RIGHT;");

        Button btnEdit = createIconButton("/edit.png", 18);
        Button btnDel = createIconButton("/delete.png", 18);
        Button btnPdf = createIconButton("/pdf.png", 18);

        // Add verification button for pending Stripe payments
        if ("en attente".equalsIgnoreCase(p.getStatut()) && "Carte".equalsIgnoreCase(p.getMethode())) {
            Button btnVerify = new Button("🔄");
            btnVerify.setStyle(
                    "-fx-background-color: #0081C9; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
            btnVerify.setOnAction(e -> validerStripe(p));
            btns.getChildren().add(btnVerify);
        }

        btnEdit.setOnAction(e -> ouvrirEdition(p));
        btnPdf.setOnAction(e -> imprimerRecu(p));
        btnDel.setOnAction(e -> {
            service.supprimer(p.getId());
            showToast("Paiement supprimé.", NotificationUtil.Type.SUCCESS);
            charger();
        });
        btns.getChildren().addAll(btnPdf, btnEdit, btnDel);
        right.getChildren().addAll(montant, btns);

        card.getChildren().addAll(methodIcon, info, right);
        return card;
    }

    private void imprimerRecu(Paiement p) {
        try {
            Reservation res = resService.afficher().stream()
                    .filter(r -> r.getId() == p.getIdReservation())
                    .findFirst().orElse(null);
            if (res != null) {
                String path = pdfService.generateReceipt(res, p);
                File file = new File(path);
                if (file.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                    showToast("Reçu ouvert !", NotificationUtil.Type.SUCCESS);
                }
            } else {
                showToast("Réservation introuvable pour ce reçu.", NotificationUtil.Type.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Erreur PDF: " + e.getMessage(), NotificationUtil.Type.ERROR);
        }
    }

    @FXML
    private void ouvrirFormulaire() {
        ouvrirEdition(null);
    }

    private void ouvrirEdition(Paiement p) {
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
            ex.printStackTrace();
        }
    }

    @FXML
    private void ouvrirReservations() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UserReservationView.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 720));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showToast(String msg, NotificationUtil.Type type) {
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
                "-fx-font-size:9.5px;-fx-font-weight:bold;" +
                "-fx-background-radius:12;-fx-padding:2 10 2 10;";
    }

    private void validerStripe(Paiement p) {
        try {
            com.stripe.model.checkout.Session session = stripeService.findLatestSessionByPaiementRef(p.getReference());
            if (session != null && "complete".equalsIgnoreCase(session.getStatus())
                    && "paid".equalsIgnoreCase(session.getPaymentStatus())) {
                p.setStatut("payé");
                service.modifier(p);

                // Also update reservation status if possible
                Reservation res = new ReservationService().afficher().stream()
                        .filter(r -> r.getId() == p.getIdReservation())
                        .findFirst().orElse(null);
                if (res != null) {
                    res.setStatut("confirmée");
                    new ReservationService().modifier(res);
                }

                showToast("Paiement validé avec succès !", NotificationUtil.Type.SUCCESS);
                charger();
            } else {
                showToast("Le paiement n'est pas encore complété.", NotificationUtil.Type.WARNING);
            }
        } catch (Exception e) {
            showToast("Erreur lors de la vérification : " + e.getMessage(), NotificationUtil.Type.ERROR);
        }
    }

    private String methodIcon(String m) {
        if (m == null)
            return "💰";
        return switch (m.toLowerCase()) {
            case "carte" -> "💳";
            case "virement" -> "🏦";
            default -> "💰";
        };
    }

    private Button createIconButton(String iconPath, int size) {
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(size);
            icon.setFitHeight(size);
            Button btn = new Button();
            btn.setGraphic(icon);
            btn.setStyle(
                    "-fx-background-color: #FEFAF6; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: #A7927744; -fx-border-radius: 8; -fx-padding: 6;");
            return btn;
        } catch (Exception e) {
            return new Button("?");
        }
    }
}
