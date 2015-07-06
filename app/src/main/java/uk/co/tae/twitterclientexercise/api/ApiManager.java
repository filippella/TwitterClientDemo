package uk.co.tae.twitterclientexercise.api;

import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import uk.co.tae.twitterclientexercise.api.callbacks.TweetApi;
import uk.co.tae.twitterclientexercise.api.callbacks.VerifyCredentialApi;
import uk.co.tae.twitterclientexercise.api.gson.StringDeserializer;
import uk.co.tae.twitterclientexercise.model.Constants;

/**
 * Created by Filippo-TheAppExpert on 6/4/2015.
 */
public class ApiManager {

    public static final String TAG = ApiManager.class.getSimpleName();
    private TweetApi mTweetApi;
    private VerifyCredentialApi mVerify;

    public TweetApi getTweetApi(final String authorization) {

            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(String.class, new StringDeserializer());

            mTweetApi = new RestAdapter.Builder()
                    .setEndpoint(Constants.Url.BASE_URL)
                    .setConverter(new GsonConverter(gson.create()))
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestInterceptor.RequestFacade request) {
                            request.addHeader(Constants.Headers.AUTHORIZATION_KEY, authorization);
                            request.addHeader(Constants.Headers.HOST_KEY, Constants.Headers.HOST_VALUE);
                            request.addHeader(Constants.Headers.CONTENT_TYPE_KEY, Constants.Headers.CONTENT_TYPE_VALUE);
                            request.addHeader(Constants.Headers.ACCEPT_KEY, Constants.Headers.ACCEPT_VALUE);
                            request.addHeader(Constants.Headers.USER_AGENT_KEY, Constants.Headers.USER_AGENT_VALUE);
                            request.addHeader(Constants.Headers.TARGET_URI_KEY, Constants.Headers.TARGET_URI_VALUE);
                            request.addHeader(Constants.Headers.KEEP_ALIVE_KEY, Constants.Headers.KEEP_ALIVE_VALUE);
                        }
                    })
                    .build()
                    .create(TweetApi.class);
        return mTweetApi;
    }

    public VerifyCredentialApi getVerifyCredentialApi(final String authorization) {

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(String.class, new StringDeserializer());

        mVerify = new RestAdapter.Builder()
                .setEndpoint(Constants.Url.BASE_URL)
                .setConverter(new GsonConverter(gson.create()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addHeader(Constants.Headers.AUTHORIZATION_KEY, authorization);
                        request.addHeader(Constants.Headers.HOST_KEY, Constants.Headers.HOST_VALUE);
                        request.addHeader(Constants.Headers.CONTENT_TYPE_KEY, Constants.Headers.CONTENT_TYPE_VALUE);
                        request.addHeader(Constants.Headers.ACCEPT_KEY, Constants.Headers.ACCEPT_VALUE);
                        request.addHeader(Constants.Headers.USER_AGENT_KEY, Constants.Headers.USER_AGENT_VALUE);
                        request.addHeader(Constants.Headers.TARGET_URI_KEY, Constants.Headers.TARGET_URI_VALUE);
                        request.addHeader(Constants.Headers.KEEP_ALIVE_KEY, Constants.Headers.KEEP_ALIVE_VALUE);
                    }
                })
                .build()
                .create(VerifyCredentialApi.class);
        return mVerify;
    }
}
