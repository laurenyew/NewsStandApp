package laurenyew.newsstandapp.api.response;

import com.squareup.moshi.Json;

import java.util.List;

public class NYTimesArticlePageResponse {
    @Json(name = "docs")
    public List<NYTimesArticleDataResponse> articleDataList;
}
