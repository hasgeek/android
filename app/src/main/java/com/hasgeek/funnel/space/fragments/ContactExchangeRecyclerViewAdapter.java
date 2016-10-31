package com.hasgeek.funnel.space.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Attendee;
import com.hasgeek.funnel.space.SpaceActivity;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ContactExchangeRecyclerViewAdapter extends RealmRecyclerViewAdapter<Attendee, ContactExchangeRecyclerViewAdapter.AttendeeViewHolder> {

    private final ItemInteractionListener mListener;
    private final SpaceActivity activity;


    public ContactExchangeRecyclerViewAdapter(SpaceActivity activity, OrderedRealmCollection<Attendee> data, ItemInteractionListener<Attendee> listener) {
        super(activity, data, true);
        this.activity = activity;
        mListener = listener;
    }

    @Override
    public AttendeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_exchange_item, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AttendeeViewHolder holder, int position) {

        Attendee a = getData().get(position);

        holder.mItem = a;
        holder.fullname.setText(a.getFullname());
        holder.company.setText(a.getCompany());

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

    public class AttendeeViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView fullname;
        public TextView company;
        public Attendee mItem;

        public AttendeeViewHolder(View view) {
            super(view);
            mView = view;
            fullname = (TextView) view.findViewById(R.id.fragment_contact_exchange_item_fullname);
            company = (TextView) view.findViewById(R.id.fragment_contact_exchange_item_company);
        }
    }
}
