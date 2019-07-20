package com.roman.fightnet.requests.models;

import java.util.List;

import lombok.Data;

@Data
public class SearchResponse<T> {
    private List<T> records;
    private int count;
}
