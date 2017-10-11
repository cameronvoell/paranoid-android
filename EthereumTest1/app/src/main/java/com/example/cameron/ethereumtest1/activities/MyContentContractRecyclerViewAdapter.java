package com.example.cameron.ethereumtest1.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.activities.ContentContractListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Content.ContentContract.ContentContractItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyContentContractRecyclerViewAdapter extends RecyclerView.Adapter<MyContentContractRecyclerViewAdapter.ViewHolder> {

    private final List<Content.ContentContract.ContentContractItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private String mSelected;

    public MyContentContractRecyclerViewAdapter(List<Content.ContentContract.ContentContractItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        SharedPreferences sharedPreferences = ((Activity)listener).getSharedPreferences(MainActivity.sharedPreferencesName, 0);
        mSelected = sharedPreferences.getString("selected", "");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_content_contract, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).numPosts + "");
        holder.mNameView.setText(mValues.get(position).name);
        holder.mDescriptionView.setText(mValues.get(position).description);

        if (mValues.get(position).name.equals(mSelected)) {
            holder.mNameView.getRootView().setBackgroundColor(Color.GREEN);
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mNameView;
        public final TextView mDescriptionView;
        public Content.ContentContract.ContentContractItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mNameView = (TextView) view.findViewById(R.id.name);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
