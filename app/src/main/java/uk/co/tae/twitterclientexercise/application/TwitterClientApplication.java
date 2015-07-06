package uk.co.tae.twitterclientexercise.application;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;
import uk.co.tae.twitterclientexercise.model.Constants;

/**
 * Created by Filippo-TheAppExpert on 6/23/2015.
 */
public class TwitterClientApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(Constants.SigningKey.TWITTER_KEY, Constants.SigningKey.TWITTER_SECRET);

        Fabric.with(this, new TwitterCore(authConfig), new Digits(), new TweetComposer());
    }
}
