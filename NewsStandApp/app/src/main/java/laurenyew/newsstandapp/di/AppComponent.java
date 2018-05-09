package laurenyew.newsstandapp.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import laurenyew.newsstandapp.di.modules.AppModule;
import laurenyew.newsstandapp.di.modules.NetworkModule;
import laurenyew.newsstandapp.di.modules.PresenterModule;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Component interface (Dagger2 inflates with appropriate singletons provided)
 */
@Singleton
@Component(modules = {NetworkModule.class, AppModule.class})
public interface AppComponent {
    //region Context
    Context getContext();

    //endregion
    //region Networking
    NYTimesArticleApi getNYTimesArticleApi();
    //endregion

    //region SubComponents
    FragmentComponent plus(PresenterModule presenterModule);
    //endregion
}