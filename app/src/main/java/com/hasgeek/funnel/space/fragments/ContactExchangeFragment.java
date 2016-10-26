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
            spaceId = getArguments().getString(EXTRA_SPACE_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_exchange, container, false);
        Realm realm = Realm.getDefaultInstance();
        Space s = SpaceController.getSpaceById_Cold(realm, spaceId);
        TextView textView = (TextView) view.findViewById(R.id.placeholder);

        textView.setText("Placeholder for attendee list for "+s.getTitle());

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
