package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainAjtRes extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // ✅ Chemin sensible à la casse
        Parent root = FXMLLoader.load(getClass().getResource("/AjouterReservation.fxml"));

        primaryStage.setTitle("Ajouter Réservation");
        primaryStage.setScene(new Scene(root, 1000, 600)); // taille uniforme
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
