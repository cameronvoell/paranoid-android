package com.example.cameron.ethereumtest1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.ethereum.EthereumConstants;
import com.example.cameron.ethereumtest1.fragments.UserFragment.OnListFragmentInteractionListener;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.model.UserFragmentContentItem;
import com.example.cameron.ethereumtest1.util.DataUtils;
import java.util.List;

/**
 * Created by cameron on 10/26/17.
 */

public class UserFragmentContentItemListAdapter extends RecyclerView.Adapter<UserFragmentContentItemListAdapter.ViewHolder> {

    private final List<UserFragmentContentItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public UserFragmentContentItemListAdapter(List<UserFragmentContentItem> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ContentItem ci = mValues.get(position).contentItem;

        if (!ci.primaryImageUrl.equals("empty")) {
            holder.mImageView.setVisibility(View.VISIBLE);
            //Loading image from url into imageView
            Glide.with(mContext)
                    .load(EthereumConstants.IPFS_GATEWAY_URL + ci.primaryImageUrl)
                    .into(holder.mImageView);
        } else {
            holder.mImageView.setVisibility(View.GONE);
        }

        holder.mItem = ci;
        holder.mTitleView.setText(ci.title);
        holder.mBodyView.setText(ci.primaryText);
        holder.mDateAndAuthorView.setText("Published " + DataUtils.convertTimeStampToDateString(ci.publishedDate)
                + " by " + ci.publishedBy);



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(position, holder.mItem);
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
        public final ImageView mImageView;
        public ContentItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mBodyView = (TextView) view.findViewById(R.id.body);
            mDateAndAuthorView = (TextView) view.findViewById(R.id.dateAndAuthor);
            mImageView = (ImageView) view.findViewById(R.id.contentImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
