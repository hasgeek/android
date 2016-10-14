package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.Proposal;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author: @karthikb351
 * Project: android
 */

public class ProposalService {

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

    public static Proposal getProposalById_Cold(Realm realm, int proposalId) {
        return realm.copyFromRealm(realm.where(Proposal.class)
                .equalTo("id", proposalId)
                .findFirst());
    }

    public static Proposal getProposalById_Hot(Realm realm, int proposalId) {
        return realm.where(Proposal.class)
                .equalTo("id", proposalId)
                .findFirst();
    }
}
