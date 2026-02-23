package gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class MapViewController {

    @FXML
    private BorderPane mapContainer;

    // 🔐 Ta clé Mapbox
    private static final String MAPBOX_API_KEY = "pk.eyJ1IjoiZXlha2hlZGlyaTEyIiwiYSI6ImNtbHc4YnVxNzBmbnozZnIwMnpmeTJ1d2wifQ.-bChuWudKJGHaBcvRpukkw";

    private double startLng, startLat;
    private double endLng, endLat;

    /**
     * Initialisation de la carte / calcul des itinéraires
     */
    public void initMap(String lieuActivite) {

        // Liste de villes exemple pour le choix
        List<String> villes = List.of("Paris", "New York", "Tokyo", "Londres", "Sydney", "Berlin", "Tunis");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Paris", villes);
        dialog.setTitle("Choisir la ville de départ");
        dialog.setHeaderText("Sélectionnez votre ville de départ");
        dialog.setContentText("Ville :");

        dialog.showAndWait().ifPresent(ville -> {

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {

                        // 🔎 Coordonnées départ et arrivée (n’importe quel pays)
                        double[] coordsStart = getCoordinates(ville);
                        startLng = coordsStart[0];
                        startLat = coordsStart[1];

                        double[] coordsEnd = getCoordinates(lieuActivite);
                        endLng = coordsEnd[0];
                        endLat = coordsEnd[1];

                        // 🚗🚶🚲 Appels Directions API
                        JSONObject driving = safeGetDirections("driving", startLng, startLat, endLng, endLat);
                        JSONObject walking = safeGetDirections("walking", startLng, startLat, endLng, endLat);
                        JSONObject cycling = safeGetDirections("cycling", startLng, startLat, endLng, endLat);

                        Platform.runLater(() -> showItineraire(driving, walking, cycling));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() ->
                                showAlert("Erreur", "Impossible de calculer l'itinéraire.")
                        );
                    }
                    return null;
                }
            };

            new Thread(task).start();
        });
    }

    /**
     * Récupération des coordonnées (longitude, latitude) pour un lieu
     */
    private double[] getCoordinates(String lieu) throws Exception {

        String urlString = "https://api.mapbox.com/geocoding/v5/mapbox.places/"
                + URLEncoder.encode(lieu, StandardCharsets.UTF_8)
                + ".json?access_token=" + MAPBOX_API_KEY;

        String response = new Scanner(new URL(urlString).openStream(), "UTF-8")
                .useDelimiter("\\A").next();

        JSONObject json = new JSONObject(response);
        JSONArray features = json.getJSONArray("features");

        if (features.length() == 0)
            throw new Exception("Lieu introuvable : " + lieu);

        JSONArray coords = features.getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates");

        double lng = coords.getDouble(0);
        double lat = coords.getDouble(1);

        return new double[]{lng, lat};
    }

    /**
     * Appel de l'API Directions pour un mode spécifique
     */
    private JSONObject getDirections(String profile,
                                     double startLng, double startLat,
                                     double endLng, double endLat) throws Exception {

        String urlStr = "https://api.mapbox.com/directions/v5/mapbox/"
                + profile + "/"
                + startLng + "," + startLat + ";"
                + endLng + "," + endLat
                + "?overview=false&access_token=" + MAPBOX_API_KEY;

        String response = new Scanner(new URL(urlStr).openStream(), "UTF-8")
                .useDelimiter("\\A").next();

        return new JSONObject(response);
    }

    /**
     * Sécurisation de l'appel pour éviter les erreurs (ex: marche impossible)
     */
    private JSONObject safeGetDirections(String profile,
                                         double startLng, double startLat,
                                         double endLng, double endLat) {
        try {
            return getDirections(profile, startLng, startLat, endLng, endLat);
        } catch (Exception e) {
            System.out.println(profile + " impossible pour ces coordonnées.");
            return null;
        }
    }

    /**
     * Extraction distance et durée
     */
    private String extractInfo(JSONObject directions) {
        if (directions == null) return "Non disponible";

        JSONArray routes = directions.getJSONArray("routes");
        if (routes.length() == 0) return "Non disponible";

        JSONObject route = routes.getJSONObject(0);

        double distanceKm = route.getDouble("distance") / 1000;
        double durationMin = route.getDouble("duration") / 60;

        return String.format("%.1f km - %.0f min", distanceKm, durationMin);
    }

    /**
     * Affichage du dialogue avec les informations
     */
    private void showItineraire(JSONObject driving,
                                JSONObject walking,
                                JSONObject cycling) {

        String voiture = extractInfo(driving);
        String marche = extractInfo(walking);
        String velo = extractInfo(cycling);

        showAlert("Itinéraire estimé",
                "🚗 Voiture : " + voiture +
                        "\n🚶 Marche : " + marche +
                        "\n🚲 Vélo : " + velo
        );
    }

    /**
     * Affichage d'une alerte
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        mapContainer.getScene().getWindow().hide();
    }
    public void openItineraire(String lieuActivite) {

        // Liste des villes disponibles pour le départ
        List<String> villes = List.of("Paris, France", "Tunis, Tunisia", "New York, USA", "Tokyo, Japan", "Londres, UK");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Paris, France", villes);
        dialog.setTitle("Choisir la ville de départ");
        dialog.setHeaderText("Sélectionnez votre ville de départ");
        dialog.setContentText("Ville :");

        dialog.showAndWait().ifPresent(villeDepart -> {

            // Thread pour calculer l'itinéraire sans bloquer l'UI
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        // 🔎 Coordonnées départ et destination
                        double[] coordsStart = getCoordinates(villeDepart);
                        double[] coordsEnd = getCoordinates(lieuActivite);

                        // 🚗🚶🚲 Appels API Mapbox
                        JSONObject driving = safeGetDirections("driving", coordsStart[0], coordsStart[1], coordsEnd[0], coordsEnd[1]);
                        JSONObject walking = safeGetDirections("walking", coordsStart[0], coordsStart[1], coordsEnd[0], coordsEnd[1]);
                        JSONObject cycling = safeGetDirections("cycling", coordsStart[0], coordsStart[1], coordsEnd[0], coordsEnd[1]);

                        // Affichage du dialogue
                        Platform.runLater(() -> showItineraire(driving, walking, cycling));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> showAlert("Erreur", "Impossible de calculer l'itinéraire."));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });
    }}