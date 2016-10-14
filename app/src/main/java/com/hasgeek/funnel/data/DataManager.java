package com.hasgeek.funnel.data;

import android.content.Context;
import android.util.Log;

import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Space;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public class DataManager {


    public static RealmResults<Space> getAllSpaces(Realm realm) {
        return SpaceService.getSpaces(realm);
    }

    public static RealmResults<Proposal> getConfirmedProposals(Realm realm, String spaceId) {
        return ProposalService.getConfirmedProposals(realm, spaceId);
    }

    public static Proposal getProposal(Realm realm, int proposalId) {
        return ProposalService.getProposalById_Cold(realm, proposalId);
    }
}
