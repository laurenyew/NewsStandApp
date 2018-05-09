package laurenyew.newsstandapp.di.modules;

import dagger.Module;
import dagger.Provides;
import laurenyew.newsstandapp.adapters.ArticlePreviewRecyclerViewAdapter;
import laurenyew.newsstandapp.di.scope.FragmentScope;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Module Implementation
 * Provides Presenters and Adapter
 */
@Module
public class AdapterModule {
    @Provides
    @FragmentScope
    public ArticlePreviewRecyclerViewAdapter getArticlePreviewAdapter() {
        return new ArticlePreviewRecyclerViewAdapter();
    }
}