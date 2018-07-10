package com.fire.assignement.dagger;

import android.content.Context;

import com.fire.assignement.net.ForecastService;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fire.assignement.Constants.BASE_URL;
import static com.fire.assignement.Constants.CONNECTION_TIMEOUT;
import static com.fire.assignement.Constants.READ_TIMEOUT;


@Module
public class NetworkModule {

    @Provides
    @Singleton
    @Named("OkHttpCacheSize")
    public int provideOkHttpCacheSize() {
        return 5 * 1024 * 1024; //5 MB
    }

    @Provides
    @Singleton
    public Cache provideOkHttpCache(Context context, @Named("OkHttpCacheSize") int cacheSize) {
        return new Cache(context.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Cache cache) {
        return new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public ForecastService provideForecastService(Retrofit retrofit) {
        return retrofit.create(ForecastService.class);
    }
}
