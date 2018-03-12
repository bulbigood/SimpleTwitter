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
import com.example.myapplication.page.TweetRepliesPage;
import com.example.myapplication.ui.ScrollingActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class NetworkLoader {

    public enum TweetsLoadType{
        NEW_USER_PAGE, NEW_TWEET_PAGE, UPDATE_NEW, UPDATE_OLD
    }

    public final static int DEFAULT_TWEETS_COUNT = 10;
    public final static int REFRESH_TWEETS_COUNT = 5;

    private static final String CONSUMER_KEY = "e6z8OmNtVqHgViBcFaH0Fx8CL";
    private static final String CONSUMER_SECRET = "LiHtrwLPKkClHdvvEqnti6BhPbZN9nlneWe9NjhohP5Yjlvx4v";

    private static NetworkLoader networkLoader = null;

    private ScrollingActivity activity;
    private BearerToken token = null;

    private NetworkLoader(ScrollingActivity activity){
        this.activity = activity;
    }

    public static NetworkLoader getInstance(ScrollingActivity activity){
        if(networkLoader == null)
            networkLoader = new NetworkLoader(activity);
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

    public boolean loadTweet(Long id){
        if(token == null) return false;
        AppInitializer.getApi().getTweet(id, token.getRequestToken()).enqueue(new CallbackTweet());
        return true;
    }

    public boolean loadTweets(String screen_name, TweetsLoadType loadType){
        if(token == null) return false;

        PageController controller = PageController.getInstance(activity);

        if(screen_name == null)
            screen_name = controller.currentPage().getUser().getScreenName();

        switch(loadType){
            case NEW_USER_PAGE:
                AppInitializer.getApi().getTimeline(screen_name, DEFAULT_TWEETS_COUNT, null, null,
                        token.getRequestToken()).enqueue(new CallbackTweets(loadType));
                break;
            case UPDATE_NEW:
                AppInitializer.getApi().getTimeline(screen_name, REFRESH_TWEETS_COUNT,
                        controller.currentPage().newestID(), null, token.getRequestToken()).enqueue(new CallbackTweets(loadType));
                activity.findViewById(R.id.progressBarTop).setVisibility(View.VISIBLE);
                break;
            case UPDATE_OLD:
                AppInitializer.getApi().getTimeline(screen_name, REFRESH_TWEETS_COUNT,
                        null, controller.currentPage().oldestID() - 1, token.getRequestToken()).enqueue(new CallbackTweets(loadType));
                activity.findViewById(R.id.progressBarBottom).setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }

    public boolean loadReplies(Tweet tweet){
        if(token == null) return false;

        String q = "to:" + tweet.getUser().getScreenName();
        AppInitializer.getApi().searchTweets(q, 100, tweet.getId(),
                token.getRequestToken()).enqueue(new CallbackSearch());
        return true;
    }

    private class CallbackToken implements Callback<BearerToken> {

        @Override
        public void onResponse(Call<BearerToken> call, Response<BearerToken> response) {
            PageController controller = PageController.getInstance(activity);
            token = response.body();
            if(token == null)
                Log.e("API_ERROR", response.message());
            else if(controller.currentPage() == null) {
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
                PageController.getInstance(activity).createUserPage(body);
                loadTweets(body.getScreenName(), NetworkLoader.TweetsLoadType.NEW_USER_PAGE);
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.e("API_CONNECTION_ERROR", t.getMessage());
        }
    }

    private class CallbackTweets implements Callback<List<Tweet>> {

        private TweetsLoadType loadType;

        private CallbackTweets(TweetsLoadType type){
            loadType = type;
        }

        @Override
        public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
            List<Tweet> body = response.body();
            if(body == null)
                Log.e("API_ERROR", response.message());
            else {
                PageController controller = PageController.getInstance(activity);
                switch(loadType){
                    case NEW_USER_PAGE:
                        controller.addFirstTweets(body);
                        break;
                    case UPDATE_NEW:
                        if(body.size() == REFRESH_TWEETS_COUNT)
                            controller.replaceTweets(body);
                        else
                            controller.addFirstTweets(body);
                        activity.findViewById(R.id.progressBarTop).setVisibility(View.GONE);
                        break;
                    case UPDATE_OLD:
                        controller.addLastTweets(body);
                        activity.findViewById(R.id.progressBarBottom).setVisibility(View.GONE);
                        break;
                }
            }
        }

        @Override
        public void onFailure(Call<List<Tweet>> call, Throwable t) {
            Log.e("API_CONNECTION_ERROR", t.getMessage());
        }
    }

    private class CallbackSearch implements Callback<SearchResult> {

        @Override
        public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
            SearchResult body = response.body();
            if(body == null)
                Log.e("API_ERROR", response.message());
            else {
                for(Status status : body.getStatuses()){
                    PageController controller = PageController.getInstance(activity);
                    TweetRepliesPage page = (TweetRepliesPage) controller.currentPage();
                    if(page != null) {
                        //loadTweet(status.getId());
                        String reply_id = status.getInReplyToStatusIdStr();
                        String tweet_id = page.getHeaderTweet().getIdStr();
                        if (tweet_id.equals(reply_id))
                            loadTweet(status.getId());
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<SearchResult> call, Throwable throwable) {
            Log.e("API_CONNECTION_ERROR", throwable.getMessage());
        }
    }

    private class CallbackTweet implements Callback<Tweet> {

        @Override
        public void onResponse(Call<Tweet> call, Response<Tweet> response) {
            Tweet body = response.body();
            if(body == null)
                Log.e("API_ERROR", response.message());
            else {
                ArrayList<Tweet> list = new ArrayList<>();
                list.add(body);
                PageController.getInstance(activity).addLastTweets(list);
            }
        }

        @Override
        public void onFailure(Call<Tweet> call, Throwable throwable) {
            Log.e("API_CONNECTION_ERROR", throwable.getMessage());
        }
    }
}
