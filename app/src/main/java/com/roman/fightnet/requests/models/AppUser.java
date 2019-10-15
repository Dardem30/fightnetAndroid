package com.roman.fightnet.requests.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUser implements Serializable {
    private String username;
    private String email;
    private String weight;
    private String growth;
    private String preferredKind;
    private String name;
    private String surname;
    private String description;
    private String country;
    private String city;
    private List<String> photos;
    private String mainPhoto;
    private Integer notifications;
    private Integer unreadedMessages;
    private Loses loses;
    private Wins wins;

    public AppUser(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}
