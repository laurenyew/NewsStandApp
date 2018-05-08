package laurenyew.newsstandapp.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import laurenyew.newsstandapp.R;

public class ArticlePreviewViewHolder extends RecyclerView.ViewHolder {
    public ImageView previewImageView;
    public TextView previewTitleTextView;

    public ArticlePreviewViewHolder(View itemView) {
        super(itemView);
        previewImageView = itemView.findViewById(R.id.previewImageView);
        previewTitleTextView = itemView.findViewById(R.id.previewTitleTextView);
    }
}