package com.fire.assignement;

import android.app.Application;


import com.fire.assignement.dagger.AppComponent;
import com.fire.assignement.dagger.AppModule;
import com.fire.assignement.dagger.DaggerAppComponent;

import net.danlew.android.joda.JodaTimeAndroid;


public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                            .appModule(new AppModule(this))
                            .build();
        JodaTimeAndroid.init(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
