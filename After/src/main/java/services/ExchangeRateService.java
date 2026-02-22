package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class ExchangeRateService {

    private static final String API_KEY = "39006912c0ec320f06584324"; // replace this
    private OkHttpClient client = new OkHttpClient();

    public double convertToEUR(double amountTND) {
        return convert(amountTND, "TND", "EUR");
    }

    public double convertToUSD(double amountTND) {
        return convert(amountTND, "TND", "USD");
    }

    private double convert(double amount, String from, String to) {
        try {
            Request request = new Request.Builder()
                    .url("https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/" + from + "/" + to + "/" + amount)
                    .build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            return Math.round(json.getDouble("conversion_result") * 100.0) / 100.0;
        } catch (Exception e) {
            System.out.println("ExchangeRate error: " + e.getMessage());
            return 0;
        }
    }
}