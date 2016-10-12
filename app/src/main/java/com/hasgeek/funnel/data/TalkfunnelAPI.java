package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.wrapper.SpaceWrapper;
import com.hasgeek.funnel.model.wrapper.SpacesWrapper;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public interface TalkfunnelAPI {

    @GET("json")
    Observable<SpacesWrapper> getAllSpaces();

    @GET("json")
    Observable<SpaceWrapper> getSpace();

}
