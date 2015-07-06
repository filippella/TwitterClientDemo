package uk.co.tae.twitterclientexercise.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.tae.twitterclientexercise.R;
import uk.co.tae.twitterclientexercise.model.User;
import uk.co.tae.twitterclientexercise.utilities.DateUtils;

/**
 * Created by Filippo-TheAppExpert on 6/25/2015.
 */
public class HomeTimelineTweetAdapter extends RecyclerView.Adapter<HomeTimelineTweetAdapter.Holder> {

    private List<User> mList;

    public HomeTimelineTweetAdapter(List<User> list) {
        mList = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_row, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        View rowHolder = holder.itemView;
        ImageView profileImage = (ImageView) rowHolder.findViewById(R.id.profile_image);
        TextView name = (TextView) rowHolder.findViewById(R.id.name);
        TextView twitterHandle = (TextView) rowHolder.findViewById(R.id.twitter_handle);
        TextView postedDate = (TextView) rowHolder.findViewById(R.id.post_age);
        TextView tweetContent = (TextView) rowHolder.findViewById(R.id.tweet_content);

        User currentUser = mList.get(position);
        name.setText(currentUser.mProfileName);
        twitterHandle.setText("@" + currentUser.mTwitterHandle);
        tweetContent.setText(currentUser.mTweetContent);

        //DateUtils.with(rowHolder.getContext()).setDate(currentUser.mPostedDate).into(postedDate);
        new DateUtils(postedDate, currentUser.mPostedDate);
        Picasso.with(rowHolder.getContext()).load(currentUser.mImageUrl).into(profileImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
