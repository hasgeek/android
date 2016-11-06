package com.hasgeek.funnel.space.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.ContactExchangeController;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseFragment;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.ContactExchangeContact;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.space.SpaceActivity;


import io.realm.Realm;
import io.realm.RealmResults;

public class ContactExchangeFragment extends BaseFragment {

    public static final String EXTRA_SPACE_ID = "extra_space_id";
    public static final String FRAGMENT_TAG = "ContactExchangeFragment";
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        RealmResults<ContactExchangeContact> contactExchangeContacts = ContactExchangeController.getContactExchangeContactsBySpaceId_Hot(realm, space.getId());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(new ContactExchangeRecyclerViewAdapter((SpaceActivity)getActivity(), contactExchangeContacts, new ItemInteractionListener<ContactExchangeContact>() {
            @Override
            public void onItemClick(View v, ContactExchangeContact item) {
                contactExchangeFragmentListener.onContactExchangeContactClick(item);
            }

            @Override
            public void onItemLongClick(View v, ContactExchangeContact item) {
                contactExchangeFragmentListener.onContactExchangeContactLongClick(item);
            }
        }));


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_exchange_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        void onContactExchangeContactClick(ContactExchangeContact contactExchangeContact);
        void onScanBadgeClick(View view);
        void onContactExchangeContactLongClick(ContactExchangeContact contactExchangeContact);
    }
}
