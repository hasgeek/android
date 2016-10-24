package com.hasgeek.funnel.space.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
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

import org.w3c.dom.Text;

import java.sql.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmResults;

public class SingleTrackFragment extends BaseFragment {

    private static final String EXTRA_SPACE_ID = "extra_space_id";
    private String spaceId;
    public SingleTrackFragment() {
    }

    public static SingleTrackFragment newInstance(String spaceId) {
        SingleTrackFragment fragment = new SingleTrackFragment();
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
        View view = inflater.inflate(R.layout.fragment_singleday_list, container, false);
        Context context = view.getContext();
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.canvas);


        List<Session> allSessions = Realm.getDefaultInstance().copyFromRealm(SessionController.getSessionsBySpaceId(Realm.getDefaultInstance(), spaceId));

        List<Session> sessions = new ArrayList<>();
        for (Session s: allSessions) {
            if (TimeUtils.getCalendarFromISODateString(s.getStart()).get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
                sessions.add(s);
        }
        l("We have: "+sessions.size()+" items");

        List<SessionViewHolder> sessionViewHolderList = scheduleHelper(sessions);

        int width = 0;
        int height = 0;
        for (SessionViewHolder s: sessionViewHolderList) {
            l(s.toString());
            LinearLayout linearLayout = getScheduleViewForSession(inflater, relativeLayout, s.s);
            if (width < (s.left + s.width))
                width = s.left + s.width;
            if (height < (s.top + s.height))
                height = s.top + s.height;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(s.width, s.height);
            params.leftMargin = s.left;
            params.topMargin = s.top;
            relativeLayout.addView(linearLayout, params);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeLayout.getLayoutParams());
        layoutParams.height = height;
        layoutParams.width = width;

        ViewGroup parent = (ViewGroup)relativeLayout.getParent();
        if (parent!=null){
            parent.removeView(relativeLayout);
        }
        relativeLayout.setLayoutParams(layoutParams);
        parent.addView(relativeLayout);
        return view;
    }

    String getSimpleTime(String date) {
        Calendar cal = TimeUtils.getCalendarFromISODateString(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(cal.getTime());
    }

    LinearLayout getScheduleViewForSession(LayoutInflater inflater, ViewGroup container, Session s) {

        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_schedule_item, container, false);

        TextView title = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_title);
        TextView speaker = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_speaker);
        TextView location = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_location);
        TextView time = (TextView) linearLayout.findViewById(R.id.fragment_schedule_item_time);

        title.setText(s.getTitle());
        speaker.setText(s.getSpeaker());
        location.setText("Main Auditorium");

        time.setText(getSimpleTime(s.getStart()));

        return linearLayout;

    }

    @Override
    public void notFoundError() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public class SessionViewHolder {
        public Session s;
        public int top;
        public int left;
        public int width;
        public int height;

        public SessionViewHolder(Session s, int top, int left, int width, int height) {
            this.s = s;
            this.top = top;
            this.left = left;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "SessionViewHolder{" +
                    "s=" + s.getTitle() +
                    ", top=" + top +
                    ", left=" + left +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    public List<SessionViewHolder> scheduleHelper(List<Session> sessions) {
        int TRACK_WIDTH = 800;
        int SESSION_HEIGHT = 300;

        HashMap<String, Integer> timeMap = new HashMap<>();
        HashMap<String, Integer> trackMap = new HashMap<>();
        int trackCount = 0;
        for(Session s: sessions) {
            if (!trackMap.containsKey( s.getRoom()!=null ? "Main Audi" : "Banquet"));
                trackMap.put(s.getRoom()!=null ? "Main Audi" : "Banquet", trackCount++);
            timeMap.put(getSimpleTime(s.getStart()), 0);
            timeMap.put(getSimpleTime(s.getEnd()), 0);
        }
        Collections.sort(sessions, new Comparator<Session>() {
            @Override
            public int compare(Session s1, Session s2) {
                Calendar t1 = TimeUtils.getCalendarFromISODateString(s1.getStart());
                Calendar t2 = TimeUtils.getCalendarFromISODateString(s2.getStart());
                return t1.compareTo(t2);
            }
        });

        List<SessionViewHolder> sessionViewHolderList = new ArrayList<>();

        for (Session s: sessions) {
            int top = timeMap.get(getSimpleTime(s.getStart()));

            int mul = 0;
            if(s.getRoom()==null) {
                mul = 0;
            } else if(s.getRoom().contains("audi")) {
                mul = 1;
            }
            else {
                mul = 2;
            }

            int left = TRACK_WIDTH * mul;
            int width = TRACK_WIDTH;
            int height = timeMap.get(getSimpleTime(s.getEnd()));
            int layoutHeight =  SESSION_HEIGHT;
            timeMap.put(getSimpleTime(s.getEnd()), timeMap.get(getSimpleTime(s.getStart()))+layoutHeight);
            height = layoutHeight;


            sessionViewHolderList.add(new SessionViewHolder(s, top, left, width, height));

        }

        return sessionViewHolderList;

    }
}
