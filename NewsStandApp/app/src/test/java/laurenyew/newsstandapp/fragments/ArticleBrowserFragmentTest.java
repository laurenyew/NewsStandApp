package laurenyew.newsstandapp.fragments;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;

import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.activities.ArticleBrowserActivity;
import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;
import laurenyew.newsstandapp.helpers.AppTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startVisibleFragment;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
public class ArticleBrowserFragmentTest extends AppTestBase {
    private ArticleBrowserFragment fragment = null;
    private SwipeRefreshLayout pullRefreshLayout = null;
    private RecyclerView recyclerView = null;
    private TextView emptyTextView = null;

    @Override
    public void setup() {
        super.setup();
        //Setup up Picasso
        try {
            Picasso.get();
        } catch (IllegalStateException e) {
            Picasso.Builder builder = new Picasso.Builder(context);
            Picasso.setSingletonInstance(builder.build());
        }
        fragment = startTestFragment();
        setupViews(fragment.getView());
    }

    /**
     * On view created, presenter should be bound and refreshed
     */
    @Test
    public void testOnViewCreatedBindPresenter() {
        /** Verify **/
        verify(mockBrowserPresenter).onBind(fragment);
        verify(mockBrowserPresenter, atLeastOnce()).refreshArticles(null);
    }

    /**
     * On view destroyed, presenter should be unbound
     */
    @Test
    public void testOnViewDestroyedUnbindPresenter() {
        /** Exercise **/
        fragment.onDestroyView();

        /** Verify **/
        verify(mockBrowserPresenter).unBind();
    }

    /**
     * On view pulled to refresh, presenter should be refreshed
     */
    @Test
    public void testPullToRefresh() {
        /** Exercise **/
        fragment.onRefresh();

        /** Verify **/
        verify(mockBrowserPresenter, atLeast(2)).refreshArticles(any());
    }

    /**
     * On view created, pull to refresh view should show
     */
    @Test
    public void onViewCreatedViewsSetup() {
        /** Verify **/
        assertEquals(View.VISIBLE, pullRefreshLayout.getVisibility());
        assertTrue(pullRefreshLayout.isRefreshing());
        assertEquals(View.VISIBLE, recyclerView.getVisibility());
        assertEquals(View.GONE, emptyTextView.getVisibility());
    }


    /**
     * When articles loaded into view first time in regular mode, recycler view updated, pull to refresh hidden, empty text hidden
     */
    @Test
    public void whenArticlesLoadedInRegularView() {
        /** Arrange **/
        ArrayList<ArticlePreviewDataWrapper> data = new ArrayList<>();
        data.add(new ArticlePreviewDataWrapper("id", "title", "url"));
        fragment.mIsRunningTwoPaneMode = false;

        /** Exercise **/
        fragment.onArticlesLoaded(data);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        setupViews(fragment.getView());

        /** Verify **/
        assertEquals(View.VISIBLE, pullRefreshLayout.getVisibility());
        assertFalse(pullRefreshLayout.isRefreshing());
        assertEquals(View.VISIBLE, recyclerView.getVisibility());
        assertEquals(View.GONE, emptyTextView.getVisibility());
        verify(mockBrowserPresenter, never()).onSelectPreview(any());
    }


    /**
     * when articles loaded into view first time in two pane mode, recycler view updated, pull to refresh hidden, empty text hidden
     */
    @Test
    public void testArticlesLoadedInTwoPaneViewFirstTime() {
        /** Arrange **/
        ArrayList<ArticlePreviewDataWrapper> data = new ArrayList<>();
        data.add(new ArticlePreviewDataWrapper("id", "title", "url"));
        fragment.mIsRunningTwoPaneMode = true;

        /** Exercise **/
        fragment.onRefresh();
        fragment.onArticlesLoaded(data);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        setupViews(fragment.getView());

        /** Verify **/
        assertEquals(View.VISIBLE, pullRefreshLayout.getVisibility());
        assertFalse(pullRefreshLayout.isRefreshing());
        assertEquals(View.VISIBLE, recyclerView.getVisibility());
        assertEquals(View.GONE, emptyTextView.getVisibility());
        verify(mockBrowserPresenter).onSelectPreview("id");
    }

    /**
     * when articles loaded into view after first time in two pane mode, recycler view updated, pull to refresh hidden, empty text hidden
     */
    @Test
    public void testArticlesLoadedInTwoPaneViewAfterFirstTime() {
        /** Arrange **/
        ArrayList<ArticlePreviewDataWrapper> data = new ArrayList<>();
        data.add(new ArticlePreviewDataWrapper("id", "title", "url"));
        fragment.mIsRunningTwoPaneMode = true;
        fragment.mShouldShowFirstItem = false;

        /** Exercise **/
        fragment.onArticlesLoaded(data);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        setupViews(fragment.getView());

        /** Verify **/
        assertEquals(View.VISIBLE, pullRefreshLayout.getVisibility());
        assertFalse(pullRefreshLayout.isRefreshing());
        assertEquals(View.VISIBLE, recyclerView.getVisibility());
        assertNotNull(recyclerView.getAdapter());
        assertEquals(View.GONE, emptyTextView.getVisibility());
        verify(mockBrowserPresenter, never()).onSelectPreview("id");
    }

    /**
     * when no articles loaded into view, recycler view updated, pull to refresh hidden, empty text should show
     */
    @Test
    public void testNoArticlesLoaded() {
        /** Arrange **/
        ArrayList<ArticlePreviewDataWrapper> data = new ArrayList<>();

        /** Exercise **/
        fragment.onArticlesLoaded(data);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        setupViews(fragment.getView());

        /** Verify **/
        assertEquals(View.VISIBLE, pullRefreshLayout.getVisibility());
        assertFalse(pullRefreshLayout.isRefreshing());
        assertEquals(View.VISIBLE, recyclerView.getVisibility());
        assertEquals(View.VISIBLE, emptyTextView.getVisibility());
    }


    /**
     * when article load fails, empty view should show
     */
    @Test
    public void testArticlesFailedToLoad() {
        /** Arrange **/
        String expectedErrorMessage = context.getString(R.string.article_browser_error_call_failed);
        /** Exercise **/
        fragment.onArticlesFailedToLoad(expectedErrorMessage);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        /** Verify **/
        assertEquals(expectedErrorMessage, ShadowToast.getTextOfLatestToast().toString());
    }

    /**
     * when article load checks and finds internet connected,
     * proper toast is shown
     */
    @Test
    public void testInternetNotAvailable() {
        /** Arrange **/
        String expectedErrorMessage = context.getString(R.string.article_browser_no_internet);

        /** Exercise **/
        fragment.onInternetNotAvailable();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        /** Verify **/
        assertEquals(expectedErrorMessage, ShadowToast.getTextOfLatestToast().toString());
    }

    //region Helper Methods
    private ArticleBrowserFragment startTestFragment() {
        ArticleBrowserFragment fragment = ArticleBrowserFragment.newInstance();
        startVisibleFragment(fragment, ArticleBrowserActivity.class, R.id.articleBrowserFrameLayout);
        return fragment;
    }

    private void setupViews(View view) {
        pullRefreshLayout = view.findViewById(R.id.articleBrowserSwipeRefreshLayout);
        recyclerView = view.findViewById(R.id.articleBrowserRecyclerView);
        emptyTextView = view.findViewById(R.id.articleBrowserEmptyTextView);
    }
    //endregion
}