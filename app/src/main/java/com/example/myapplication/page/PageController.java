package com.example.myapplication.page;

import com.example.myapplication.NetworkLoader;
import com.example.myapplication.Utils;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;
import com.example.myapplication.ui.ScrollingActivity;

import java.util.List;
import java.util.Stack;

import static com.example.myapplication.page.PageController.PageType.TWEET_PAGE;
import static com.example.myapplication.page.PageController.PageType.USER_PAGE;

public class PageController {

    public enum PageType {USER_PAGE, TWEET_PAGE}

    public final static String HOME_PAGE = "navalny";

    private static PageController pageController = null;

    private Stack<Page> pages = new Stack<>();
    private Page currentPage = null;

    private PageController() {
    }

    public static PageController getInstance() {
        if (pageController == null)
            pageController = new PageController();

        return pageController;
    }

    public void loadUserPage(String user) {
        NetworkLoader.getInstance().loadUserHeader(user);
    }

    public void loadTweetPage(Tweet tweet) {
        NetworkLoader.getInstance().loadExtendedTweet(tweet.getId());
    }

    public void createUserPage(User user) {
        UserPage page = new UserPage(user);
        pages.push(page);
        currentPage = page;
        ScrollingActivity.getInstance().setPageHeader(user);
        ScrollingActivity.getInstance().showUserPage();
    }

    public void createTweetPage(Tweet tweet) {
        TweetPage page = new TweetPage(tweet);
        pages.push(page);
        currentPage = page;
        ScrollingActivity.getInstance().setPageHeader(tweet.getUser());
        ScrollingActivity.getInstance().showTweetPage(tweet);
    }

    public void addFirstTweets(List<Tweet> tweets) {
        currentPage.addNewTweets(tweets);
        for (int i = tweets.size() - 1; i >= 0; i--) {
            ScrollingActivity.getInstance().getAdapter().insert(tweets.get(i), 0);
        }
        refreshIDfile();
    }

    public void addLastTweets(List<Tweet> tweets) {
        currentPage.addOldTweets(tweets);
        ScrollingActivity.getInstance().getAdapter().addAll(tweets);
        refreshIDfile();
    }

    public void replaceTweets(List<Tweet> tweets) {
        currentPage.replaceTweets(tweets);
        ScrollingActivity.getInstance().getAdapter().clear();
        ScrollingActivity.getInstance().getAdapter().addAll(tweets);
        refreshIDfile();
    }

    public Page popPage() {
        Page page = pages.pop();
        currentPage = pages.size() == 0 ? null : pages.peek();

        if (currentPage instanceof UserPage) {
            ScrollingActivity.getInstance().showUserPage();
            ScrollingActivity.getInstance().setPageHeader(currentPage.getUser());
        } else {
            TweetPage tweet_page = (TweetPage) currentPage;
            ScrollingActivity.getInstance().showTweetPage(tweet_page.getHeaderTweet());
            ScrollingActivity.getInstance().setPageHeader(tweet_page.getUser());
        }

        return page;
    }

    public void comeBackHome() {
        while (pages.size() > 1) {
            pages.pop();
        }
        currentPage = pages.peek();

        ScrollingActivity.getInstance().showUserPage();
        ScrollingActivity.getInstance().setPageHeader(currentPage.getUser());
    }

    public Page getHomePage() {
        return pages.get(0);
    }

    public int getPagesCount() {
        return pages.size();
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public PageType getCurrentPageType() {
        if (currentPage instanceof UserPage)
            return USER_PAGE;
        else
            return TWEET_PAGE;
    }

    private void refreshIDfile(){
        if (currentPage.getUser().getScreenName().toLowerCase().equals(HOME_PAGE) && getCurrentPageType() == USER_PAGE) {
            Utils.writeIDtoFile(ScrollingActivity.getInstance(), currentPage.newestID());
        }
    }
}
