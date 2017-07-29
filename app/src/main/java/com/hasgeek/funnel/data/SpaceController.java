package com.hasgeek.funnel.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hasgeek.funnel.model.Metadata;
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
        return realm.where(Space.class).equalTo("id","116").or().equalTo("id","117").or().equalTo("id","102").findAll();
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

    public static void saveSpaceMetadataJSONBySpaceId(String spaceId, String metadataJSON) {
        SharedPreferenceController.setSharedPref(spaceId+"_metadata", metadataJSON);
    }

    public static Metadata getSpaceMetadataBySpaceId(String spaceId) {
        Gson gson = new GsonBuilder().create();
        try {
            String metadataJSON = SharedPreferenceController.getSharedPref(spaceId+"_metadata");
            Metadata metadata = gson.fromJson(metadataJSON, Metadata.class);
            return metadata;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
