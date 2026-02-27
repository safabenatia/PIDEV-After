package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import models.Document;
import services.serviceDocument;

public class DocumentCardController {

    @FXML private Label lblNom;
    @FXML private Label lblCategorie;
    @FXML private Label lblExpiration;

    private Document document;
    private AfficherDocumentController parent;
    private final serviceDocument service = new serviceDocument();

    public void setData(Document doc, AfficherDocumentController parent) {
        this.document = doc;
        this.parent = parent;

        lblNom.setText(doc.getNomDocument());
        lblCategorie.setText(doc.getCategorie().getLibelle());

        if (doc.getDateExpiration() != null)
            lblExpiration.setText("Expire : " + doc.getDateExpiration());
        else
            lblExpiration.setText("Pas d'expiration");
    }

    @FXML
    private void supprimer() {
        service.delete(document);
        if (parent != null) parent.refresh();
    }

    @FXML
    private void modifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDocument.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            ModifierDocumentController controller = loader.getController();
            controller.setDocument(document);

            stage.showAndWait();
            if (parent != null) parent.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
