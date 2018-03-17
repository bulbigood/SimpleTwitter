package com.example.myapplication.api.structure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BearerToken {

    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRequestToken() {
        return "Bearer " + accessToken;
    }
}