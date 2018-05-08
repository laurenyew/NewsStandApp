package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

public class NYTimesArticleMultiMediaResponse {
    @Json(name = "subtype")
    public String subType;
    @Json(name = "url")
    public String imageUrl;
}
