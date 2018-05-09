package laurenyew.newsstandapp.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import laurenyew.newsstandapp.NewsStandApplication;
import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.activities.ArticleDetailActivity;
import laurenyew.newsstandapp.adapters.ArticlePreviewRecyclerViewAdapter;
import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;

/**
 * @author Lauren Yew on 5/8/18.
 * <p>
 * Main view logic for Article Browser
 * <p>
 * Features:
 * - RecyclerView
 * - Master Detail Fragment View
 * - Swipe Refresh Layout
 * - Search View
 */
public class ArticleBrowserFragment extends Fragment implements ArticleBrowserContract.View, SwipeRefreshLayout.OnRefreshListener {
    public static long DEFAULT_SEARCH_DELAY = 500L; //Wait for .5 secs (avg user typing time is .3-1 sec per character)

    public static ArticleBrowserFragment newInstance() {
        ArticleBrowserFragment fragment = new ArticleBrowserFragment();
        return fragment;
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    @VisibleForTesting
    boolean mIsRunningTwoPaneMode = false;
    @VisibleForTesting
    boolean mShouldShowFirstItem = true;
    //don't allow more than one load call at once. Hitting NYTimes api call limit
    //so have this here to help try to mitigate
    @VisibleForTesting
    boolean mIsLoadingData = false;
    private String mSearchTerm = null;

    private MenuItem mSearchMenuItem = null;

    //Views
    private RecyclerView mRecyclerView = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private LinearLayoutManager mLayoutManager = null;
    private TextView mEmptyTextView = null;
    private View mArticleDetailContainer = null;

    private SearchView mSearchView = null;
    private Timer mSearchQueryKeyEntryTimer = null;

    private ArticlePreviewRecyclerViewAdapter mAdapter = null;
    protected ArticleBrowserContract.Presenter mPresenter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = NewsStandApplication.getInstance().getAppComponent().getArticleBrowserPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.article_browser_fragment, container, false);
        mRecyclerView = rootView.findViewById(R.id.articleBrowserRecyclerView);
        mSwipeRefreshLayout = rootView.findViewById(R.id.articleBrowserSwipeRefreshLayout);
        mEmptyTextView = rootView.findViewById(R.id.articleBrowserEmptyTextView);
        mArticleDetailContainer = rootView.findViewById(R.id.articleDetailContainer);
        return rootView;
    }

    @Override
    public void onViewCreated(final @NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mRecyclerView != null) {
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int directionX, int directionY) {
                    //Scrolling down
                    if (mLayoutManager != null && directionY > 0) {
                        int visibleItemCount = mLayoutManager.getChildCount();
                        int totalItemCount = mLayoutManager.getItemCount();
                        int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        //Pre-Load more data
                        //TODO: Infinite scroll with fast paging
                        if (mPresenter != null && !mIsLoadingData && ((visibleItemCount + pastVisibleItems) >= (totalItemCount - visibleItemCount))) {
                            mIsLoadingData = true;
                            mPresenter.loadNextPageOfArticles();
                        }
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //Hide the search keyboard if you start scrolling the list
                    if (newState == SCROLL_STATE_DRAGGING && !mSearchView.isIconified()) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
        }

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // The detail container view will be present only in the
        // large-screen layouts (res/values-w900dp).
        // If this view is present, then the
        // activity should be in two-pane mode.
        if (mArticleDetailContainer != null) {
            mIsRunningTwoPaneMode = true;
        }

        //Start loading the data
        if (mPresenter != null) {
            mPresenter.onBind(this);
        }
        onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.unBind();
        }

        if (mSearchQueryKeyEntryTimer != null) {
            mSearchQueryKeyEntryTimer.cancel();
            mSearchQueryKeyEntryTimer = null;
        }

        mSearchMenuItem = null;
        mRecyclerView = null;
        mSwipeRefreshLayout = null;
        mLayoutManager = null;
        mEmptyTextView = null;
        mArticleDetailContainer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.article_browser_menu, menu);
        mSearchMenuItem = menu.findItem(R.id.menu_search);

        //Setup Search View
        View menuView = mSearchMenuItem.getActionView();
        if (menuView != null && menuView instanceof SearchView) {
            mSearchView = (SearchView) menuView;
            mSearchView.setQueryHint(getString(R.string.search_hint));

            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            if (searchManager != null) {
                mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            }

            //Customizable query text listener
            mSearchView.setOnQueryTextListener(getQueryTextListener());

            //Setup hide / show search view
            mSearchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    if (mSearchMenuItem != null) {
                        mSearchMenuItem.collapseActionView();
                    }
                } else {
                    if (mSearchView != null) {
                        mSearchView.setIconified(true);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //region MVP

    /**
     * Load the articles into the view
     */
    @Override
    public void onArticlesLoaded(List<ArticlePreviewDataWrapper> data) {
        mIsLoadingData = false;
        if (isAdded() && isVisible()) {
            if (mAdapter == null) {
                mAdapter = new ArticlePreviewRecyclerViewAdapter(mPresenter);
                mRecyclerView.setAdapter(mAdapter);
            }
            mAdapter.updateData(data);
            mSwipeRefreshLayout.setRefreshing(false);

            if (data == null || data.isEmpty()) {
                mEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyTextView.setVisibility(View.GONE);

                //If we are in two pane mode and we have just opened the browser,
                //load the first detail
                if (mShouldShowFirstItem && mPresenter != null) {
                    //if this view has just been refreshed, we should
                    //show the first item
                    ArticlePreviewDataWrapper firstItem = data.get(0);
                    mPresenter.onSelectPreview(firstItem.id);
                    mShouldShowFirstItem = false;
                }
            }
        }
    }

    /**
     * Show a toast message on why the articles failed to load
     */
    @Override
    public void onArticlesFailedToLoad(String message) {
        if (isAdded() && isVisible()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show the internet not available message
     */
    @Override
    public void onInternetNotAvailable() {
        if (isAdded() && isVisible()) {
            Toast.makeText(getContext(), R.string.article_browser_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Depending on if we're running in two pane mode (Tablet in landscape or really large screen),
     * show the detail in master-detail form on the same page, or start up a new detail activity
     */
    @Override
    public void onShowArticleDetail(String itemImageUrl, String itemTitle, String
            itemDescription, String itemWebUrl) {
        if (isAdded() && isVisible()) {
            if (mIsRunningTwoPaneMode && mArticleDetailContainer != null) {
                ArticleDetailContract.View detailView = ArticleDetailFragment.newInstance(itemImageUrl, itemTitle, itemDescription, itemWebUrl);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                FragmentManager supportFragmentManger = activity != null ? activity.getSupportFragmentManager() : null;
                if (supportFragmentManger != null) {
                    supportFragmentManger.beginTransaction()
                            .replace(R.id.articleDetailContainer, (Fragment) detailView)
                            .commit();
                }
            } else {
                Context context = getContext();
                if (context != null) {
                    Intent intent = ArticleDetailActivity.newInstance(getContext(), itemImageUrl, itemTitle, itemDescription, itemWebUrl);
                    context.startActivity(intent);
                }
            }
        }
    }
    //endregion

    //region SwipeRefreshLayout

    /**
     * SwipeRefreshLayout callback when refreshing
     */
    @Override
    public void onRefresh() {
        mIsLoadingData = true;
        mShouldShowFirstItem = mIsRunningTwoPaneMode;
        if (mPresenter != null) {
            mPresenter.refreshArticles(mSearchTerm);
        }
    }
    //endregion

    //region Helper Methods

    /**
     * Available to override if you don't like the query behavior
     * Default behavior is to have a timer that will go off when the user stops
     * typing and then perform the search. This way search is quick and dynamic and
     * user doesn't have to press enter to search, but the search won't be happening
     * as the user is typing.
     */
    public SearchView.OnQueryTextListener getQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                //Only search after the user has finished/slowed typing to avoid quick typing
                //and running multiple searches
                if (mSearchQueryKeyEntryTimer != null) {
                    mSearchQueryKeyEntryTimer.cancel();
                }
                mSearchQueryKeyEntryTimer = new Timer();
                mSearchQueryKeyEntryTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(() -> {
                                if (isAdded() && isVisible()) {
                                    mSearchTerm = !TextUtils.isEmpty(newText) ? newText : null;
                                    onRefresh();
                                }
                            });
                        }
                    }
                }, DEFAULT_SEARCH_DELAY);
                return true;
            }

        };
    }
    //endregion
}