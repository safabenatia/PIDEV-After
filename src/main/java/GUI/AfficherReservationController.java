package GUI;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import model.Reservation;
import services.ReservationService;

import java.util.List;

public class AfficherReservationController {

    @FXML
    private VBox reservationsContainer;

    private ReservationService service = new ReservationService();

    @FXML
    public void initialize() {
        chargerReservations();
    }

    @FXML
    private void chargerReservations() {
        reservationsContainer.getChildren().clear();

        List<Reservation> reservations = service.afficher();

        for (Reservation r : reservations) {
            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: rgba(0,0,0,0.4);" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 15;");

            // Champs de la réservation
            Label lblId = new Label("ID: " + r.getId());
            lblId.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label lblDate = new Label("Date: " + r.getDateReservation());
            lblDate.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label lblStatut = new Label("Statut: " + r.getStatut());
            lblStatut.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label lblNb = new Label("Nb Personnes: " + r.getNbPersonnes());
            lblNb.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            // Boutons Modifier / Supprimer
            HBox buttons = new HBox(10);

            Button btnModifier = new Button("Modifier");
            btnModifier.setStyle("-fx-background-color: #ffcc66; -fx-font-weight: bold; -fx-background-radius: 15;");
            btnModifier.setOnAction(e -> ouvrirModifierReservation(r));

            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #ff6666; -fx-font-weight: bold; -fx-background-radius: 15; -fx-text-fill: white;");
            btnSupprimer.setOnAction(e -> {
                service.supprimer(r.getId());
                chargerReservations();
            });

            buttons.getChildren().addAll(btnModifier, btnSupprimer);

            card.getChildren().addAll(lblId, lblDate, lblStatut, lblNb, buttons);

            reservationsContainer.getChildren().add(card);
        }
    }

    private void ouvrirModifierReservation(Reservation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterReservation.fxml"));
            Parent root = loader.load();

            AjouterReservationController controller = loader.getController();
            controller.setReservation(r);

            // Callback pour rafraîchir la page après modification
            controller.setOnModification(this::chargerReservations);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Réservation");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
