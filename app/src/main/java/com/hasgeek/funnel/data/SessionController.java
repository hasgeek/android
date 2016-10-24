package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author: @karthikb351
 * Project: android
 */

public class SessionController {

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

    public static void deleteSessions(Realm realm, String spaceId) {
        realm.beginTransaction();
        realm.where(Session.class).equalTo("space.id", spaceId).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }
}
