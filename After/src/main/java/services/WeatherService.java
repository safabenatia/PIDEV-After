package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * WeatherService - Fetches real-time weather for a destination city.
 * Uses Open-Meteo API (https://open-meteo.com/) — 100% free, no API key required.
 *
 * Flow: city name → geocoding API → lat/lon → weather API
 */
public class WeatherService {

    private final OkHttpClient client = new OkHttpClient();

    // Step 1: Get coordinates from city name using Open-Meteo Geocoding
    private double[] getCoordinates(String city) {
        try {
            String encoded = java.net.URLEncoder.encode(city, "UTF-8");
            String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + encoded + "&count=1&language=fr&format=json";

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONObject json = new JSONObject(body);
            JSONArray results = json.getJSONArray("results");
            if (results.length() == 0) return null;

            JSONObject first = results.getJSONObject(0);
            return new double[]{first.getDouble("latitude"), first.getDouble("longitude")};
        } catch (Exception e) {
            System.out.println("WeatherService geocoding error: " + e.getMessage());
            return null;
        }
    }

    // Step 2: Get current weather using coordinates
    public JSONObject getCurrentWeather(String city) {
        try {
            double[] coords = getCoordinates(city);
            if (coords == null) return null;

            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + coords[0]
                    + "&longitude=" + coords[1]
                    + "&current_weather=true"
                    + "&hourly=relativehumidity_2m,precipitation_probability"
                    + "&forecast_days=1";

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONObject json = new JSONObject(body);
            return json.getJSONObject("current_weather");
        } catch (Exception e) {
            System.out.println("WeatherService error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns a formatted weather summary for the given city.
     * Example: "Paris: 18°C, Vent 12 km/h, Code météo: 1"
     */
    public String getWeatherSummary(String city) {
        JSONObject weather = getCurrentWeather(city);
        if (weather == null) return "Météo indisponible pour " + city;

        double temp = weather.getDouble("temperature");
        double windspeed = weather.getDouble("windspeed");
        int weatherCode = weather.getInt("weathercode");

        String condition = getConditionFromCode(weatherCode);
        return city + ": " + temp + "°C, " + condition + ", Vent: " + windspeed + " km/h";
    }

    // WMO Weather Interpretation Codes → French labels
    private String getConditionFromCode(int code) {
        if (code == 0) return "Ciel dégagé ☀️";
        else if (code <= 3) return "Partiellement nuageux ⛅";
        else if (code <= 49) return "Brouillard 🌫️";
        else if (code <= 69) return "Pluie 🌧️";
        else if (code <= 79) return "Neige ❄️";
        else if (code <= 99) return "Orage ⛈️";
        else return "Conditions inconnues";
    }
}
