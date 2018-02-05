package com.example.cameron.ethereumtest1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.data.EthereumConstants;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.fragments.ContentListFragment.OnListFragmentInteractionListener;
import com.example.cameron.ethereumtest1.model.PublicationContentItem;
import com.example.cameron.ethereumtest1.util.DataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContentItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyContentItemRecyclerViewAdapter extends RecyclerView.Adapter<MyContentItemRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final List<PublicationContentItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private Spinner mPublicationSpinner;
    private Spinner mTagSpinner;
    private Spinner mSortBySpinner;

    public MyContentItemRecyclerViewAdapter(List<PublicationContentItem> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_content_list, parent, false);
            mPublicationSpinner = (Spinner) view.findViewById(R.id.publication);
            mTagSpinner = (Spinner) view.findViewById(R.id.tag);
            mSortBySpinner = (Spinner) view.findViewById(R.id.sortBy);
            setPublicationSpinnerOptions();
            setSortBySpinnerOptions();
            setTagSpinnerOptions();
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_content_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == 0) {

        } else {
            PublicationContentItem pci = mValues.get(position - 1);
            ContentItem ci = pci.contentItem;

            holder.mRevenueView.setText(DataUtils.formatAccountBalanceEther(pci.contentRevenue, 4));

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
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size() + 1;
    }

    private PublicationContentItem getItem(int position) {
        return mValues.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mBodyView;
        public final TextView mDateAndAuthorView;
        public final ImageView mImageView;
        public final TextView mRevenueView;
        public ContentItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mBodyView = (TextView) view.findViewById(R.id.body);
            mDateAndAuthorView = (TextView) view.findViewById(R.id.dateAndAuthor);
            mImageView = (ImageView) view.findViewById(R.id.contentImage);
            mRevenueView = (TextView) view.findViewById(R.id.revenue);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }

    private AdapterView.OnItemSelectedListener mPublicationSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void setPublicationSpinnerOptions() {
        ArrayList<String> publicationOptions = new ArrayList<>();
        publicationOptions.add("none");
        publicationOptions.add("slush-pile");
        publicationOptions.add("publication options");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_content_list, publicationOptions);
        mPublicationSpinner.setAdapter(spinnerArrayAdapter);
        mPublicationSpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mPublicationSpinner.setSelection(0);
    }
    private void setTagSpinnerOptions() {
        ArrayList<String> tagOptions = new ArrayList<>();
        tagOptions.add("all");
        tagOptions.add("tag options");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_content_list, tagOptions);
        mTagSpinner.setAdapter(spinnerArrayAdapter);
        mTagSpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mTagSpinner.setSelection(0);
    }
    private void setSortBySpinnerOptions() {
        ArrayList<String> sortByOptions = new ArrayList<>();
        sortByOptions.add("Date Added Desc");
        sortByOptions.add("Date Added Asc");
        sortByOptions.add("Upvotes Desc");
        sortByOptions.add("Upvotes Asc");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_content_list, sortByOptions);
        mSortBySpinner.setAdapter(spinnerArrayAdapter);
        mSortBySpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mSortBySpinner.setSelection(0);
    }
}
