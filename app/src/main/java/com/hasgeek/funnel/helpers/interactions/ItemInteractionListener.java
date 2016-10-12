package com.hasgeek.funnel.helpers.interactions;

import android.view.View;

/**
 * Author: @karthikb351
 * Project: android
 */

public interface ItemInteractionListener<T> {
    void onItemClick(View v, T item);
    void onItemLongClick(View v, T item);
}