package com.hasgeek.funnel.spaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.APIController;
import com.hasgeek.funnel.data.DataManager;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.space.SpaceActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Author: @karthikb351
 * Project: android
 */

public class SpacesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button retryButton;
    private LinearLayout linearLayout;

    private boolean skip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spaces_list);

        initViews(savedInstanceState);

        fetchSpaces();
    }

    void fetchSpaces() {
        APIController.getService().getAllSpaces()
                .doOnNext(new Action1<List<Space>>() {
                    @Override
                    public void call(List<Space> spaceList) {
                        Realm realm = Realm.getDefaultInstance();
                        SpaceController.saveSpaces(realm, spaceList);
                        realm.close();
                        l("Saved spaces");
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Space>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        retryButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<Space> spaces) {
                        linearLayout.setVisibility(View.GONE);
                        if (!skip) {}
//                            goToDroidcon();
                    }
                });
    }


    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = (RecyclerView) findViewById(R.id.activity_spaces_list_recyclerview);
        progressBar = (ProgressBar) findViewById(R.id.activity_spaces_list_progressbar);
        retryButton = (Button) findViewById(R.id.activity_spaces_list_retryBtn);
        retryButton.setVisibility(View.GONE);
        linearLayout = (LinearLayout) findViewById(R.id.activity_spaces_list_progress_layout);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSpaces();
                progressBar.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        RealmResults<Space> spaces = DataManager.getAllSpaces(getRealm());
        if (spaces.size() != 0) {
            linearLayout.setVisibility(View.GONE);
//            goToDroidcon();

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(SpacesActivity.this));

        recyclerView.setAdapter(new SpacesRecyclerViewAdapter(SpacesActivity.this, spaces, new ItemInteractionListener<Space>() {

            @Override
            public void onItemClick(View v, Space item) {
                goToSpace(item);
            }

            @Override
            public void onItemLongClick(View v, Space item) {   }

        }));
    }

    void goToSpace(Space space) {
        Context context = SpacesActivity.this;
        Intent intent = new Intent(context, SpaceActivity.class);
        intent.putExtra(SpaceActivity.EXTRA_SPACE_ID, space.getId());
        context.startActivity(intent);
    }

    void goToDroidcon() {

        Space droidcon = SpaceController.getSpaceById_Hot(getRealm(), "117");
        if (droidcon != null) {
            skip = true;
            goToSpace(droidcon);
            finish();
        }

    }

    @Override
    public void notFoundError() {

    }
}
