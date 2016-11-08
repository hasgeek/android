package com.hasgeek.funnel.space.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.SessionController;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseFragment;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.helpers.utils.ValueUtils;
import com.hasgeek.funnel.model.Announcement;
import com.hasgeek.funnel.model.Metadata;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.space.SpaceActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class OverviewFragment extends BaseFragment {

    public static final String EXTRA_SPACE_ID = "extra_space_id";
    public static final String FRAGMENT_TAG = "OverviewFragment";
    private OverviewFragmentInteractionListener overviewFragmentInteractionListener;
    private String spaceId;
    RecyclerView recyclerViewAnnouncements;
    CardView discussionCardView;
    CardView foodCourtCardView;
    CardView liveStreamCardView;
    CardView venueMapCardView;

    Metadata metadata;

    public OverviewFragment() {
    }

    public static OverviewFragment newInstance(String spaceId) {

        OverviewFragment fragment = new OverviewFragment();

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
            overviewFragmentInteractionListener = spaceActivity.getOverviewFragmentInteractionListener();
            spaceId = getArguments().getString(EXTRA_SPACE_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        Realm realm = Realm.getDefaultInstance();

        RecyclerView recyclerViewUpcomingSessions = (RecyclerView)view.findViewById(R.id.fragment_overview_recyclerview_upcoming);
        recyclerViewAnnouncements = (RecyclerView)view.findViewById(R.id.fragment_overview_recyclerview_annoucements);


        discussionCardView = (CardView) view.findViewById(R.id.fragment_overview_discussion_cardview);

        foodCourtCardView = (CardView) view.findViewById(R.id.fragment_overview_foodcourt_cardview);

        liveStreamCardView = (CardView) view.findViewById(R.id.fragment_overview_livestream_cardview);

        venueMapCardView = (CardView) view.findViewById(R.id.fragment_overview_venuemap_cardview);

        recyclerViewUpcomingSessions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RealmResults<Session> allSessions = SessionController.getSessionsBySpaceId(realm, spaceId);


        List<Session> sessions = new ArrayList<>();

        Calendar now = Calendar.getInstance();

        for (Session s: allSessions) {
            if (now.before(TimeUtils.getCalendarFromISODateString(s.getStart())))
                sessions.add(realm.copyFromRealm(s));
            if (sessions.size()==2)
                break;
        }

        List<Session> allthesessions = realm.copyFromRealm(allSessions);

        recyclerViewUpcomingSessions.setAdapter(new UpnextRecyclerViewAdapter(allthesessions, new ItemInteractionListener<Session>() {
            @Override
            public void onItemClick(View v, Session item) {

                overviewFragmentInteractionListener.onSessionClick(item);
            }

            @Override
            public void onItemLongClick(View v, Session item) {

            }
        }));


        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        metadata = SpaceController.getSpaceMetadataBySpaceId(spaceId);

        if (metadata != null)
            recyclerViewAnnouncements.setAdapter(new AnnouncementsRecyclerViewAdapter(metadata.getAnnouncements()));
        else
            recyclerViewAnnouncements.setAdapter(new AnnouncementsRecyclerViewAdapter(new ArrayList<Announcement>()));

//        scheduleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                overviewFragmentInteractionListener.onScheduleClick();
//            }
//        });
//
//        scanBadgeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                overviewFragmentInteractionListener.onScanBadgeClick(view);
//            }
//        });

        refresh();

        return view;
    }

    @Override
    public void refresh() {
        metadata = SpaceController.getSpaceMetadataBySpaceId(spaceId);

        discussionCardView.setVisibility(View.INVISIBLE);
        foodCourtCardView.setVisibility(View.INVISIBLE);
        liveStreamCardView.setVisibility(View.INVISIBLE);
        venueMapCardView.setVisibility(View.INVISIBLE);

        if (metadata == null) {
            return;
        }

        if (ValueUtils.isNotBlank(metadata.getDiscussionSlackDeeplink()) && ValueUtils.isNotBlank(metadata.getDiscussionSlackWeb()) ) {
            discussionCardView.setVisibility(View.VISIBLE);
            discussionCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    overviewFragmentInteractionListener.onDiscussionClick();
                }
            });
        }

        if (ValueUtils.isNotBlank(metadata.getLivestreamUrl())) {
            liveStreamCardView.setVisibility(View.VISIBLE);
            liveStreamCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    overviewFragmentInteractionListener.onLiveStreamClick();
                }
            });
        }

        if (ValueUtils.isNotBlank(metadata.getVenueMapUrl())) {
            venueMapCardView.setVisibility(View.VISIBLE);
            venueMapCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    overviewFragmentInteractionListener.onVenueMapClick();
                }
            });
        }

        if (metadata.getAnnouncements() != null) {
            recyclerViewAnnouncements.setAdapter(new AnnouncementsRecyclerViewAdapter(metadata.getAnnouncements()));
        }

        if (metadata.getFoodCourtVendors() !=null) {
            if (metadata.getFoodCourtVendors().size()!=0) {
                foodCourtCardView.setVisibility(View.VISIBLE);
                foodCourtCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        overviewFragmentInteractionListener.onFoodCourtClick();
                    }
                });
            }

        }
    }

    @Override
    public void notFoundError() {
        // TODO: Implement not found view
    }

    @Override
    public void onDetach() {
        super.onDetach();
        overviewFragmentInteractionListener = null;
    }

    public interface OverviewFragmentInteractionListener {
        void onDiscussionClick();
        void onFoodCourtClick();
        void onLiveStreamClick();
        void onVenueMapClick();
        void onScheduleClick();
        void onScanBadgeClick(View view);
        void onSessionClick(Session s);
    }
}
