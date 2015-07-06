package uk.co.tae.twitterclientexercise.presenter;

/**
 * Created by Filippo-TheAppExpert on 6/25/2015.
 */
public interface Presenter {

    void initialize();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
