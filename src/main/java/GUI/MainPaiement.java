package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainPaiement extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger l'interface d'affichage des paiements
        Parent root = FXMLLoader.load(getClass().getResource("/AfficherPaiement.fxml"));

        primaryStage.setTitle("Gestion des Paiements");
        primaryStage.setScene(new Scene(root, 700, 400)); // taille fenêtre
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
