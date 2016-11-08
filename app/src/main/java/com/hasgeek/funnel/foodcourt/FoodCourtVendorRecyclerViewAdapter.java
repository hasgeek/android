package com.hasgeek.funnel.foodcourt;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.DeviceController;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.model.FoodCourtVendorItem;
import com.hasgeek.funnel.model.Session;
import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter;

import java.util.HashMap;
import java.util.List;


public class FoodCourtVendorRecyclerViewAdapter extends SimpleSectionedAdapter<FoodCourtVendorRecyclerViewAdapter.FoodCourtVendorViewHolder> {

    private final ItemInteractionListener mListener;

    private HashMap<String, List<FoodCourtVendorItem>> data;
    private List<String> sections;

    public FoodCourtVendorRecyclerViewAdapter(HashMap<String, List<FoodCourtVendorItem>> data, List<String> sections, ItemInteractionListener<FoodCourtVendorItem> listener) {
        mListener = listener;
        this.data = data;
        this.sections = sections;
    }

    @Override
    protected String getSectionHeaderTitle(int section) {
        return sections.get(section);
    }

    @Override
    protected int getSectionCount() {
        return sections.size();
    }

    @Override
    protected int getItemCountForSection(int section) {
        return data.get(sections.get(section)).size();
    }

    @Override
    protected FoodCourtVendorViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_foodcourt_foodcourt_vendor_page_item, parent, false);
        return new FoodCourtVendorViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(final FoodCourtVendorViewHolder holder, int section, int position) {

        String sectionKey = sections.get(section);
        FoodCourtVendorItem item = data.get(sectionKey).get(position);

        holder.mItem = item;

        holder.tv_title.setText(item.getTitle());
        holder.tv_price.setText("Rs. "+item.getPrice());

        String type = item.getType();

        if (type.equals("Veg")) {
            holder.tv_title.setTextColor(Color.parseColor("#008400"));
        } else if (type.equals("Non-veg")) {
            holder.tv_title.setTextColor(Color.parseColor("#954222"));
        } else if (type.equals("Beverage")) {
            holder.tv_title.setTextColor(Color.DKGRAY);
        } else {
            holder.tv_title.setTextColor(Color.LTGRAY);
        }

        holder.tv_type.setText(type);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemClick(v, holder.mItem);
                }
            }
        });
    }


    public class FoodCourtVendorViewHolder extends RecyclerView.ViewHolder {
        public CardView mView;
        public TextView tv_title;
        public TextView tv_price;
        public TextView tv_type;
        public FoodCourtVendorItem mItem;

        public FoodCourtVendorViewHolder(CardView view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.activity_foodcourt_foodcourt_vendor_page_item_title);
            tv_price = (TextView) view.findViewById(R.id.activity_foodcourt_foodcourt_vendor_page_item_price);
            tv_type = (TextView) view.findViewById(R.id.activity_foodcourt_foodcourt_vendor_page_item_type);
        }
    }
}
