package laurenyew.newsstandapp.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.presenters.ArticleBrowserPresenter;
import laurenyew.newsstandapp.presenters.ArticleDetailPresenter;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Module Implementation
 * Provides Presenters and Adapter
 */
@Module
public class AppModule {
    //region Presenters
    @Provides
    @Singleton
    public ArticleBrowserContract.Presenter getArticleBrowserPresenter() {
        return new ArticleBrowserPresenter();
    }

    @Provides
    @Singleton
    public ArticleDetailContract.Presenter getArticleDetailPresenter() {
        return new ArticleDetailPresenter();
    }
    //endregion
}