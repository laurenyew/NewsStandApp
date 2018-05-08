package laurenyew.newsstandapp.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;
import laurenyew.newsstandapp.adapters.data.ArticlePreviewDiffCallback;
import laurenyew.newsstandapp.adapters.viewHolders.ArticlePreviewViewHolder;
import laurenyew.newsstandapp.contracts.ArticleBrowserContract;

public class ArticlePreviewRecyclerViewAdapter extends RecyclerView.Adapter<ArticlePreviewViewHolder> {
    private ArticleBrowserContract.Presenter mPresenter;
    private ArrayList<ArticlePreviewDataWrapper> mData = new ArrayList<>();

    @VisibleForTesting
    public ArrayDeque<List<ArticlePreviewDataWrapper>> pendingDataUpdates = new ArrayDeque<>();

    public ArticlePreviewRecyclerViewAdapter(ArticleBrowserContract.Presenter presenter) {
        mPresenter = presenter;
    }

    //RecyclerView Diff.Util (List Updates)
    public void updateData(List<ArticlePreviewDataWrapper> newData) {
        List<ArticlePreviewDataWrapper> data = newData != null ? newData : new ArrayList<ArticlePreviewDataWrapper>();
        pendingDataUpdates.add(data);
        if (pendingDataUpdates.size() <= 1) {
            updateDataInternal(data);
        }
    }

    /**
     * Handle the diff util update on a background thread
     * (this can take O(n) time so we don't want it on the main thread)
     */
    @VisibleForTesting
    public void updateDataInternal(final List<ArticlePreviewDataWrapper> newData) {
        final ArrayList oldData = new ArrayList(mData);
        new Thread(() -> {
            DiffUtil.Callback diffCallback = createDataDiffCallback(oldData, newData);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> applyDataDiffResult(newData, diffResult));
        }).start();
    }

    /**
     * UI thread callback to apply the diff result to the adapter
     * and take in the latest update
     */
    @VisibleForTesting
    public void applyDataDiffResult(List<ArticlePreviewDataWrapper> newData, DiffUtil.DiffResult diffResult) {
        if (!pendingDataUpdates.isEmpty()) {
            pendingDataUpdates.remove();
        }

        //Apply the mData to the view
        mData.clear();
        if (newData != null) {
            mData.addAll(newData);
        }
        diffResult.dispatchUpdatesTo(this);

        //Take in the next latest update
        if (!pendingDataUpdates.isEmpty()) {
            List<ArticlePreviewDataWrapper> latestDataUpdate = pendingDataUpdates.pop();
            pendingDataUpdates.clear();
            updateDataInternal(latestDataUpdate);
        }
    }

    public DiffUtil.Callback createDataDiffCallback(List<ArticlePreviewDataWrapper> oldData, List<ArticlePreviewDataWrapper> newData) {
        return new ArticlePreviewDiffCallback(oldData, newData);
    }
    //endregion

    //region RecyclerView.Adapter
    @NonNull
    @Override
    public ArticlePreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_browser_preview_view, parent, false);
        return new ArticlePreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlePreviewViewHolder holder, int position) {
        final ArticlePreviewDataWrapper item = mData != null ? mData.get(position) : null;
        if (item != null) {
            String itemImageUrl = item.imageUrl;
            if (holder.previewImageView != null) {
                Picasso.get()
                        .load(itemImageUrl)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(holder.previewImageView);
            }

            if (holder.previewTitleTextView != null) {
                holder.previewTitleTextView.setText(item.title);
            }

            holder.itemView.setOnClickListener(view -> {
                if (mPresenter != null) {
                    mPresenter.onSelectPreview(item.id);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
    //endregion
}
