package laurenyew.newsstandapp.presenters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import laurenyew.newsstandapp.NewsStandApplication;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;

/**
 * @author Lauren Yew on 5/8/18.
 * <p>
 * Article Detail Feature Logic
 * - Share Detail
 * - Open Detail Website
 */
public class ArticleDetailPresenter implements ArticleDetailContract.Presenter {
    private WeakReference<ArticleDetailContract.View> mViewRef = null;
    private String mTitle = null;
    private String mDescription = null;
    private String mWebUrl = null;

    //region Getters
    @VisibleForTesting
    ArticleDetailContract.View getView() {
        return mViewRef != null ? mViewRef.get() : null;
    }
    //endregion

    @Override
    public void onBind(ArticleDetailContract.View view, String title, String description, String webUrl) {
        mViewRef = new WeakReference<>(view);
        mTitle = title;
        mDescription = description;
        mWebUrl = webUrl;
    }

    @Override
    public void unBind() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        mTitle = null;
        mDescription = null;
        mWebUrl = null;
    }

    /**
     * Launch an intent to open a chooser
     * for a share intent
     * (Email, Message, Social Media)
     */
    @Override
    public void onShare() {
        Context context = NewsStandApplication.getInstance().getAppComponent().getContext();
        if (context != null) {
            StringBuilder bodyBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(mDescription)) {
                bodyBuilder.append(mDescription);
                bodyBuilder.append("\n");
            }

            if (mWebUrl != null)
                bodyBuilder.append(mWebUrl);


            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, mTitle);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
            sendIntent.putExtra(Intent.EXTRA_TEXT, bodyBuilder.toString());
            sendIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(sendIntent, "Share"));
        }
    }

    /**
     * Attempt to launch an intent to open the web url
     * If there is an exception, tell the view
     */
    @Override
    public void onOpenWebsite() {
        Context context = NewsStandApplication.getInstance().getAppComponent().getContext();
        if (context != null) {
            try {
                Uri uri = Uri.parse(mWebUrl);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(uri);
                context.startActivity(i);
            } catch (Exception e) {
                ArticleDetailContract.View view = getView();
                if (view != null) {
                    view.onOpenWebsiteFailed();
                }
            }
        }
    }
}
