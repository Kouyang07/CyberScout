package dev.kouyang.database;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import dev.kouyang.database.structs.sheets.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetSheetData {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials2.json";

    private List<Response> responses = new ArrayList<>();

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GetSheetData.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void fetchData() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1gEnF_QZd632Xk0AbygqRg9A1BV1F2yhdFyW5NphUzXY"; // Replace with your sheet ID
        final String range = "A1:G"; // Modified range to include all columns (adjust "G" if needed)

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return;
        }

        // Extract headers, handling potential nulls
        List<String> headers = new ArrayList<>();
        if (!values.isEmpty()) {  // Check if there are any rows at all
            for (Object h : values.get(0)) {
                headers.add(h != null ? h.toString() : ""); // Handle null headers
            }
        } else {
            System.out.println("Warning: Spreadsheet is empty, no headers found.");
            return; // Exit early if no headers
        }


        for (int i = 1; i < values.size(); i++) { // Start from row 2 (index 1)
            List<Object> row = values.get(i);
            Response teamResponse = new Response();

            for (int j = 0; j < Math.min(row.size(), headers.size()); j++) {
                String headerName = headers.get(j).trim(); // Trim whitespace!
                Object cellValue = row.get(j);
                String cellStringValue = (cellValue != null) ? cellValue.toString().trim() : ""; // Handle nulls and trim

                switch (headerName) {  // Use a more robust matching strategy
                    case "Describe their intake (can they intake both game pieces?)":
                    case "Describe their intake ": // Account for extra space
                        teamResponse.setIntake(cellStringValue);
                        break;
                    case "Describe their scoring (on average, best ever, where do they like to score the best)": // Handle typo in header
                    case "Describe their scoring":
                        teamResponse.setScoring(cellStringValue);
                        break;
                    case "How many auto on average (what do they do, on average auto score, best ever auto score)": // Handle typo
                    case "How many auto on average":
                        teamResponse.setAutoAverage(cellStringValue);
                        break;
                    case "Describe their Endgame (can they climb, if so which one)": // Handle typo
                    case "Describe their Endgame":
                        teamResponse.setEndgame(cellStringValue);
                        break;
                    case "Put team number likv": // Handle typo
                    case "Put team number like (2872)":
                        teamResponse.setTeamNumber(cellStringValue);
                        break;
                    case "What type of player are they (defensive, aggressive, both?)":
                        teamResponse.setPlayerType(cellStringValue);
                        break;
                    case "Additional information (they have big bot, really unstable)": // Handle typo
                    case "Additional information":
                        teamResponse.setAdditionalInformation(cellStringValue);
                        break;
                    // ... other fields
                    default:
                        System.out.println("Warning: Unrecognized header: '" + headerName + "'");
                }
            }
            responses.add(teamResponse);
        }
    }

    public List<Response> getResponses() {
        return responses;
    }
    public int getTeam(String teamNum) {
        if (teamNum == null) {
            return -1;
        }
        for (int i = 0; i < responses.size(); i++) {
            String currentTeamNumber = responses.get(i).getTeamNumber();
            if (currentTeamNumber != null && currentTeamNumber.trim().equals(teamNum.trim())) {
                return i;
            }
        }
        return -1;
    }

    public String getAlldata(int getTeam){

        if(getTeam(String.valueOf(getTeam)) == -1){
            return "Team not found";
        }
        int temp = getTeam(String.valueOf(getTeam));



        String promptBuilder = "Team Number: " + responses.get(temp).getTeamNumber() + "\n" +
                "Player Type: " + responses.get(temp).getPlayerType() + "\n" +
                "Intake: " + responses.get(temp).getIntake() + "\n" +
                "Scoring: " + responses.get(temp).getScoring() + "\n" +
                "Auto Average: " + responses.get(temp).getAutoAverage() + "\n" +
                "Endgame: " + responses.get(temp).getEndgame() + "\n" +
                "Additional Information: " + responses.get(temp).getAdditionalInformation() ;
        return promptBuilder;
    }


}