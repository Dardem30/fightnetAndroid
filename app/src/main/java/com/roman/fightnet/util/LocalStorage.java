package com.roman.fightnet.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalStorage {
    private String token;
    private String email;
    private String facebookToken;
}
