package dev.kouyang;

import dev.kouyang.api.APIUtil;
import dev.kouyang.database.GetSheetData;
import dev.kouyang.database.structs.sheets.Response;
import dev.kouyang.discord.Bot;
import dev.kouyang.discord.Methods;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws GeneralSecurityException, IOException {

        GetSheetData sheetData = new GetSheetData();
        sheetData.fetchData();
        List<Response> responses = sheetData.getResponses();

        for (Response response : responses) {
            System.out.println(response); // Lombok's @Data provides a nice toString()
            System.out.println("--------------------");
        }
        //System.out.println("Total responses: " + responses.size());


        Bot.initBot();
    }

}
//AIzaSyD-AZ5zTZaGxibzQ8drMFnVN7Ffqr0RGLQ