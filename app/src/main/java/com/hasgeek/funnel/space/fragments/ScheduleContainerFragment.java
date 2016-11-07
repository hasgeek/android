package com.hasgeek.funnel.space.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.hasgeek.funnel.helpers.schedule.ScheduleHelper;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.space.SpaceActivity;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ScheduleContainerFragment extends BaseFragment {

    public static final String EXTRA_SPACE_ID = "extra_space_id";
    public static final String FRAGMENT_TAG = "ScheduleContainerFragment";
    private String spaceId;

    public ScheduleContainerFragment() {
    }

    public static ScheduleContainerFragment newInstance(String spaceId) {

        ScheduleContainerFragment fragment = new ScheduleContainerFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SPACE_ID, spaceId);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SpaceActivity) {
            SpaceActivity spaceActivity = (SpaceActivity) getActivity();
            spaceId = getArguments().getString(EXTRA_SPACE_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_container, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        List<Session> sessions = SessionController.getSessionsBySpaceId(Realm.getDefaultInstance(), spaceId);
        HashMap<Integer, List<Session>> hashMap = ScheduleHelper.getDayOfYearMapFromSessions(sessions);

        List<Integer> days = new ArrayList<>(hashMap.keySet());

        Collections.sort(days, new Comparator<Integer>() {

            @Override
            public int compare(Integer i1, Integer i2) {
                return i1.compareTo(i2);
            }
        });


        SessionPagerAdapter sessionPagerAdapter = new SessionPagerAdapter(getChildFragmentManager());

        for (int i : days) {
            Calendar x = Calendar.getInstance();
            x.set(Calendar.DAY_OF_YEAR, i);
            sessionPagerAdapter.addFragment(ScheduleFragment.newInstance(spaceId, i), TimeUtils.getDayOfWeekFromCalendar(x));
        }

        viewPager.setAdapter(sessionPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


        return view;
    }

    @Override
    public void notFoundError() {
        // TODO: Implement not found view
    }

    @Override
    public void refresh() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class SessionPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public SessionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}
