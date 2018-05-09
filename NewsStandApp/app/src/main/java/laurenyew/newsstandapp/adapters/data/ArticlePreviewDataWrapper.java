package laurenyew.newsstandapp.adapters.data;

/**
 * @author Lauren Yew on 5/8/18.
 * POJO for adapter to interact with data
 *
 * NOTE: Not using `member` notation just so it's easier to access the values
 */
public class ArticlePreviewDataWrapper {
    public String id;
    public String title;
    public String imageUrl;

    public ArticlePreviewDataWrapper(String id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }
}