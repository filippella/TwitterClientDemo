package uk.co.tae.twitterclientexercise.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Random;

import uk.co.tae.twitterclientexercise.R;
import uk.co.tae.twitterclientexercise.presenter.SplashPresenter;

/**
 * Created by Filippo-TheAppExpert on 6/25/2015.
 */
public class SplashActivity extends BaseActivity<Integer> implements SplashPresenter.ViewContract {

    private SplashPresenter mPresenter;
    private static int SPLASH_TIME_OUT = new Random().nextInt(3000);

    @Override
    protected int getToolbarId() {
        return 0;
    }

    @Override
    protected Integer getViewResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void configViews(Intent intent, Bundle savedInstanceState) {
        mPresenter = new SplashPresenter(SplashActivity.this);
        mPresenter.initialize();
        mPresenter.showSplashScreen(SPLASH_TIME_OUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public AppCompatActivity getActivity() {
        return SplashActivity.this;
    }

    @Override
    public void showActivity(Intent intent) {
        startActivity(intent);
        finish();
    }
}
