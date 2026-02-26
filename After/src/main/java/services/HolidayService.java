package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HolidayService {

    public List<String> getPublicHolidays(int year, String countryCode) {

        List<String> holidays = new ArrayList<>();

        try {
            String apiUrl = "https://date.nager.at/api/v3/PublicHolidays/"
                    + year + "/" + countryCode;

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("User-Agent", "Java 11 HttpClient")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray jsonArray = new JSONArray(response.body());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String date = obj.getString("date");
                String name = obj.getString("localName");

                holidays.add(date + " - " + name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return holidays;
    }
}
