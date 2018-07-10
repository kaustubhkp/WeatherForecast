package com.fire.assignement.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    @Expose
    public String temp;

    @SerializedName("temp_min")
    @Expose
    public String tempMin;

    @SerializedName("temp_max")
    @Expose
    public String tempMax;

    @SerializedName("pressure")
    @Expose
    public double pressure;

    @SerializedName("sea_level")
    @Expose
    public double seaLevel;

    @SerializedName("grnd_level")
    @Expose
    public double grndLevel;

    @SerializedName("humidity")
    @Expose
    public double humidity;

    @SerializedName("temp_kf")
    @Expose
    public double tempKf;
}
