package com.hasgeek.funnel.space.fragments;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.space.SpaceActivity;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UpnextRecyclerViewAdapter extends RealmRecyclerViewAdapter<Session, UpnextRecyclerViewAdapter.UpnextViewHolder> {

    private final ItemInteractionListener mListener;


    public UpnextRecyclerViewAdapter(Activity activity, OrderedRealmCollection<Session> data, ItemInteractionListener<Session> listener) {
        super(activity, data, true);
        mListener = listener;
    }

    @Override
    public UpnextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_overview_item_upnext, parent, false);
        return new UpnextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UpnextViewHolder holder, int position) {

        Session s = getData().get(position);

        holder.mItem = s;

        holder.tv_title.setText(s.getTitle());
        holder.tv_speaker.setText(s.getSpeaker());
        holder.tv_time.setText("12:00pm");

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
