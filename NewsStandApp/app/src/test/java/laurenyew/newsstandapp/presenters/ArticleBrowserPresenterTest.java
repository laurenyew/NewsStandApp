package laurenyew.newsstandapp.presenters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;
import laurenyew.newsstandapp.api.commands.SearchArticlesCommand;
import laurenyew.newsstandapp.api.model.ArticleData;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.helpers.AppTestBase;

import static laurenyew.newsstandapp.api.commands.SearchArticlesErrorCodes.SEARCH_API_RATE_EXCEEDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ArticleBrowserPresenterTest extends AppTestBase {
    private ArticleBrowserPresenter presenter = new ArticleBrowserPresenter();
    private ArticleBrowserContract.View mockView = Mockito.mock(ArticleBrowserContract.View.class);

    private ArrayList<ArticleData> fullData = new ArrayList<>();
    private ArrayList<ArticleData> dataWithoutTitle = new ArrayList<>();
    private ArrayList<ArticleData> dataWithoutTitleOrDescription = new ArrayList<>();

    @Override
    public void setup() {
        super.setup();
        presenter.onBind(mockView);
        ArticleData article = new ArticleData();
        article.id = "id";
        article.title = expectedHeadline;
        article.detailImageUrl = expectedLargeImgUrl;
        article.thumbnailImageUrl = expectedSmallImgUrl;
        article.webUrl = expectedWebUrl;
        article.description = expectedDescription;

        ArticleData articleWithoutTitle = new ArticleData();
        articleWithoutTitle.id = "id2";
        articleWithoutTitle.title = null;
        articleWithoutTitle.detailImageUrl = expectedLargeImgUrl;
        articleWithoutTitle.thumbnailImageUrl = expectedSmallImgUrl;
        articleWithoutTitle.webUrl = expectedWebUrl;
        articleWithoutTitle.description = expectedDescription;

        ArticleData articleWithoutTitleOrDescription = new ArticleData();
        articleWithoutTitleOrDescription.id = "id3";
        articleWithoutTitleOrDescription.title = null;
        articleWithoutTitleOrDescription.detailImageUrl = expectedLargeImgUrl;
        articleWithoutTitleOrDescription.thumbnailImageUrl = expectedSmallImgUrl;
        articleWithoutTitleOrDescription.webUrl = expectedWebUrl;
        articleWithoutTitleOrDescription.description = null;

        fullData.add(article);
        dataWithoutTitle.add(articleWithoutTitle);
        dataWithoutTitleOrDescription.add(articleWithoutTitleOrDescription);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        presenter.unBind();
    }

    /**
     * when onBind, presenter should set up the view reference and api key
     */
    @Test
    public void testOnBind() {
        /** Verify **/
        assertNotNull(presenter.getView());
        assertEquals(mockView, presenter.getView());
        assertEquals(context.getString(R.string.api_key), presenter.getApiKey());
        assertEquals(context.getString(R.string.article_title_unknown), presenter.getDefaultTitle());
    }

    /**
     * when unbind, presenter should teardown view reference and api key
     */
    @Test
    public void testUnbind() {
        /** Exercise **/
        presenter.unBind();

        /** Verify **/
        assertNull(presenter.getView());
        assertNull(presenter.getApiKey());
        assertNull(presenter.getDefaultTitle());
    }

    /**
     * when refresh with valid search term, should create search articles command
     */
    @Test
    public void testRefreshArticles() {
        /** Exercise **/
        presenter.refreshArticles("test");

        /** Verify **/
        assertNotNull(presenter.getCommand());
        assertTrue(presenter.getCommand() instanceof SearchArticlesCommand);
        assertEquals(0, presenter.getCurrentPageNum());
        assertEquals("test", presenter.getSearchTerm());
    }

    /**
     * when loadNextPageOfArticles with search term, command is search command
     */
    @Test
    public void testLoadNextPageOfArticles() {
        /** Arrange **/
        presenter.refreshArticles("test");

        /** Exercise **/
        presenter.loadNextPageOfArticles();

        /** Verify **/
        assertNotNull(presenter.getCommand());
        assertTrue(presenter.getCommand() instanceof SearchArticlesCommand);
        assertEquals(0, presenter.getCurrentPageNum());// page num not incremented until page comes back
        assertEquals("test", presenter.getSearchTerm());
    }

    /**
     * when presenter is requested to select a preview, presenter should update the view
     */
    @Test
    public void testOnSelectPreviewWithValidId() {
        /** Arrange **/
        presenter.onLoadArticlesSuccess(fullData, 0);
        /** Exercise **/
        presenter.onSelectPreview("id");

        /** Verify **/
        verify(mockView).onShowArticleDetail(expectedLargeImgUrl, expectedHeadline, expectedDescription, expectedWebUrl);
    }

    /**
     * when onLoadArticlesSuccess, current page num is updated, total num pages is updated, and view is updated
     */
    @Test
    public void testOnLoadArticlesSuccessFullData() {
        /** Exercise **/
        presenter.onLoadArticlesSuccess(fullData, 0);

        /** Verify **/
        assertEquals(0, presenter.getCurrentPageNum());
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).onArticlesLoaded(argumentCaptor.capture());

        List data = argumentCaptor.getValue();
        assertNotNull(data);
        assertEquals(1, data.size());
        ArticlePreviewDataWrapper preview = (ArticlePreviewDataWrapper) data.get(0);
        assertEquals("id", preview.id);
        assertEquals(expectedHeadline, preview.title);
        assertEquals(expectedSmallImgUrl, preview.imageUrl);
    }

    /**
     * when onLoadArticlesSuccess with article w/o title w/ description
     * puts description in place of title
     */
    @Test
    public void testOnLoadArticlesSuccessWithDataWithoutTitleWithDescription() {
        /** Exercise **/
        presenter.onLoadArticlesSuccess(dataWithoutTitle, 0);

        /** Verify **/
        assertEquals(0, presenter.getCurrentPageNum());
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).onArticlesLoaded(argumentCaptor.capture());

        List data = argumentCaptor.getValue();
        assertNotNull(data);
        assertEquals(1, data.size());
        ArticlePreviewDataWrapper preview = (ArticlePreviewDataWrapper) data.get(0);
        assertEquals("id2", preview.id);
        assertEquals(expectedDescription, preview.title);
        assertEquals(expectedSmallImgUrl, preview.imageUrl);
    }

    /**
     * when onLoadArticlesSuccess with article w/o title w/o description
     * uses default title
     */
    @Test
    public void testOnLoadArticlesSuccessWithDataWithoutTitleWithOutDescription() {
        /** Exercise **/
        presenter.onLoadArticlesSuccess(dataWithoutTitleOrDescription, 0);

        /** Verify **/
        assertEquals(0, presenter.getCurrentPageNum());
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).onArticlesLoaded(argumentCaptor.capture());

        List data = argumentCaptor.getValue();
        assertNotNull(data);
        assertEquals(1, data.size());
        ArticlePreviewDataWrapper preview = (ArticlePreviewDataWrapper) data.get(0);
        assertEquals("id3", preview.id);
        assertEquals(presenter.getDefaultTitle(), preview.title);
        assertEquals(expectedSmallImgUrl, preview.imageUrl);
    }

    /**
     * when onLoadArticlesSuccess with null data,view is updated with empty list
     */
    @Test
    public void onLoadArticlesSuccessWithEmptyData() {
        /** Exercise **/
        presenter.onLoadArticlesSuccess(new ArrayList<>(), 1);

        /** Verify **/
        assertEquals(1, presenter.getCurrentPageNum());
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).onArticlesLoaded(argumentCaptor.capture());

        List data = argumentCaptor.getValue();
        assertNotNull(data);
        assertEquals(0, data.size());
    }

    /**
     * when onLoadArticlesSuccess with paged data, view is updated with appended list
     */
    @Test
    public void testOnLoadArticlesSuccessWIthPagedData() {
        /** Exercise **/
        presenter.onLoadArticlesSuccess(fullData, 0);
        presenter.onLoadArticlesSuccess(dataWithoutTitle, 1);

        /** Verify **/
        assertEquals(1, presenter.getCurrentPageNum());
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView, times(2)).onArticlesLoaded(argumentCaptor.capture());

        List data = argumentCaptor.getValue();
        assertNotNull(data);
        assertEquals(2, data.size());
    }

    /**
     * when onLoadArticlesSuccess with first page, view is updated with refreshed list
     */
    @Test
    public void testOnLoadArticlesSuccessWithFirstPage() {
        /** Exercise **/
        presenter.onLoadArticlesSuccess(fullData, 0);
        presenter.onLoadArticlesSuccess(dataWithoutTitle, 1);
        presenter.onLoadArticlesSuccess(dataWithoutTitleOrDescription, 0);

        /** Verify **/
        assertEquals(0, presenter.getCurrentPageNum());
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView, times(3)).onArticlesLoaded(argumentCaptor.capture());

        List data = argumentCaptor.getValue();
        assertNotNull(data);
        assertEquals(1, data.size());
    }

    /**
     * when onLoadArticlesFailure, data is left intact, view is updated to show failure
     */
    @Test
    public void testOnLoadArticlesFailure() {
        /** Arrange **/
        presenter.onLoadArticlesSuccess(fullData, 0);

        /** Exercise **/
        presenter.onLoadArticlesFailure(SEARCH_API_RATE_EXCEEDED, null);

        /** Verify **/
        verify(mockView, atLeastOnce()).onArticlesLoaded(any());
        verify(mockView).onArticlesFailedToLoad(context.getString(R.string.article_browser_exceeded_api_limit));
    }

    /**
     * when refresh after loading next page, reset current page num
     */
    @Test
    public void testRefreshAfterLoadingNextPage() {
        /** Arrange **/
        presenter.refreshArticles("test");
        presenter.onLoadArticlesSuccess(fullData, 0);
        presenter.loadNextPageOfArticles();
        presenter.onLoadArticlesSuccess(dataWithoutTitle, 1);
        assertEquals(1, presenter.getCurrentPageNum());

        /** Exercise **/
        presenter.refreshArticles("test2");

        /** Verify **/
        assertNotNull(presenter.getCommand());
        assertTrue(presenter.getCommand() instanceof SearchArticlesCommand);
        assertEquals(0, presenter.getCurrentPageNum());
        assertEquals("test2", presenter.getSearchTerm());
    }
}