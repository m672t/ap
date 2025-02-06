import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class OllamaQueryRunner {

    public static void main(String[] args) {
        String prompt = "What is the capital of Iran?";
        String response = runOllamaQuery(prompt);
        System.out.println(response);
    }

    public static String runOllamaQuery(String question) {
        ProcessBuilder processBuilder = new ProcessBuilder("ollama", "run", "llama3.2:1b");
        processBuilder.redirectErrorStream(true); 

        try {
            Process process = processBuilder.start();

            
            try (OutputStream outputStream = process.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                writer.println(question);
                writer.flush();
            }

            
            StringBuilder output = new StringBuilder();
            try (InputStream inputStream = process.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return "Error: The process timed out.";
            }

            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return "Process ended with error code " + exitCode;
            }

            
            String cleanedOutput = removeUnwantedChars(output.toString()).trim();
            return cleanedOutput;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }

    
    private static String removeUnwantedChars(String input) {
        StringBuilder cleanedOutput = new StringBuilder();
        
        for (char c : input.toCharArray()) {
            
            if (Character.isLetter(c) || Character.isWhitespace(c) || Character.isDigit(c) || c == '.') {
                cleanedOutput.append(c);
            }
        }
        
        
        String result = cleanedOutput.toString().replaceAll("\\s+", " ").trim();
        return result;
    }
}
