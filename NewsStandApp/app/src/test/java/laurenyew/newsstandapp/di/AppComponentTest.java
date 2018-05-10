package laurenyew.newsstandapp.di;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import laurenyew.newsstandapp.di.modules.AppModule;
import laurenyew.newsstandapp.di.modules.NetworkModule;
import laurenyew.newsstandapp.di.modules.PresenterModule;
import laurenyew.newsstandapp.helpers.AppTestBase;

import static org.junit.Assert.assertNotNull;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppComponentTest extends AppTestBase {
    private AppComponent component = null;

    @Before
    public void setup() {
        component = DaggerAppComponent.builder()
                .networkModule(new NetworkModule())
                .appModule(new AppModule(context))
                .build();
    }

    @After
    public void teardown() {
        component = null;
    }

    @Test
    public void testGetNYTimesArticleApiReturnsProperApi() {
        /** Exercise **/
        NYTimesArticleApi api = component.getNYTimesArticleApi();

        /** Verify **/
        assertNotNull(api);
    }


    @Test
    public void testAddSubComponentFragmentModuleShouldAllowFragmentModuleToBeRetrieved() {
        /** Exercise **/
        FragmentComponent fragmentComponent = component.plus(new PresenterModule());

        /** Verify **/
        assertNotNull(fragmentComponent);
        assertNotNull(fragmentComponent.getArticleDetailPresenter());
        assertNotNull(fragmentComponent.getArticleDetailPresenter());
    }
}
