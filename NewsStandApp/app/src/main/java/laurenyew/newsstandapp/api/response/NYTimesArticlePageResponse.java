package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * @author Lauren Yew on 5/8/18.
 * Page response with the list of article data
 */
public class NYTimesArticlePageResponse {
    @Json(name = "docs")
    public List<NYTimesArticleDataResponse> articleDataList;
}
