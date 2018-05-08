package laurenyew.newsstandapp.api;

import laurenyew.newsstandapp.api.response.NYTimesArticleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NYTimesArticleApi {
    @GET("svc/search/v2/articlesearch.json")
    Call<NYTimesArticleResponse> searchArticles(
            @Query("api-key") String apiKey,
            @Query("q") String queryTerm,
            @Query("page") Integer pageNum,
            @Query("fq") String filterQuery,
            @Query("sort") String sort,
            @Query("fl") String filterResults);
}
