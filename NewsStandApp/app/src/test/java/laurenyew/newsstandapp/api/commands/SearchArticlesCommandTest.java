package laurenyew.newsstandapp.api.commands;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.api.model.ArticleData;
import laurenyew.newsstandapp.helpers.CommandTestBase;

import static laurenyew.newsstandapp.api.commands.SearchArticlesErrorCodes.SEARCH_API_CALL_FAILED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SearchArticlesCommandTest extends CommandTestBase {
    private SearchArticlesCommand command;

    @Override
    public void setup() {
        super.setup();
        command = Mockito.spy(new SearchArticlesCommand(apiKey, searchTerm, pageNum, onSuccessFunction, onFailureFunction));
        command.execute();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        command.cancel();
        command = null;
    }

    /**
     * Given command run, api should get request for articles
     */
    @Test
    public void testCommandExecutedThenApiShouldBeCalled() {
        /** Exercise **/
        command.executeCommandImpl();

        /** Verify **/
        verify(mockArticleApi, atLeastOnce()).searchArticles(apiKey, searchTerm, pageNum, "multimedia", "newest", "multimedia, headline, snippet, web_url, _id");
    }

    /**
     * Given command run and api returns success, get request should hit onSuccess with results
     */
    @Test
    public void testCommandSuccessHitsOnSuccessListener() {
        /** Arrange **/
        Mockito.when(mockResponse.code()).thenReturn(200);
        Mockito.when(mockResponse.body()).thenReturn(response);

        /** Exercise **/
        command.executeCommandImpl();

        /** Verify **/
        //OnSuccess should have been called
        assertNull(resultErrorMessage);
        assertNotNull(resultData);
        assertEquals(1, resultData.size());
        ArticleData data = resultData.get(0);
        assertEquals(expectedId, data.id);
        assertEquals(expectedHeadline, data.title);
        assertEquals("http://www.nytimes.com/" + expectedSmallImgUrl, data.thumbnailImageUrl);
        assertEquals("http://www.nytimes.com/" + expectedLargeImgUrl, data.detailImageUrl);
        assertEquals(expectedDescription, data.description);
        assertEquals(expectedWebUrl, data.webUrl);
    }

    /**
     * given command run and api returns failure, get request should hit onFailure with results
     */
    @Test
    public void testCommandFailureHitsOnFailureListener() {
        /** Arrange **/
        Mockito.when(mockResponse.code()).thenReturn(404);
        Mockito.when(mockResponse.body()).thenReturn(null);

        /** Exercise **/
        command.executeCommandImpl();

        /** Verify **/
        //OnSuccess should have been called
        assertNull(resultData);
        assertNotNull(resultErrorCode);
        assertEquals(SEARCH_API_CALL_FAILED, resultErrorCode);
        assertNull(resultErrorMessage);
    }
    //endregion
}