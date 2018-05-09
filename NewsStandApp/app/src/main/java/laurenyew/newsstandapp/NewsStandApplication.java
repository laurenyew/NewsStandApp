package laurenyew.newsstandapp;

import android.app.Application;

import laurenyew.newsstandapp.di.AppModule;
import laurenyew.newsstandapp.di.DaggerNewsStandAppComponent;
import laurenyew.newsstandapp.di.NetworkModule;
import laurenyew.newsstandapp.di.NewsStandAppComponent;

/**
 * @author Lauren Yew on 5/8/18.
 * Singleton Custom Application
 * - Sets up Dagger App Component which is used to access the network and app modules
 * - Used for its ApplicationContext
 */
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
        mAppComponent = DaggerNewsStandAppComponent.builder()
                .networkModule(new NetworkModule())
                .appModule(new AppModule()).build();
    }
}
