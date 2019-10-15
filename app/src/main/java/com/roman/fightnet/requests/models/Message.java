package com.roman.fightnet.requests.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {
    private String userSender;
    private String userResiver;
    private String photo;
    private String text;
    private Long date;
    private String titleName;
    private String email;

    public Message(final SocketMessage message) {
        this.userSender = message.getUserSender();
        this.userResiver = message.getUserResiver();
        this.photo = message.getPhoto();
        this.text = message.getText();
        this.date = message.getDate().getTime();
    }
}
