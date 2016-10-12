package com.hasgeek.funnel.space.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hasgeek.funnel.helpers.BaseFragment;
import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.DataManager;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.space.SpaceActivity;

import io.realm.Realm;
import io.realm.RealmResults;

public class SingleTrackFragment extends BaseFragment {

    private ItemInteractionListener mListener;

    public SingleTrackFragment() {
    }

    public static SingleTrackFragment newInstance(ItemInteractionListener itemInteractionListener) {
        SingleTrackFragment fragment = new SingleTrackFragment();
        fragment.mListener = itemInteractionListener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singleday_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));


            RealmResults<Proposal> proposals = DataManager.getConfirmedProposals(Realm.getDefaultInstance(), "84");
            l("We have: "+proposals.size()+" items");
            recyclerView.setAdapter(new SingleTrackRecyclerViewAdapter((SpaceActivity)getActivity(), proposals, mListener));

        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
