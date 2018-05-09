package laurenyew.newsstandapp;

import android.app.Application;

import laurenyew.newsstandapp.di.modules.AdapterModule;
import laurenyew.newsstandapp.di.AppComponent;
import laurenyew.newsstandapp.di.DaggerAppComponent;
import laurenyew.newsstandapp.di.FragmentComponent;
import laurenyew.newsstandapp.di.modules.NetworkModule;
import laurenyew.newsstandapp.di.modules.PresenterModule;

/**
 * @author Lauren Yew on 5/8/18.
 * Singleton Custom Application
 * - Sets up Dagger App Component which is used to access the network and app modules
 * - Used for its ApplicationContext
 */
public class NewsStandApplication extends Application {
    private static NewsStandApplication mInstance = null;
    private AppComponent mAppComponent = null;
    private FragmentComponent mFragmentComponent = null;

    public static NewsStandApplication getInstance() {
        return mInstance;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public FragmentComponent addFragmentComponent() {
        if (mFragmentComponent == null) {
            mFragmentComponent = getAppComponent().plus(new PresenterModule(), new AdapterModule());
        }
        return mFragmentComponent;
    }

    public void releaseFragmentComponent() {
        mFragmentComponent = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mAppComponent = DaggerAppComponent.builder()
                .networkModule(new NetworkModule())
                .build();
    }
}
