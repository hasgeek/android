package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Space;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public class DataManager {


    public static RealmResults<Space> getAllSpaces(Realm realm) {
        return SpaceController.getSpaces(realm);
    }

    public static RealmResults<Proposal> getConfirmedProposals(Realm realm, String spaceId) {
        return ProposalController.getConfirmedProposals(realm, spaceId);
    }

    public static Proposal getProposal(Realm realm, int proposalId) {
        return ProposalController.getProposalById_Cold(realm, proposalId);
    }
}
