package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private AnchorPane contentArea;

    @FXML
    public void openService() throws IOException {
        Parent view = FXMLLoader.load(getClass().getResource("/service.fxml"));
        contentArea.getChildren().setAll(view);
    }

    @FXML
    public void openOffre() throws IOException {
        Parent view = FXMLLoader.load(getClass().getResource("/offre.fxml"));
        contentArea.getChildren().setAll(view);
    }
}
