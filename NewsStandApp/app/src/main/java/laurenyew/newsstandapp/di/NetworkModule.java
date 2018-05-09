package laurenyew.newsstandapp.di;

import com.squareup.moshi.Moshi;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * @author Lauren Yew on 5/8/18.
 * Dagger2 Module Implementation
 * Provides Retrofit and NYTimesArticleApi singletons
 */
@Module
public class NetworkModule {
    private Retrofit mRetrofit;
    private Moshi mMoshi = new Moshi.Builder().build();
    private OkHttpClient.Builder mOkHttpClient;

    public NetworkModule() {
        mOkHttpClient = setupOkHttp();
        mRetrofit = new Retrofit.Builder().baseUrl("https://api.nytimes.com/")
                .addConverterFactory(MoshiConverterFactory.create(mMoshi))
                .client(mOkHttpClient.build()).build();
    }

    @Provides
    @Singleton
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    @Provides
    @Singleton
    public NYTimesArticleApi getNYTimesArticleApi(Retrofit retrofit) {
        return retrofit.create(NYTimesArticleApi.class);
    }

    //region Helper Methods
    private OkHttpClient.Builder setupOkHttp() {
        //Setup HttpClient
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(httpLoggingInterceptor);

        //Add headers
        httpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                    .build();
            return chain.proceed(request);
        });

        httpClientBuilder.followRedirects(true);
        httpClientBuilder.followSslRedirects(true);

        return httpClientBuilder;
    }
    //endregion
}