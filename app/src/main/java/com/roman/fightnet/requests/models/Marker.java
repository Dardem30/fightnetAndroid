package com.roman.fightnet.requests.models;

import java.util.Date;

import lombok.Data;

@Data
public class Marker {
    private BookedUser fighterInviter;
    private BookedUser fighterInvited;
    private float latitude;
    private float longitude;
    private String fightStyle;
    private String comment;
    private Date date;
    private boolean accepted;
}
