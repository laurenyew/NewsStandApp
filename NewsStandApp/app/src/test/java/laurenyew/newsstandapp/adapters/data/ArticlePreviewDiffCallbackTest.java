package laurenyew.newsstandapp.adapters.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import laurenyew.newsstandapp.BuildConfig;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ArticlePreviewDiffCallbackTest {
    private ArticlePreviewDiffCallback diffCallback = null;
    private ArrayList<ArticlePreviewDataWrapper> oldList = null;
    private ArrayList<ArticlePreviewDataWrapper> newList = null;
    private ArticlePreviewDataWrapper itemA = new ArticlePreviewDataWrapper("A", "titleA", "urlA");
    private ArticlePreviewDataWrapper itemAv2 = new ArticlePreviewDataWrapper("A", "title2A", "url2A");
    private ArticlePreviewDataWrapper itemCwithAContent = new ArticlePreviewDataWrapper("C", "titleA", "urlA");
    private ArticlePreviewDataWrapper itemB = new ArticlePreviewDataWrapper("B", "titleB", "urlB");

    @Before
    public void setup() {
        oldList = new ArrayList<>();
        newList = new ArrayList<>();
    }

    /**
     * Given two items are the same, diffCallback are items same should return true
     */
    @Test
    public void testItemsTheSameOnSameItems() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemA);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areItemsTheSame(0, 0);

        /** Verify **/
        assertTrue(result);
    }

    /**
     * Given two items are the same, diffCallback are contents same should return true
     */
    @Test
    public void testContentsTheSameOnSameItems() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemA);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areContentsTheSame(0, 0);

        /** Verify **/
        assertTrue(result);
    }

    /**
     * Given two items are different, diffCallback are items same should return false
     */
    @Test
    public void testItemsTheSameOnDifferentItems() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemB);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areItemsTheSame(0, 0);

        /** Verify **/
        assertFalse(result);
    }

    /**
     * Given two items are different, diffCallback are contents same should return false
     */
    @Test
    public void testContentsTheSameOnDifferentItems() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemB);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areContentsTheSame(0, 0);

        /** Verify **/
        assertFalse(result);
    }

    /**
     * Given two items share the same id with different contents, diffCallback are items same should return true
     */
    @Test
    public void testItemsTheSameOnItemsWithSameIdDifferentContents() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemAv2);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areItemsTheSame(0, 0);

        /** Verify **/
        assertTrue(result);
    }

    /**
     * Given two items share the same id with different contents, diffCallback are contents same should return false
     */
    @Test
    public void testContentsTheSameOnItemsWithSameIdDifferentContents() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemAv2);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areContentsTheSame(0, 0);

        /** Verify **/
        assertFalse(result);
    }

    /**
     * given two items share the same content with different ids, diffCallback are items same should return false
     */
    @Test
    public void testItemsTheSameOnItemsWithSameContentsDifferentIds() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemCwithAContent);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areItemsTheSame(0, 0);

        /** Verify **/
        assertFalse(result);
    }

    /**
     * Given two items share the same content with different ids, diffCallback are contents same should return true
     */
    @Test
    public void testContentsTheSameOnItemsWithSameContentsDifferentIds() {
        /** Arrange **/
        oldList.add(itemA);
        newList.add(itemCwithAContent);
        diffCallback = new ArticlePreviewDiffCallback(oldList, newList);

        /** Exercise **/
        boolean result = diffCallback.areContentsTheSame(0, 0);

        /** Verify **/
        assertTrue(result);
    }
}