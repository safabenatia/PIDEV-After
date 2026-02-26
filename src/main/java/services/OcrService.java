package services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OcrService {

    // 🔑 mets ta clé OCR.space ici
    private static final String API_KEY = "K83352894288957";

    public String extractTextFromFile(String filePath) {

        String boundary = "----OCRBoundary";
        String LINE_FEED = "\r\n";

        try {
            URL url = new URL("https://api.ocr.space/parse/image");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("apikey", API_KEY);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            File uploadFile = new File(filePath);

            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + uploadFile.getName() + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
            writer.append(LINE_FEED).flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}