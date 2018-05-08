package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

import java.util.List;

public class NYTimesArticleDataResponse {
    @Json(name = "web_url")
    public String webUrl;
    @Json(name = "snippet")
    public String snippet;
    @Json(name = "multimedia")
    public List<NYTimesArticleMultiMediaResponse> multiMediaResponses;
    @Json(name = "headline")
    public NYTimeArticleHeadlineResponse headlineResponse;
    @Json(name = "_id")
    public String id;
}
