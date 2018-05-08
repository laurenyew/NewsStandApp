package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

public class NYTimeArticleHeadlineResponse {
    @Json(name = "print_headline")
    public String printHeadline;
}
