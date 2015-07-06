package uk.co.tae.twitterclientexercise.api.callbacks;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Filippo-TheAppExpert on 6/26/2015.
 */
public interface VerifyCredentialApi {
    @GET("/account/verify_credentials.json")
    void verifyCredentials(Callback<String> callback);
}
