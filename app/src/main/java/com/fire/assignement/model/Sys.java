package com.fire.assignement.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {
    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("sunset")
    @Expose
    public long sunset;

    @SerializedName("sunrise")
    @Expose
    public long sunrise;

    @SerializedName("country")
    @Expose
    public String country;
}
