package laurenyew.newsstandapp.adapters;

import android.support.v7.util.DiffUtil;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import laurenyew.newsstandapp.BuildConfig;
import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;
import laurenyew.newsstandapp.adapters.viewHolders.ArticlePreviewViewHolder;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ArticlePreviewRecyclerViewAdapterTest {
    private ArticleBrowserContract.Presenter mockPresenter = Mockito.mock(ArticleBrowserContract.Presenter.class);
    private ArticlePreviewRecyclerViewAdapter adapter = null;
    private ArrayList<ArticlePreviewDataWrapper> testList = new ArrayList<>();
    private ArrayList<ArticlePreviewDataWrapper> testList1 = new ArrayList<>();
    private ArrayList<ArticlePreviewDataWrapper> testList2 = new ArrayList<>();

    @Before
    public void setup() {
        adapter = Mockito.spy(new ArticlePreviewRecyclerViewAdapter(mockPresenter));
    }

    //region Test Update Data

    /**
     * Given adapter updates the data,
     * - update data internal will be called
     */
    @Test
    public void testUpdateData() {
        /** Exercise **/
        adapter.updateData(testList);

        /** Verify **/
        verify(adapter).updateDataInternal(testList);
    }

    /**
     * given data is applied to adapter, diff result will dispatch updates to the adapter
     */
    @Test
    public void testApplyDataDiffResult() {
        /** Arrange **/
        DiffUtil.DiffResult mockDiffResult = Mockito.mock(DiffUtil.DiffResult.class);

        /** Exercise **/
        adapter.applyDataDiffResult(testList, mockDiffResult);

        /** Verify **/
        verify(mockDiffResult).dispatchUpdatesTo(adapter);
    }

    /**
     * Given multiple updates before an update finishes, the adapter will only run the first update, and queue the rest
     */
    @Test
    public void testMultipleUpdateData() {
        /** Exercise **/
        adapter.updateData(testList);
        adapter.updateData(testList1);
        adapter.updateData(testList2);

        /** Verify **/
        //Adapter should only be called once. The other parts should be queued
        verify(adapter).updateDataInternal(any());
        assertEquals(3, adapter.pendingDataUpdates.size());
        assertEquals(testList, adapter.pendingDataUpdates.getFirst());
        assertEquals(testList2, adapter.pendingDataUpdates.getLast());
    }

    /**
     * Given pending updates, on applying result, adapter will only take latest update and clear the rest
     */
    @Test
    public void testMultipleApplyDataResult() {
        /** Arrange **/
        DiffUtil.DiffResult mockDiffResult = Mockito.mock(DiffUtil.DiffResult.class);

        adapter.updateData(testList);
        adapter.updateData(testList1);
        adapter.updateData(testList2);

        /** Exercise **/
        adapter.applyDataDiffResult(testList, mockDiffResult);

        /** Verify **/
        verify(adapter, times(2)).updateDataInternal(any());
        assertEquals(0, adapter.pendingDataUpdates.size());
    }
    //endregion

    //region Test Adapter Views

    /**
     * Given onBindView, adapter should create a click listener on the view
     */
    @Test
    public void testOnBindView() {
        /** Arrange **/
        ArrayList<ArticlePreviewDataWrapper> list = new ArrayList<>();
        ArticlePreviewDataWrapper dataWrapper = new ArticlePreviewDataWrapper("1", "title", "description");
        list.add(dataWrapper);
        DiffUtil.DiffResult mockDiffResult = Mockito.mock(DiffUtil.DiffResult.class);
        adapter.applyDataDiffResult(list, mockDiffResult);

        View mockView = Mockito.mock(View.class);
        ArticlePreviewViewHolder mockViewHolder = new ArticlePreviewViewHolder(mockView);

        /** Exercise **/
        adapter.onBindViewHolder(mockViewHolder, 0);

        /** Verify **/
        verify(mockView).setOnClickListener(any());
    }
    //endregion
}
