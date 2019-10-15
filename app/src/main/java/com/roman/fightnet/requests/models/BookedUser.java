package com.roman.fightnet.requests.models;

import lombok.Data;

@Data
public class BookedUser {
    private String email;
    private String name;
    private String surname;
    private String mainPhoto;
}