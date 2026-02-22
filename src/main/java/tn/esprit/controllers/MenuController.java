package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button btnDepenses;

    @FXML
    private Button btnCategories;

    @FXML
    public void initialize() {

        btnDepenses.setOnAction(event -> {
            ouvrirInterface("/gestion_depenses_v2.fxml", "AFTER Travel - Gestion des Dépenses");
        });

        btnCategories.setOnAction(event -> {
            ouvrirInterface("/gestion_categories_v2.fxml", "AFTER Travel - Gestion des Catégories [ADMIN]");
        });

        ajouterEffetHover(btnDepenses, "#FFD700", "#FFC700");
        ajouterEffetHover(btnCategories, "#16325c", "#0f2847");
    }

    private void ouvrirInterface(String fxmlPath, String titre) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root, 1200, 700));
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de l'interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ajouterEffetHover(Button button, String couleurNormale, String couleurHover) {
        String styleBase = button.getStyle();

        button.setOnMouseEntered(e -> {
            button.setStyle(styleBase.replace(couleurNormale, couleurHover) + "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle(styleBase);
        });
    }
}