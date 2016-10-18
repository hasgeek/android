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
        RealmConfiguration config = new RealmConfiguration.Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

}