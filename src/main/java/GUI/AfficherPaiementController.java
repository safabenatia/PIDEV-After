package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Paiement;
import services.PaiementService;

import java.util.List;

public class AfficherPaiementController {

    @FXML
    private VBox paiementsContainer;

    private PaiementService service = new PaiementService();

    @FXML
    public void initialize() {
        chargerPaiements();
    }

    private void chargerPaiements() {
        paiementsContainer.getChildren().clear();
        List<Paiement> paiements = service.afficher();

        for (Paiement p : paiements) {
            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-background-radius: 10; -fx-padding: 15;");

            Label lblId = new Label("ID: " + p.getId());
            lblId.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label lblRef = new Label("Référence: " + p.getReference());
            lblRef.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label lblMontant = new Label("Montant: " + p.getMontant() + " €");
            lblMontant.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label lblMethode = new Label("Méthode: " + p.getMethode());
            lblMethode.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label lblIdRes = new Label("ID Réservation: " + p.getIdReservation());
            lblIdRes.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            HBox buttons = new HBox(10);

            Button btnModifier = new Button("Modifier");
            btnModifier.setStyle("-fx-background-color: #ffcc66; -fx-font-weight: bold;");
            btnModifier.setOnAction(e -> ouvrirAjouterPaiement(p));

            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #ff6666; -fx-font-weight: bold; -fx-text-fill: white;");
            btnSupprimer.setOnAction(e -> {
                service.supprimer(p.getId());
                chargerPaiements();
            });

            Button btnVoirReservations = new Button("Voir Réservations");
            btnVoirReservations.setStyle("-fx-background-color: #66ccff; -fx-font-weight: bold; -fx-text-fill: white;");
            btnVoirReservations.setOnAction(e -> ouvrirAfficherReservation());

            buttons.getChildren().addAll(btnModifier, btnSupprimer, btnVoirReservations);
            card.getChildren().addAll(lblId, lblRef, lblMontant, lblMethode, lblIdRes, buttons);

            paiementsContainer.getChildren().add(card);
        }
    }

    private void ouvrirAjouterPaiement(Paiement p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPaiement.fxml"));
            Parent root = loader.load();

            AjouterPaiementController controller = loader.getController();
            controller.setPaiement(p);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("Modifier Paiement");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ouvrirAfficherReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReservation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("Afficher Réservations");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
