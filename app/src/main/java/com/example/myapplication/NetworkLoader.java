package com.example.myapplication;

import android.util.Log;
import android.view.View;
import com.example.myapplication.api.structure.SearchResult;
import com.example.myapplication.api.structure.Status;
import com.example.myapplication.api.structure.User;
import com.example.myapplication.api.AppInitializer;
import com.example.myapplication.api.BearerToken;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.URLEncoder;
import com.example.myapplication.page.PageController;
import com.example.myapplication.page.TweetPage;
import com.example.myapplication.ui.ScrollingActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class NetworkLoader {

    public enum TweetsLoadType{
        NEW_PAGE, UPDATE_NEW, UPDATE_OLD
    }

    public final static int DEFAULT_TWEETS_COUNT = 10;
    public final static int REFRESH_TWEETS_COUNT = 5;

    public final static int DEFAULT_SEARCH_COUNT = 100;
    public final static int REFRESH_SEARCH_COUNT = 50;

    private static final String CONSUMER_KEY = "e6z8OmNtVqHgViBcFaH0Fx8CL";
    private static final String CONSUMER_SECRET = "LiHtrwLPKkClHdvvEqnti6BhPbZN9nlneWe9NjhohP5Yjlvx4v";

    private static NetworkLoader networkLoader = null;

    private BearerToken token = null;

    private NetworkLoader(){ }

    public static NetworkLoader getInstance(){
        if(networkLoader == null)
            networkLoader = new NetworkLoader();
        return networkLoader;
    }

    public boolean loadBearerToken(){
        if(token == null) {
            String authCode = "Basic " + URLEncoder.getBearerTokenCredentials(CONSUMER_KEY, CONSUMER_SECRET);
            AppInitializer.getApi().getBearerToken("client_credentials", authCode).enqueue(new CallbackToken());
        }
        return true;
    }

    public boolean loadUserHeader(String screen_name){
        if(token == null) return false;
        AppInitializer.getApi().getUser(screen_name, token.getRequestToken()).enqueue(new CallbackUserPageHeader());
        return true;
    }

    public boolean loadTweet(Long id, TweetsLoadType loadType){
        if(token == null) return false;
        AppInitializer.getApi().getTweet(id, null, token.getRequestToken()).enqueue(new CallbackTweets(loadType));
        return true;
    }

    public boolean loadExtendedTweet(Long id){
        if(token == null) return false;
        AppInitializer.getApi().getTweet(id, "extended", token.getRequestToken()).enqueue(new CallbackExtendedTweet());
        return true;
    }

    public boolean loadTweets(String screen_name, TweetsLoadType loadType){
        if(token == null) return false;

        PageController controller = PageController.getInstance();
        if(screen_name == null) {
            screen_name = controller.getCurrentPage().getUser().getScreenName();
        }

        switch(loadType){
            case NEW_PAGE:
                AppInitializer.getApi().getTimeline(screen_name, DEFAULT_TWEETS_COUNT, null, null,
                        token.getRequestToken()).enqueue(new CallbackTweets(loadType));
                break;
            case UPDATE_NEW:
                AppInitializer.getApi().getTimeline(screen_name, REFRESH_TWEETS_COUNT,
                        controller.getCurrentPage().newestID(), null, token.getRequestToken()).enqueue(new CallbackTweets(loadType));
                ScrollingActivity.getInstance().findViewById(R.id.progressBarTop).setVisibility(View.VISIBLE);
                break;
            case UPDATE_OLD:
                AppInitializer.getApi().getTimeline(screen_name, REFRESH_TWEETS_COUNT,
                        null, controller.getCurrentPage().oldestID() - 1, token.getRequestToken()).enqueue(new CallbackTweets(loadType));
                ScrollingActivity.getInstance().findViewById(R.id.progressBarBottom).setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }

    public boolean loadReplies(Tweet tweet, TweetsLoadType loadType){
        PageController controller = PageController.getInstance();
        if(token == null) return false;

        TweetPage page = null;
        if(tweet == null) {
            page = (TweetPage)controller.getCurrentPage();
            tweet = page.getHeaderTweet();
        }
        String q = "to:" + tweet.getUser().getScreenName();

        switch(loadType){
            case NEW_PAGE:
                AppInitializer.getApi().searchTweets(q, DEFAULT_SEARCH_COUNT, tweet.getId(), null,
                        token.getRequestToken()).enqueue(new CallbackSearch(loadType));
                break;
            case UPDATE_NEW:
                AppInitializer.getApi().searchTweets(q, REFRESH_SEARCH_COUNT, page.newestID(), null,
                        token.getRequestToken()).enqueue(new CallbackSearch(loadType));
                ScrollingActivity.getInstance().findViewById(R.id.progressBarTop).setVisibility(View.VISIBLE);
                break;
            case UPDATE_OLD:
                AppInitializer.getApi().searchTweets(q, REFRESH_SEARCH_COUNT, tweet.getId(), page.oldestID() - 1,
                        token.getRequestToken()).enqueue(new CallbackSearch(loadType));
                ScrollingActivity.getInstance().findViewById(R.id.progressBarBottom).setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }

    private class CallbackToken implements Callback<BearerToken> {

        @Override
        public void onResponse(Call<BearerToken> call, Response<BearerToken> response) {
            PageController controller = PageController.getInstance();
            token = response.body();
            if(token == null)
                Log.e("API_ERROR", response.message());
            else if(controller.getCurrentPage() == null) {
                controller.loadUserPage(PageController.HOME_PAGE);
            }
        }

        @Override
        public void onFailure(Call<BearerToken> call, Throwable t) {
            Log.e("API_CONNECTION_ERROR", t.getMessage());
        }
    }

    private class CallbackUserPageHeader implements Callback<User> {

        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            User body = response.body();
            if(body == null)
                Log.e("API_ERROR", response.message());
            else {
                PageController.getInstance().createUserPage(body);
                loadTweets(body.getScreenName(), NetworkLoader.TweetsLoadType.NEW_PAGE);
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.e("API_CONNECTION_ERROR", t.getMessage());
        }
    }

    private class CallbackTweets<T> implements Callback<T> {

        private TweetsLoadType loadType;

        private CallbackTweets(TweetsLoadType type){
            loadType = type;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            List<Tweet> body = new ArrayList<>();
            if(response.body() instanceof List){
                body = (List<Tweet>)response.body();
            } else {
                body.add((Tweet)response.body());
            }

            if(body == null)
                Log.e("API_ERROR", response.message());
            else {
                PageController controller = PageController.getInstance();
                ScrollingActivity.getInstance().findViewById(R.id.progressBarTop).setVisibility(View.GONE);
                ScrollingActivity.getInstance().findViewById(R.id.progressBarBottom).setVisibility(View.GONE);
                switch(loadType){
                    case NEW_PAGE:
                        controller.addFirstTweets(body);
                        break;
                    case UPDATE_NEW:
                        if(body.size() == REFRESH_TWEETS_COUNT)
                            controller.replaceTweets(body);
                        else
                            controller.addFirstTweets(body);
                        break;
                    case UPDATE_OLD:
                        controller.addLastTweets(body);
                        break;
                }
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.e("API_CONNECTION_ERROR", t.getMessage());
        }
    }

    private class CallbackSearch implements Callback<SearchResult> {

        private TweetsLoadType loadType;

        private CallbackSearch(TweetsLoadType type){
            loadType = type;
        }

        @Override
        public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
            SearchResult body = response.body();
            if(body == null)
                Log.e("API_ERROR", response.message());
            else {
                int load_size = loadType == TweetsLoadType.NEW_PAGE? DEFAULT_TWEETS_COUNT : REFRESH_TWEETS_COUNT;
                ArrayList<Long> searched = new ArrayList();

                for(Status status : body.getStatuses()){
                    PageController controller = PageController.getInstance();
                    TweetPage page = null;
                    if(controller.getCurrentPage() instanceof TweetPage){
                        page = (TweetPage) controller.getCurrentPage();
                    }
                    if(page != null) {
                        //loadTweet(status.getId());
                        String reply_id = status.getInReplyToStatusIdStr();
                        String tweet_id = page.getHeaderTweet().getIdStr();
                        if (tweet_id.equals(reply_id)){
                            searched.add(status.getId());
                        }
                    }
                }

                int i = Math.min(searched.size(), load_size) - 1;
                if(i < 0){
                    ScrollingActivity.getInstance().findViewById(R.id.progressBarTop).setVisibility(View.GONE);
                    ScrollingActivity.getInstance().findViewById(R.id.progressBarBottom).setVisibility(View.GONE);
                }
                for( ; i >= 0; i--){
                    loadTweet(searched.get(i), loadType);
                }
            }
        }

        @Override
        public void onFailure(Call<SearchResult> call, Throwable throwable) {
            Log.e("API_CONNECTION_ERROR", throwable.getMessage());
        }
    }

    private class CallbackExtendedTweet implements Callback<Tweet> {

        @Override
        public void onResponse(Call<Tweet> call, Response<Tweet> response) {
            Tweet body = response.body();
            if(body == null)
                Log.e("API_ERROR", response.message());
            else {
                PageController.getInstance().createTweetPage(body);
                NetworkLoader.getInstance().loadReplies(body, NetworkLoader.TweetsLoadType.NEW_PAGE);
            }
        }

        @Override
        public void onFailure(Call<Tweet> call, Throwable throwable) {
            Log.e("API_CONNECTION_ERROR", throwable.getMessage());
        }
    }
}
