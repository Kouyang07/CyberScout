package dev.kouyang.database.structs.sheets;

import lombok.Data;

@Data
public class Response {
    private String intake;
    private String scoring;
    private String autoAverage;
    private String endgame; // Changed from Auto Description
    private String teamNumber;
    private String playerType;
    private String additionalInformation;

}
