package laurenyew.newsstandapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.fragments.ArticleBrowserFragment;

/**
 * @author Lauren Yew on 5/8/18.
 * Launcher Activity: News Article Browser
 */
public class ArticleBrowserActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG = "imageDetailFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.article_browser_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(getString(R.string.app_name));
        }

        //Show the view
        ArticleBrowserFragment fragment = ArticleBrowserFragment.newInstance();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.articleBrowserFrameLayout, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }
}