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
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.space.SpaceActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
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
        Space s = SpaceController.getSpaceById_Cold(realm, spaceId);

        TextView headline = (TextView)view.findViewById(R.id.fragment_overview_tv_headline);
        TextView description = (TextView)view.findViewById(R.id.fragment_overview_tv_description);
        TextView announcement = (TextView)view.findViewById(R.id.fragment_overview_tv_announcement);
//        Button scheduleBtn = (Button)view.findViewById(R.id.btn_view_schedule);
//        Button scanBadgeBtn = (Button)view.findViewById(R.id.btn_scan_badge);
        RecyclerView recyclerViewUpcomingSessions = (RecyclerView)view.findViewById(R.id.fragment_overview_recyclerview_upcoming);

        recyclerViewUpcomingSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        RealmResults<Session> sessions = SessionController.getSessionsBySpaceId(realm, spaceId);

        recyclerViewUpcomingSessions.setAdapter(new UpnextRecyclerViewAdapter(getActivity(), sessions, new ItemInteractionListener<Session>() {
            @Override
            public void onItemClick(View v, Session item) {
                overviewFragmentInteractionListener.onSessionClick(item);
            }

            @Override
            public void onItemLongClick(View v, Session item) {

            }
        }));

        headline.setText(s.getTitle());
        description.setText(s.getDatelocation());
        announcement.setText("Please collect your food tokens from the registration desk. You will receive new food tokens tomorrow.");

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
