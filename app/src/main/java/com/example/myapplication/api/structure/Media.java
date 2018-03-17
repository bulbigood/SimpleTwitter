package com.example.myapplication.api.structure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Media {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("id_str")
    @Expose
    private String idStr;
    @SerializedName("indices")
    @Expose
    private List<Integer> indices = null;
    @SerializedName("media_url")
    @Expose
    private String mediaUrl;
    @SerializedName("media_url_https")
    @Expose
    private String mediaUrlHttps;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("display_url")
    @Expose
    private String displayUrl;
    @SerializedName("expanded_url")
    @Expose
    private String expandedUrl;
    @SerializedName("type")
    @Expose
    private String type;

    public Long getId() {
        return id;
    }

    public String getIdStr() {
        return idStr;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getMediaUrlHttps() {
        return mediaUrlHttps;
    }

    public String getUrl() {
        return url;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public String getType() {
        return type;
    }

}
