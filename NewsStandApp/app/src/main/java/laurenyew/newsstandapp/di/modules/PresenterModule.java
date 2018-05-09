package laurenyew.newsstandapp.di.modules;

import dagger.Module;
import dagger.Provides;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.di.scope.FragmentScope;
import laurenyew.newsstandapp.presenters.ArticleBrowserPresenter;
import laurenyew.newsstandapp.presenters.ArticleDetailPresenter;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Module Implementation
 * Provides Presenters and Adapter
 */
@Module
public class PresenterModule {
    @Provides
    @FragmentScope
    public ArticleBrowserContract.Presenter getArticleBrowserPresenter() {
        return new ArticleBrowserPresenter();
    }

    @Provides
    @FragmentScope
    public ArticleDetailContract.Presenter getArticleDetailPresenter() {
        return new ArticleDetailPresenter();
    }
}