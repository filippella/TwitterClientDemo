package uk.co.tae.twitterclientexercise.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import uk.co.tae.twitterclientexercise.R;
import uk.co.tae.twitterclientexercise.model.Constants;
import uk.co.tae.twitterclientexercise.utilities.SharedPreferenceUtils;


public class LoginActivity extends BaseActivity<Integer> {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TwitterLoginButton mLoginButton;
    private TwitterSession mSession;

    @Override
    protected Integer getViewResource() {
        return R.layout.activity_main;
    }

    private void configActionBar() {
        getToolbar().setTitle("Login");
        getToolbar().setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(getToolbar());
    }

    @Override
    protected int getToolbarId() {
        return R.id.tool_bar;
    }

    @Override
    protected void configViews(Intent intent, Bundle savedInstanceState) {
        configActionBar();
        configViews();
    }

    private void configViews() {

        mLoginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                mSession = result.data;
                TwitterAuthToken authToken = mSession.getAuthToken();

                String token = authToken.token;
                String secret = authToken.secret;
                String userName = mSession.getUserName();
                long userId = mSession.getUserId();

                saveCredentials(token, secret, userName, userId);

                Toast.makeText(LoginActivity.this, "Authentication Success", Toast.LENGTH_SHORT);
                Log.d(TAG, "" + "Token :: " + token + " || Secret :: " + secret + " || " + userName + " || " + userId);
            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(LoginActivity.this, "Authentication Failed!!! " + e.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void saveCredentials(String token, String secret, String userName, long userId) {
        SharedPreferenceUtils.save(getApplicationContext(), Constants.Credential.TOKEN, token);
        SharedPreferenceUtils.save(getApplicationContext(), Constants.Credential.SECRET, secret);
        SharedPreferenceUtils.save(getApplicationContext(), Constants.Credential.USERNAME, userName);
        SharedPreferenceUtils.save(getApplicationContext(), Constants.Credential.USER_ID, String.valueOf(userId));
        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "" + "Result Code :: " + resultCode + " request code :: " + requestCode + " ");

        switch (requestCode) {
            case 140:
                switch (resultCode) {
                    case 0:
                        unauthorized();
                        return;
                    case -1:
                        authorized(requestCode, resultCode, data);
                        break;
                    default:
                        undefinedAccess(resultCode);
                }
                break;
        }
    }

    private void undefinedAccess(int resultCode) {
        Toast.makeText(getApplicationContext(), "Authorize failed! Undefined Access. Received result code :: " + resultCode, Toast.LENGTH_LONG).show();
    }

    private void authorized(int requestCode, int resultCode, Intent data) {
        mLoginButton.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(LoginActivity.this, HomeTimelineActivity.class));
        finish();
    }

    private void unauthorized() {
        Toast.makeText(getApplicationContext(), "Authorize failed! Unable to get Login credential.", Toast.LENGTH_LONG).show();
    }
}
