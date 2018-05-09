package laurenyew.newsstandapp.contracts;

import android.support.annotation.NonNull;

/**
 * @author Lauren Yew on 5/8/18.
 * MVP Contract for Article Detail Feature
 */
public interface ArticleDetailContract {
    interface View {
        //Included args passed so can replace Fragment/Activity if you like
        String ARG_ITEM_IMAGE_URL = "item_image_url";
        String ARG_ITEM_TITLE = "item_title";
        String ARG_ITEM_DESCRIPTION = "item_description";
        String ARG_ITEM_WEB_URL = "item_web_url";

        void onOpenWebsiteFailed();
    }

    interface Presenter {
        void onBind(@NonNull View view, String title, String description, String webUrl);

        void unBind();

        void onShare();

        void onOpenWebsite();
    }
}
