package laurenyew.newsstandapp.adapters.data;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * @author Lauren Yew on 5/8/18.
 * DiffUtil.Callback for dealing with Article Preview data
 * (RecyclerView.Adapter performance enhancement)
 * <p>
 * This takes O(n) time, so the execution is run on a background thread
 */
public class ArticlePreviewDiffCallback extends DiffUtil.Callback {
    private List<ArticlePreviewDataWrapper> mOldData;
    private List<ArticlePreviewDataWrapper> mNewData;

    public ArticlePreviewDiffCallback(List<ArticlePreviewDataWrapper> oldData, List<ArticlePreviewDataWrapper> newData) {
        mOldData = oldData;
        mNewData = newData;
    }

    @Override
    public int getOldListSize() {
        return mOldData != null ? mOldData.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewData != null ? mNewData.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ArticlePreviewDataWrapper oldItem = mOldData != null ? mOldData.get(oldItemPosition) : null;
        ArticlePreviewDataWrapper newItem = mNewData != null ? mNewData.get(newItemPosition) : null;

        String oldId = oldItem != null ? oldItem.id : "";
        String newId = newItem != null ? newItem.id : "";

        return oldId.equals(newId);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ArticlePreviewDataWrapper oldItem = mOldData != null ? mOldData.get(oldItemPosition) : null;
        ArticlePreviewDataWrapper newItem = mNewData != null ? mNewData.get(newItemPosition) : null;

        String oldImageUrl = oldItem != null ? oldItem.imageUrl : null;
        String newImageUrl = newItem != null ? newItem.imageUrl : null;
        String oldTitle = oldItem != null ? oldItem.title : null;
        String newTitle = newItem != null ? newItem.title : null;

        return ((oldImageUrl == null && newImageUrl == null) ||
                (oldImageUrl != null && newImageUrl != null && oldImageUrl.equals(newImageUrl)))
                && ((oldTitle == null && newTitle == null) ||
                (oldTitle != null && newTitle != null && oldTitle.equals(newTitle)));

    }
}
