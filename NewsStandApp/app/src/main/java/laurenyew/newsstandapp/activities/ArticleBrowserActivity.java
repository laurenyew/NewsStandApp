package laurenyew.newsstandapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.fragments.ArticleBrowserFragment;

public class ArticleBrowserActivity extends AppCompatActivity {
    private String FRAGMENT_TAG = "imageDetailFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.article_browser_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(getString(R.string.app_name));


        //Show the view
        ArticleBrowserFragment fragment = ArticleBrowserFragment.newInstance();
        if (fragment instanceof Fragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.articleBrowserFrameLayout, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }
}
