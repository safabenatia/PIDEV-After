package services;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service de conversion de devises utilisant l'API ExchangeRate
 * API gratuite sans cle: https://api.exchangerate-api.com
 */
public class ConvertisseurDeviseService {

    // URL de l'API gratuite (sans cle requise)
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    /**
     * Convertit un montant d'une devise vers une autre
     * @param montant Le montant a convertir
     * @param deviseSource La devise source (EUR, USD, etc.)
     * @param deviseCible La devise cible (TND, EUR, USD, etc.)
     * @return Le montant converti
     */
    public double convertirDevise(double montant, String deviseSource, String deviseCible) {
        try {
            // Si meme devise, pas de conversion
            if (deviseSource.equals(deviseCible)) {
                return montant;
            }

            // Obtenir le taux de change
            double tauxChange = obtenirTauxChange(deviseSource, deviseCible);

            if (tauxChange > 0) {
                double montantConverti = montant * tauxChange;
                System.out.println("Conversion: " + montant + " " + deviseSource +
                        " = " + montantConverti + " " + deviseCible +
                        " (taux: " + tauxChange + ")");
                return montantConverti;
            } else {
                System.err.println("Impossible d'obtenir le taux de change");
                return montant; // Retourner le montant original
            }

        } catch (Exception e) {
            System.err.println("Erreur conversion devise: " + e.getMessage());
            e.printStackTrace();
            return montant; // En cas d'erreur, retourner le montant original
        }
    }

    /**
     * Obtient le taux de change entre deux devises via l'API
     * @param deviseSource La devise source
     * @param deviseCible La devise cible
     * @return Le taux de change
     */
    private double obtenirTauxChange(String deviseSource, String deviseCible) {
        try {
            // Construire l'URL de l'API
            String urlString = API_URL + deviseSource;
            URL url = new URL(urlString);

            // Ouvrir la connexion
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // Timeout de 5 secondes
            conn.setReadTimeout(5000);

            // Lire la reponse
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) { // Success
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parser le JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject rates = jsonResponse.getJSONObject("rates");

                // Obtenir le taux pour la devise cible
                if (rates.has(deviseCible)) {
                    return rates.getDouble(deviseCible);
                } else {
                    System.err.println("Devise cible non trouvee: " + deviseCible);
                    return -1;
                }

            } else {
                System.err.println("Erreur HTTP: " + responseCode);
                return -1;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel API: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Convertit EUR vers TND
     */
    public double convertirEURversTND(double montantEUR) {
        return convertirDevise(montantEUR, "EUR", "TND");
    }

    /**
     * Convertit USD vers TND
     */
    public double convertirUSDversTND(double montantUSD) {
        return convertirDevise(montantUSD, "USD", "TND");
    }

    /**
     * Convertit TND vers EUR
     */
    public double convertirTNDversEUR(double montantTND) {
        return convertirDevise(montantTND, "TND", "EUR");
    }

    /**
     * Convertit TND vers USD
     */
    public double convertirTNDversUSD(double montantTND) {
        return convertirDevise(montantTND, "TND", "USD");
    }

    /**
     * Teste la connexion a l'API
     */
    public boolean testerConnexion() {
        try {
            double taux = obtenirTauxChange("USD", "EUR");
            return taux > 0;
        } catch (Exception e) {
            return false;
        }
    }
}