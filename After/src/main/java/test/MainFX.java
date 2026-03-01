package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ServiceUsers;
import services.VerificationServer;   // ← assure-toi que ce import existe

public class MainFx extends Application {

    private VerificationServer verificationServer;  // pour pouvoir l'arrêter proprement

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("After Travel - Connexion");
        stage.setScene(scene);
        stage.show();
    }

    // ───────────────────────────────────────────────
    // IMPORTANT : on lance le serveur ici (après init JavaFX)
    // ───────────────────────────────────────────────
    @Override
    public void init() throws Exception {
        super.init();

        ServiceUsers serviceUsers = new ServiceUsers();
        verificationServer = new VerificationServer(serviceUsers);

        try {
            // Port 8081 → différent de XAMPP/Apache (souvent sur 80 ou 8080)
            verificationServer.start(8081);
            System.out.println("Serveur de vérification démarré sur : http://localhost:8081/verify");
        } catch (Exception e) {
            System.err.println("ÉCHEC démarrage serveur vérification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ───────────────────────────────────────────────
    // Très important : arrêter le serveur quand l'app se ferme
    // ───────────────────────────────────────────────
    @Override
    public void stop() throws Exception {
        if (verificationServer != null) {
            verificationServer.stop();
            System.out.println("Serveur de vérification arrêté");
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}