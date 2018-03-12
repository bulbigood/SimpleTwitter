package com.example.myapplication.page;

import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Никита on 05.03.2018.
 */
public class UserPage implements Page{
    private LinkedList<Tweet> tweets = new LinkedList();
    private User user;

    private long oldestID = -1;
    private long newestID = -1;

    UserPage(User us){
        user = us;
    }

    @Override
    public long oldestID(){
        return oldestID;
    }

    @Override
    public long newestID(){
        return newestID;
    }

    @Override
    public int tweetsCount() {
        return tweets.size();
    }

    @Override
    public User getUser(){
        return user;
    }

    @Override
    public List<Tweet> getTweets(){
        return new ArrayList(tweets);
    }

    @Override
    public void addNewTweets(List<Tweet> m){
        tweets.addAll(0, m);
        refreshIDs();
    }

    @Override
    public void addOldTweets(List<Tweet> m){
        tweets.addAll(m);
        refreshIDs();
    }

    @Override
    public void replaceTweets(List<Tweet> m){
        tweets.clear();
        tweets.addAll(m);
        refreshIDs();
    }

    private void refreshIDs(){
        newestID = tweets.get(0).getId();
        oldestID = tweets.get(tweets.size() - 1).getId();
    }
}
