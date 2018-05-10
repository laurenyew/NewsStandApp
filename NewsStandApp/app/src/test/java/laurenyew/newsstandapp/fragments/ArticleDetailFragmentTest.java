package laurenyew.newsstandapp.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.helpers.AppTestBase;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startVisibleFragment;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
public class ArticleDetailFragmentTest extends AppTestBase {
    private ArticleDetailFragment fragment = null;
    private ImageView imageDetailView = null;
    private TextView titleTextView = null;
    private TextView descriptionTextView = null;
    private Button shareButton = null;
    private Button websiteButton = null;

    @Override
    public void setup() {
        super.setup();
        //Setup up Picasso
        try {
            Picasso.get();
        } catch (IllegalStateException e) {
            Picasso.Builder builder = new Picasso.Builder(context);
            Picasso.setSingletonInstance(builder.build());
        }
        fragment = startTestFragment();
        setupViews(fragment.getView());
    }

    /**
     * on view created, image is visible and title is shown
     * and view is bound to the presenter
     */
    @Test
    public void testOnViewCreated() {
        /** Verify **/
        verify(mockDetailPresenter).onBind(fragment, expectedHeadline, expectedDescription, expectedWebUrl);

        assertEquals(View.VISIBLE, imageDetailView.getVisibility());
        assertEquals(View.VISIBLE, titleTextView.getVisibility());
        assertEquals(View.VISIBLE, descriptionTextView.getVisibility());
        assertEquals(View.VISIBLE, shareButton.getVisibility());
        assertEquals(View.VISIBLE, websiteButton.getVisibility());

        assertEquals(expectedHeadline, titleTextView.getText().toString());
        assertEquals(expectedDescription, descriptionTextView.getText().toString());
        assertEquals(context.getString(R.string.article_detail_share), shareButton.getText().toString());
        assertEquals(context.getString(R.string.article_detail_website), websiteButton.getText().toString());
    }

    /**
     * on share button clicked, presenter is updated
     */
    @Test
    public void testShareButtonClicked() {
        /** Exercise **/
        shareButton.performClick();

        /** Verify **/
        verify(mockDetailPresenter).onShare();
    }

    /**
     * on website button clicked, presenter is updated
     */
    @Test
    public void testWebisteButtonClicked() {
        /** Exercise **/
        websiteButton.performClick();

        /** Verify **/
        verify(mockDetailPresenter).onOpenWebsite();
    }

    //region Helper Methods
    private ArticleDetailFragment startTestFragment() {
        ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(expectedLargeImgUrl, expectedHeadline, expectedDescription, expectedWebUrl);
        startVisibleFragment(fragment);
        return fragment;
    }

    private void setupViews(View view) {
        imageDetailView = view.findViewById(R.id.detailImageView);
        titleTextView = view.findViewById(R.id.detailTitleTextView);
        descriptionTextView = view.findViewById(R.id.detailDescriptionTextView);
        shareButton = view.findViewById(R.id.openShareButton);
        websiteButton = view.findViewById(R.id.openWebsiteButton);
    }
    //endregion
}