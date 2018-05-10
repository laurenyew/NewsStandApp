package laurenyew.newsstandapp.api.commands;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import laurenyew.newsstandapp.NewsStandApplication;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import laurenyew.newsstandapp.api.model.ArticleData;
import laurenyew.newsstandapp.api.response.NYTimesArticleDataResponse;
import laurenyew.newsstandapp.api.response.NYTimesArticleMultiMediaResponse;
import laurenyew.newsstandapp.api.response.NYTimesArticleResponse;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Lauren Yew on 5/8/18.
 * <p>
 * Async Command that runs the retrofit call and parses the resulting article response
 * into our own data model class
 */
public class SearchArticlesCommand extends AsyncJobCommand {
    @VisibleForTesting
    public static final String thumbNailSubtype = "thumbnail";
    @VisibleForTesting
    public static final String detailSubtype = "xlarge";

    private String mApiKey;
    private String mSearchTerm;
    private int mRequestedPageNum;
    //On Success includes requested page num and data
    private BiFunction<List<ArticleData>, Integer, Object> mOnSuccess;
    //On Failure should include error string
    private BiFunction<Integer, String, Object> mOnFailure;

    public SearchArticlesCommand(String apiKey, String searchTerm, int pageNum, BiFunction<List<ArticleData>, Integer, Object> onSuccess, BiFunction<Integer, String, Object> onFailure) {
        mApiKey = apiKey;
        mSearchTerm = searchTerm;
        mRequestedPageNum = pageNum;
        mOnSuccess = onSuccess;
        mOnFailure = onFailure;
    }

    @Override
    void executeCommandImpl() {
        if (job != null && !job.isInterrupted()) {
            NYTimesArticleApi searchArticlesApi = NewsStandApplication.getInstance().getAppComponent().getNYTimesArticleApi();
            if (searchArticlesApi != null) {
                Response<NYTimesArticleResponse> response = null;

                //Make the call
                try {
                    Call<NYTimesArticleResponse> searchArticlesCall = searchArticlesApi.searchArticles(mApiKey, mSearchTerm, mRequestedPageNum, "multimedia", "newest", "multimedia, headline, snippet, web_url, _id");
                    if (searchArticlesCall != null) {
                        response = searchArticlesCall.execute();
                    } else {
                        Log.d(SearchArticlesCommand.class.toString(), "Search api call not setup correctly");
                    }
                } catch (final IOException e) {
                    postFailure(SearchArticlesErrorCodes.EXCEPTION_OCCURRED_WHILE_MAKING_CALL, e.getLocalizedMessage());
                }

                //Parse the response
                if (response != null) {
                    NYTimesArticleResponse articleResponse = response.body();
                    if (response.code() != 200 || articleResponse == null) {
                        //Post failure onto main thread
                        if (response.code() == 429) {
                            postFailure(SearchArticlesErrorCodes.SEARCH_API_RATE_EXCEEDED);
                        } else {
                            postFailure(SearchArticlesErrorCodes.SEARCH_API_CALL_FAILED);
                        }
                        Log.d(SearchArticlesCommand.class.toString(), "Response failure. Error code:" + response.code() + " error body: " + response.errorBody());
                    } else {
                        ArrayList<ArticleData> massagedData = parseArticleData(articleResponse);
                        postSuccess(massagedData);
                    }
                } else {
                    postFailure(SearchArticlesErrorCodes.SEARCH_API_CALL_FAILED, null);
                }
            } else {
                Log.d(SearchArticlesCommand.class.toString(), "Search api not setup correctly");
            }
        }
    }


    //region Helper Methods
    @VisibleForTesting
    @NonNull
    ArrayList<ArticleData> parseArticleData(NYTimesArticleResponse articleResponse) {
        ArrayList<ArticleData> massagedData = new ArrayList<>();
        try {
            boolean foundThumbnailImage;
            boolean foundDetailImage;
            List<NYTimesArticleDataResponse> responseData = articleResponse.pageResponse.articleDataList;
            for (NYTimesArticleDataResponse data : responseData) {
                ArticleData article = new ArticleData();
                article.id = data.id;
                article.title = data.headlineResponse.printHeadline;
                article.description = data.snippet;
                article.webUrl = data.webUrl;

                foundDetailImage = false;
                foundThumbnailImage = false;
                for (NYTimesArticleMultiMediaResponse multiMediaResponse : data.multiMediaResponses) {
                    if (thumbNailSubtype.equals(multiMediaResponse.subType)) {
                        article.thumbnailImageUrl = "http://www.nytimes.com/" + multiMediaResponse.imageUrl;
                        foundThumbnailImage = true;
                    } else if (detailSubtype.equals(multiMediaResponse.subType)) {
                        article.detailImageUrl = "http://www.nytimes.com/" + multiMediaResponse.imageUrl;
                        foundDetailImage = true;
                    }
                    if (foundThumbnailImage && foundDetailImage) {
                        break;
                    }
                }

                massagedData.add(article);
            }
        } catch (Exception e) {
            postFailure(SearchArticlesErrorCodes.EXCEPTION_OCCURRED_WHILE_PARSING_DATA, e.getLocalizedMessage());
        }
        return massagedData;
    }

    private void postFailure(@SearchArticlesErrorCodes.SEARCH_ARTICLES_ERROR_CODES int errorCode) {
        postFailure(errorCode, null);
    }

    @VisibleForTesting
    void postFailure(final @SearchArticlesErrorCodes.SEARCH_ARTICLES_ERROR_CODES int errorCode, final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (mOnFailure != null) {
                mOnFailure.apply(errorCode, message);
            }
        });
    }

    @VisibleForTesting
    void postSuccess(ArrayList<ArticleData> massagedData) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (mOnSuccess != null) {
                mOnSuccess.apply(massagedData, mRequestedPageNum);
            }
        });
    }
    //endregion
}
