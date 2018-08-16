package com.example.cameron.ethereumtest1.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.database.DBUserContentItem;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;
import com.example.cameron.ethereumtest1.ethereum.EthereumConstants;
import com.example.cameron.ethereumtest1.fragments.UserFragment.OnListFragmentInteractionListener;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.model.UserFragmentContentItem;
import com.example.cameron.ethereumtest1.util.DataUtils;

import org.jsoup.Jsoup;

import java.util.List;

/**
 * Created by cameron on 10/26/17.
 */

public class UserFragmentContentItemRecyclerViewAdapter extends RecyclerView.Adapter<UserFragmentContentItemRecyclerViewAdapter.ViewHolder> {

    private CursorAdapter mCursorAdapter;
    private final Context mContext;
    private OnListFragmentInteractionListener mListener;

    public UserFragmentContentItemRecyclerViewAdapter(Context context, Cursor cursor, OnListFragmentInteractionListener listener) {
        mContext = context;
        mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_content_item, parent, false);
                return view;
            }

            @Override
            public void bindView(View view, Context context, final Cursor cursor) {
                final DBUserContentItem ci = DatabaseHelper.convertCursorToDBUserContentItem(cursor);
                ViewHolder holder = new ViewHolder(view);

                if (ci.imageIPFS == null || !ci.imageIPFS.equals("empty")) {
                    holder.mImageView.setVisibility(View.VISIBLE);
                    //Loading image from url into imageView
                    Glide.with(mContext)
                            .load(EthereumConstants.IPFS_GATEWAY_URL + ci.imageIPFS)
                            .into(holder.mImageView);
                } else {
                    holder.mImageView.setVisibility(View.GONE);
                }

                String title = ci.draft ? "<DRAFT> " + ci.title : ci.title;
                holder.mTitleView.setText(title);
                String textFromHtml = Jsoup.parse(ci.primaryText == null ? "" : ci.primaryText).text();
                holder.mBodyView.setText(textFromHtml);
                holder.mDateAndAuthorView.setText("Published " + DataUtils.convertTimeStampToDateString(ci.publishedDate)
                        + " by " + ci.publishedByEthAddress);



                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onListFragmentInteraction(cursor.getPosition(), ci);
                        }
                    }
                });
            }
        };
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.mView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mBodyView;
        public final TextView mDateAndAuthorView;
        public final ImageView mImageView;

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
