package com.hasgeek.funnel.spaces;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.interactions.ItemInteractionListener;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;
import com.hasgeek.funnel.space.SpaceActivity;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class SpacesRecyclerViewAdapter extends RealmRecyclerViewAdapter<Space, SpacesRecyclerViewAdapter.SpaceViewHolder> {

    private final ItemInteractionListener mListener;
    private final SpacesActivity activity;


    public SpacesRecyclerViewAdapter(SpacesActivity activity, OrderedRealmCollection<Space> data, ItemInteractionListener<Space> listener) {
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
                    Realm realm = Realm.getDefaultInstance();
                    mListener.onItemClick(v, realm.copyFromRealm(holder.mItem));
                    realm.close();
                }
            }
        });
    }

    public class SpaceViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public Space mItem;

        public SpaceViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
        }
    }
}
