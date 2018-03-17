package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.myapplication.api.structure.Media;
import com.example.myapplication.api.structure.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;

public class Utils {

    public static Long getIDfromFile(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.temp_file), Context.MODE_PRIVATE);
        int defaultValue = context.getResources().getInteger(R.integer.last_id_default);
        Long id = sharedPref.getLong(context.getString(R.string.last_id), defaultValue);
        return id;
    }

    public static void writeIDtoFile(Context context, Long id) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.temp_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.last_id), id);
        editor.apply();
    }

    public static void loadImages(View view, Tweet tweet) {
        ArrayList<String> media_urls = new ArrayList();
        if (tweet.getEntities().getMedia() != null) {
            for (Media media : tweet.getEntities().getMedia()) {
                media_urls.add(media.getMediaUrl());
            }
            if (media_urls.size() > 0) {
                ImageView image = view.findViewById(R.id.tweet_image);
                image.getLayoutParams().height = (int) view.getResources().getDimension(R.dimen.tweet_image_height);
                Glide.with(view).load(media_urls.get(0)).into(image);
            }
        }
    }

    public static ArrayList<String> extractLinks(String text) {
        ArrayList<String> links = new ArrayList();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }

        return links;
    }

    public static String removeLinks(String str) {
        ArrayList<String> links = extractLinks(str);
        for (String link : links) {
            str = str.replace(link, "");
        }
        return str.trim();
    }

    public static String getRelativeTime(String formated_date) {
        return DateUtils.getRelativeTimeSpanString(getAbsoluteTime(formated_date)).toString();
    }

    public static Long getAbsoluteTime(String formated_date) {
        long millis_time = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z y", Locale.US);
        try {
            Date d = dateFormat.parse(formated_date);
            millis_time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis_time;
    }
}
