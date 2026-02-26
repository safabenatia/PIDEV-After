package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import models.CategorieDocument;
import models.Document;
import services.serviceDocument;

import java.util.List;

public class DocumentsParCategorieController {

    @FXML private VBox containerDocuments;

    private final serviceDocument service = new serviceDocument();
    private AfficherDocumentController parent;

    public void setParent(AfficherDocumentController parent) {
        this.parent = parent;
    }

    public void setCategorie(CategorieDocument categorie) {

        List<Document> docs = service.getByCategorie(categorie.getIdCategorie());

        containerDocuments.getChildren().clear();

        for (Document doc : docs) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DocumentCard.fxml"));
                VBox card = loader.load();

                DocumentCardController controller = loader.getController();
                controller.setData(doc, parent); // ⭐ CORRECTION ICI

                containerDocuments.getChildren().add(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
