package com.fire.assignement.home.mvp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fire.assignement.App;
import com.fire.assignement.BuildConfig;
import com.fire.assignement.Constants;
import com.fire.assignement.R;
import com.fire.assignement.Utils;
import com.fire.assignement.home.dagger.DaggerHomeComponent;
import com.fire.assignement.home.dagger.HomeModule;
import com.fire.assignement.model.WeatherPrediction;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fire.assignement.home.mvp.HomePresenter.REQUEST_PERMISSIONS_REQUEST_CODE;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    private static final String TAG = HomeActivity.class.getName();

    @BindView(R.id.relative_layout_forecast_view)
    RelativeLayout mRelativeLayoutForecastView;
    @BindView(R.id.pb_home_progress)
    ProgressBar progressBar;
    @BindView(R.id.tv_try_again)
    TextView tryAgain;

    @BindView(R.id.tv_current_date)
    TextView mTextViewCurrentDate;
    @BindView(R.id.tv_current_temperature)
    TextView mTextViewCurrentTemperature;
    @BindView(R.id.iv_current_weather_icon)
    ImageView mImageViewCurrentWeatherIcon;
    @BindView(R.id.tv_current_weather_condition)
    TextView mTextViewCurrentWeatherCondition;

    @BindView(R.id.tv_wind_info)
    TextView mTextViewWindInfo;
    @BindView(R.id.tv_humidity_info)
    TextView mTextViewHumidityInfo;
    @BindView(R.id.tv_temperature_info)
    TextView mTextViewTemperatureInfo;
    @BindView(R.id.tv_sunrise_sunset_info)
    TextView mTextViewSunriseSunsetInfo;
    @BindView(R.id.tv_pressure_info)
    TextView mTextViewPressureInfo;
    @BindView(R.id.tv_clouds_info)
    TextView mTextViewCloudsInfo;

    @Inject
    HomeContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DaggerHomeComponent.builder()
                .appComponent(((App)getApplication()).getAppComponent())
                .homeModule(new HomeModule(this, this))
                .build().inject(this);

        ButterKnife.bind(this);
    }


    @Override
    public void showSnackBar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        showProgress(false);
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                showProgress(true);
                presenter.getLastLocation();
            } else {
                // Permission denied.
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless.
                showSnackBar(R.string.permission_denied_explanation, R.string.settings,
                        view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }


    @Override
    public void showForecast(@NonNull WeatherPrediction weatherPrediction) {
        tryAgain.setVisibility(View.GONE);
        mRelativeLayoutForecastView.setVisibility(View.VISIBLE);

        mTextViewCurrentDate.setText(Utils.getDateForLocaleFromUtc(weatherPrediction.dateAndTime,
                Utils.DD_MM_YYY));

        mTextViewCurrentTemperature.setText(getResources().getString(R.string.current_temperature,
                weatherPrediction.main.temp));

        Picasso.get().load(Constants.ICON_BASE_URL + weatherPrediction.weather.get(0).icon
                + Constants.ICON_EXTENSION).into(mImageViewCurrentWeatherIcon);

        mTextViewCurrentWeatherCondition.setText(weatherPrediction.weather.get(0).description.toUpperCase());

        mTextViewWindInfo.setText(getResources().getString(R.string.wind_speed,
                Utils.roundDoubleToTwoDecimalPoints(weatherPrediction.wind.speed)));
        mTextViewHumidityInfo.setText(Utils.roundDoubleToTwoDecimalPoints(weatherPrediction.main.humidity));

        mTextViewTemperatureInfo.setText((getResources().getString(R.string.temperature_min_max,
                weatherPrediction.main.tempMin, weatherPrediction.main.tempMax)));

        mTextViewSunriseSunsetInfo.setText((getResources().getString(R.string.sunrise_sunset,
                Utils.getDateForLocaleFromUtc(weatherPrediction.sys.sunrise, Utils.HH_MM),
                Utils.getDateForLocaleFromUtc(weatherPrediction.sys.sunset, Utils.HH_MM))));

        mTextViewPressureInfo.setText(Utils.roundDoubleToTwoDecimalPoints(weatherPrediction.main.pressure));
        mTextViewCloudsInfo.setText(getResources().getString(R.string.cloud_percentage, weatherPrediction.clouds.all));
    }

    @Override
    public void showError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean shouldShow) {
        progressBar.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showTryAgain(boolean shouldShow) {
        if (shouldShow) {
            mRelativeLayoutForecastView.setVisibility(View.GONE);
            tryAgain.setVisibility(View.VISIBLE);
        } else {
            tryAgain.setVisibility(View.GONE);
            mRelativeLayoutForecastView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setActivityTitle(String title) {
        setTitle(title);
    }

    @OnClick(R.id.tv_try_again)
    public void onTryAgainClicked() {
        presenter.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                presenter.start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
