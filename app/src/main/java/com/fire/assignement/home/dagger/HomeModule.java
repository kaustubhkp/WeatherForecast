package com.fire.assignement.home.dagger;


import android.content.Context;

import com.fire.assignement.home.mvp.HomeContract;
import com.fire.assignement.home.mvp.HomePresenter;
import com.fire.assignement.net.ForecastService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    private final HomeContract.View homeView;
    private final Context mContext;
    private final FusedLocationProviderClient mFusedLocationClient;

    public HomeModule(Context context, HomeContract.View homeView) {
        this.homeView = homeView;
        this.mContext = context;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Provides
    @HomeScope
    public HomeContract.Presenter provideHomePresenter(ForecastService forecastService) {
        return new HomePresenter(mContext, forecastService, homeView, mFusedLocationClient);
    }
}
