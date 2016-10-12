package com.hasgeek.funnel.data;

import android.content.Context;
import android.util.Log;

import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Space;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public class DatabaseService {

    public static void init(Context context) {
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
    }

    public static RealmResults<Space> getSpaces(Realm realm) {
        return realm.where(Space.class).findAll();
    }

    public static Space getSpaceById(Realm realm, String id) {
        return realm.copyFromRealm(realm.where(Space.class).equalTo("id", id).findFirst());
    }

    public static void saveSpaces(Realm realm, final List<Space> spaces) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(spaces);
        realm.commitTransaction();
    }

    public static RealmResults<Proposal> getConfirmedProposals(Realm realm, String spaceId) {
        return realm.where(Proposal.class)
                .equalTo("space.id", spaceId)
                .findAll()
                .where()
                .equalTo("confirmed", true)
                .findAll();
    }

    public static void saveProposals(Realm realm, final List<Proposal> proposals) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(proposals);
        realm.commitTransaction();
    }

    public static Proposal getProposal(Realm realm, int proposalId) {
        return realm.where(Proposal.class)
                .equalTo("id", proposalId)
                .findFirst();
    }
}