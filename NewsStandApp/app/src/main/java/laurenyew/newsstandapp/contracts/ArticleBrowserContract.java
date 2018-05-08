package laurenyew.newsstandapp.contracts;

import java.util.List;

import laurenyew.newsstandapp.adapters.data.ArticlePreviewDataWrapper;

public interface ArticleBrowserContract {
    interface View {
        void onArticlesLoaded(List<ArticlePreviewDataWrapper> data);

        void onArticlesFailedToLoad(String message);

        void onInternetNotAvailable();

        void onShowArticleDetail(String itemImageUrl, String itemTitle, String itemDescription, String itemWebUrl);
    }

    interface Presenter {

        void onBind(View view);

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
        void onSelectPreview(String itemId);
    }
}
