package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

/**
 * @author Lauren Yew on 5/8/18.
 * Response includes several multimedia types
 */
public class NYTimesArticleMultiMediaResponse {
    @Json(name = "subtype")
    public String subType;
    @Json(name = "url")
    public String imageUrl;
}
