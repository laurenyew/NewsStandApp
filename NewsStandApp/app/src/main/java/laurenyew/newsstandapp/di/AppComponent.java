package laurenyew.newsstandapp.di;

import javax.inject.Singleton;

import dagger.Component;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import laurenyew.newsstandapp.di.modules.AdapterModule;
import laurenyew.newsstandapp.di.modules.NetworkModule;
import laurenyew.newsstandapp.di.modules.PresenterModule;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Component interface (Dagger2 inflates with appropriate singletons provided)
 */
@Singleton
@Component(modules = {NetworkModule.class})
public interface AppComponent {
    //region Networking
    NYTimesArticleApi getNYTimesArticleApi();
    //endregion

    //region SubComponents
    FragmentComponent plus(PresenterModule presenterModule, AdapterModule adapterModule);
    //endregion
}