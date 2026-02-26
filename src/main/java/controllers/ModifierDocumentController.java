package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.CategorieDocument;
import models.Document;
import services.serviceCategorieDocument;
import services.serviceDocument;

import java.io.File;
import java.sql.Date;
import java.util.List;

public class ModifierDocumentController {

    @FXML private TextField txtNom;
    @FXML private TextField txtChemin;
    @FXML private DatePicker dpAjout;
    @FXML private DatePicker dpExpiration;
    @FXML private ComboBox<CategorieDocument> cbCategorie;

    private final serviceDocument docService = new serviceDocument();
    private final serviceCategorieDocument catService = new serviceCategorieDocument();

    private Document document;

    @FXML
    public void initialize() {
        List<CategorieDocument> categories = catService.getAll();
        cbCategorie.getItems().addAll(categories);
    }

    public void setDocument(Document document) {
        this.document = document;

        txtNom.setText(document.getNomDocument());
        txtChemin.setText(document.getCheminFichier());

        if (document.getDateAjout() != null)
            dpAjout.setValue(document.getDateAjout().toLocalDate());

        if (document.getDateExpiration() != null)
            dpExpiration.setValue(document.getDateExpiration().toLocalDate());

        cbCategorie.setValue(document.getCategorie());
    }

    @FXML
    private void choisirFichier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier");
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            txtChemin.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void modifierDocument() {

        if (txtNom.getText().isEmpty()
                || txtChemin.getText().isEmpty()
                || dpAjout.getValue() == null
                || cbCategorie.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        document.setNomDocument(txtNom.getText());
        document.setCheminFichier(txtChemin.getText());
        document.setDateAjout(Date.valueOf(dpAjout.getValue()));
        document.setDateExpiration(
                dpExpiration.getValue() != null ? Date.valueOf(dpExpiration.getValue()) : null
        );
        document.setCategorie(cbCategorie.getValue());

        docService.update(document);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Document modifié avec succès ✔");

        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
