package com.roman.fightnet.requests.models;

import java.util.UUID;

import lombok.Data;

@Data
public class Invite {
    private UUID id;
    private AppUser fighterInviter;
    private AppUser fighterInvited;
    private double latitude;
    private double longitude;
    private String fightStyle;
    private String comment;
    private String date;
    private boolean accepted;
    private String displayDate;
    private AppUser displayUser;
}
