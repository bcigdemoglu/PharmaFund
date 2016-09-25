package com.teamcrisis.pharmafund;

import java.util.UUID;

/**
 * CampLeader class to identify leaders.
 */
public class CampLeader {
    private String location;
    private String leaderId;

    public CampLeader() {
//        this.leaderId = UUID.randomUUID().toString();
        this.leaderId = Integer.toString(PharmaService.getNewId());
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
