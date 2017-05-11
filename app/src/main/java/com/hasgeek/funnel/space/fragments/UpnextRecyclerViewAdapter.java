package com.hasgeek.funnel.space.fragments;

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
import com.hasgeek.funnel.model.Session;

import java.util.List;


public class UpnextRecyclerViewAdapter extends RecyclerView.Adapter<UpnextRecyclerViewAdapter.UpnextViewHolder> {

    private final ItemInteractionListener mListener;

    private List<Session> data;

    public UpnextRecyclerViewAdapter(List<Session> data, ItemInteractionListener<Session> listener) {
        mListener = listener;
        this.data = data;
    }

    @Override
    public UpnextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_overview_item_upnext, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int)(DeviceController.getDeviceWidth()*0.84);
        view.setLayoutParams(layoutParams);
        return new UpnextViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(final UpnextViewHolder holder, int position) {

        Session s = data.get(position);

        holder.mItem = s;

        holder.tv_title.setText(s.getTitle());
        if (s.getSpeaker() == null || s.getSpeaker() == "")
            holder.tv_speaker.setVisibility(View.GONE);
        else
            holder.tv_speaker.setText(s.getSpeaker());
        holder.tv_time.setText(TimeUtils.getSimpleTimeForString(s.getStart()));

        if(s.getRoom()==null) {
            holder.tv_location.setText("");
        } else if(s.getRoom().contains("banq")) {
            holder.tv_location.setText("Banquet hall - DevConf.in");
        } else if(s.getRoom().contains("otr")) {
            holder.tv_location.setText("Porch on first floor, opposite the auditorium – OTR sessions");
        }
        else {
            holder.tv_location.setText("Auditorium – Rootconf");
        }

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

    public class UpnextViewHolder extends RecyclerView.ViewHolder {
        public CardView mView;
        public TextView tv_title;
        public TextView tv_speaker;
        public TextView tv_location;
        public TextView tv_time;
        public Session mItem;

        public UpnextViewHolder(CardView view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.fragment_overview_item_upnext_tv_session_title);
            tv_speaker = (TextView) view.findViewById(R.id.fragment_overview_item_upnext_tv_session_speaker);
            tv_time = (TextView) view.findViewById(R.id.fragment_overview_item_upnext_tv_session_time);
            tv_location = (TextView) view.findViewById(R.id.fragment_overview_item_upnext_tv_session_location);
        }
    }
}
