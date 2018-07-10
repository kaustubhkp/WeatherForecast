package com.fire.assignement.dagger;


import com.fire.assignement.net.ForecastService;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
    ForecastService forecastService();
}
