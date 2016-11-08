package com.hasgeek.funnel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author: @karthikb351
 * Project: android
 */

public class FoodCourtVendorSection {

    @SerializedName("title")
    public String title;

    @SerializedName("items")
    public List<FoodCourtVendorItem> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FoodCourtVendorItem> getItems() {
        return items;
    }

    public void setItems(List<FoodCourtVendorItem> items) {
        this.items = items;
    }
}
