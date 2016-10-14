package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.Session;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author: @karthikb351
 * Project: android
 */

public class SessionService {

    public static RealmResults<Session> getSessionsBySpaceId(Realm realm, String id) {
        return realm.where(Session.class)
                .equalTo("space.id", id)
                .findAll();
    }

    public static Session getSessionById_Cold(Realm realm, String id) {
        return realm.copyFromRealm(realm.where(Session.class).equalTo("id", id).findFirst());
    }

    public static Session getSessionById_Hot(Realm realm, String id) {
        return realm.where(Session.class).equalTo("id", id).findFirst();
    }

    public static void saveSessions(Realm realm, final List<Session> sessions) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(sessions);
        realm.commitTransaction();
    }
}
