/*package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountryService {

    public List<String> getAllCountries() {

        List<String> countries = new ArrayList<>();

        try {
            String apiUrl = "https://restcountries.com/v3.1/all";

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            con.disconnect();

            JSONArray jsonArray = new JSONArray(content.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONObject nameObj = obj.getJSONObject("name");
                String countryName = nameObj.getString("common");

                countries.add(countryName);
            }

            Collections.sort(countries);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return countries;
    }
}*/
package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountryService {

    public List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        try {
            String apiUrl = "https://restcountries.com/v3.1/all?fields=name";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("User-Agent", "Java 11 HttpClient")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray jsonArray = new JSONArray(response.body());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONObject nameObj = obj.getJSONObject("name");
                String countryName = nameObj.getString("common");
                countries.add(countryName);
            }

            Collections.sort(countries);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return countries;
    }
}

