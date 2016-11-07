package com.hasgeek.funnel.space.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.SessionController;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseFragment;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.model.Announcement;
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

//        Button scheduleBtn = (Button)view.findViewById(R.id.btn_view_schedule);
//        Button scanBadgeBtn = (Button)view.findViewById(R.id.btn_scan_badge);
        RecyclerView recyclerViewUpcomingSessions = (RecyclerView)view.findViewById(R.id.fragment_overview_recyclerview_upcoming);
        RecyclerView recyclerViewAnnouncements = (RecyclerView)view.findViewById(R.id.fragment_overview_recyclerview_annoucements);


        recyclerViewUpcomingSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        RealmResults<Session> allSessions = SessionController.getSessionsBySpaceId(realm, spaceId);


        List<Session> sessions = new ArrayList<>();

        Calendar now = Calendar.getInstance();

        for (Session s: allSessions) {
            if (now.before(TimeUtils.getCalendarFromISODateString(s.getStart())))
                sessions.add(realm.copyFromRealm(s));
            if (sessions.size()==2)
                break;
        }

        recyclerViewUpcomingSessions.setAdapter(new UpnextRecyclerViewAdapter(sessions, new ItemInteractionListener<Session>() {
            @Override
            public void onItemClick(View v, Session item) {

                overviewFragmentInteractionListener.onSessionClick(item);
            }

            @Override
            public void onItemLongClick(View v, Session item) {

            }
        }));


        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        List<Announcement> announcements = new ArrayList<>();

        Announcement a1 = new Announcement();
        a1.setTitle("Food!");
        a1.setTime("8:00AM");
        a1.setDescription("Please collect your food tokens from the registration desk. You will receive new food tokens tomorrow.");

        Announcement a2 = new Announcement();
        a2.setTitle("Wifi");
        a2.setTime("9:00AM");
        a2.setDescription("WiFi SSID: HasGeek \n Password: geeksrus");

        Announcement a3 = new Announcement();
        a3.setTitle("Flash Talks");
        a3.setTime("11:00AM");
        a3.setDescription("Please register for flash talks at the registration desk");

        announcements.add(a1);
        announcements.add(a2);
        announcements.add(a3);

        recyclerViewAnnouncements.setAdapter(new AnnouncementsRecyclerViewAdapter(announcements));

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


        return view;
    }

    @Override
    public void refresh() {

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
        void onScheduleClick();
        void onScanBadgeClick(View view);
        void onSessionClick(Session s);
    }
}
