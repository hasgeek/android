package com.hasgeek.funnel.spaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.APIService;
import com.hasgeek.funnel.data.DataManager;
import com.hasgeek.funnel.data.DatabaseService;
import com.hasgeek.funnel.data.SessionService;
import com.hasgeek.funnel.data.SpaceService;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.session.SessionActivity;
import com.hasgeek.funnel.space.SpaceActivity;
import com.hasgeek.funnel.space.fragments.SingleTrackRecyclerViewAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Author: @karthikb351
 * Project: android
 */

public class SpacesActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spaces_list);
        recyclerView = (RecyclerView) findViewById(R.id.activity_spaces_list_recyclerview);

        APIService.getService().getAllSpaces()
                .doOnNext(new Action1<List<Space>>() {
                    @Override
                    public void call(List<Space> spaceList) {
                        Realm realm = Realm.getDefaultInstance();
                        SpaceService.saveSpaces(realm, spaceList);
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
                        l("Saved "+spaceList.size()+" spaces");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RealmResults<Space> spaces = DataManager.getAllSpaces(getRealm());

        recyclerView.setLayoutManager(new LinearLayoutManager(SpacesActivity.this));

        l("We have: "+spaces.size()+" spaces");
        recyclerView.setAdapter(new SpacesRecyclerViewAdapter(SpacesActivity.this, spaces, new ItemInteractionListener<Space>() {
            @Override
            public void onItemClick(View v, Space item) {
                Context context = v.getContext();
                Intent intent = new Intent(context, SpaceActivity.class);
                intent.putExtra(SpaceActivity.EXTRA_SPACE_ID, item.getId());
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View v, Space item) {

            }
        }));
    }
}
