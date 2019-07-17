package com.roman.fightnet.requests.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Wins {
    @SerializedName("Aikido")
    public int aikido;
    @SerializedName("Boxing")
    public int boxing;
    @SerializedName("Muay Thai")
    public int muayThai;
    @SerializedName("Greco-Roman wrestling")
    public int grecoRomanWrestling;
    @SerializedName("Freestyle wrestling")
    public int freestyleWrestling;
    @SerializedName("Taekwondo")
    public int taekwondo;
    @SerializedName("Jujutsu")
    public int jujutsu;
    @SerializedName("Krav Maga")
    public int kravMaga;
    @SerializedName("Wing Chun")
    public int wingChun;

}
