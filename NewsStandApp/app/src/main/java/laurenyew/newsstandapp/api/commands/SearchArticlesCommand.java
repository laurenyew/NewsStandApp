package laurenyew.newsstandapp.api.commands;

import android.os.Handler;
import android.os.Looper;
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

public class SearchArticlesCommand extends AsyncJobCommand {
    private static final String thumbNailSubtype = "thumbnail";
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
                try {
                    Call<NYTimesArticleResponse> searchArticlesCall = searchArticlesApi.searchArticles(mApiKey, mSearchTerm, mRequestedPageNum, "multimedia", "newest", "multimedia, headline, snippet, web_url, _id");
                    if (searchArticlesCall != null) {
                        Response<NYTimesArticleResponse> response = searchArticlesCall.execute();
                        if (response != null) {
                            NYTimesArticleResponse articleResponse = response.body();
                            if (response.code() != 200 || articleResponse == null) {
                                //Post failure onto main thread
                                Log.d(SearchArticlesCommand.class.toString(), "Response failure. Error code:" + response.code() + " error body: " + response.errorBody());
                                if(response.code() == 429){
                                    postFailure(SearchArticlesErrorCodes.SEARCH_API_RATE_EXCEEDED);
                                }else {
                                    postFailure(SearchArticlesErrorCodes.SEARCH_API_CALL_FAILED);
                                }
                            } else {
                                try {
                                    ArrayList<ArticleData> massagedData = new ArrayList<>();

                                    List<NYTimesArticleDataResponse> responseData = articleResponse.pageResponse.articleDataList;
                                    for (NYTimesArticleDataResponse data : responseData) {
                                        ArticleData article = new ArticleData();
                                        article.id = data.id;
                                        article.title = data.headlineResponse.printHeadline;
                                        article.description = data.snippet;
                                        article.webUrl = data.webUrl;

                                        for (NYTimesArticleMultiMediaResponse multiMediaResponse : data.multiMediaResponses) {
                                            if (thumbNailSubtype.equals(multiMediaResponse.subType)) {
                                                article.thumbnailImageUrl = "http://www.nytimes.com/" + multiMediaResponse.imageUrl;
                                                break;
                                            }
                                        }

                                        massagedData.add(article);
                                    }

                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(() -> {
                                        if (mOnSuccess != null) {
                                            mOnSuccess.apply(massagedData, mRequestedPageNum);
                                        }
                                    });
                                } catch (Exception e) {
                                    postFailure(SearchArticlesErrorCodes.EXCEPTION_OCCURRED_WHILE_PARSING_DATA, e.getLocalizedMessage());
                                }
                            }
                        } else {
                            postFailure(SearchArticlesErrorCodes.SEARCH_API_CALL_FAILED, (response.errorBody() != null) ? response.errorBody().toString() : null);
                        }
                    } else {
                        Log.d(SearchArticlesCommand.class.toString(), "Search api call not setup correctly");
                    }
                } catch (final IOException e) {
                    postFailure(SearchArticlesErrorCodes.EXCEPTION_OCCURRED_WHILE_MAKING_CALL, e.getLocalizedMessage());
                }
            } else {
                Log.d(SearchArticlesCommand.class.toString(), "Search api not setup correctly");
            }
        }
    }

    //region Helper Methods
    private void postFailure(@SearchArticlesErrorCodes.SEARCH_ARTICLES_ERROR_CODES int errorCode) {
        postFailure(errorCode, null);
    }

    private void postFailure(final @SearchArticlesErrorCodes.SEARCH_ARTICLES_ERROR_CODES int errorCode, final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (mOnFailure != null) {
                mOnFailure.apply(errorCode, message);
            }
        });
    }
    //endregion
}
