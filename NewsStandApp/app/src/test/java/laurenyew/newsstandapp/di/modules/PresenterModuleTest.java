package laurenyew.newsstandapp.di.modules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.presenters.ArticleBrowserPresenter;
import laurenyew.newsstandapp.presenters.ArticleDetailPresenter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PresenterModuleTest {
    private PresenterModule module = new PresenterModule();

    @Test
    public void testGetBrowserPresenterReturnsProperPresenter() {
        /** Exercise **/
        ArticleBrowserContract.Presenter presenter = module.getArticleBrowserPresenter();

        /** Verify **/
        assertNotNull(presenter);
        assertTrue(presenter instanceof ArticleBrowserPresenter);
    }

    @Test
    public void testGetDetailPresenterReturnsProperPresenter() {
        /** Exercise **/
        ArticleDetailContract.Presenter presenter = module.getArticleDetailPresenter();

        /** Verify **/
        assertNotNull(presenter);
        assertTrue(presenter instanceof ArticleDetailPresenter);
    }
}