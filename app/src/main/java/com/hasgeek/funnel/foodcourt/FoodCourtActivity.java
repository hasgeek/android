package com.hasgeek.funnel.foodcourt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.FoodCourtVendor;
import com.hasgeek.funnel.model.FoodCourtVendorItem;
import com.hasgeek.funnel.model.FoodCourtVendorSection;
import com.hasgeek.funnel.model.Metadata;
import com.hasgeek.funnel.model.Space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Author: @karthikb351
 * Project: android
 */

public class FoodCourtActivity extends BaseActivity {

    public static final String EXTRA_SPACE_ID = "extra_space_id";

    public Space space_Cold;
    public Metadata metadata_Cold;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_foodcourt);


        Intent intent = getIntent();
        final String spaceId = intent.getStringExtra(EXTRA_SPACE_ID);

        space_Cold = SpaceController.getSpaceById_Cold(getRealm(), spaceId);

        if(space_Cold ==null) {
            notFoundError();
            return;
        }

        metadata_Cold = SpaceController.getSpaceMetadataBySpaceId(space_Cold.getId());

        if (metadata_Cold == null) {
            notFoundError();
            return;
        }

        if (metadata_Cold.getFoodCourtVendors()==null) {
            notFoundError();
            return;
        }

        if (metadata_Cold.getFoodCourtVendors().size()==0) {
            notFoundError();
            return;
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_foodcourt_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Food Court");
        getSupportActionBar().setSubtitle(space_Cold.getTitle());


        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_foodcourt_viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_foodcourt_tablayout);

        FoodCourtVendorPagerAdapter foodCourtVendorPagerAdapter = new FoodCourtVendorPagerAdapter(FoodCourtActivity.this, metadata_Cold.getFoodCourtVendors());

        viewPager.setAdapter(foodCourtVendorPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        initViews(savedInstanceState);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void notFoundError() {
        finish();
        toast("Space not found");
    }


    public class FoodCourtVendorPagerAdapter extends PagerAdapter {

        private Context context;

        public List<FoodCourtVendor> foodCourtVendors;

        public FoodCourtVendorPagerAdapter(Context context, List<FoodCourtVendor> foodCourtVendors){
            this.context = context;
            this.foodCourtVendors = foodCourtVendors;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_foodcourt_foodcourt_vendor_page, container, false);
            RecyclerView recyclerView = (RecyclerView) viewGroup.findViewById(R.id.activity_foodcourt_foodcourt_vendor_page_recyclerview);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            List<String> sections = new ArrayList<>();

            List<FoodCourtVendorSection> foodCourtVendorSections = foodCourtVendors.get(position).getSections();
            HashMap<String, List<FoodCourtVendorItem>> hashMap = new HashMap<>();

            for (FoodCourtVendorSection s: foodCourtVendorSections) {
                sections.add(s.getTitle());
                hashMap.put(s.getTitle(), s.getItems());
            }




            recyclerView.setAdapter(new FoodCourtVendorRecyclerViewAdapter(hashMap, sections, new ItemInteractionListener<FoodCourtVendorItem>() {
                @Override
                public void onItemClick(View v, FoodCourtVendorItem item) {

                }

                @Override
                public void onItemLongClick(View v, FoodCourtVendorItem item) {

                }
            }));

            container.addView(viewGroup);
            return viewGroup;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return foodCourtVendors.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return foodCourtVendors.get(position).getTitle();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous activity
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
