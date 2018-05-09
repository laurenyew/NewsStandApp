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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import laurenyew.newsstandapp.NewsStandApplication;
import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;

/**
 * @author Lauren Yew on 5/8/18.
 * Simple fragment to show the article details.
 * Added Presenter so that its easier to switch out logic for handling onSearch / onWebsite buttons
 */
public class ArticleDetailFragment extends Fragment implements ArticleDetailContract.View {
    /**
     * Helper method to create an ArticleDetailFragment with the proper arguments
     */
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
    private Button mShareButton = null;
    private Button mWebsiteButton = null;

    private String mItemImageUrl = null;
    private String mItemTitle = null;
    private String mItemDescription = null;
    private String mItemWebUrl = null;

    protected ArticleDetailContract.Presenter mPresenter;

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

        //Dagger setup
        mPresenter = NewsStandApplication.getInstance().addFragmentComponent().getArticleDetailPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.article_detail_fragment, container, false);
        mDetailImageView = rootView.findViewById(R.id.detailImageView);
        mDetailTitleTextView = rootView.findViewById(R.id.detailTitleTextView);
        mDetailDescriptionTextView = rootView.findViewById(R.id.detailDescriptionTextView);
        mShareButton = rootView.findViewById(R.id.openShareButton);
        mWebsiteButton = rootView.findViewById(R.id.openWebsiteButton);
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

        if (mShareButton != null) {
            mShareButton.setOnClickListener(shareView -> {
                if (mPresenter != null) {
                    mPresenter.onShare();
                }
            });
        }

        if (mWebsiteButton != null && mWebsiteButton.getVisibility() == View.VISIBLE) {
            mWebsiteButton.setOnClickListener(websiteView -> {
                if (mPresenter != null) {
                    mPresenter.onOpenWebsite();
                }
            });
        }

        //Bind the presenter so it can handle onShare and onOpenWebsite
        mPresenter.onBind(this, mItemTitle, mItemDescription, mItemWebUrl);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mPresenter.unBind();

        mDetailImageView = null;
        mDetailTitleTextView = null;
        mDetailDescriptionTextView = null;
        mShareButton = null;
        mWebsiteButton = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter = null;
        mItemImageUrl = null;
        mItemTitle = null;
        mItemDescription = null;
        mItemWebUrl = null;

        //Release the component and the presenter
        NewsStandApplication.getInstance().releaseFragmentComponent();
    }

    //region MVP

    /**
     * Show a toast when you fail to open a given website
     */
    @Override
    public void onOpenWebsiteFailed() {
        if (isAdded() && isVisible()) {
            Toast.makeText(getContext(), R.string.article_detail_unable_to_open_website, Toast.LENGTH_SHORT).show();
        }
    }
    //endregion
}
