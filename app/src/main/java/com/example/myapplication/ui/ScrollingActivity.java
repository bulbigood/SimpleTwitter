package com.example.myapplication.ui;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.example.myapplication.NetworkLoader;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;
import com.example.myapplication.page.Page;
import com.example.myapplication.page.PageController;
import com.example.myapplication.page.TweetPage;
import com.example.myapplication.page.UserPage;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity implements NestedScrollView.OnScrollChangeListener{

    private static boolean initialized = false;
    private static ScrollingActivity activity = null;

    private ArrayAdapter adapter = null;
    private ExpandableHeightListView listView = null;

    private Point screen_size;
    private Rect scrollBounds = new Rect();
    private int firstVisiblePosition = 0;
    private int lastVisiblePosition = 0;
    private int positionsCount = 0;

    public static ScrollingActivity getInstance(){
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        initializeProgressBars();

        Display display = getWindowManager().getDefaultDisplay();
        screen_size = new Point();
        display.getSize(screen_size);

        if(!initialized) {
            NetworkLoader.getInstance().loadBearerToken();
            PageController.getInstance();
        } else {
            reloadInterface();
        }
        activity = this;
        initialized = true;
    }

    public void reloadInterface(){
        Page page = PageController.getInstance().getCurrentPage();

        if(page instanceof UserPage) {
            setPageHeader(page.getUser());
            showUserPage();
        } else {
            setPageHeader(page.getUser());
            showTweetPage(((TweetPage) page).getHeaderTweet());
        }
    }

    @Override
    public void onBackPressed() {
        PageController controller = PageController.getInstance();
        if(controller.getPagesCount() <= 1)
            super.onBackPressed();
        else
            controller.popPage();
    }

    public Point getScreenSize(){
        return screen_size;
    }

    public int getFirstVisiblePosition() {
        return firstVisiblePosition;
    }

    public int getLastVisiblePosition() {
        return lastVisiblePosition;
    }

    public int getPositionsCount() {
        return positionsCount;
    }

    public void showUserPage(){
        NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
        scrollView.setOnTouchListener(TouchListener.getInstance());
        scrollView.getHitRect(scrollBounds);
        scrollView.setOnScrollChangeListener(this);

        Page page = PageController.getInstance().getCurrentPage();
        List<Tweet> tweet_list = page == null? new ArrayList() : page.getTweets();
        adapter = new TweetsListAdapter(R.layout.tweet_layout, tweet_list);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        listView = (ExpandableHeightListView) inflater.inflate(R.layout.custom_list_view, null);
        listView.setAdapter(adapter);
        listView.setExpanded(true);

        LinearLayout layout = findViewById(R.id.contentLayout);
        layout.removeAllViews();
        layout.addView(listView);
    }

    public void showTweetPage(Tweet tweet){
        //Отключаю слушатель нажатий
        NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
        scrollView.setOnTouchListener(TouchListener.getInstance());
        scrollView.getHitRect(scrollBounds);
        scrollView.setOnScrollChangeListener(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View big_tweet = inflater.inflate(R.layout.tweet_big_layout, null);
        TextView text = big_tweet.findViewById(R.id.tweet_text);

        //Фавориты, ретвиты...
        String favor_count = String.valueOf(tweet.getFavoriteCount());
        String retweets_count = String.valueOf(tweet.getRetweetCount());
        TextView favor = big_tweet.findViewById(R.id.tweet_favorite_count);
        TextView retweet = big_tweet.findViewById(R.id.tweet_retweet_count);
        favor.setText(favor_count);
        retweet.setText(retweets_count);

        text.setText(tweet.getFullText());
        Utils.loadImages(big_tweet, tweet);

        Page page = PageController.getInstance().getCurrentPage();
        List<Tweet> tweet_list = page == null? new ArrayList() : page.getTweets();
        adapter = new RepliesListAdapter(R.layout.tweet_icon_layout, tweet_list);

        listView = (ExpandableHeightListView) inflater.inflate(R.layout.custom_list_view, null);
        listView.setAdapter(adapter);
        listView.setExpanded(true);

        LinearLayout layout = findViewById(R.id.contentLayout);
        layout.removeAllViews();
        layout.addView(big_tweet);
        layout.addView(listView);
    }

    public ArrayAdapter getAdapter(){
        return adapter;
    }

    public void setPageHeader(User user){
        final User final_user = user;
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);

        final TextView screen_name = findViewById(R.id.user_screen_name);
        ImageView icon = findViewById(R.id.user_icon_image);
        ImageView banner = findViewById(R.id.user_banner_image);

        //Слушатель развертки тулбара для показа @ScreenName
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (screen_name.getVisibility() != View.VISIBLE) {
                        screen_name.setVisibility(View.VISIBLE);
                    }
                } else if (screen_name.getVisibility() != View.GONE) {
                    screen_name.setVisibility(View.GONE); // hide title bar
                }
            }
        });

        //Изменение титульной надписи
        toolbarLayout.setTitle(user.getName());
        screen_name.setText("@" + final_user.getScreenName());

        //Загрузка изображений
        String icon_url = user.getProfileImageUrl();
        String banner_url = user.getProfileBannerUrl();
        icon_url = icon_url.replace("_normal.", ".");
        Glide.with(this).load(banner_url).into(banner);
        Glide.with(this).load(icon_url).into(icon);
    }

    public void initializeProgressBars(){
        ProgressBar spinnerTop = findViewById(R.id.progressBarTop);
        ProgressBar spinnerBottom = findViewById(R.id.progressBarBottom);
        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        ColorFilter cp = new PorterDuffColorFilter(colorPrimary, android.graphics.PorterDuff.Mode.MULTIPLY);

        spinnerTop.getIndeterminateDrawable().setColorFilter(cp);
        spinnerBottom.getIndeterminateDrawable().setColorFilter(cp);
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        positionsCount = listView.getChildCount();

        int i = 0;
        for ( ; i < positionsCount; i++) {
            View childView = listView.getChildAt(i);
            if (childView.getLocalVisibleRect(scrollBounds)) {
                firstVisiblePosition = i;
                break;
            }
        }

        for ( ; i < positionsCount; i++) {
            View childView = listView.getChildAt(i);
            if (childView.getLocalVisibleRect(scrollBounds)) {
                lastVisiblePosition = i;
            } else break;
        }
    }
}
