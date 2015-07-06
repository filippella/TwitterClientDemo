package uk.co.tae.twitterclientexercise.model;

/**
 * Created by Filippo-TheAppExpert on 6/23/2015.
 */
public class User {

    public String mImageUrl, mProfileName, mTwitterHandle, mPostedDate, mTweetContent;

    public User(String imageUrl, String profileName, String twitterHandle, String postedDate, String tweetContent) {
        mImageUrl = imageUrl;
        mProfileName = profileName;
        mTwitterHandle = twitterHandle;
        mPostedDate = postedDate;
        mTweetContent = tweetContent;
    }
}