package com.roman.fightnet.requests.models;

import java.util.Date;

import lombok.Data;

@Data
public class SocketMessage {
    private String userSender;
    private String userResiver;
    private String photo;
    private String text;
    private Date date;
    private String titleName;
    private String email;
}
