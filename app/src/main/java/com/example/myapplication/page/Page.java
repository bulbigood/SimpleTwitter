package com.example.myapplication.page;

import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;

import java.util.List;

public interface Page {
    long oldestID();

    long newestID();

    int tweetsCount();

    List<Tweet> getTweets();

    User getUser();

    void addNewTweets(List<Tweet> list);

    void addOldTweets(List<Tweet> list);

    void replaceTweets(List<Tweet> list);
}
