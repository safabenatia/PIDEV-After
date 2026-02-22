package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class RestCountriesService {

    private OkHttpClient client = new OkHttpClient();

    public JSONObject getCountryInfo(String pays) {
        try {
            Request request = new Request.Builder()
                    .url("https://restcountries.com/v3.1/name/" + pays + "?fullText=false")
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONArray array = new JSONArray(body);
            return array.getJSONObject(0);

        } catch (Exception e) {
            System.out.println("RestCountries API error: " + e.getMessage());
            return null;
        }
    }

    public String getFlag(String pays) {
        JSONObject country = getCountryInfo(pays);
        if (country == null) return "";
        return country.getJSONObject("flags").getString("png");
    }

    public String getCurrency(String pays) {
        JSONObject country = getCountryInfo(pays);
        if (country == null) return "";
        try {
            JSONObject currencies = country.getJSONObject("currencies");
            String code = currencies.keys().next();
            JSONObject currency = currencies.getJSONObject(code);
            return code + " - " + currency.getString("name");
        } catch (Exception e) {
            return "";
        }
    }

    public String getLanguage(String pays) {
        JSONObject country = getCountryInfo(pays);
        if (country == null) return "";
        try {
            JSONObject languages = country.getJSONObject("languages");
            return languages.toMap().values().iterator().next().toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCapital(String pays) {
        JSONObject country = getCountryInfo(pays);
        if (country == null) return "";
        try {
            return country.getJSONArray("capital").getString(0);
        } catch (Exception e) {
            return "";
        }
    }
}