package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CountryInfoService {

    public JSONObject getCountryInfo(String countryName) {
        try {
            String apiUrl = "https://restcountries.com/v3.1/name/" +
                    java.net.URLEncoder.encode(countryName, "UTF-8") +
                    "?fullText=true";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("User-Agent", "Java 11 HttpClient")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray array = new JSONArray(response.body());
            return array.getJSONObject(0); // on prend le premier résultat

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
