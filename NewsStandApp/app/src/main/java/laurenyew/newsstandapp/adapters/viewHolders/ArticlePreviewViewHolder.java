package laurenyew.newsstandapp.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import laurenyew.newsstandapp.R;

/**
 * @author Lauren Yew on 5/8/18.
 * Custom View Holder for Article Preview
 * <p>
 * NOTE: Not using the `member notation` for easy access to variables
 */
public class ArticlePreviewViewHolder extends RecyclerView.ViewHolder {
    public ImageView previewImageView;
    public TextView previewTitleTextView;

    public ArticlePreviewViewHolder(View itemView) {
        super(itemView);
        previewImageView = itemView.findViewById(R.id.previewImageView);
        previewTitleTextView = itemView.findViewById(R.id.previewTitleTextView);
    }
}