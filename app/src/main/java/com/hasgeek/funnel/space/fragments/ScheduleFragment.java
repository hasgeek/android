package com.hasgeek.funnel.space.fragments;

import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.SessionController;
import com.hasgeek.funnel.helpers.BaseFragment;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.space.SpaceActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ScheduleFragment extends BaseFragment {

    public static final String EXTRA_SPACE_ID = "extra_space_id";
    private ItemInteractionListener mListener;
    private String spaceId;
    private List<WeekViewEvent> weekViewEvents;
    private RealmResults<Session> sessions;
    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(String spaceId) {
        ScheduleFragment fragment = new ScheduleFragment();

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
            mListener = spaceActivity.getItemInteractionListener();
            spaceId = getArguments().getString(EXTRA_SPACE_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Set the adapter
        if (view instanceof WeekView) {
            Context context = view.getContext();
            final WeekView weekView = (WeekView) view;
            weekView.setNumberOfVisibleDays(1);
            sessions = SessionController.getSessionsBySpaceId(Realm.getDefaultInstance(), spaceId);
            sessions.addChangeListener(new RealmChangeListener<RealmResults<Session>>() {
                @Override
                public void onChange(RealmResults<Session> element) {
                    setUpWeekView(weekView);
                }
            });
            setUpWeekView(weekView);

            Calendar eventDate = Calendar.getInstance();
            eventDate.set(2016, 10, 10);
            weekView.goToDate(eventDate);
            weekView.goToHour(8);
            weekView.setOnEventClickListener(new WeekView.EventClickListener() {
                @Override
                public void onEventClick(WeekViewEvent event, RectF eventRect) {
                    Realm realm = Realm.getDefaultInstance();
                    Session s = SessionController.getSessionById_Cold(realm, String.valueOf(event.getId()));
                    realm.close();
                    mListener.onItemClick(weekView, s);
                }
            });

        }
        return view;
    }

    void setUpWeekView(WeekView weekView) {
        Context context = weekView.getContext();
        l("We have: "+sessions.size()+" items");
        weekViewEvents = new ArrayList<>();
        for (Session session: sessions) {
            WeekViewEvent event = new WeekViewEvent();
            event.setStartTime(TimeUtils.getCalendarFromISODateString(session.getStart()));
            event.setEndTime(TimeUtils.getCalendarFromISODateString(session.getEnd()));
            event.setName(session.getTitle());
            event.setId(Long.parseLong(session.getId()));

            // FIXME: Get actual room details, load colors for event dynamically.

            if(session.getRoom()==null) {
            } else if(session.getRoom().contains("audi")) {
                event.setColor(context.getResources().getColor(R.color.colorPrimary));
                event.setLocation("Main Auditorium");
            }
            else {
                event.setColor(context.getResources().getColor(R.color.colorAccent));
                event.setLocation("Banquet Hall");
            }
            weekViewEvents.add(event);
        }
        weekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            boolean setEvents = false;
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                if (!setEvents) {
                    setEvents = true;
                    return weekViewEvents;
                } else {
                    return new ArrayList<WeekViewEvent>();
                }
            }
        });
        weekView.notifyDatasetChanged();
    }

    @Override
    public void notFoundError() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
