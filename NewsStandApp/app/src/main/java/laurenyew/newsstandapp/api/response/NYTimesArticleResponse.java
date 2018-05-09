package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

/**
 * @author Lauren Yew on 5/8/18.
 * Initial wrapper response
 */
public class NYTimesArticleResponse {
    @Json(name = "response")
    public NYTimesArticlePageResponse pageResponse;
}
