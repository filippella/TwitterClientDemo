package uk.co.tae.twitterclientexercise.api.callbacks;


import retrofit.Callback;
import retrofit.http.GET;

public interface TweetApi {

    @GET("/statuses/home_timeline.json")
    void getTweets(Callback<String> callback);
}