package com.roman.fightnet.requests.models.searchCriteria;

import lombok.Data;

@Data
public class MapSearchCriteria {
    private String name;
    private String fightStyle;
    private String startDate;
    private String endDate;
}