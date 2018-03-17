package com.example.myapplication.api;

import com.example.myapplication.api.structure.BearerToken;
import com.example.myapplication.api.structure.SearchResult;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.api.structure.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TwitterAPI {

    @GET("/1.1/statuses/show.json")
    Call<Tweet> getTweet(@Query("id") Long id, @Query("tweet_mode") String tweet_mode,
                         @Header("Authorization") String credentials);

    @GET("/1.1/search/tweets.json")
    Call<SearchResult> searchTweets(@Query("q") String q, @Query("count") Integer count,
                                    @Query("since_id") Long since_id, @Query("max_id") Long max_id,
                                    @Header("Authorization") String credentials);

    @GET("/1.1/statuses/user_timeline.json")
    Call<List<Tweet>> getTimeline(@Query("screen_name") String screen_name, @Query("count") Integer count,
                                  @Query("since_id") Long since_id, @Query("max_id") Long max_id,
                                  @Header("Authorization") String credentials);

    @GET("/1.1/users/show.json")
    Call<User> getUser(@Query("screen_name") String screen_name, @Header("Authorization") String credentials);

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("/oauth2/token")
    Call<BearerToken> getBearerToken(@Query("grant_type") String client_credentials,
                                     @Header("Authorization") String credentials);

}
