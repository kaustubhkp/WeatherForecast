package com.fire.assignement.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherPrediction {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("dt")
    @Expose
    public long dateAndTime;

    @SerializedName("clouds")
    @Expose
    public Clouds clouds;

    @SerializedName("wind")
    @Expose
    public Wind wind;

    @SerializedName("cod")
    @Expose
    public String cod;

    @SerializedName("visibility")
    @Expose
    public String visibility;

    @SerializedName("sys")
    @Expose
    public Sys sys;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("base")
    @Expose
    public String base;

    @SerializedName("weather")
    @Expose
    public java.util.List<Weather> weather;

    @SerializedName("main")
    @Expose
    public Main main;
}
