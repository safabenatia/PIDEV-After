package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * WeatherService - Gets real-time and forecast weather for any city.
 * Uses Open-Meteo API (https://open-meteo.com/) — completely free, no API key required.
 * Geocoding via Nominatim (OpenStreetMap).
 *
 * Perfect for the travel app: show travelers the weather at their destination
 * before and during their voyage.
 */
public class WeatherService {

    private final OkHttpClient client = new OkHttpClient();

    // ── Step 1: Geocode city → lat/lon using Nominatim (OSM) ──────────────
    private double[] geocodeCity(String city) {
        try {
            String encoded = java.net.URLEncoder.encode(city, "UTF-8");
            String url = "https://nominatim.openstreetmap.org/search?q=" + encoded
                    + "&format=json&limit=1";

            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "GestionVoyageApp/1.0")
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONArray results = new JSONArray(body);
            if (results.length() == 0) return null;

            JSONObject first = results.getJSONObject(0);
            return new double[]{
                    first.getDouble("lat"),
                    first.getDouble("lon")
            };
        } catch (Exception e) {
            System.out.println("WeatherService geocoding error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get current weather for a city.
     * @param city City name (e.g. "Paris", "Tokyo", "Tunis")
     * @return JSONObject with fields: temperature_2m, windspeed_10m, weathercode, time
     *         Returns null on error.
     */
    public JSONObject getCurrentWeather(String city) {
        try {
            double[] coords = geocodeCity(city);
            if (coords == null) return null;

            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + coords[0]
                    + "&longitude=" + coords[1]
                    + "&current_weather=true";

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONObject json = new JSONObject(body);
            return json.getJSONObject("current_weather");
        } catch (Exception e) {
            System.out.println("WeatherService getCurrentWeather error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get a human-readable weather summary for a city.
     * Example: "Météo à Paris : 18.5°C, vent 12 km/h, Partiellement nuageux"
     */
    public String getWeatherSummary(String city) {
        JSONObject weather = getCurrentWeather(city);
        if (weather == null) return "Météo indisponible pour " + city;

        double temp   = weather.getDouble("temperature");
        double wind   = weather.getDouble("windspeed");
        int    code   = weather.getInt("weathercode");
        String desc   = weatherCodeToDescription(code);

        return String.format("Météo à %s : %.1f°C, vent %.0f km/h, %s", city, temp, wind, desc);
    }

    /**
     * Get 7-day temperature forecast (max/min) for a city.
     * @param city City name
     * @return Formatted string with daily forecast
     */
    public String getWeeklyForecast(String city) {
        try {
            double[] coords = geocodeCity(city);
            if (coords == null) return "Prévisions indisponibles pour " + city;

            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + coords[0]
                    + "&longitude=" + coords[1]
                    + "&daily=temperature_2m_max,temperature_2m_min,weathercode"
                    + "&timezone=auto";

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONObject json       = new JSONObject(body);
            JSONObject daily      = json.getJSONObject("daily");
            JSONArray  dates      = daily.getJSONArray("time");
            JSONArray  maxTemps   = daily.getJSONArray("temperature_2m_max");
            JSONArray  minTemps   = daily.getJSONArray("temperature_2m_min");
            JSONArray  codes      = daily.getJSONArray("weathercode");

            StringBuilder sb = new StringBuilder("Prévisions météo à " + city + " (7 jours) :\n");
            for (int i = 0; i < dates.length(); i++) {
                sb.append(String.format("  %s : %.0f°C / %.0f°C — %s\n",
                        dates.getString(i),
                        maxTemps.getDouble(i),
                        minTemps.getDouble(i),
                        weatherCodeToDescription(codes.getInt(i))));
            }
            return sb.toString().trim();

        } catch (Exception e) {
            System.out.println("WeatherService forecast error: " + e.getMessage());
            return "Prévisions indisponibles pour " + city;
        }
    }

    /**
     * Returns a travel suitability assessment based on weather.
     * Useful to display on voyage cards: "Bon moment pour voyager ✓" or "Pluie attendue !"
     */
    public String getTravelAdvice(String city) {
        JSONObject weather = getCurrentWeather(city);
        if (weather == null) return "Conseil météo indisponible.";

        double temp = weather.getDouble("temperature");
        int    code = weather.getInt("weathercode");

        if (code >= 95) return "⚠️ Orage prévu à " + city + " — voyage déconseillé.";
        if (code >= 61) return "🌧️ Pluie prévue à " + city + " — prévoyez un imperméable.";
        if (temp < 0)   return "🥶 Températures négatives à " + city + " — habillez-vous chaudement.";
        if (temp > 35)  return "🌡️ Forte chaleur à " + city + " — restez hydraté.";
        return "✅ Bon temps à " + city + " — idéal pour voyager !";
    }

    // ── WMO Weather Code → French description ────────────────────────────
    private String weatherCodeToDescription(int code) {
        if (code == 0)              return "Ciel dégagé ☀️";
        if (code <= 2)              return "Partiellement nuageux ⛅";
        if (code == 3)              return "Couvert ☁️";
        if (code <= 49)             return "Brouillard 🌫️";
        if (code <= 59)             return "Bruine 🌦️";
        if (code <= 69)             return "Pluie 🌧️";
        if (code <= 79)             return "Neige ❄️";
        if (code <= 84)             return "Averses 🌦️";
        if (code <= 94)             return "Averses de neige ❄️";
        return "Orage ⛈️";
    }
}