package com.hasgeek.funnel.space.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hasgeek.funnel.data.SessionController;
import com.hasgeek.funnel.helpers.BaseFragment;
import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.space.SpaceActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

public class ScheduleFragment extends BaseFragment {

    private static final String EXTRA_SPACE_ID = "extra_space_id";
    private static final String EXTRA_SESSION_DAY_OF_YEAR = "extra_day_of_year";
    public static final int FRAGMENT_ID = 3;
    private String spaceId;
    private int sessionDayOfYear;
    private ItemInteractionListener<Session> sessionItemInteractionListener;
    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(String spaceId, int sessionDayOfYear) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SPACE_ID, spaceId);
        arguments.putInt(EXTRA_SESSION_DAY_OF_YEAR, sessionDayOfYear);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SpaceActivity) {
            SpaceActivity spaceActivity = (SpaceActivity)getActivity();
            spaceId = getArguments().getString(EXTRA_SPACE_ID, null);
            sessionDayOfYear = getArguments().getInt(EXTRA_SESSION_DAY_OF_YEAR);
            sessionItemInteractionListener = (ItemInteractionListener<Session>) spaceActivity.getSessionItemInteractionListener();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singleday_list, container, false);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.canvas);


        List<Session> allSessions = Realm.getDefaultInstance().copyFromRealm(SessionController.getSessionsBySpaceId(Realm.getDefaultInstance(), spaceId));

        List<Session> sessions = new ArrayList<>();
        for (Session s: allSessions) {
            if (TimeUtils.getCalendarFromISODateString(s.getStart()).get(Calendar.DAY_OF_YEAR) == sessionDayOfYear)
                sessions.add(s);
        }
        l("We have: "+sessions.size()+" items");


        int width = 0;
        int height = 0;
        for (final Session s: sessions) {
            l(s.toString());
            LinearLayout linearLayout = getScheduleViewForSession(inflater, relativeLayout, s);
            if (width < (s.getMarginLeft() + s.getWidth()))
                width = s.getMarginLeft() + s.getWidth();
            if (height < (s.getMarginTop() + s.getHeight()))
                height = s.getMarginTop() + s.getHeight();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(s.getWidth(), s.getHeight());
            params.leftMargin = s.getMarginLeft();
            params.topMargin = s.getMarginTop();
            relativeLayout.addView(linearLayout, params);

        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeLayout.getLayoutParams());
        layoutParams.height = height;
        layoutParams.width = width + 5;

        ViewGroup parent = (ViewGroup)relativeLayout.getParent();
        if (parent!=null){
            parent.removeView(relativeLayout);
        }
        relativeLayout.setLayoutParams(layoutParams);
        parent.addView(relativeLayout);
        return view;
    }

    LinearLayout getScheduleViewForSession(LayoutInflater inflater, ViewGroup container, final Session s) {

        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_schedule_item, container, false);

        LinearLayout background = (LinearLayout)linearLayout.findViewById(R.id.fragment_schedule_item_layout);

        TextView title = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_title);
        TextView speaker = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_speaker);
        TextView location = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_location);
        TextView time = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_time);

        title.setText(s.getTitle());

        String speakerText = s.getSpeaker();

        if (speakerText==null) {
            speaker.setVisibility(View.GONE);
        } else if (speakerText.equals("")) {
            speaker.setVisibility(View.GONE);
        } else {
            speaker.setText(speakerText);
        }


        if(s.getRoom()==null) {
            background.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
            location.setText("Main Auditorium");
        } else if(s.getRoom().contains("audi")) {
            background.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
            location.setText("Main Auditorium");
        }
        else {
            background.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.colorAccent));
            location.setText("Banquet Hall");
        }

        if (s.getIsBreak()) {
            background.setBackgroundColor(Color.LTGRAY);
            background.setClickable(false);
        } else {
            linearLayout.setClickable(true);
            background.setClickable(true);
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionItemInteractionListener.onItemClick(view, s);
                }
            });

        }

        time.setText(TimeUtils.getSimpleTimeForString(s.getStart()));

        return linearLayout;

    }

    @Override
    public void notFoundError() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
