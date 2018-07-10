package com.fire.assignement.home.mvp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.fire.assignement.BuildConfig;
import com.fire.assignement.R;
import com.fire.assignement.Utils;
import com.fire.assignement.model.WeatherPrediction;
import com.fire.assignement.net.ForecastService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.LOCATION_SERVICE;

public class HomePresenter implements HomeContract.Presenter, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final String TAG = HomePresenter.class.getSimpleName();

    private final ForecastService forecastService;
    private final CompositeDisposable compositeDisposable;
    private final HomeContract.View view;
    private final Context mContext;
    private final FusedLocationProviderClient mFusedLocationClient;

    static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private GoogleApiClient mGoogleApiClient;

    public HomePresenter(Context context, ForecastService forecastService, HomeContract.View view,
                         FusedLocationProviderClient fusedLocationProviderClient) {
        this.mContext = context;
        this.forecastService = forecastService;
        this.view = view;
        this.mFusedLocationClient = fusedLocationProviderClient;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getForecast(Double latitude, Double longitude) {
        Observable<WeatherPrediction> observable = getForecastFromAPI(latitude, longitude);
        compositeDisposable.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(weatherPrediction -> view.setActivityTitle(weatherPrediction.name))
                .doOnSubscribe(disposable -> view.showProgress(true))
                .doOnTerminate(() -> view.showProgress(false))
                .subscribe(this::handleResult, view::showError));
        }

    private Observable<WeatherPrediction> getForecastFromAPI(Double latitude, Double longitude) {
        return forecastService.getFiveDayForecast(String.valueOf(latitude), String.valueOf(longitude), BuildConfig.ApiKey);
    }

    private void handleResult(WeatherPrediction weatherPrediction) {
        if (weatherPrediction.cod == null) {
            view.showTryAgain(true);
        } else {
            view.showTryAgain(false);
            view.showForecast(weatherPrediction);
        }
    }

    @Override
    public void start() {
        if (!Utils.isOnline(mContext)) {
            view.showTryAgain(true);
            return;
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        view.showProgress(true);

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @Override
    public void stop() {
        compositeDisposable.clear();
        disconnectGoogleClient();
    }

    private void disconnectGoogleClient() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions((HomeActivity)mContext,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale((HomeActivity)mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        view.showProgress(false);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            view.showSnackBar(R.string.permission_rationale, android.R.string.ok,
                    view -> {
                        // Request permission
                        startLocationPermissionRequest();
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(mContext))
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener((HomeActivity)mContext, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location result = task.getResult();
                        if (result != null) {
                            getForecast(result.getLatitude(), result.getLongitude());
                        }

                    } else {
                        LocationManager service = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
                        boolean enabled = service != null && service.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (!enabled) {
                            view.showSnackBar(R.string.permission_denied_explanation, R.string.settings,
                                    v -> {
                                        // Build intent that displays the App settings screen.
                                        Intent intent = new Intent();
                                        intent.setAction(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                    });
                        } else {
                            // Location service is enabled but in some cases (Android API 8.0.0) wont be able to get lastLocation from the
                            // fusedLocation API so we create a client and ask for location updates.
                            buildGoogleApiClient();
                            if (!mGoogleApiClient.isConnected()) {
                                mGoogleApiClient.connect();
                            }
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000) // Update location every second
                .setFastestInterval(1000);
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            getForecast(location.getLatitude(), location.getLongitude());
            disconnectGoogleClient();
        }
    }
}
