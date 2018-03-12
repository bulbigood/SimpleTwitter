package com.example.myapplication.page;

import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TweetRepliesPage implements Page{

    private LinkedList<Tweet> replies = new LinkedList();
    private Tweet tweet;

    private long oldestID = -1;
    private long newestID = -1;

    TweetRepliesPage(Tweet tw){
        tweet = tw;
    }

    @Override
    public long oldestID() {
        return oldestID;
    }

    @Override
    public long newestID() {
        return newestID;
    }

    @Override
    public List<Tweet> getTweets(){
        return new ArrayList(replies);
    }

    @Override
    public User getUser() {
        return tweet.getUser();
    }

    @Override
    public int tweetsCount() {
        return replies.size();
    }

    @Override
    public void addNewTweets(List<Tweet> list) {
        replies.addAll(0, list);
        refreshIDs();
    }

    @Override
    public void addOldTweets(List<Tweet> list) {
        replies.addAll(list);
        refreshIDs();
    }

    @Override
    public void replaceTweets(List<Tweet> list) {
        replies.clear();
        replies.addAll(list);
        refreshIDs();
    }

    public Tweet getHeaderTweet(){
        return tweet;
    }

    private void refreshIDs(){
        newestID = replies.get(0).getId();
        oldestID = replies.get(replies.size() - 1).getId();
    }
}
