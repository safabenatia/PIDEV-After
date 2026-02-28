package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * TouristAttractionService - Finds nearby tourist attractions for any city.
 * Uses OpenTripMap API (https://opentripmap.io/) — free tier, no key needed for Geoname lookups.
 *
 * For attractions list, uses the Nominatim geocoding (OSM) + Overpass API approach
 * which is completely free and requires no API key.
 *
 * Flow: city name → Nominatim geocoding → Overpass API → list of POIs
 */
public class TouristAttractionService {

    private final OkHttpClient client = new OkHttpClient();

    // Step 1: Geocode city name to lat/lon using Nominatim (OpenStreetMap) — no key needed
    private double[] geocodeCity(String city) {
        try {
            String encoded = java.net.URLEncoder.encode(city, "UTF-8");
            String url = "https://nominatim.openstreetmap.org/search?q=" + encoded
                    + "&format=json&limit=1";

            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "GestionVoyageApp/1.0")  // Nominatim requires a User-Agent
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
            System.out.println("TouristAttractionService geocoding error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns a list of tourist attractions near a given city.
     * Uses Overpass API (OSM) — completely free, no key needed.
     *
     * @param city  The destination city name (e.g. "Paris", "Tokyo")
     * @param radius Search radius in meters (default: 5000 = 5km from city center)
     * @param limit  Max number of results to return
     * @return List of attraction names
     */
    public List<String> getAttractions(String city, int radius, int limit) {
        List<String> attractions = new ArrayList<>();
        try {
            double[] coords = geocodeCity(city);
            if (coords == null) return attractions;

            double lat = coords[0];
            double lon = coords[1];

            // Overpass QL query: find tourism POIs within radius
            String overpassQuery = "[out:json][timeout:10];"
                    + "node[\"tourism\"](around:" + radius + "," + lat + "," + lon + ");"
                    + "out " + limit + ";";

            String encoded = java.net.URLEncoder.encode(overpassQuery, "UTF-8");
            String url = "https://overpass-api.de/api/interpreter?data=" + encoded;

            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "GestionVoyageApp/1.0")
                    .build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONObject json = new JSONObject(body);
            JSONArray elements = json.getJSONArray("elements");

            for (int i = 0; i < elements.length(); i++) {
                JSONObject el = elements.getJSONObject(i);
                if (el.has("tags")) {
                    JSONObject tags = el.getJSONObject("tags");
                    String name = tags.optString("name", null);
                    String type = tags.optString("tourism", "");
                    if (name != null && !name.isEmpty()) {
                        attractions.add(name + " [" + type + "]");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("TouristAttractionService error: " + e.getMessage());
        }
        return attractions;
    }

    /**
     * Returns a formatted string of top N attractions in a city.
     * Example: "Top attractions in Paris:\n- Eiffel Tower [attraction]\n- Louvre [museum]"
     */
    public String getAttractionsSummary(String city, int topN) {
        List<String> list = getAttractions(city, 5000, topN);
        if (list.isEmpty()) return "Aucune attraction trouvée pour " + city;

        StringBuilder sb = new StringBuilder("Attractions touristiques à " + city + ":\n");
        for (String a : list) {
            sb.append("  • ").append(a).append("\n");
        }
        return sb.toString().trim();
    }
}
