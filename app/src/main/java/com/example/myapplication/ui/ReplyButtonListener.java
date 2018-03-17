package com.example.myapplication.ui;

import android.view.View;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.page.PageController;

public class ReplyButtonListener implements View.OnClickListener {

    private PageController controller;
    private Tweet tweet;

    public ReplyButtonListener(PageController controller, Tweet tweet) {
        this.controller = controller;
        this.tweet = tweet;
    }

    @Override
    public void onClick(View v) {
        controller.loadTweetPage(tweet);
    }
}
