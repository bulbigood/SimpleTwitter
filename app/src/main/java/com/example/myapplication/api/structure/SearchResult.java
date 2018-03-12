package com.example.myapplication.api.structure;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResult {

    @SerializedName("statuses")
    @Expose
    private List<Status> statuses = null;

    public List<Status> getStatuses() {
        return statuses;
    }

}
