package com.hasgeek.funnel.space;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.APIService;
import com.hasgeek.funnel.data.DatabaseService;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.session.SessionActivity;
import com.hasgeek.funnel.space.fragments.ScannerFragment;
import com.hasgeek.funnel.space.fragments.SingleTrackFragment;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SpaceActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    public Spinner mSpinner;
    public String selectedEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spaces_list);

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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        mSpinner = (Spinner) findViewById(R.id.spinner_nav);
        List<String> list = new ArrayList<String>();
        list.add("Day 1");
        list.add("Day 2");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        APIService.getService().getAllSpaces()
                .doOnNext(new Action1<List<Space>>() {
                    @Override
                    public void call(List<Space> spaceList) {
                        Realm realm = Realm.getDefaultInstance();
                        DatabaseService.saveSpaces(realm, spaceList);
                        realm.close();
                        l("Saved spaces");
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Space>>() {
                    @Override
                    public void call(List<Space> spaceList) {
                        Toast.makeText(getApplicationContext(), "Updated data: "+spaceList.size()+" spaces.", Toast.LENGTH_SHORT).show();
                    }
                });

        APIService.getService().getProposals("https://droidconin.talkfunnel.com/2016/")
                .doOnNext(new Action1<List<Proposal>>() {
                    @Override
                    public void call(List<Proposal> proposals) {
                        Realm realm = Realm.getDefaultInstance();
                        DatabaseService.saveProposals(realm, proposals);
                        realm.close();
                        l("Saved proposals for droidconin 2016");
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Proposal>>() {
                   @Override
                   public void call(List<Proposal> proposals) {
                       Toast.makeText(getApplicationContext(), "Updated data: " + proposals.size() + " proposals.", Toast.LENGTH_SHORT).show();
                   }
               });


    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(SingleTrackFragment.newInstance(itemInteractionListener), "Main Auditorium");
        adapter.addFragment(SingleTrackFragment.newInstance(itemInteractionListener), "Auditorium 2");
        adapter.addFragment(SingleTrackFragment.newInstance(itemInteractionListener), "Banquet Hall");
        adapter.addFragment(SingleTrackFragment.newInstance(itemInteractionListener), "BOF Area");
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

    ItemInteractionListener itemInteractionListener = new ItemInteractionListener<Proposal>() {
        @Override
        public void onItemClick(View v, Proposal item) {

            Context context = v.getContext();
            Intent intent = new Intent(context, SessionActivity.class);
            intent.putExtra(SessionActivity.EXTRA_PROPOSAL_ID, item.getId());

            context.startActivity(intent);

        }

        @Override
        public void onItemLongClick(View v, Proposal item) {

        }
    };
}
