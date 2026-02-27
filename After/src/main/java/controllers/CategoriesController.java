package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.CategorieDocument;
import services.serviceCategorieDocument;

public class CategoriesController {

    @FXML private ListView<CategorieDocument> listCategories;

    private final serviceCategorieDocument service = new serviceCategorieDocument();
    private AfficherDocumentController parent;

    public void setParent(AfficherDocumentController parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {
        listCategories.getItems().addAll(service.getAll());

        listCategories.setOnMouseClicked(event -> {
            CategorieDocument cat = listCategories.getSelectionModel().getSelectedItem();
            if (cat != null && parent != null) {
                parent.afficherParCategorie(cat.getIdCategorie());
            }
        });
    }
}
