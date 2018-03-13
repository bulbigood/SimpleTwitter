package com.example.myapplication.page;

import com.example.myapplication.NetworkLoader;
import com.example.myapplication.ui.ScrollingActivity;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;

import java.util.List;
import java.util.Stack;

public class PageController {

    public enum PageType{ USER_PAGE, TWEET_PAGE }

    public final static String HOME_PAGE = "kremlinrussia";

    private static PageController pageController = null;

    private Stack<Page> pages = new Stack<>();
    private Page currentPage = null;

    private PageController(){ }

    public static PageController getInstance(){
        if(pageController == null)
            pageController = new PageController();

        return pageController;
    }

    public void loadUserPage(String user){
        NetworkLoader.getInstance().loadUserHeader(user);
    }

    public void loadTweetPage(Tweet tweet){
        NetworkLoader.getInstance().loadExtendedTweet(tweet.getId());
        NetworkLoader.getInstance().loadReplies(tweet, NetworkLoader.TweetsLoadType.NEW_PAGE);
    }

    public void createUserPage(User user){
        UserPage page = new UserPage(user);
        pages.push(page);
        currentPage = page;
        ScrollingActivity.getInstance().setPageHeader(user);
        ScrollingActivity.getInstance().showUserPage();
    }

    public void createTweetPage(Tweet tweet){
        TweetPage page = new TweetPage(tweet);
        pages.push(page);
        currentPage = page;
        ScrollingActivity.getInstance().setPageHeader(tweet.getUser());
        ScrollingActivity.getInstance().showTweetPage(tweet);
    }

    public void addFirstTweets(List<Tweet> tweets){
        currentPage.addNewTweets(tweets);
        for(int i = tweets.size() - 1; i >= 0; i-- ) {
            ScrollingActivity.getInstance().getAdapter().insert(tweets.get(i), 0);
        }
    }

    public void addLastTweets(List<Tweet> tweets){
        currentPage.addOldTweets(tweets);
        ScrollingActivity.getInstance().getAdapter().addAll(tweets);
    }

    public void replaceTweets(List<Tweet> tweets){
        currentPage.replaceTweets(tweets);
        ScrollingActivity.getInstance().getAdapter().clear();
        ScrollingActivity.getInstance().getAdapter().addAll(tweets);
    }

    public Page popPage(){
        Page page = pages.pop();
        currentPage = pages.size() == 0 ? null : pages.peek();

        if(currentPage instanceof UserPage){
            ScrollingActivity.getInstance().showUserPage();
            ScrollingActivity.getInstance().setPageHeader(currentPage.getUser());
        } else {
            TweetPage tweet_page = (TweetPage) currentPage;
            ScrollingActivity.getInstance().showTweetPage(tweet_page.getHeaderTweet());
            ScrollingActivity.getInstance().setPageHeader(tweet_page.getUser());
        }


        return page;
    }

    public int getPagesCount(){
        return pages.size();
    }

    public Page getCurrentPage(){
        return currentPage;
    }

    public PageType getCurrentPageType(){
        if (currentPage instanceof UserPage)
            return PageType.USER_PAGE;
        else
            return PageType.TWEET_PAGE;
    }
}
