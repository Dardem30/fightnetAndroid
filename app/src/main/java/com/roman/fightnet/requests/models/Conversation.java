package com.roman.fightnet.requests.models;

import lombok.Data;

@Data
public class Conversation {
    private String userSender;
    private String userResiver;
    private String text;
    private Long date;
    private String titleName;
    private String photo;
}
