package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class TranslateService {

    private OkHttpClient client = new OkHttpClient();

    public String translate(String text, String targetLang) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String url = "https://api.mymemory.translated.net/get?q="
                    + encodedText + "&langpair=fr|" + targetLang;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();
            System.out.println("TRANSLATE RESPONSE: " + body); // add this line
            JSONObject json = new JSONObject(body);
            return json.getJSONObject("responseData").getString("translatedText");

        } catch (Exception e) {
            System.out.println("Translate error: " + e.getMessage());
            return text;
        }
    }
}