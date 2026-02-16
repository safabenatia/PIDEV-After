package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Paiement;
import services.PaiementService;

import java.io.IOException;

public class AjouterPaiementController {

    @FXML
    private TextField tfReference;

    @FXML
    private TextField tfMontant;

    @FXML
    private ComboBox<String> cbMethode;

    @FXML
    private TextField tfIdReservation;

    @FXML
    private Label lblMessage;

    private PaiementService service = new PaiementService();
    private Paiement paiementAModifier;

    // ✅ Callback pour rafraîchir la table après modification
    private Runnable onModification;

    public void setOnModification(Runnable callback) {
        this.onModification = callback;
    }

    @FXML
    private void initialize() {
        cbMethode.getItems().addAll("Carte", "Virement");
    }

    // ✅ Ajouter ou Modifier
    @FXML
    private void ajouterPaiement() {
        try {
            String reference = tfReference.getText();
            double montant = Double.parseDouble(tfMontant.getText());
            String methode = cbMethode.getValue();
            int idReservation = Integer.parseInt(tfIdReservation.getText());

            if (reference.isEmpty() || methode == null) {
                lblMessage.setText("Veuillez remplir tous les champs !");
                return;
            }

            if (paiementAModifier == null) {
                // 🔹 AJOUT
                Paiement p = new Paiement(reference, montant, methode, idReservation);
                service.ajouter(p);
                lblMessage.setText("✅ Paiement ajouté !");
                clearForm();
            } else {
                // 🔹 MODIFICATION
                paiementAModifier.setReference(reference);
                paiementAModifier.setMontant(montant);
                paiementAModifier.setMethode(methode);
                paiementAModifier.setIdReservation(idReservation);

                service.modifier(paiementAModifier);
                lblMessage.setText("✏ Paiement modifié !");

                // Rafraîchir table si besoin
                if (onModification != null) {
                    onModification.run();
                }
            }

        } catch (NumberFormatException e) {
            lblMessage.setText("❌ Montant ou ID invalide !");
        } catch (Exception e) {
            lblMessage.setText("❌ Erreur : " + e.getMessage());
        }
    }

    // ✅ Supprimer
    @FXML
    private void supprimerPaiement() {
        String reference = tfReference.getText();

        if (reference.isEmpty()) {
            lblMessage.setText("Veuillez entrer la référence !");
            return;
        }

        service.supprimerParReference(reference);
        lblMessage.setText("🗑 Paiement supprimé !");
        clearForm();

        if (onModification != null) {
            onModification.run();
        }
    }

    // ✅ Navigation vers Affichage
    @FXML
    private void allerVersAffichage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPaiement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tfReference.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));

        } catch (IOException e) {
            e.printStackTrace();
            lblMessage.setText("Erreur ouverture page affichage !");
        }
    }

    // ✅ Pré-remplir formulaire pour modification
    public void setPaiement(Paiement p) {
        this.paiementAModifier = p;

        tfReference.setText(p.getReference());
        tfMontant.setText(String.valueOf(p.getMontant()));
        cbMethode.setValue(p.getMethode());
        tfIdReservation.setText(String.valueOf(p.getIdReservation()));
    }

    // ✅ Fermer fenêtre
    @FXML
    private void fermerFenetre() {
        tfReference.getScene().getWindow().hide();
    }

    // ✅ Vider formulaire
    private void clearForm() {
        tfReference.clear();
        tfMontant.clear();
        cbMethode.setValue(null);
        tfIdReservation.clear();
        paiementAModifier = null;
    }
}
