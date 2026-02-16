package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Reservation;
import services.ReservationService;

import java.io.IOException;

public class AjouterReservationController {

    @FXML
    private DatePicker dpDate;

    @FXML
    private ComboBox<String> cbStatut;

    @FXML
    private TextField tfNbPersonnes;

    @FXML
    private Label lblMessage;

    private ReservationService service = new ReservationService();
    private Reservation reservationAModifier;

    // Callback optionnel pour rafraîchir affichage ailleurs
    private Runnable onModification;

    public void setOnModification(Runnable callback) {
        this.onModification = callback;
    }

    @FXML
    private void initialize() {
        cbStatut.getItems().addAll("en attente", "confirmée", "annulée");
        cbStatut.setValue("en attente");
        dpDate.setValue(null); // utilisateur choisit la date
    }

    // Ajouter ou Modifier
    @FXML
    private void ajouterReservation() {
        try {
            if (dpDate.getValue() == null) {
                lblMessage.setText("Veuillez sélectionner une date !");
                return;
            }

            int nbPersonnes = Integer.parseInt(tfNbPersonnes.getText());

            if (reservationAModifier == null) {
                // AJOUT
                Reservation res = new Reservation(dpDate.getValue(), cbStatut.getValue(), nbPersonnes);
                service.ajouter(res);
                lblMessage.setText("✅ Réservation ajoutée !");
                clearForm();
            } else {
                // MODIFICATION
                reservationAModifier.setDateReservation(dpDate.getValue());
                reservationAModifier.setStatut(cbStatut.getValue());
                reservationAModifier.setNbPersonnes(nbPersonnes);

                service.modifier(reservationAModifier);
                lblMessage.setText("✏ Réservation modifiée !");

                if (onModification != null) onModification.run();
            }
        } catch (NumberFormatException e) {
            lblMessage.setText("❌ Nombre invalide !");
        } catch (Exception e) {
            lblMessage.setText("❌ Erreur : " + e.getMessage());
        }
    }

    // Supprimer par ID
    @FXML
    private void supprimerReservation() {
        if (reservationAModifier == null) {
            lblMessage.setText("Aucune réservation sélectionnée pour supprimer !");
            return;
        }

        service.supprimer(reservationAModifier.getId()); // ✅ Correction : passer l'ID
        lblMessage.setText("🗑 Réservation supprimée !");
        clearForm();

        if (onModification != null) onModification.run();
    }

    // Navigation vers AfficherReservation
    @FXML
    private void allerVersAffichage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReservation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) dpDate.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600)); // taille uniforme
        } catch (IOException e) {
            e.printStackTrace();
            lblMessage.setText("❌ Impossible d'ouvrir la page affichage !");
        }
    }

    // Pré-remplir formulaire pour modification
    public void setReservation(Reservation reservation) {
        this.reservationAModifier = reservation;
        dpDate.setValue(reservation.getDateReservation());
        cbStatut.setValue(reservation.getStatut());
        tfNbPersonnes.setText(String.valueOf(reservation.getNbPersonnes()));
    }

    // Vider formulaire
    private void clearForm() {
        dpDate.setValue(null);
        cbStatut.setValue("en attente");
        tfNbPersonnes.clear();
        reservationAModifier = null;
    }

    // Fermer la fenêtre
    @FXML
    private void fermerFenetre() {
        dpDate.getScene().getWindow().hide();
    }
}
