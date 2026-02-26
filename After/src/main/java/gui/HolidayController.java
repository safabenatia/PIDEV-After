package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import services.HolidayService;

import java.time.Year;
import java.util.List;

public class HolidayController {

    @FXML
    private ComboBox<String> comboCountryCode;

    @FXML
    private ListView<String> listHolidays;

    private HolidayService service = new HolidayService();

    @FXML
    public void initialize() {

        // Exemple codes pays
        comboCountryCode.setItems(FXCollections.observableArrayList(
                "TN", "FR", "US", "DE"
        ));

        comboCountryCode.setOnAction(event -> {

            String code = comboCountryCode.getValue();
            int currentYear = Year.now().getValue();

            new Thread(() -> {

                List<String> holidays =
                        service.getPublicHolidays(currentYear, code);

                javafx.application.Platform.runLater(() ->
                        listHolidays.setItems(
                                FXCollections.observableArrayList(holidays)
                        )
                );

            }).start();
        });
    }
}