package laurenyew.newsstandapp.di;

import javax.inject.Singleton;

import dagger.Component;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Component interface (Dagger2 inflates with appropriate singletons provided)
 */
@Singleton
@Component(modules = {NetworkModule.class, AppModule.class})
public interface NewsStandAppComponent {
    //region Networking
    NYTimesArticleApi getNYTimesArticleApi();
    //endregion

    //region Presenters
    ArticleBrowserContract.Presenter getArticleBrowserPresenter();

    ArticleDetailContract.Presenter getArticleDetailPresenter();
    //endregion
}