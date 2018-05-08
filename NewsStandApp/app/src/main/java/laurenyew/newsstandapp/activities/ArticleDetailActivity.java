package laurenyew.newsstandapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import laurenyew.newsstandapp.R;
import laurenyew.newsstandapp.contracts.ArticleDetailContract;
import laurenyew.newsstandapp.fragments.ArticleDetailFragment;

public class ArticleDetailActivity extends AppCompatActivity {
    public static Intent newInstance(Context context, String itemImageUrl, String itemTitle, String itemDescription, String itemWebUrl) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        if (itemImageUrl != null) {
            intent.putExtra(ArticleDetailContract.View.ARG_ITEM_IMAGE_URL, itemImageUrl);
        }
        if (itemTitle != null) {
            intent.putExtra(ArticleDetailContract.View.ARG_ITEM_TITLE, itemTitle);
        }
        if (itemDescription != null) {
            intent.putExtra(ArticleDetailContract.View.ARG_ITEM_DESCRIPTION, itemDescription);
        }
        if (itemWebUrl != null) {
            intent.putExtra(ArticleDetailContract.View.ARG_ITEM_WEB_URL, itemWebUrl);
        }
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        // Show the Up button in the action bar.
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String imageUrl = intent.getStringExtra(ArticleDetailContract.View.ARG_ITEM_IMAGE_URL);
            String itemTitle = intent.getStringExtra(ArticleDetailContract.View.ARG_ITEM_TITLE);
            String itemDescription = intent.getStringExtra(ArticleDetailContract.View.ARG_ITEM_DESCRIPTION);
            String itemWebUrl = intent.getStringExtra(ArticleDetailContract.View.ARG_ITEM_WEB_URL);
            ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(imageUrl, itemTitle, itemDescription, itemWebUrl);

            if (fragment instanceof Fragment) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.articleDetailContainer, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}