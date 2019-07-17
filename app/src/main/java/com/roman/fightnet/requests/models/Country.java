package com.roman.fightnet.requests.models;

import java.util.List;

import lombok.Data;

@Data
public class Country {
    private Object id;
    private String name;
    private List<City> cities;
}
