package com.fire.assignement.net;


import com.fire.assignement.Constants;
import com.fire.assignement.model.WeatherPrediction;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastService {

    @GET(Constants.GET_WEATHER_FORECAST)
    Observable<WeatherPrediction> getFiveDayForecast(@Query("lat") String lat, @Query("lon") String lon,
                                                     @Query("APPID") String appId);

}
