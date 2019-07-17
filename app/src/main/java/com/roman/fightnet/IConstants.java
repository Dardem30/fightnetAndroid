package com.roman.fightnet;


import com.roman.fightnet.util.LocalStorage;

import java.util.Arrays;
import java.util.List;

public interface IConstants {
    LocalStorage storage = new LocalStorage();
    List<String> fightStyles = Arrays.asList("Preferable fight style",
            "Aikido",
            "Boxing",
            "Muay Thai",
            "Greco-Roman wrestling",
            "Freestyle wrestling",
            "Taekwondo",
            "Jujutsu",
            "Krav Maga",
            "Wing Chun");
    List<String> chartColours = Arrays.asList("#C01B1B", "#4480BD", "#57BD44", "#DED830", "#5104B2", "#566375", "#E64A19", "#212121", "#e62727");

}
