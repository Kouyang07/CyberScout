package dev.kouyang.database.structs.statbotics;

import lombok.Data;

@Data
public class Team {
    private int teamNumber;
    private EPA epa;
    private Ratings ratings;
}
