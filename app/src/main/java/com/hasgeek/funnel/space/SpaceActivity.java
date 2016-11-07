package com.hasgeek.funnel.space;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.hasgeek.funnel.data.AuthController;
import com.hasgeek.funnel.data.ContactExchangeController;
import com.hasgeek.funnel.data.SessionController;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.APIController;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Attendee;
import com.hasgeek.funnel.model.ContactExchangeContact;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.scanner.ScannerActivity;
import com.hasgeek.funnel.session.SessionActivity;
import com.hasgeek.funnel.space.fragments.ContactExchangeFragment;
import com.hasgeek.funnel.space.fragments.OverviewFragment;
import com.hasgeek.funnel.space.fragments.ScheduleContainerFragment;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SpaceActivity extends BaseActivity {

    public static final String EXTRA_SPACE_ID = "extra_space_id";

    public static final String STATE_FRAGMENT_ID = "state_fragment_id";

    public String stateCurrentFragmentId;

    public Space space_Cold;

    public boolean currentLoggedIn;

    Toolbar toolbar;
    AHBottomNavigation bottomNavigation;
    FloatingActionButton fab;

    OverviewFragment overviewFragment;
    ScheduleContainerFragment scheduleContainerFragment;
    ContactExchangeFragment contactExchangeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_space_bottombar);

        Intent intent = getIntent();
        final String spaceId = intent.getStringExtra(EXTRA_SPACE_ID);

        space_Cold = SpaceController.getSpaceById_Cold(getRealm(), spaceId);

        if(space_Cold ==null) {
            notFoundError();
        }

        if (AuthController.isLoggedIn()) {
            currentLoggedIn = true;
            fetchAttendees();
            syncContactExchangeContacts();
        }

        fetchSessions();

        initViews(savedInstanceState);
    }

    void fetchSessions() {
        APIController.getService().getSessionsBySpaceId(space_Cold.getId())
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
                        Realm realm = Realm.getDefaultInstance();
                        SessionController.deleteSessionsBySpaceId(realm, space_Cold.getId());
                        SessionController.saveSessions(realm, sessions);
                        realm.close();
                        l("Saved "+sessions.size()+" sessions for "+space_Cold.getTitle());
                    }
                });
    }

    void fetchAttendees() {
        APIController.getService().getAttendeesBySpaceId(space_Cold.getId())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Attendee>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Attendee> attendeeList) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        ContactExchangeController.deleteAttendeesBySpaceId(realm, space_Cold.getId());
                        ContactExchangeController.saveAttendees(realm, attendeeList);
                        realm.commitTransaction();
                        realm.close();
                        l("Saved "+attendeeList.size()+" attendees for "+space_Cold.getTitle());
                    }
                });
    }


    void syncContactExchangeContacts() {
        List<ContactExchangeContact> contactExchangeContacts = ContactExchangeController.getUnsyncedContactExchangeContactsBySpaceId_Cold(getRealm(), space_Cold.getId());

        for (ContactExchangeContact c: contactExchangeContacts) {
            APIController.getService().syncContactExchangeContact(c)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ContactExchangeContact>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ContactExchangeContact contactExchangeContact) {
                            contactExchangeContact.setSpace(space_Cold);
                            contactExchangeContact.setSynced(true);
                            ContactExchangeController.updateContactExchangeContact(getRealm(), contactExchangeContact);
                            l("synced");
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        if (!currentLoggedIn && AuthController.isLoggedIn()) {
            fetchAttendees();
            syncContactExchangeContacts();
            currentLoggedIn = true;
        }
        super.onResume();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

//        if (AuthController.isLoggedIn()!=true) {
//            String url = "http://auth.hasgeek.com/auth?client_id=eDnmYKApSSOCXonBXtyoDQ&scope=id+email+phone+organizations+teams+com.talkfunnel:*&response_type=token";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
//        }

        toolbar = (Toolbar) findViewById(R.id.spaces_list_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(space_Cold.getTitle());
        getSupportActionBar().setSubtitle(space_Cold.getDatelocation());
        getSupportActionBar().setIcon(R.drawable.ic_droidcon);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem overviewBottomNavItem = new AHBottomNavigationItem("Overview", R.drawable.ic_home, R.color.colorAccent);
        AHBottomNavigationItem scheduleBottomNavItem = new AHBottomNavigationItem("Schedule", R.drawable.ic_time_schedule, R.color.colorAccent);
        AHBottomNavigationItem contactBottomNavItem = new AHBottomNavigationItem("Contacts", R.drawable.ic_person, R.color.colorAccent);



        ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

        bottomNavigationItems.add(overviewBottomNavItem);
        bottomNavigationItems.add(scheduleBottomNavItem);
        bottomNavigationItems.add(contactBottomNavItem);


        bottomNavigation.addItems(bottomNavigationItems);


        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (wasSelected)
                    return true;
                switch (position) {
                    case 0:
                        switchToOverview();
                        return true;
                    case 1:
                        switchToSchedule();
                        return true;
                    case 2:
                        switchToContacts();
                        return true;
                }
                return false;
            }
        });

        bottomNavigation.setBehaviorTranslationEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);


        if (savedInstanceState != null ) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment overviewFragmentTemp = fragmentManager.getFragment(savedInstanceState, OverviewFragment.FRAGMENT_TAG);

            overviewFragment = overviewFragmentTemp != null ? (OverviewFragment) overviewFragmentTemp: OverviewFragment.newInstance(space_Cold.getId());


            Fragment scheduleContainerFragmentTemp = fragmentManager.getFragment(savedInstanceState, ScheduleContainerFragment.FRAGMENT_TAG);

            scheduleContainerFragment = scheduleContainerFragmentTemp != null ? (ScheduleContainerFragment) scheduleContainerFragmentTemp : ScheduleContainerFragment.newInstance(space_Cold.getId());


            Fragment contactExchangeFragmentTemp = fragmentManager.getFragment(savedInstanceState, ContactExchangeFragment.FRAGMENT_TAG);

            contactExchangeFragment = contactExchangeFragmentTemp != null ? (ContactExchangeFragment) contactExchangeFragmentTemp : ContactExchangeFragment.newInstance(space_Cold.getId());
            stateCurrentFragmentId = savedInstanceState.getString(STATE_FRAGMENT_ID, OverviewFragment.FRAGMENT_TAG);
        }
        else {

            overviewFragment =  OverviewFragment.newInstance(space_Cold.getId());

            scheduleContainerFragment = ScheduleContainerFragment.newInstance(space_Cold.getId());

            contactExchangeFragment = ContactExchangeFragment.newInstance(space_Cold.getId());

            stateCurrentFragmentId = OverviewFragment.FRAGMENT_TAG;
        }

        switch (stateCurrentFragmentId) {
            case OverviewFragment.FRAGMENT_TAG:
                switchToOverview();
                break;
            case ScheduleContainerFragment.FRAGMENT_TAG:
                switchToSchedule();
                break;
            case ContactExchangeFragment.FRAGMENT_TAG:
                switchToContacts();
                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(STATE_FRAGMENT_ID, stateCurrentFragmentId);

        super.onSaveInstanceState(outState);
    }

    void switchToOverview() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


        if (overviewFragment.isAdded()) {
            fragmentTransaction.show(overviewFragment);
        } else {
            fragmentTransaction.add(R.id.activity_space_fragment_frame, overviewFragment, OverviewFragment.FRAGMENT_TAG);
        }

        if (scheduleContainerFragment.isAdded())
            fragmentTransaction.hide(scheduleContainerFragment);

        if (contactExchangeFragment.isAdded())
            fragmentTransaction.hide(contactExchangeFragment);

        fragmentTransaction.commit();

        stateCurrentFragmentId = OverviewFragment.FRAGMENT_TAG;


        fab.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.space_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!AuthController.isLoggedIn()) {
            MenuItem menuItem = menu.findItem(R.id.contact_exchange_menu_logout);
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contact_exchange_menu_logout:
                AuthController.deleteAuthToken();
                currentLoggedIn = false;
                return true;
            case R.id.contact_exchange_menu_export:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void switchToSchedule() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (scheduleContainerFragment.isAdded()) {
            fragmentTransaction.show(scheduleContainerFragment);
        } else {
            fragmentTransaction.add(R.id.activity_space_fragment_frame, scheduleContainerFragment, ScheduleContainerFragment.FRAGMENT_TAG);
        }

        if (overviewFragment.isAdded())
            fragmentTransaction.hide(overviewFragment);

        if (contactExchangeFragment.isAdded())
            fragmentTransaction.hide(contactExchangeFragment);


        fragmentTransaction.commit();


        stateCurrentFragmentId = ScheduleContainerFragment.FRAGMENT_TAG;

        fab.setVisibility(View.GONE);

    }

    void switchToContacts() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim., android.R.anim.slide_out_right);

        if (contactExchangeFragment.isAdded()) {
            fragmentTransaction.show(contactExchangeFragment);
        } else {
            fragmentTransaction.add(R.id.activity_space_fragment_frame, contactExchangeFragment, ContactExchangeFragment.FRAGMENT_TAG);
        }

        if (overviewFragment.isAdded())
            fragmentTransaction.hide(overviewFragment);

        if (scheduleContainerFragment.isAdded())
            fragmentTransaction.hide(scheduleContainerFragment);


        fragmentTransaction.commit();

        stateCurrentFragmentId = ContactExchangeFragment.FRAGMENT_TAG;

        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBadgeScan(view);
            }
        });

    }


    void showBadgeScan(View view) {

        if(view == null)
            view = getCurrentFocus();

        if (AuthController.isLoggedIn()) {
            Intent intent = new Intent(view.getContext(), ScannerActivity.class);
            intent.putExtra(ScannerActivity.EXTRA_SPACE_ID, space_Cold.getId());
            view.getContext().startActivity(intent);
        }

        else {
            Snackbar.make(view, "Hang on, we need to know who you are", Snackbar.LENGTH_LONG)
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

    }

    void showSessionDetails(Context context, Session session) {
        Intent intent = new Intent(context, SessionActivity.class);
        intent.putExtra(SessionActivity.EXTRA_SESSION_ID, session.getId());
        context.startActivity(intent);
    }

    OverviewFragment.OverviewFragmentInteractionListener overviewFragmentInteractionListener = new OverviewFragment.OverviewFragmentInteractionListener() {
        @Override
        public void onScheduleClick() {
            switchToSchedule();
        }

        @Override
        public void onScanBadgeClick(View v) {
            showBadgeScan(v);
        }

        @Override
        public void onSessionClick(Session s) {
            showSessionDetails(SpaceActivity.this, s);
        }
    };

    @Override
    public void notFoundError() {
        finish();
        toast("Space not found");
    }


    ItemInteractionListener sessionItemInteractionListener = new ItemInteractionListener<Session>() {
        @Override
        public void onItemClick(View v, Session session) {
            showSessionDetails(v.getContext(), session);
        }

        @Override
        public void onItemLongClick(View v, Session item) {

        }
    };


    ContactExchangeFragment.ContactExchangeFragmentListener contactExchangeFragmentListener = new ContactExchangeFragment.ContactExchangeFragmentListener() {
        @Override
        public void onContactExchangeContactClick(ContactExchangeContact contactExchangeContact) {
            syncContactExchangeContacts();
        }

        @Override
        public void onScanBadgeClick(View view) {

        }

        @Override
        public void onContactExchangeContactLongClick(ContactExchangeContact contactExchangeContact) {
            RealmResults<ContactExchangeContact> contactExchangeContactRealmResults = getRealm().where(ContactExchangeContact.class)
                    .equalTo("id", contactExchangeContact.getId())
                    .findAll();
            if (contactExchangeContactRealmResults.size() > 0) {
                ContactExchangeContact c = contactExchangeContactRealmResults.first();
                getRealm().beginTransaction();
                c.setSynced(false);
                getRealm().commitTransaction();
            }
        }
    };


    public OverviewFragment.OverviewFragmentInteractionListener getOverviewFragmentInteractionListener() {
        return overviewFragmentInteractionListener;
    }

    public ItemInteractionListener getSessionItemInteractionListener() {
        return sessionItemInteractionListener;
    }

    public ContactExchangeFragment.ContactExchangeFragmentListener getContactExchangeFragmentListener() {
        return contactExchangeFragmentListener;
    }
}
