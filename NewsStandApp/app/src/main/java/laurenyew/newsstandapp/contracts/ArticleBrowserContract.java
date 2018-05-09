package laurenyew.newsstandapp.contracts;

import android.support.annotation.NonNull;

import java.util.List;

import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;

/**
 * @author Lauren Yew on 5/8/18.
 * MVP Contract for Article Browser Feature
 */
public interface ArticleBrowserContract {
    interface View {
        void onArticlesLoaded(List<ArticlePreviewDataWrapper> data);

        void onArticlesFailedToLoad(@NonNull String message);

        void onInternetNotAvailable();

        void onShowArticleDetail(String itemImageUrl, String itemTitle, String itemDescription, String itemWebUrl);
    }

    interface Presenter {

        void onBind(@NonNull View view);

        void unBind();

        /**
         * Each new search term will refresh the articles loaded
         */
        void refreshArticles(String searchTerm);

        /**
         * Will add articles for a given pageNum to the current list
         */
        void loadNextPageOfArticles();

        /**
         * Preview is selected (from the adapter)
         */
        void onSelectPreview(@NonNull String itemId);
    }
}
