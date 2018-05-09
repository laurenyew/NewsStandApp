package laurenyew.newsstandapp.di;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.di.modules.AppModule;
import laurenyew.newsstandapp.di.modules.NetworkModule;
import laurenyew.newsstandapp.di.modules.PresenterModule;
import laurenyew.newsstandapp.helpers.AppTestBase;
import laurenyew.newsstandapp.presenters.ArticleBrowserPresenter;
import laurenyew.newsstandapp.presenters.ArticleDetailPresenter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FragmentComponentTest extends AppTestBase {
    private AppComponent component = null;
    private FragmentComponent fragmentComponent = null;

    @Override
    public void setup() {
        super.setup();
        component = DaggerAppComponent.builder()
                .networkModule(new NetworkModule())
                .appModule(new AppModule(context))
                .build();
        fragmentComponent = component.plus(new PresenterModule());
    }

    @Override
    public void tearDown() {
        component = null;
        fragmentComponent = null;
    }

    @Test
    public void testFragmentComponentBrowserGetters() {
        /** Exercise **/
        ArticleBrowserContract.Presenter browserPresenter = fragmentComponent.getArticleBrowserPresenter();
        ArticleDetailContract.Presenter detailPresenter = fragmentComponent.getArticleDetailPresenter();

        /** Verify **/
        assertNotNull(browserPresenter);
        assertTrue(browserPresenter instanceof ArticleBrowserPresenter);
        assertNotNull(detailPresenter);
        assertTrue(detailPresenter instanceof ArticleDetailPresenter);
    }
}
