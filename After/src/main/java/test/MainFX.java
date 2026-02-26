package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ServiceUsers;
import services.VerificationServer;

public class MainFX extends Application {

    private VerificationServer verificationServer;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("After Travel - Connexion");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        ServiceUsers serviceUsers = new ServiceUsers();
        verificationServer = new VerificationServer(serviceUsers);
        try {
            verificationServer.start(8081);
        } catch (Exception e) {
            System.err.println("ÉCHEC démarrage serveur vérification : " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        if (verificationServer != null) {
            verificationServer.stop();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}