package api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MeteoAPI {

    // 🔑 VOTRE CLÉ API
    private static final String API_KEY = "5a05e8ea52fb118e9b1fa729b5e0b5ac";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final HttpClient client;

    public MeteoAPI() {
        this.client = HttpClient.newHttpClient();
    }

    private String getMeteoData(String ville) {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=fr",
                    BASE_URL, ville, API_KEY);

            // ✅ CORRECT: Création de la requête
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // ✅ CORRECT: Envoi et réception de la réponse
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Erreur API: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getTemperature(String ville) {
        String json = getMeteoData(ville);
        if (json != null) {
            try {
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                return obj.getAsJsonObject("main").get("temp").getAsDouble();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public String getDescription(String ville) {
        String json = getMeteoData(ville);
        if (json != null) {
            try {
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                return obj.getAsJsonArray("weather")
                        .get(0).getAsJsonObject()
                        .get("description").getAsString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Inconnue";
    }

    public String getIcone(String ville) {
        String json = getMeteoData(ville);
        if (json != null) {
            try {
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                String main = obj.getAsJsonArray("weather")
                        .get(0).getAsJsonObject()
                        .get("main").getAsString();

                switch (main) {
                    case "Clear": return "☀️";
                    case "Clouds": return "☁️";
                    case "Rain": return "🌧️";
                    case "Snow": return "❄️";
                    case "Thunderstorm": return "⛈️";
                    case "Drizzle": return "🌦️";
                    case "Mist":
                    case "Fog": return "🌫️";
                    default: return "🌤️";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "🌡️";
    }

    public int getHumidite(String ville) {
        String json = getMeteoData(ville);
        if (json != null) {
            try {
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                return obj.getAsJsonObject("main").get("humidity").getAsInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public double getVent(String ville) {
        String json = getMeteoData(ville);
        if (json != null) {
            try {
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                return obj.getAsJsonObject("wind").get("speed").getAsDouble();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public String suggererOffre(String ville, String serviceName) {
        double temp = getTemperature(ville);
        String desc = getDescription(ville);
        String icone = getIcone(ville);

        StringBuilder suggestion = new StringBuilder();
        suggestion.append(icone).append(" ").append(desc).append(" - ");

        if (desc.contains("pluie") || desc.contains("rain")) {
            suggestion.append("Offre spéciale activités intérieures: Musée, Spa, Cinéma à -20%");
        } else if (temp > 28) {
            suggestion.append("Offre plage et sports nautiques: Parasol, Jet-ski, Glaces offertes");
        } else if (temp > 20) {
            suggestion.append("Offre découverte: Visites guidées, Randonnées à -15%");
        } else if (temp < 15) {
            suggestion.append("Offre hiver: Hammam, Thé chaud, Shopping à -10%");
        } else {
            suggestion.append("Offre standard: Réduction de 10% sur tous nos services");
        }

        return suggestion.toString();
    }
}