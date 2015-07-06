package uk.co.tae.twitterclientexercise.presenter;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import uk.co.tae.twitterclientexercise.activity.HomeTimelineActivity;
import uk.co.tae.twitterclientexercise.activity.LoginActivity;
import uk.co.tae.twitterclientexercise.model.Constants;
import uk.co.tae.twitterclientexercise.utilities.SharedPreferenceUtils;

/**
 * Created by Filippo-TheAppExpert on 6/25/2015.
 */
public class SplashPresenter implements Presenter {

    private ViewContract mContract;

    public SplashPresenter(ViewContract contract) {
        mContract = contract;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    public void showSplashScreen(int timeout) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String username = SharedPreferenceUtils.getValue(mContract.getActivity(), Constants.Credential.USERNAME);
                if (username == null) {
                    mContract.showActivity(new Intent(mContract.getActivity(), LoginActivity.class));
                } else {
                    mContract.showActivity(new Intent(mContract.getActivity(), HomeTimelineActivity.class));
                }
            }
        }, timeout);
    }

    public interface ViewContract {

        AppCompatActivity getActivity();

        void showActivity(Intent intent);
    }
}
