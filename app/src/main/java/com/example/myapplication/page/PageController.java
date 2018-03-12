package com.example.myapplication.page;

import com.example.myapplication.NetworkLoader;
import com.example.myapplication.ui.ScrollingActivity;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;

import java.util.List;
import java.util.Stack;

public class PageController {
    public final static String HOME_PAGE = "kremlinrussia";

    private static PageController pageController = null;

    private ScrollingActivity activity;

    private Stack<Page> pages = new Stack<>();
    private Page currentPage = null;

    private PageController(ScrollingActivity activity){
        this.activity = activity;
    }

    public static PageController getInstance(ScrollingActivity activity){
        if(pageController == null)
            pageController = new PageController(activity);

        return pageController;
    }

    public void loadUserPage(String user){
        NetworkLoader.getInstance(activity).loadUserHeader(user);
    }

    public void loadTweetPage(Tweet tweet){
        NetworkLoader.getInstance(activity).loadExtendedTweet(tweet.getId());
        NetworkLoader.getInstance(activity).loadReplies(tweet);
    }

    public void createUserPage(User user){
        UserPage page = new UserPage(user);
        pages.push(page);
        currentPage = page;
        activity.setPageHeader(user);
        activity.showUserPage();
    }

    public void createTweetPage(Tweet tweet){
        TweetRepliesPage page = new TweetRepliesPage(tweet);
        pages.push(page);
        currentPage = page;
        activity.setPageHeader(tweet.getUser());
        activity.showTweetPage(tweet);
    }

    public void addFirstTweets(List<Tweet> tweets){
        currentPage.addNewTweets(tweets);
        for(int i = tweets.size() - 1; i >= 0; i-- ) {
            activity.getAdapter().insert(tweets.get(i), 0);
        }
    }

    public void addLastTweets(List<Tweet> tweets){
        currentPage.addOldTweets(tweets);
        activity.getAdapter().addAll(tweets);
    }

    public void replaceTweets(List<Tweet> tweets){
        currentPage.replaceTweets(tweets);
        activity.getAdapter().clear();
        activity.getAdapter().addAll(tweets);
    }

    public Page popPage(){
        Page page = pages.pop();
        currentPage = pages.size() == 0 ? null : pages.peek();

        if(currentPage instanceof UserPage){
            activity.showUserPage();
            activity.setPageHeader(currentPage.getUser());
        } else {
            TweetRepliesPage tweet_page = (TweetRepliesPage) currentPage;
            activity.showTweetPage(tweet_page.getHeaderTweet());
            activity.setPageHeader(tweet_page.getUser());
        }


        return page;
    }

    public int getPagesCount(){
        return pages.size();
    }

    public Page currentPage(){
        return currentPage;
    }
}
