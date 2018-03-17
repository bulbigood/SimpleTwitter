package com.example.myapplication.api.structure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {

    @SerializedName("statuses")
    @Expose
    private List<Status> statuses = null;

    public List<Status> getStatuses() {
        return statuses;
    }

}
