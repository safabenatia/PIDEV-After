/*package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import services.CountryService;

import java.util.List;

public class CountryController {

    @FXML
    private ComboBox<String> comboCountry;

    private CountryService service = new CountryService();

    @FXML
    public void initialize() {

        // ⚠️ Important : charger dans un Thread pour éviter blocage UI
        new Thread(() -> {

            List<String> countries = service.getAllCountries();

            javafx.application.Platform.runLater(() ->
                    comboCountry.setItems(FXCollections.observableArrayList(countries))
            );

        }).start();
    }
}*/
package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.CountryService;
import services.CountryInfoService;
import org.json.JSONObject;

import java.util.List;

public class CountryController {

    @FXML
    private ComboBox<String> comboCountry;

    // Labels et ImageView pour afficher les infos du pays
    @FXML
    private Label lblCapital;
    @FXML
    private Label lblRegion;
    @FXML
    private Label lblPopulation;
    @FXML
    private ImageView imgFlag;

    private CountryService service = new CountryService();

    @FXML
    public void initialize() {

        // ⚠️ Important : charger dans un Thread pour éviter blocage UI
        new Thread(() -> {

            List<String> countries = service.getAllCountries();

            javafx.application.Platform.runLater(() -> {
                comboCountry.setItems(FXCollections.observableArrayList(countries));

                // 🔹 Ajouter listener pour afficher infos du pays
                comboCountry.setOnAction(event -> {
                    String selectedCountry = comboCountry.getValue();
                    if (selectedCountry != null) {
                        CountryInfoService infoService = new CountryInfoService();
                        JSONObject info = infoService.getCountryInfo(selectedCountry);

                        if (info != null) {
                            String capital = info.has("capital") ? info.getJSONArray("capital").getString(0) : "N/A";
                            String region = info.has("region") ? info.getString("region") : "N/A";
                            long population = info.has("population") ? info.getLong("population") : 0;
                            String flag = info.has("flags") ? info.getJSONObject("flags").getString("png") : "";

                            // Affichage dans les Labels et ImageView
                            lblCapital.setText("Capitale : " + capital);
                            lblRegion.setText("Région : " + region);
                            lblPopulation.setText("Population : " + population);

                            if (!flag.isEmpty()) {
                                imgFlag.setImage(new Image(flag));
                                imgFlag.setFitWidth(150);
                                imgFlag.setPreserveRatio(true);
                            }
                        }
                    }
                });
            });

        }).start();
    }
}
