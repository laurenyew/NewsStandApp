package laurenyew.newsstandapp.presenters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.NewsStandApplication;
import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;
import laurenyew.newsstandapp.api.commands.AsyncJobCommand;
import laurenyew.newsstandapp.api.commands.SearchArticlesCommand;
import laurenyew.newsstandapp.api.model.ArticleData;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;

import static laurenyew.newsstandapp.api.commands.SearchArticlesErrorCodes.EXCEPTION_OCCURRED_WHILE_MAKING_CALL;
import static laurenyew.newsstandapp.api.commands.SearchArticlesErrorCodes.EXCEPTION_OCCURRED_WHILE_PARSING_DATA;
import static laurenyew.newsstandapp.api.commands.SearchArticlesErrorCodes.SEARCH_API_CALL_FAILED;
import static laurenyew.newsstandapp.api.commands.SearchArticlesErrorCodes.SEARCH_API_RATE_EXCEEDED;

public class ArticleBrowserPresenter implements ArticleBrowserContract.Presenter {

    private WeakReference<ArticleBrowserContract.View> mViewRef = null;
    private @NonNull
    HashMap<String, ArticleData> mData = new HashMap<>();
    private ArrayList<ArticlePreviewDataWrapper> mPreviewData = new ArrayList<>();
    private String mApiKey = null;
    private String mDefaultTitle = null;
    private AsyncJobCommand mCommand = null;
    private int mCurrentPageNum = 0;
    private String mSearchTerm = null;

    private BiFunction<List<ArticleData>, Integer, Object> onSuccessFunction = (data, pageNum) -> onLoadArticlesSuccess(data, pageNum);
    private BiFunction<Integer, String, Object> onFailureFunction = (errorCode, message) -> onLoadArticlesFailure(errorCode, message);


    //region Getters
    private ArticleBrowserContract.View getView() {
        return mViewRef != null ? mViewRef.get() : null;
    }
    //endregion

    @Override
    public void onBind(ArticleBrowserContract.View view) {
        mViewRef = new WeakReference<>(view);
        Context context = NewsStandApplication.getInstance().getApplicationContext();
        mApiKey = context != null ? context.getString(R.string.api_key) : null;
        mDefaultTitle = context != null ? context.getString(R.string.article_title_unknown) : "";
    }

    @Override
    public void unBind() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        if (mCommand != null) {
            mCommand.cancel();
            mCommand = null;
        }

        mData.clear();

        mSearchTerm = null;
        mApiKey = null;
        mDefaultTitle = null;
    }

    @Override
    public void refreshArticles(String searchTerm) {
        //Only let 1 command run at a time
        if (mCommand != null) {
            mCommand.cancel();
        }

        if (checkIfInternetIsAvailable()) {
            //reset the current page
            mCurrentPageNum = 0;
            mSearchTerm = searchTerm != null ? searchTerm : "";

            //load the images async
            if (mApiKey != null) {
                mCommand = new SearchArticlesCommand(mApiKey, mSearchTerm, mCurrentPageNum,
                        onSuccessFunction, onFailureFunction);
                mCommand.execute();
            }
        }
    }

    @Override
    public void loadNextPageOfArticles() {
        //Only let 1 command run at a time
        if (mCommand != null) {
            mCommand.cancel();
        }

        if (checkIfInternetIsAvailable()) {
            int nextPageNum = mCurrentPageNum + 1;
            //load the images async for next page
            if (mApiKey != null) {
                mCommand = new SearchArticlesCommand(mApiKey, mSearchTerm, nextPageNum,
                        onSuccessFunction, onFailureFunction);
                mCommand.execute();
            }
        }
    }

    @Override
    public void onSelectPreview(String itemId) {
        ArticleBrowserContract.View view = getView();
        ArticleData data = mData.get(itemId);
        if (view != null && data != null) {
            view.onShowArticleDetail(data.detailImageUrl, data.title, data.description, data.webUrl);
        }
    }

    //region Command Callback Methods
    protected Object onLoadArticlesSuccess(List<ArticleData> data, Integer requestedPage) {
        ArticleBrowserContract.View view = getView();
        if (view != null) {
            mCurrentPageNum = requestedPage;

            //As per NYTimesAPI, first page is 0
            //So if this is the first page, clear data
            if (mCurrentPageNum == 0) {
                mData.clear();
                mPreviewData.clear();
            }

            //Add all the articles
            for (ArticleData article : data) {
                //Don't allow empty titles, either use description or default title
                if (TextUtils.isEmpty(article.title)) {
                    article.title = TextUtils.isEmpty(article.description) ? article.description : mDefaultTitle;
                }
                //Fill in the data
                mData.put(article.id, article);
                mPreviewData.add(new ArticlePreviewDataWrapper(article.id, article.title, article.thumbnailImageUrl));
            }

            //Update the view
            view.onArticlesLoaded(mPreviewData);
        }
        return null;
    }

    protected Object onLoadArticlesFailure(int errorCode, String message) {
        ArticleBrowserContract.View view = getView();
        Context context = NewsStandApplication.getInstance().getApplicationContext();
        if (view != null && context != null) {
            //Tell the view to stop loading
            view.onArticlesLoaded(mPreviewData);
            //Sometimes if we page fast we can exceed the search api rate, if we do, don't error
            if (errorCode != SEARCH_API_RATE_EXCEEDED) {
                //Show the failure message
                StringBuilder stringBuilder = new StringBuilder();

                switch (errorCode) {
                    case SEARCH_API_CALL_FAILED:
                    case EXCEPTION_OCCURRED_WHILE_MAKING_CALL:
                        stringBuilder.append(context.getString(R.string.article_browser_error_call_failed));
                        break;
                    case EXCEPTION_OCCURRED_WHILE_PARSING_DATA:
                        stringBuilder.append(context.getString(R.string.article_browser_error_call_failed_to_parse));
                        break;
                    default:
                        break;
                }

                //Debug mode: include error message
                if (BuildConfig.DEBUG && !TextUtils.isEmpty(message)) {
                    stringBuilder.append(context.getString(R.string.article_browser_error_message, message));
                }

                //Update the view of failure
                view.onArticlesFailedToLoad(stringBuilder.toString());
            } else {
                //Update the user that they need to wait a bit before the next request
                //TODO: Queue up requests for the user on a sleep time? This message assumes the user is impatient
                //and will be doing the load more requests themselves
                view.onArticlesFailedToLoad(context.getString(R.string.article_browser_exceeded_api_limit));
            }
        }
        return null;
    }
    //endregion

    //region Helper Methods

    /**
     * Check network connection
     * This method also will update the view if the network is not connected
     */
    protected boolean checkIfInternetIsAvailable() {
        boolean isInternetConnected = false;
        Context context = NewsStandApplication.getInstance().getApplicationContext();
        if (context != null) {
            isInternetConnected = isNetworkAvailable(context);
        }

        if (!isInternetConnected) {
            ArticleBrowserContract.View view = getView();
            if (view != null) {
                view.onArticlesLoaded(mPreviewData);
                view.onInternetNotAvailable();
            }
        }
        return isInternetConnected;
    }

    //https://stackoverflow.com/questions/9570237/android-check-internet-connection
    private boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    //endregion
}
