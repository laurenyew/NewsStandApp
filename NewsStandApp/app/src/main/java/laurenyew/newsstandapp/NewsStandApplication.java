package laurenyew.newsstandapp;

import android.app.Application;

import laurenyew.newsstandapp.di.DaggerNewsStandAppComponent;
import laurenyew.newsstandapp.di.NetworkModule;
import laurenyew.newsstandapp.di.NewsStandAppComponent;

public class NewsStandApplication extends Application {
    private static NewsStandApplication mInstance = null;
    protected NewsStandAppComponent mAppComponent = null;

    public static NewsStandApplication getInstance() {
        return mInstance;
    }

    public NewsStandAppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mAppComponent = DaggerNewsStandAppComponent.builder().networkModule(new NetworkModule()).build();
    }
}
