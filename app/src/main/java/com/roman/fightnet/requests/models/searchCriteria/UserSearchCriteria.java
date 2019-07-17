package com.roman.fightnet.requests.models.searchCriteria;

import lombok.Data;

@Data
public class UserSearchCriteria {
    private String name;
    private String description;
    private String country;
    private String city;
    private String searcherEmail;
    private int pageNum;
    private String preferredKind;
    private String width;
    private String height;
}
