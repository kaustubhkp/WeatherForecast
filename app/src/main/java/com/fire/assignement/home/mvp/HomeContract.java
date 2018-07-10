package com.fire.assignement.home.mvp;

import android.support.annotation.NonNull;

import com.fire.assignement.model.WeatherPrediction;


public interface HomeContract {

    interface View {
        void showForecast(@NonNull WeatherPrediction weatherPrediction);
        void showError(Throwable throwable);
        void showProgress(boolean shouldShow);
        void showTryAgain(boolean shouldShow);
        void setActivityTitle(String title);
        void showSnackBar(final int mainTextStringId, final int actionStringId,
                                  android.view.View.OnClickListener listener);
    }

    interface Presenter {
        void getForecast(Double latitude, Double longitude);
        void getLastLocation();
        void start();
        void stop();
    }
}
