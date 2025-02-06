import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OllamaClient {

    private static final String API_URL = "http://localhost:11434/run"; // آدرس محلی Ollama

    public static void main(String[] args) {
        String question = "where is Iran?"; // سوال خود را اینجا وارد کنید
        String response = sendQuestionToOllama(question);
        System.out.println("پاسخ: " + response);
    }

    private static String sendQuestionToOllama(String question) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"prompt\": \"%s\"}", question);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // بررسی وضعیت پاسخ
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
            } else {
                System.out.println("خطا در اتصال: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}