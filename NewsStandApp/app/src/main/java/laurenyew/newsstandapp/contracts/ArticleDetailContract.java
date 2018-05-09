package laurenyew.newsstandapp.contracts;

import android.support.annotation.NonNull;

public interface ArticleDetailContract {
    interface View {
        String ARG_ITEM_IMAGE_URL = "item_image_url";
        String ARG_ITEM_TITLE = "item_title";
        String ARG_ITEM_DESCRIPTION = "item_description";
        String ARG_ITEM_WEB_URL = "item_web_url";

        void onHideOpenWebsiteButton();
    }

    interface Presenter {
        void onBind(@NonNull View view, String title, String description, String webUrl);

        void unBind();

        void onShare();

        void onOpenWebsite();
    }
}
