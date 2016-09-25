package com.teamcrisis.pharmafund;

import java.util.UUID;

/**
 * CampLeader class to identify leaders.
 */
public class CampLeader {
    private static int idCounter = 1;
    private String location;
    private String leaderId;

    public CampLeader() {
//        leaderId = UUID.randomUUID().toString();
        leaderId = Integer.toString(idCounter++);
    }

    public CampLeader(String loc) {
        location = loc;
    }

    public String getLocation() {
        return location;
    }

    public String getLeaderId() {
        return leaderId;
    }

    @Override
    public String toString() {
        return leaderId + " " + location;
    }
}
