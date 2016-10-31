package com.hasgeek.funnel.space.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.ContactExchangeController;
import com.hasgeek.funnel.data.SessionController;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseFragment;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Attendee;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.space.SpaceActivity;

import org.w3c.dom.Text;

import io.realm.Realm;
import io.realm.RealmResults;

public class ContactExchangeFragment extends BaseFragment {

    public static final String EXTRA_SPACE_ID = "extra_space_id";
    public static final int FRAGMENT_ID = 1;
    private ContactExchangeFragmentListener contactExchangeFragmentListener;
    private String spaceId;
    public ContactExchangeFragment() {
    }

    public static ContactExchangeFragment newInstance(String spaceId) {

        ContactExchangeFragment fragment = new ContactExchangeFragment();

        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SPACE_ID, spaceId);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SpaceActivity) {
            SpaceActivity spaceActivity = (SpaceActivity)getActivity();
            contactExchangeFragmentListener = spaceActivity.getContactExchangeFragmentListener();
            spaceId = getArguments().getString(EXTRA_SPACE_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_exchange, container, false);
        Realm realm = Realm.getDefaultInstance();
        Space space = SpaceController.getSpaceById_Cold(realm, spaceId);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_contact_exchange_recyclerview);
        RealmResults<Attendee> attendees = ContactExchangeController.getAttendeesBySpaceId_Hot(realm, space.getId());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(new ContactExchangeRecyclerViewAdapter((SpaceActivity)getActivity(), attendees, new ItemInteractionListener<Attendee>() {
            @Override
            public void onItemClick(View v, Attendee item) {
                contactExchangeFragmentListener.onAttendeeClick(item);
            }

            @Override
            public void onItemLongClick(View v, Attendee item) {
                contactExchangeFragmentListener.onAttendeeLongClick(item);
            }
        }));


        return view;
    }

    @Override
    public void notFoundError() {
        // TODO: Implement not found view
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contactExchangeFragmentListener = null;
    }

    public interface ContactExchangeFragmentListener {
        void onAttendeeClick(Attendee a);
        void onScanBadgeClick(View view);
        void onAttendeeLongClick(Attendee s);
    }
}
