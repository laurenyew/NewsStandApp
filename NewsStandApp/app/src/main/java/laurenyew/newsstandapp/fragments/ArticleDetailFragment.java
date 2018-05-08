package laurenyew.newsstandapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;

/**
 * Simple fragment to show the article details
 */
public class ArticleDetailFragment extends Fragment implements ArticleDetailContract.View {
    public static ArticleDetailFragment newInstance(String itemImageUrl, String itemTitle, String itemDescription, String itemWebUrl) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        if (itemImageUrl != null) {
            args.putString(ARG_ITEM_IMAGE_URL, itemImageUrl);
        }
        if (itemTitle != null) {
            args.putString(ARG_ITEM_TITLE, itemTitle);
        }
        if (itemDescription != null) {
            args.putString(ARG_ITEM_DESCRIPTION, itemDescription);
        }
        if (itemWebUrl != null) {
            args.putString(ARG_ITEM_WEB_URL, itemWebUrl);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView mDetailImageView = null;
    private TextView mDetailTitleTextView = null;
    private TextView mDetailDescriptionTextView = null;

    private String mItemImageUrl = null;
    private String mItemTitle = null;
    private String mItemDescription = null;
    private String mItemWebUrl = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mItemImageUrl = arguments.getString(ARG_ITEM_IMAGE_URL);
            mItemTitle = arguments.getString(ARG_ITEM_TITLE);
            mItemDescription = arguments.getString(ARG_ITEM_DESCRIPTION);
            mItemWebUrl = arguments.getString(ARG_ITEM_WEB_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.article_detail_fragment, container, false);
        mDetailImageView = rootView.findViewById(R.id.detailImageView);
        mDetailTitleTextView = rootView.findViewById(R.id.detailTitleTextView);
        mDetailDescriptionTextView = rootView.findViewById(R.id.detailDescriptionTextView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mItemImageUrl != null && mDetailImageView != null) {
            Picasso.get()
                    .load(mItemImageUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(mDetailImageView);
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity != null ? activity.getSupportActionBar() : null;
        if (actionBar != null) {
            actionBar.setTitle(mItemTitle);
        }

        if (mDetailTitleTextView != null) {
            mDetailTitleTextView.setText(mItemTitle);
        }

        if (mDetailDescriptionTextView != null) {
            mDetailDescriptionTextView.setText(mItemDescription);
        }
    }
}
