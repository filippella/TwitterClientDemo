package uk.co.tae.twitterclientexercise.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import uk.co.tae.twitterclientexercise.R;

/**
 * Created by Filippo-TheAppExpert on 6/24/2015.
 */
public abstract class BaseActivity<T extends Object> extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getViewResource() instanceof Integer) {
            setContentView((Integer) getViewResource());
        } else if (getViewResource() instanceof View) {
            setContentView((View) getViewResource());
        }

        int toolbarId = getToolbarId();

        if (toolbarId != 0) {
            mToolbar = (Toolbar) findViewById(toolbarId);
            mToolbar.setNavigationIcon(R.mipmap.ic_launcher);
            setSupportActionBar(mToolbar);
        }

        configViews(getIntent(), savedInstanceState);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected abstract int getToolbarId();

    protected abstract void configViews(Intent intent, Bundle savedInstanceState);

    protected abstract T getViewResource();
}
