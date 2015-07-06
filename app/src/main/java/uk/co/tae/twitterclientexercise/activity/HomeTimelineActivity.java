package uk.co.tae.twitterclientexercise.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.twitter.sdk.android.core.OAuthSigning;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.tae.twitterclientexercise.R;
import uk.co.tae.twitterclientexercise.adapter.HomeTimelineTweetAdapter;
import uk.co.tae.twitterclientexercise.api.ApiManager;
import uk.co.tae.twitterclientexercise.api.callbacks.TweetApi;
import uk.co.tae.twitterclientexercise.api.callbacks.VerifyCredentialApi;
import uk.co.tae.twitterclientexercise.custom.DividerItemDecoration;
import uk.co.tae.twitterclientexercise.model.User;
import uk.co.tae.twitterclientexercise.utilities.ConnectionUtils;
import uk.co.tae.twitterclientexercise.model.Constants;
import uk.co.tae.twitterclientexercise.utilities.SharedPreferenceUtils;

public class HomeTimelineActivity extends BaseActivity<Integer> {

    private static final String TAG = HomeTimelineActivity.class.getSimpleName();
    private static int REFRESH_TIME_OUT = 80000;
    private static final int TWEET_COMPOSER_REQUEST_CODE = 100;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<User> mUsers;
    private ImageView mPostStatusBtn;
    private ApiManager mApiManager;

    @Override
    protected Integer getViewResource() {
        return R.layout.activity_timeline;
    }

    @Override
    protected int getToolbarId() {
        return R.id.tool_bar;
    }

    private void configActionBar() {
        String username = SharedPreferenceUtils.getValue(getApplicationContext(), Constants.Credential.USERNAME);
        getToolbar().setTitle("Hello :: " + username);
        getToolbar().setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(getToolbar());
    }


    @Override
    protected void configViews(Intent intent, Bundle savedInstanceState) {
        configActionBar();
        setUpViews();
        mApiManager = new ApiManager();
        syncTimeline();
    }

    private boolean checkConnectivity() {
        if (!ConnectionUtils.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Sorry, there is no internet connection!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void syncTimeline() {

        if (checkConnectivity()) {
            String storedJSON = SharedPreferenceUtils.getValue(getApplicationContext(), Constants.StoreJSON.TWEET_JSON);
            if (storedJSON != null) {
                parseJSON(storedJSON);
            } else {
                playWithJSON();
            }
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();

        String token = SharedPreferenceUtils.getValue(getApplicationContext(), Constants.Credential.TOKEN);
        String secret = SharedPreferenceUtils.getValue(getApplicationContext(), Constants.Credential.SECRET);

        TwitterAuthToken authToken = new TwitterAuthToken(token, secret);
        OAuthSigning oauthSigning = new OAuthSigning(authConfig, authToken);


        Map<String, String> authHeaders = oauthSigning.getOAuthEchoHeaders("GET", "https://api.twitter.com/1.1/statuses/home_timeline.json", null);
        String authorizationHeader = authHeaders.get("X-Verify-Credentials-Authorization");

        //verifyCredential(authorizationHeader);

        manageTweets(authorizationHeader);

        Log.d(TAG, "oaut :: " + authorizationHeader);

        for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
            Log.d(TAG, "Key :: " + entry.getKey() + " Value :: " + entry.getValue());
        }

    }

    private void manageTweets(String authorizationHeader) {

        TweetApi tweets = mApiManager.getTweetApi(authorizationHeader);
        tweets.getTweets(new retrofit.Callback<String>() {
            @Override
            public void success(String tweets, Response response) {
                Log.d(TAG, tweets);
                new SaveJSON(tweets).start();
                parseJSON(tweets);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(getApplicationContext(), "Sorry, Error Occurred! " + error.getMessage(), Toast.LENGTH_LONG).show();

                Log.e(TAG, error.getMessage() + " ::: " + error.getResponse().getStatus() + " >>> Url :: " + error.getResponse().getUrl());
                for (retrofit.client.Header header : error.getResponse().getHeaders()) {
                    Log.e(TAG, "Headers >>> " + header.getName() + " :::: " + header.getValue());
                }

                String storedJSON = SharedPreferenceUtils.getValue(getApplicationContext(), Constants.StoreJSON.TWEET_JSON);
                if (storedJSON != null) {
                    parseJSON(storedJSON);
                } else {
                    playWithJSON();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void verifyCredential(String authorizationHeader) {

        VerifyCredentialApi credentials = mApiManager.getVerifyCredentialApi(authorizationHeader);
        credentials.verifyCredentials(new retrofit.Callback<String>() {
            @Override
            public void success(String credential, Response response) {
                Log.d(TAG, credential);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void setUpViews() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mPostStatusBtn = (ImageView) findViewById(R.id.postStatus);
        mRecyclerView = (RecyclerView) findViewById(R.id.timeline_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            protected void hide() {
                mPostStatusBtn.animate().translationY(mPostStatusBtn.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
            }

            @Override
            protected void show() {
                mPostStatusBtn.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });

        mUsers = new ArrayList<>();
        mAdapter = new HomeTimelineTweetAdapter(mUsers);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.green), getResources().getColor(R.color.orange), getResources().getColor(R.color.blue));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        mPostStatusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postUpdate();
            }
        });
    }

    private void postUpdate() {
        final View dialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(dialog)
                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editText = (EditText) dialog.findViewById(R.id.statusMsg);
                        String updateStatus = editText.getText().toString();
                        postStatusUpdate(updateStatus);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void postStatusUpdate(String updateStatus) {
        Intent intent = new TweetComposer.Builder(HomeTimelineActivity.this)
                .text(updateStatus)
                .createIntent();
        startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);
    }

    private void playWithJSON() {

        try {
            InputStream inputJSON = getResources().getAssets().open("twitter.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputJSON, "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            parseJSON(builder.toString());
        } catch (Exception ex) {
            Log.e(TAG, "Error Occurred :: " + ex.getMessage());
        }
    }

    private void parseJSON(String json) {
        mUsers.clear();
        mAdapter.notifyDataSetChanged();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String createdDate = jsonObject.getString("created_at");
                String text = jsonObject.getString("text");

                JSONObject jsonUserObject = jsonObject.getJSONObject("user");

                String name = jsonUserObject.getString("name");
                String twitterHandle = jsonUserObject.getString("screen_name");
                String profileImageUrl = jsonUserObject.getString("profile_image_url");

                mUsers.add(new User(profileImageUrl, name, twitterHandle, createdDate, text));
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshContent() {
        syncTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_timeline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            confirmLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TWEET_COMPOSER_REQUEST_CODE:
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        syncTimeline();
                    }
                }, REFRESH_TIME_OUT);*/
                Toast.makeText(getApplicationContext(), "Refresh me now", Toast.LENGTH_LONG).show();
                break;
        }
        Log.d(TAG, "Request Code :: " + requestCode + " -- Result Code :: " + resultCode + " {TWEET_COMPOSER_REQUEST_CODE :: " + TWEET_COMPOSER_REQUEST_CODE + " }");
    }

    private void confirmLogout() {
        new AlertDialog.Builder(HomeTimelineActivity.this)
                .setTitle("Logout?")
                .setMessage("Do you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    private void logout() {
        SharedPreferenceUtils.clearSharedPreference(getApplicationContext());
        startActivity(new Intent(HomeTimelineActivity.this, LoginActivity.class));
        finish();
    }

    public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

        private int mScrollDist = 0;
        private boolean mIsVisible = true;
        private static final float MINIMUM = 25;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (mIsVisible && mScrollDist > MINIMUM) {
                hide();
                mScrollDist = 0;
                mIsVisible = false;
            } else if (!mIsVisible && mScrollDist < -MINIMUM) {
                show();
                mScrollDist = 0;
                mIsVisible = true;
            }
            if ((mIsVisible && dy > 0) || (!mIsVisible && dy < 0)) {
                mScrollDist += dy;
            }
        }

        protected abstract void hide();

        protected abstract void show();
    }

    public class SaveJSON extends Thread {

        private String mJSON;

        public SaveJSON(String json) {
            mJSON = json;
        }

        @Override
        public void run() {
            super.run();
            SharedPreferenceUtils.save(getApplicationContext(), Constants.StoreJSON.TWEET_JSON, mJSON);
        }
    }
}
