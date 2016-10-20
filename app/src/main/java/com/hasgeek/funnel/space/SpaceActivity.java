package com.hasgeek.funnel.space;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.hasgeek.funnel.data.SessionController;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.APIController;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.session.SessionActivity;
import com.hasgeek.funnel.space.fragments.ScannerFragment;
import com.hasgeek.funnel.space.fragments.ScheduleFragment;
import com.hasgeek.funnel.space.fragments.SingleTrackFragment;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SpaceActivity extends BaseActivity {

    public static final String EXTRA_SPACE_ID = "extra_space_id";
    private DrawerLayout mDrawerLayout;
    public Spinner mSpinner;

    public Space space;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_space);

        Intent intent = getIntent();
        String spaceId = intent.getStringExtra(EXTRA_SPACE_ID);

        space = SpaceController.getSpaceById_Cold(getRealm(), spaceId);

        if(space==null) {
            finish();
            toast("No session with that ID found");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.spaces_list_toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_space_fragment_frame, ScheduleFragment.newInstance(space.getId(), itemInteractionListener));
        fragmentTransaction.commit();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ScannerFragment scannerFragment = new ScannerFragment();
                scannerFragment.show(fm, "scanner_fragment");
                Snackbar.make(view, "Hang on, who are you?", Snackbar.LENGTH_LONG)
                        .setAction("Login", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = "http://auth.hasgeek.com/auth?client_id=eDnmYKApSSOCXonBXtyoDQ&scope=id+email+phone+organizations+teams+com.talkfunnel:*&response_type=token";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }).show();
            }
        });


//        APIService.getService().getProposals(space.getId())
//                .doOnNext(new Action1<List<Proposal>>() {
//                    @Override
//                    public void call(List<Proposal> proposals) {
//                        Realm realm = Realm.getDefaultInstance();
//                        Space space = SpaceService.getSpaceById_Cold(realm, "84");
//                        for (Proposal p: proposals) {
//                            p.setSpace(space);
//                        }
//                        ProposalService.saveProposals(realm, proposals);
//                        realm.close();
//                        l("Saved proposals for "+space.getTitle());
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<Proposal>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(List<Proposal> proposals) {
//                        Toast.makeText(getApplicationContext(), "Updated data: " + proposals.size() + " proposals.", Toast.LENGTH_SHORT).show();
//                    }
//                });

        APIController.getService().getSessions(space.getId())
                .doOnNext(new Action1<List<Session>>() {
                    @Override
                    public void call(List<Session> sessions) {
                        Realm realm = Realm.getDefaultInstance();
                        SessionController.saveSessions(realm, sessions);
                        realm.close();
                        l("Saved sessions for space");
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Session>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Session> sessions) {
                        Toast.makeText(getApplicationContext(), "Updated data: " + sessions.size() + " sessions.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(SingleTrackFragment.newInstance(space.getId(), itemInteractionListener), "Main Auditorium");
        adapter.addFragment(SingleTrackFragment.newInstance(space.getId(), itemInteractionListener), "Auditorium 2");
        adapter.addFragment(SingleTrackFragment.newInstance(space.getId(), itemInteractionListener), "Banquet Hall");
        adapter.addFragment(ScheduleFragment.newInstance(space.getId(), itemInteractionListener), "BOF Area");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_overview:
                                break;
                            case R.id.nav_schedule:
                                break;
                            case R.id.nav_contacts:
                                break;
                            case R.id.nav_discussion:
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    ItemInteractionListener itemInteractionListener = new ItemInteractionListener<Session>() {
        @Override
        public void onItemClick(View v, Session session) {

            Context context = v.getContext();
            Intent intent = new Intent(context, SessionActivity.class);
            intent.putExtra(SessionActivity.EXTRA_SESSION_ID, session.getId());

            context.startActivity(intent);

        }

        @Override
        public void onItemLongClick(View v, Session item) {

        }
    };
}
