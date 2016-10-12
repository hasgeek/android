package com.hasgeek.funnel.space.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.space.SpaceActivity;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class SingleTrackRecyclerViewAdapter extends RealmRecyclerViewAdapter<Proposal, SingleTrackRecyclerViewAdapter.SpaceViewHolder> {

    private final ItemInteractionListener mListener;
    private final SpaceActivity activity;


    public SingleTrackRecyclerViewAdapter(SpaceActivity activity, OrderedRealmCollection<Proposal> data, ItemInteractionListener listener) {
        super(activity, data, true);
        this.activity = activity;
        mListener = listener;
    }

    @Override
    public SpaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_singleday, parent, false);
        return new SpaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpaceViewHolder holder, int position) {
        holder.mItem = getData().get(position);
        holder.mIdView.setText(getData().get(position).getTitle());

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

    public class SpaceViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public Proposal mItem;

        public SpaceViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
        }
    }
}
