import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class OllamaQueryRunner {

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
        String cleanedOutput = input.replaceAll("\u001B\\[[;\\d]*m", ""); 
        cleanedOutput = cleanedOutput.replaceAll("\\[\\?25[lh]", "");
        cleanedOutput = cleanedOutput.replaceAll("\\[2K|\\[1G", ""); 
        cleanedOutput = cleanedOutput.replaceAll("[^\\p{Print}\\p{Space}]", "");
        return cleanedOutput.replaceAll("\\s+", " ").trim();
    }

    private static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your prompt: ");
        return scanner.nextLine();  
    }
}
