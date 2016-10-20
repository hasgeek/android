package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.Space;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author: @karthikb351
 * Project: android
 */

public class SpaceController {

    public static RealmResults<Space> getSpaces(Realm realm) {
        return realm.where(Space.class).findAll();
    }

    public static Space getSpaceById_Hot(Realm realm, String id) {
        return realm.where(Space.class).equalTo("id", id).findFirst();
    }

    public static Space getSpaceById_Cold(Realm realm, String id) {
        return realm.copyFromRealm(realm.where(Space.class).equalTo("id", id).findFirst());
    }

    public static void saveSpaces(Realm realm, final List<Space> spaces) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(spaces);
        realm.commitTransaction();
    }
}
