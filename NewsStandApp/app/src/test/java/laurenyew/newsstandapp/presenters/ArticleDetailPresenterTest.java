package laurenyew.newsstandapp.presenters;

import android.content.Intent;
import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.helpers.AppTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ArticleDetailPresenterTest extends AppTestBase {
    private ArticleDetailPresenter presenter = new ArticleDetailPresenter();
    private ArticleDetailContract.View mockView = Mockito.mock(ArticleDetailContract.View.class);


    @Override
    public void setup() {
        super.setup();
        presenter.onBind(mockView, expectedHeadline, expectedDescription, expectedWebUrl);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        presenter.unBind();
    }

    /**
     * when onBind, presenter should set up the view reference
     */
    @Test
    public void testOnBind() {
        /** Verify **/
        assertNotNull(presenter.getView());
        assertEquals(mockView, presenter.getView());
    }

    /**
     * when unbind, presenter should teardown view reference and api key
     */
    @Test
    public void testUnbind() {
        /** Exercise **/
        presenter.unBind();

        /** Verify **/
        assertNull(presenter.getView());
    }

    /**
     * Share should start a new activity with a share intent
     */
    @Test
    public void testOnShare() {
        /** Exercise **/
        presenter.onShare();

        /** Verify **/
        ArgumentCaptor<Intent> argumentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(context).startActivity(argumentCaptor.capture());
        Intent intent = argumentCaptor.getValue();
        assertNotNull(intent);
        assertEquals(Intent.ACTION_CHOOSER, intent.getAction());
        assertEquals("Share", intent.getStringExtra(Intent.EXTRA_TITLE));
    }

    /**
     * Open website should start a new activity with a website intent
     */
    @Test
    public void testOnOpenWebsite() {
        /** Exercise **/
        presenter.onOpenWebsite();

        /** Verify **/
        ArgumentCaptor<Intent> argumentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(context).startActivity(argumentCaptor.capture());
        Intent intent = argumentCaptor.getValue();
        assertNotNull(intent);
        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals(Uri.parse(expectedWebUrl), intent.getData());
    }
}