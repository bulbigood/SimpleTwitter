package com.example.myapplication.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.page.PageController;

import java.util.List;

public class TweetsListAdapter extends ArrayAdapter<Tweet> {

    private ScrollingActivity activity;
    private List<Tweet> tweets;
    private int resource;

    //constructor, call on creation
    public TweetsListAdapter(ScrollingActivity activity, int resource, List<Tweet> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.tweets = objects;
        this.resource = resource;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        Tweet tweet = tweets.get(position);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resource, null);

        //TextView screen_name = view.findViewById(R.id.tweet_screen_name);
        TextView name = view.findViewById(R.id.tweet_name);
        TextView date = view.findViewById(R.id.tweet_date);
        TextView text = view.findViewById(R.id.tweet_text);

        //String screen_name_text = "@" + tweet.getUser().getScreenName();

        //Фавориты, ретвиты...
        String favor_count = String.valueOf(tweet.getFavoriteCount());
        String retweets_count = String.valueOf(tweet.getRetweetCount());
        TextView favor = view.findViewById(R.id.tweet_favorite_count);
        TextView retweet = view.findViewById(R.id.tweet_retweet_count);
        favor.setText(favor_count);
        retweet.setText(retweets_count);

        //screen_name.setText(screen_name_text);
        name.setText(tweet.getUser().getName());
        text.setText(tweet.getText());
        date.setText(Utils.getRelativeTime(tweet.getCreatedAt()));

        Utils.loadImages(view, tweet);

        ImageButton replyButton = view.findViewById(R.id.replyButton);
        replyButton.setOnClickListener(new ReplyButtonListener(PageController.getInstance(activity), tweet));

        return view;
    }
}
