package com.example.myapplication.api.structure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Status {

    @SerializedName("coordinates")
    @Expose
    private Object coordinates;
    @SerializedName("favorited")
    @Expose
    private Boolean favorited;
    @SerializedName("truncated")
    @Expose
    private Boolean truncated;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id_str")
    @Expose
    private String idStr;
    @SerializedName("entities")
    @Expose
    private Entities entities;
    @SerializedName("in_reply_to_user_id_str")
    @Expose
    private String inReplyToUserIdStr;
    @SerializedName("contributors")
    @Expose
    private Object contributors;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("retweet_count")
    @Expose
    private Long retweetCount;
    @SerializedName("in_reply_to_status_id_str")
    @Expose
    private String inReplyToStatusIdStr;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("geo")
    @Expose
    private Object geo;
    @SerializedName("retweeted")
    @Expose
    private Boolean retweeted;
    @SerializedName("in_reply_to_user_id")
    @Expose
    private Long inReplyToUserId;
    @SerializedName("place")
    @Expose
    private Object place;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("in_reply_to_screen_name")
    @Expose
    private Object inReplyToScreenName;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("in_reply_to_status_id")
    @Expose
    private Long inReplyToStatusId;

    public Object getCoordinates() {
        return coordinates;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getIdStr() {
        return idStr;
    }

    public Entities getEntities() {
        return entities;
    }

    public String getInReplyToUserIdStr() {
        return inReplyToUserIdStr;
    }

    public Object getContributors() {
        return contributors;
    }

    public String getText() {
        return text;
    }

    public Long getRetweetCount() {
        return retweetCount;
    }

    public String getInReplyToStatusIdStr() {
        return inReplyToStatusIdStr;
    }

    public Long getId() {
        return id;
    }

    public Object getGeo() {
        return geo;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public Long getInReplyToUserId() {
        return inReplyToUserId;
    }

    public Object getPlace() {
        return place;
    }

    public User getUser() {
        return user;
    }

    public Object getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public String getSource() {
        return source;
    }

    public Long getInReplyToStatusId() {
        return inReplyToStatusId;
    }
}
