package laurenyew.newsstandapp.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {NetworkModule.class, AppModule.class})
public interface NewsStandAppComponent {
    //region Networking
    Retrofit getRetrofit();

    NYTimesArticleApi getNYTimesArticleApi();
    //endregion
}