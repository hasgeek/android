package com.hasgeek.funnel.data;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.model.wrapper.SpaceWrapper;
import com.hasgeek.funnel.model.wrapper.SpacesWrapper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public class APIService {

    public static APIService apiService;
    public static TalkfunnelAPI api;

    public static APIService getService() {
        if(apiService == null) {
            apiService = new APIService();
            apiService.api = apiService.createService();
        }
        return apiService;
    }
    public TalkfunnelAPI createService() {
        return createService(null);
    }
    public TalkfunnelAPI createService(String spaceUrl) {

        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass() == RealmObject.class;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();


        String baseUrl;
        if(spaceUrl != null)
            baseUrl = spaceUrl;
        else
            baseUrl = "https://talkfunnel.com/";
        Retrofit.Builder builder = new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl);

        return builder.build().create(TalkfunnelAPI.class);
    }

    public Observable<List<Space>> getAllSpaces() {

        return api.getAllSpaces()
                .flatMap(new Func1<SpacesWrapper, Observable<List<Space>>>() {
                    @Override
                    public Observable<List<Space>> call(SpacesWrapper spacesWrapper) {
                        List<Space> spaceList = new ArrayList<Space>();
                        for (Space s: spacesWrapper.spaces)
                            spaceList.add(s);

                        return Observable.just(spaceList);
                    }
                });
    }

    public Observable<List<Proposal>> getProposals(String spaceUrl) {

        TalkfunnelAPI api = createService(spaceUrl);
        return api.getSpace()
                .flatMap(new Func1<SpaceWrapper, Observable<List<Proposal>>>() {
                    @Override
                    public Observable<List<Proposal>> call(SpaceWrapper spaceWrapper) {
                        List<Proposal> proposalList = new ArrayList<Proposal>();
                        for (Proposal p: spaceWrapper.proposals) {
                            proposalList.add(p);
                        }
                        return Observable.just(proposalList);
                    }
                });
    }

}
