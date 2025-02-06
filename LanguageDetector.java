import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LanguageDetector {

    private static final String API_KEY = "6a733f6d65d9c3936cae20a686c9c945"; // کلید API خود را اینجا وارد کنید
    private static final String API_URL = "https://ws.detectlanguage.com/0.2/detect";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a word or phrase: ");
        String text = scanner.nextLine();
        scanner.close();

        try {
            String detectedLanguage = detectLanguage(text);
            System.out.println("Detected language: " + detectedLanguage);
        } catch (Exception e) {
            System.err.println("Error detecting language: " + e.getMessage());
        }
    }

    public static String detectLanguage(String text) throws Exception {
        // ساخت بدنه درخواست JSON
        String requestBody = String.format("{\"key\": \"%s\", \"q\": \"%s\"}", API_KEY, text);

        // ایجاد اتصال HTTP
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // ارسال بدنه درخواست
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // تجزیه پاسخ JSON
                String responseBody = response.toString();
                // استخراج زبان تشخیص داده شده از JSON
                String languageCode = responseBody.split("\"language\":\"")[1].split("\"")[0];
                return languageCode;
            }
        } else {
            throw new Exception("API request failed with status code: " + responseCode);
        }
    }
}
