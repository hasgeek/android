package com.hasgeek.funnel.space.fragments;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.DeviceController;
import com.hasgeek.funnel.model.Announcement;

import java.util.List;

public class AnnouncementsRecyclerViewAdapter extends RecyclerView.Adapter<AnnouncementsRecyclerViewAdapter.AnnouncementViewHolder> {


    private List<Announcement> data;

    public AnnouncementsRecyclerViewAdapter(List<Announcement> data) {
        this.data = data;
    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_overview_item_announcement, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int)(DeviceController.getDeviceWidth()*0.42);
        view.setLayoutParams(layoutParams);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(final AnnouncementViewHolder holder, int position) {

        Announcement a = data.get(position);
        holder.mItem = a;
        holder.tv_description.setText(a.getDescription());
        holder.tv_time.setText(a.getTime());
        holder.tv_title.setText(a.getTitle());
    }

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        public CardView mView;
        public TextView tv_title;
        public TextView tv_description;
        public TextView tv_time;
        public Announcement mItem;

        public AnnouncementViewHolder(CardView view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.fragment_overview_tv_announcement_item_title);
            tv_description = (TextView) view.findViewById(R.id.fragment_overview_tv_announcement_item_description);
            tv_time = (TextView) view.findViewById(R.id.fragment_overview_tv_announcement_item_time);
        }
    }
}
