package com.hasgeek.funnel.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by karthik on 06-01-2015.
 */

public class Room extends RealmObject{

    @Expose
    public String bgcolor;
    @Expose
    public String description;
    @SerializedName("json_url")
    @Expose
    public String jsonUrl;
    @Expose
    public String name;
    @Expose
    public String title;
    @Expose
    public String url;
    @Expose
    public String venue;

    /**
     * @return The bgcolor
     */
    public String getBgcolor() {
        return bgcolor;
    }

    /**
     * @param bgcolor The bgcolor
     */
    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The jsonUrl
     */
    public Object getJsonUrl() {
        return jsonUrl;
    }

    /**
     * @param jsonUrl The json_url
     */
    public void setJsonUrl(String jsonUrl) {
        this.jsonUrl = jsonUrl;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The url
     */
    public Object getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The venue
     */
    public String getVenue() {
        return venue;
    }

    /**
     * @param venue The venue
     */
    public void setVenue(String venue) {
        this.venue = venue;
    }
}
