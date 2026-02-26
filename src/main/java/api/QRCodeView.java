package api;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Offre;

public class QRCodeView {

    private final QRCodeAPI qrAPI;
    private final Offre offre;
    private final String serviceName;

    public QRCodeView(Offre offre, String serviceName) {
        this.qrAPI = new QRCodeAPI();
        this.offre = offre;
        this.serviceName = serviceName;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("QR Code - " + offre.getTitre());

        // Contenu principal
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #F5F5DC; -fx-padding: 25; -fx-alignment: center;");

        // Titre
        Label titleLabel = new Label("📱 OFFRE EN QR CODE");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #16325c;");

        // Détails de l'offre
        Label detailsLabel = new Label(
                offre.getTitre() + "\n" +
                        "💰 " + offre.getPrix() + " DT | ⏱️ " + offre.getDuree() + " jours\n" +
                        "🏨 " + serviceName
        );
        detailsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #16325c; -fx-alignment: center;");
        detailsLabel.setAlignment(Pos.CENTER);

        // Générer le QR Code
        String texteQR = qrAPI.formaterOffrePourQR(offre, serviceName);
        Image qrImage = qrAPI.genererQRCodeFX(texteQR);

        ImageView qrView = new ImageView(qrImage);
        qrView.setFitWidth(250);
        qrView.setFitHeight(250);
        qrView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Instructions
        Label instructionLabel = new Label("Scannez ce code avec votre téléphone\npour partager l'offre");
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-alignment: center;");

        // Boutons
        Button saveBtn = new Button("💾 Sauvegarder");
        saveBtn.setStyle("-fx-background-color: #16325c; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            String filename = "qr_offre_" + offre.getId_offre() + ".png";
            if (qrAPI.sauvegarderQRCode(texteQR, filename)) {
                showAlert("Succès", "QR Code sauvegardé: " + filename);
            } else {
                showAlert("Erreur", "Échec de la sauvegarde");
            }
        });

        Button closeBtn = new Button("Fermer");
        closeBtn.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(saveBtn, closeBtn);

        // Assemblage
        root.getChildren().addAll(titleLabel, detailsLabel, qrView, instructionLabel, buttonBox);

        Scene scene = new Scene(root, 400, 550);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}