package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.CategorieDocument;

public class CategorieCardController {

    @FXML private Label lblNom;
    private CategorieDocument categorie;

    public void setData(CategorieDocument cat) {
        this.categorie = cat;
        lblNom.setText(cat.getLibelle());

        lblNom.getParent().setOnMouseClicked(e -> ouvrirDocuments());
    }

    private void ouvrirDocuments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DocumentsParCategorie.fxml"));
            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(loader.load()));

            DocumentsParCategorieController controller = loader.getController();
            controller.setCategorie(categorie);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
