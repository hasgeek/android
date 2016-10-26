package com.hasgeek.funnel.data;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hasgeek.funnel.helpers.schedule.ScheduleHelper;
import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.model.wrapper.SpaceWrapper;
import com.hasgeek.funnel.model.wrapper.SpacesWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public class APIController {

    public static APIController apiController;
    public static TalkfunnelAPI api;

    public static APIController getService() {
        if(apiController == null) {
            apiController = new APIController();
            apiController.api = apiController.createController();
        }
        return apiController;
    }
    public TalkfunnelAPI createController() {
        return createController(null);
    }
    public TalkfunnelAPI createController(String spaceUrl) {

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

    public Observable<List<Proposal>> getProposals(String id) {
        Space s = SpaceController.getSpaceById_Cold(Realm.getDefaultInstance(), id);

        TalkfunnelAPI api = createController(s.getUrl());
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

    public Observable<List<Session>> getSessions(String id) {

        final Space space = SpaceController.getSpaceById_Cold(Realm.getDefaultInstance(), id);


        return Observable.create(new Observable.OnSubscribe<List<Session>>() {
            @Override
            public void call(Subscriber<? super List<Session>> subscriber) {
                try {
                    final Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass() == RealmObject.class;
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    }).create();
                    final OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(space.getUrl()+"json")
                            .build();
                    Response res = client.newCall(request).execute();
                    JSONObject obj = new JSONObject(res.body().string());
                    List<Session> sessions = new ArrayList<>();
                    JSONArray schedule = new JSONArray(obj.optString("schedule", "[]"));

                    for(int i=0; i<schedule.length(); i++) {
                        JSONArray slots = schedule.getJSONObject(i).getJSONArray("slots");
                        for(int k=0; k<slots.length();k++) {
                            sessions.addAll(Arrays.asList(gson.fromJson(slots.getJSONObject(k).optString("sessions", "[]"), Session[].class)));
                        }
                    }
//
                    for(Session s: sessions) {
                        s.setSpace(space);
                    }

                    HashMap<Integer, List<Session>> hashMap = ScheduleHelper.getDayOfYearMapFromSessions(sessions);
                    for (Integer key: hashMap.keySet()) {
                        ScheduleHelper.addDimensToSessions(hashMap.get(key));
                    }

                    subscriber.onNext(sessions);
                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
