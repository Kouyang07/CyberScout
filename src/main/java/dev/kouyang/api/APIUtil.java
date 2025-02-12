package dev.kouyang.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.kouyang.database.structs.sheets.Response;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class APIUtil {

    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + "AIzaSyD-AZ5zTZaGxibzQ8drMFnVN7Ffqr0RGLQ"; // **REPLACE WITH YOUR ACTUAL API KEY**  // Unchanged (except for the key)

    public static String getStat(String urlString) { // Unchanged
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("GET request failed: " + response.statusCode() + " " + response.body());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error in getStat: " + e.getMessage());
            return null;
        }
    }//statbotic info

    public static String requestGemini(String question) {
        // Use a StringBuilder for the question prompt as well
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Question: ").append(question);  // Or any other formatting you want
        // You can add more context or instructions here if needed for general questions
        promptBuilder.append(". Please provide a concise and informative answer. Make sure that the response you give is less than 1999 chatacters");

        return performGeminiRequest(promptBuilder.toString());
    }


    public static String requestGemini(Response data) {
        String promptBuilder = "Team Number: " + data.getTeamNumber() + "\n" +
                "Player Type: " + data.getPlayerType() + "\n" +
                "Intake: " + data.getIntake() + "\n" +
                "Scoring: " + data.getScoring() + "\n" +
                "Auto Average: " + data.getAutoAverage() + "\n" +
                "Endgame: " + data.getEndgame() + "\n" +
                "Additional Information: " + data.getAdditionalInformation() + "\n" +
                "Please provide a summary and analysis of this data. Also provide a score at the beginning of the message. Make sure that the response you give is less than 1999 characters.";

        return performGeminiRequest(promptBuilder);
    }


    private static String performGeminiRequest(String prompt) {
        JsonObject requestBody = new JsonObject();
        JsonArray contentsArray = new JsonArray();
        JsonObject contentObject = new JsonObject();
        JsonArray partsArray = new JsonArray();
        JsonObject partObject = new JsonObject();

        partObject.addProperty("text", prompt);
        partsArray.add(partObject);
        contentObject.add("parts", partsArray);
        contentsArray.add(contentObject);
        requestBody.add("contents", contentsArray);

        String jsonBody = gson.toJson(requestBody);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Gemini API request failed: " + response.statusCode() + " " + response.body());
                return ("Gemini API request failed: " + response.statusCode());

            }


            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonArray contentParts = firstCandidate.getAsJsonObject("content").getAsJsonArray("parts");

                if (contentParts != null && contentParts.size() > 0) {
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < contentParts.size(); i++) {
                        result.append(contentParts.get(i).getAsJsonObject().get("text").getAsString());
                    }
                    if(result.toString().length()<2000){
                        return (result.toString());
                    }
                    return (result.toString().substring(0, 1990));
                }
            }
            return ("No response found.");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
           return ("Error: " + e.getMessage());

        }
    }
}