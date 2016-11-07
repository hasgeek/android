package com.hasgeek.funnel.space.fragments;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.space.SpaceActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UpnextRecyclerViewAdapter extends RecyclerView.Adapter<UpnextRecyclerViewAdapter.UpnextViewHolder> {

    private final ItemInteractionListener mListener;

    private List<Session> data;

    public UpnextRecyclerViewAdapter(List<Session> data, ItemInteractionListener<Session> listener) {
        mListener = listener;
        this.data = data;
    }

    @Override
    public UpnextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_overview_item_upnext, parent, false);
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
        public View mView;
        public TextView tv_title;
        public TextView tv_speaker;
        public TextView tv_time;
        public Session mItem;

        public UpnextViewHolder(View view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.fragment_overview_item_upnext_tv_session_title);
            tv_speaker = (TextView) view.findViewById(R.id.fragment_overview_item_upnext_tv_session_speaker);
            tv_time = (TextView) view.findViewById(R.id.fragment_overview_item_upnext_tv_session_time);
        }
    }
}
