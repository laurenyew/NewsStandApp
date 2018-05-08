package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

public class NYTimesArticleResponse {
    @Json(name = "response")
    public NYTimesArticlePageResponse pageResponse;
}
