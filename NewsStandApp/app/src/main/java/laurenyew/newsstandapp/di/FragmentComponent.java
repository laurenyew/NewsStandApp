package laurenyew.newsstandapp.di;

import dagger.Subcomponent;
import laurenyew.newsstandapp.adapters.ArticlePreviewRecyclerViewAdapter;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.di.modules.AdapterModule;
import laurenyew.newsstandapp.di.modules.PresenterModule;
import laurenyew.newsstandapp.di.scope.FragmentScope;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Component interface (Dagger2 inflates with appropriate singletons provided)
 */
@FragmentScope
@Subcomponent(modules = {PresenterModule.class, AdapterModule.class})
public interface FragmentComponent {
    //inject the adapter so we can use @Inject there
    void inject(ArticlePreviewRecyclerViewAdapter adapter);

    //region Adapters
    ArticlePreviewRecyclerViewAdapter getArticlePreviewAdapter();
    //endregion

    //region Presenters
    ArticleBrowserContract.Presenter getArticleBrowserPresenter();

    ArticleDetailContract.Presenter getArticleDetailPresenter();
    //endregion
}
