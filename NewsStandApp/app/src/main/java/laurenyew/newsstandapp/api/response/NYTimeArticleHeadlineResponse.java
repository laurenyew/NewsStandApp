package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

/**
 * @author Lauren Yew on 5/8/18.
 * Headline response (ignoring other fields)
 */
public class NYTimeArticleHeadlineResponse {
    @Json(name = "print_headline")
    public String printHeadline;
}
