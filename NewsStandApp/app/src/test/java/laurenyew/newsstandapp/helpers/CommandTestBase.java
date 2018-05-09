package laurenyew.newsstandapp.helpers;

import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import laurenyew.newsstandapp.api.NYTimesArticleApi;
import laurenyew.newsstandapp.api.commands.SearchArticlesCommand;
import laurenyew.newsstandapp.api.model.ArticleData;
import laurenyew.newsstandapp.api.response.NYTimeArticleHeadlineResponse;
import laurenyew.newsstandapp.api.response.NYTimesArticleDataResponse;
import laurenyew.newsstandapp.api.response.NYTimesArticleMultiMediaResponse;
import laurenyew.newsstandapp.api.response.NYTimesArticlePageResponse;
import laurenyew.newsstandapp.api.response.NYTimesArticleResponse;
import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Lauren Yew on 5/9/18.
 * Test Base for any command tests
 * Helper class to hold the associated fields and set up the mocks
 */
public class CommandTestBase extends AppTestBase {
    public String apiKey = "api_key";
    public String searchTerm = "dog";
    public int pageNum = 0;
    public BiFunction<List<ArticleData>, Integer, Object> onSuccessFunction = (data, pageNum) -> onLoadArticlesSuccess(data, pageNum);
    public BiFunction<Integer, String, Object> onFailureFunction = (errorCode, message) -> onLoadArticlesFailure(errorCode, message);

    public List<ArticleData> resultData = null;
    public int resultPage = -1;
    public int resultErrorCode = -1;
    public String resultErrorMessage = null;

    public NYTimesArticleApi mockArticleApi = Mockito.mock(NYTimesArticleApi.class);

    public Call<NYTimesArticleResponse> mockCall = Mockito.mock(Call.class);

    //Extended mockito to allow inline mock so can mock this final class
    public Response mockResponse = Mockito.mock(Response.class);

    public NYTimesArticleResponse response = new NYTimesArticleResponse();
    public NYTimesArticlePageResponse pageResponse = new NYTimesArticlePageResponse();
    public NYTimesArticleDataResponse dataResponse = new NYTimesArticleDataResponse();
    public NYTimeArticleHeadlineResponse headlineResponse = new NYTimeArticleHeadlineResponse();
    public NYTimesArticleMultiMediaResponse multiMediaThumbnailResponse = new NYTimesArticleMultiMediaResponse();
    public NYTimesArticleMultiMediaResponse multiMediaLargeResponse = new NYTimesArticleMultiMediaResponse();

    @Override
    public void setup() {
        super.setup();
        Mockito.when(mockAppComponent.getNYTimesArticleApi()).thenReturn(mockArticleApi);

        //Setup return values
        multiMediaThumbnailResponse.imageUrl = expectedSmallImgUrl;
        multiMediaThumbnailResponse.subType = SearchArticlesCommand.thumbNailSubtype;

        multiMediaLargeResponse.imageUrl = expectedLargeImgUrl;
        multiMediaLargeResponse.subType = SearchArticlesCommand.detailSubtype;

        ArrayList<NYTimesArticleMultiMediaResponse> multiMediaResponses = new ArrayList<>();
        multiMediaResponses.add(multiMediaThumbnailResponse);
        multiMediaResponses.add(multiMediaLargeResponse);

        headlineResponse.printHeadline = expectedHeadline;

        dataResponse.headlineResponse = headlineResponse;
        dataResponse.multiMediaResponses = multiMediaResponses;
        dataResponse.id = expectedId;
        dataResponse.snippet = expectedDescription;
        dataResponse.webUrl = expectedWebUrl;

        ArrayList<NYTimesArticleDataResponse> dataResponses = new ArrayList<>();
        dataResponses.add(dataResponse);

        pageResponse.articleDataList = dataResponses;

        response.pageResponse = pageResponse;

        Mockito.when(mockArticleApi.searchArticles(any(), any(), any(), any(), any(), any())).thenReturn(mockCall);
        try {
            Mockito.when(mockCall.execute()).thenReturn(mockResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //region Helper Methods
    public Object onLoadArticlesSuccess(List<ArticleData> data, int requestedPage) {
        resultData = data;
        resultPage = requestedPage;
        return null;
    }

    public Object onLoadArticlesFailure(int errorCode, String message) {
        resultErrorCode = errorCode;
        resultErrorMessage = message;
        return null;
    }
    //endregion
}
