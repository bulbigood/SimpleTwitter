package com.example.myapplication.ui;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.example.myapplication.NetworkLoader;

public class TouchListener implements OnTouchListener {

    public static int THRESHOLD_TIME = 150;
    public static int THRESHOLD_VECTOR;

    private static TouchListener touchListener = null;

    private PointF start = new PointF();
    private long oldTime = 0;

    private TouchListener(){
        THRESHOLD_VECTOR = ScrollingActivity.getInstance().getScreenSize().y / 4;
    }

    public static TouchListener getInstance(){
        if(touchListener == null)
            touchListener = new TouchListener();
        return touchListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // handle touch events here
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());
                oldTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                PointF finish = new PointF(event.getX(), event.getY());
                PointF vector = new PointF(finish.x - start.x, finish.y - start.y);
                long dtime = System.currentTimeMillis() - oldTime;
                if(dtime < THRESHOLD_TIME){
                    int first_position = ScrollingActivity.getInstance().getFirstVisiblePosition();
                    int last_position = ScrollingActivity.getInstance().getLastVisiblePosition();
                    int count = ScrollingActivity.getInstance().getPositionsCount();

                    if(first_position == 0 && vector.y > THRESHOLD_VECTOR){
                        NetworkLoader.getInstance().loadTweets(null, NetworkLoader.TweetsLoadType.UPDATE_NEW);
                    } else if(last_position == count - 1 && vector.y < -THRESHOLD_VECTOR){
                        NetworkLoader.getInstance().loadTweets(null, NetworkLoader.TweetsLoadType.UPDATE_OLD);
                    }
                }
                break;
        }
        return false;
    }
}
