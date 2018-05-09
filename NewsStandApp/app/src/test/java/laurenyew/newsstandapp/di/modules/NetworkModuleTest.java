package laurenyew.newsstandapp.di.modules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.api.NYTimesArticleApi;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NetworkModuleTest {
    private NetworkModule networkModule = new NetworkModule();

    @Test
    public void testRetrofitSetupCorrectly() {
        /** Exercise **/
        Retrofit retrofit = networkModule.getRetrofit();

        /** Verify **/
        assertEquals("https://api.nytimes.com/", retrofit.baseUrl().toString());
    }

    @Test
    public void testOkHttpSetupCorrectly() {
        /** Exercise **/
        OkHttpClient okHttpClient = networkModule.mOkHttpClient.build();

        /** Verify **/
        assertNotNull(okHttpClient);
        assertEquals(2, okHttpClient.interceptors().size());
        assertTrue(okHttpClient.followRedirects());
        assertTrue(okHttpClient.followSslRedirects());
    }

    @Test
    public void testGetNYTimesArticleApi() {
        /** Exercise **/
        NYTimesArticleApi api = networkModule.getNYTimesArticleApi(networkModule.getRetrofit());

        /** Verify **/
        assertNotNull(api);
    }
}