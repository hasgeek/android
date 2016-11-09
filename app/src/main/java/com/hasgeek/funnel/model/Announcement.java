package com.hasgeek.funnel.model;

import com.google.gson.annotations.SerializedName;

/**
 * Author: @karthikb351
 * Project: android
 */

public class Announcement {

    @SerializedName("title")
    public String title;

    @SerializedName("time")
    public String time;

    @SerializedName("description")
    public String description;

    @SerializedName("url")
    public String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
