package com.example.cameron.ethereumtest1.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.data.ContentItem;
import com.example.cameron.ethereumtest1.fragments.ContentListFragment.OnListFragmentInteractionListener;
import com.example.cameron.ethereumtest1.util.DataUtils;

import java.util.List;

import static com.example.cameron.ethereumtest1.util.DataUtils.convertTimeStampToDateString;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContentItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyContentItemRecyclerViewAdapter extends RecyclerView.Adapter<MyContentItemRecyclerViewAdapter.ViewHolder> {

    private final List<ContentItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyContentItemRecyclerViewAdapter(List<ContentItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        holder.mBodyView.setText(mValues.get(position).primaryText);
        holder.mDateAndAuthorView.setText("Published " + DataUtils.convertTimeStampToDateString(mValues.get(position).publishedDate)
                + " by " + mValues.get(position).publishedBy);

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
        public final TextView mTitleView;
        public final TextView mBodyView;
        public final TextView mDateAndAuthorView;
        public ContentItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mBodyView = (TextView) view.findViewById(R.id.body);
            mDateAndAuthorView = (TextView) view.findViewById(R.id.dateAndAuthor);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
