package laurenyew.newsstandapp.helpers;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import laurenyew.newsstandapp.NewsStandApplication;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.di.AppComponent;
import laurenyew.newsstandapp.di.FragmentComponent;

/**
 * @author Lauren Yew on 5/9/18.
 * Test base class with reusable fields / setup
 */
public class AppTestBase {
    public Context context = Mockito.spy(RuntimeEnvironment.application.getApplicationContext());
    public NewsStandApplication application;
    public AppComponent mockAppComponent;
    public FragmentComponent mockFragmentComponent;

    public ArticleBrowserContract.Presenter mockBrowserPresenter = Mockito.mock(ArticleBrowserContract.Presenter.class);
    public ArticleDetailContract.Presenter mockDetailPresenter = Mockito.mock(ArticleDetailContract.Presenter.class);

    public String expectedLargeImgUrl = "LargeImgUrl";
    public String expectedSmallImgUrl = "SmallImgUrl";
    public String expectedHeadline = "Test Headline";
    public String expectedId = "TestId";
    public String expectedDescription = "Test Description";
    public String expectedWebUrl = "www.google.com";

    @Before
    public void setup() {
        application = new NewsStandApplication();
        application.setInstance(application);
        application = NewsStandApplication.getInstance();

        mockAppComponent = Mockito.mock(AppComponent.class);
        mockFragmentComponent = Mockito.mock(FragmentComponent.class);
        application.setAppComponent(mockAppComponent);
        application.setFragmentComponent(mockFragmentComponent);

        Mockito.when(mockAppComponent.plus(Mockito.any())).thenReturn(mockFragmentComponent);
        Mockito.when(mockFragmentComponent.getArticleBrowserPresenter()).thenReturn(mockBrowserPresenter);
        Mockito.when(mockFragmentComponent.getArticleDetailPresenter()).thenReturn(mockDetailPresenter);
        Mockito.when(mockAppComponent.getContext()).thenReturn(context);
    }

    @After
    public void tearDown() {
    }
}
