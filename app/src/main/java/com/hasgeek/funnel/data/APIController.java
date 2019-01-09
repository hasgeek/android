package com.hasgeek.funnel.data;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hasgeek.funnel.helpers.schedule.ScheduleHelper;
import com.hasgeek.funnel.helpers.utils.AuthUtils;
import com.hasgeek.funnel.model.Attendee;
import com.hasgeek.funnel.model.ContactExchangeContact;
import com.hasgeek.funnel.model.Metadata;
import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.model.wrapper.AuthWrapper;
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
        if (apiController == null) {
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
        if (spaceUrl != null)
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
                        for (Space s : spacesWrapper.spaces)
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
                        for (Proposal p : spaceWrapper.proposals) {
                            proposalList.add(p);
                        }
                        return Observable.just(proposalList);
                    }
                });
    }

    public Observable<List<Session>> getSessionsBySpaceId(String spaceId) {

        final Space space = SpaceController.getSpaceById_Cold(Realm.getDefaultInstance(), spaceId);


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
                            .url(space.getUrl() + "json")
                            .build();
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<Session> sessions = new ArrayList<>();
                    JSONArray schedule = new JSONArray(jsonObject.optString("schedule", "[]"));

                    for (int i = 0; i < schedule.length(); i++) {
                        JSONArray slots = schedule.getJSONObject(i).getJSONArray("slots");
                        for (int k = 0; k < slots.length(); k++) {
                            sessions.addAll(Arrays.asList(gson.fromJson(slots.getJSONObject(k).optString("sessions", "[]"), Session[].class)));
                        }
                    }

                    for (Session s : sessions) {
                        s.setSpace(space);
                    }

                    HashMap<Integer, List<Session>> hashMap = ScheduleHelper.getDayOfYearMapFromSessions(sessions);

                    for (Integer key : hashMap.keySet()) {
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

    public Observable<List<Attendee>> getAttendeesBySpaceId(String spaceId) {

        if (spaceId.equals("117"))
            spaceId = "116";

        final Space space = SpaceController.getSpaceById_Cold(Realm.getDefaultInstance(), spaceId);

        return Observable.create(new Observable.OnSubscribe<List<Attendee>>() {
            @Override
            public void call(Subscriber<? super List<Attendee>> subscriber) {
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

                    OkHttpClient client = new OkHttpClient();
                    Request request;
                    if (space.getUrl().contains(".talkfunnel.com")) {
                        String oldFunnelUrl = space.getUrl().substring(8); // strip "https://"
                        String[] urlParts = oldFunnelUrl.split(".talkfunnel.com");
                        String newHgUrl = "https://hasgeek.com/" + urlParts[0] + urlParts[1];
                        request = new Request.Builder()
                                .url(newHgUrl + "participants/json")
                                .addHeader("Authorization", AuthUtils.getAuthHeaderFromToken(AuthController.getAuthToken()))
                                .build();
                    } else {
                        request = new Request.Builder()
                                .url(space.getUrl() + "participants/json")
                                .addHeader("Authorization", AuthUtils.getAuthHeaderFromToken(AuthController.getAuthToken()))
                                .build();
                    }

                    Response response = client.newCall(request).execute();

                    List<Attendee> attendeeArrayList = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(response.body().string());

                    attendeeArrayList.addAll(Arrays.asList(gson.fromJson(jsonObject.getString("participants"), Attendee[].class)));

                    for (Attendee attendee : attendeeArrayList) {
                        attendee.setSpace(space);
                    }

                    subscriber.onNext(attendeeArrayList);
                    subscriber.onCompleted();


                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<AuthWrapper> getAuthVerification(final String authToken) {
        return Observable.create(new Observable.OnSubscribe<AuthWrapper>() {
            @Override
            public void call(Subscriber<? super AuthWrapper> subscriber) {
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

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://talkfunnel.com/api/whoami")
                            .addHeader("Authorization", AuthUtils.getAuthHeaderFromToken(authToken))
                            .build();

                    Response response = client.newCall(request).execute();
                    AuthWrapper authWrapper = gson.fromJson(response.body().string(), AuthWrapper.class);

                    subscriber.onNext(authWrapper);
                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<ContactExchangeContact> syncContactExchangeContact(final ContactExchangeContact contactExchangeContact) {
        return Observable.create(new Observable.OnSubscribe<ContactExchangeContact>() {
            @Override
            public void call(Subscriber<? super ContactExchangeContact> subscriber) {
                try {
                    Space space = contactExchangeContact.getSpace();
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

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .addHeader("Authorization", AuthUtils.getAuthHeaderFromToken(AuthController.getAuthToken()))
                            .url(space.getUrl() + "participant?"+"puk="+contactExchangeContact.getPuk()+"&key="+contactExchangeContact.getKey())
                            .build();

                    Response response = client.newCall(request).execute();

                    JSONObject jsonObject = new JSONObject(response.body().string());

                    ContactExchangeContact contact = gson.fromJson(jsonObject.getString("participant"), ContactExchangeContact.class);

                    contact.setKey(contactExchangeContact.getKey());
                    contact.setPuk(contactExchangeContact.getPuk());

                    subscriber.onNext(contact);

                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    public Observable<Metadata> getMetadataForSpaceId(final String spaceId) {
        return Observable.create(new Observable.OnSubscribe<Metadata>() {
            @Override
            public void call(Subscriber<? super Metadata> subscriber) {
                try {

                    Gson gson = new GsonBuilder().create();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            //.url("http://172.16.27.58:4000/api/space/"+spaceId+"/metadata")
                            .url("https://hasgeek.github.io/api/space/"+spaceId+"/metadata")
                            .build();

                    Response response = client.newCall(request).execute();

                    String jsonResponse = response.body().string();


                    Metadata metadata = gson.fromJson(jsonResponse, Metadata.class);

                    SpaceController.saveSpaceMetadataJSONBySpaceId(spaceId, jsonResponse);

                    subscriber.onNext(metadata);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
