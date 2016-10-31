package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.Attendee;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author: @karthikb351
 * Project: android
 */

public class ContactExchangeController {


    public static RealmResults<Attendee> getAttendeesBySpaceId_Hot(Realm realm, String spaceId) {
        return realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .findAll();
    }

    public static List<Attendee> getAttendeesBySpaceId_Cold(Realm realm, String spaceId) {
        return realm.copyFromRealm(getAttendeesBySpaceId_Hot(realm, spaceId));
    }


    public static Attendee getAttendeeBySpaceIdAndPuk_Hot(Realm realm, String spaceId, String puk) {
        return realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .equalTo("puk", puk)
                .findFirst();
    }

    public static Attendee getAttendeeBySpaceIdAndPuk_Cold(Realm realm, String spaceId, String puk) {
        return realm.copyFromRealm(getAttendeeBySpaceIdAndPuk_Hot(realm, spaceId, puk));
    }

    public static void saveAttendees(Realm realm, List<Attendee> attendeeList) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(attendeeList);
        realm.commitTransaction();
    }

    public static void deleteAttendeesBySpaceId(Realm realm, String spaceId) {
        realm.beginTransaction();
        realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
    }

}
