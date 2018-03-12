package com.example.myapplication.api.structure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entities {

    @SerializedName("hashtags")
    @Expose
    private List<Object> hashtags = null;
    @SerializedName("symbols")
    @Expose
    private List<Object> symbols = null;
    @SerializedName("user_mentions")
    @Expose
    private List<Object> userMentions = null;
    @SerializedName("urls")
    @Expose
    private List<Url> urls = null;
    @SerializedName("media")
    @Expose
    private List<Media> media = null;

    public List<Object> getHashtags() {
        return hashtags;
    }

    public List<Object> getSymbols() {
        return symbols;
    }

    public List<Object> getUserMentions() {
        return userMentions;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public List<Media> getMedia() {
        return media;
    }

}
