package com.hasgeek.funnel.space.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.ContactExchangeContact;
import com.hasgeek.funnel.space.SpaceActivity;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ContactExchangeRecyclerViewAdapter extends RealmRecyclerViewAdapter<ContactExchangeContact, ContactExchangeRecyclerViewAdapter.ContactExchangeViewHolder> {

    private final ItemInteractionListener mListener;
    private final SpaceActivity activity;


    public ContactExchangeRecyclerViewAdapter(SpaceActivity activity, OrderedRealmCollection<ContactExchangeContact> data, ItemInteractionListener<ContactExchangeContact> listener) {
        super(activity, data, true);
        this.activity = activity;
        mListener = listener;
    }

    @Override
    public ContactExchangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_exchange_item, parent, false);
        return new ContactExchangeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactExchangeViewHolder holder, int position) {

        ContactExchangeContact a = getData().get(position);

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

    public class ContactExchangeViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView fullname;
        public TextView company;
        public ContactExchangeContact mItem;

        public ContactExchangeViewHolder(View view) {
            super(view);
            mView = view;
            fullname = (TextView) view.findViewById(R.id.fragment_contact_exchange_item_fullname);
            company = (TextView) view.findViewById(R.id.fragment_contact_exchange_item_company);
        }
    }
}
