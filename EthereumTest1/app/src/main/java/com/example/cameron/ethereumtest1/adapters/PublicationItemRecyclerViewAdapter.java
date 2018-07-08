package com.example.cameron.ethereumtest1.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
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
import com.example.cameron.ethereumtest1.activities.ViewContentActivity;
import com.example.cameron.ethereumtest1.ethereum.EthereumConstants;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.fragments.PublicationContentListFragment.OnListFragmentInteractionListener;
import com.example.cameron.ethereumtest1.model.PublicationContentItem;
import com.example.cameron.ethereumtest1.util.DataUtils;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContentItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PublicationItemRecyclerViewAdapter extends RecyclerView.Adapter<PublicationItemRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final List<PublicationContentItem> mValues;
    private final Context mContext;
    private Spinner mTagSpinner;
    private Spinner mSortBySpinner;

    public PublicationItemRecyclerViewAdapter(List<PublicationContentItem> items, Context context) {
        mValues = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        if (viewType == TYPE_HEADER) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.header_content_list, parent, false);
//            mTagSpinner = (Spinner) view.findViewById(R.id.tag);
//            mSortBySpinner = (Spinner) view.findViewById(R.id.sortBy);
//            setSortBySpinnerOptions();
//            setTagSpinnerOptions();
//            return new ViewHolder(view);
//        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_content_item, parent, false);
            return new ViewHolder(view);
//        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        if (position == 0) {
//
//        } else {
            PublicationContentItem pci = mValues.get(position);
            ContentItem ci = pci.contentItem;

            holder.mRevenueView.setText(DataUtils.formatAccountBalanceEther(pci.contentRevenue, 4));

            if (!ci.primaryImageUrl.equals("empty")) {
                holder.mImageView.setVisibility(View.VISIBLE);
                //Loading image from url into imageView
                //holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.ic_account));
                Glide.with(mContext)
                        .load(EthereumConstants.IPFS_GATEWAY_URL + ci.primaryImageUrl)
                        .into(holder.mImageView);
            } else {
                holder.mImageView.setVisibility(View.GONE);
            }

            holder.mItem = ci;
            holder.mTitleView.setText(ci.title);
            String textFromHtml = Jsoup.parse(ci.primaryText).text();
            holder.mBodyView.setText(textFromHtml);
            holder.mDateAndAuthorView.setText("Published " + DataUtils.convertTimeStampToDateString(ci.publishedDate)
                    + " by " + ci.publishedBy);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityOptions options = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(mContext, ViewContentActivity.class);
                        ArrayList<ContentItem> contentItems = new ArrayList<>();
                        contentItems.add(holder.mItem);
                        intent.putParcelableArrayListExtra("content_items", contentItems);
                        if (!holder.mItem.primaryImageUrl.equals("empty")) {
                            options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, holder.mImageView, mContext.getString(R.string.picture_transition_name));
                            mContext.startActivity(intent, options.toBundle());
                        } else {
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
    //    }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private PublicationContentItem getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0)
//            return TYPE_HEADER;

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

    private void setTagSpinnerOptions() {
        ArrayList<String> tagOptions = new ArrayList<>();
        tagOptions.add("tag");
        tagOptions.add("tag options");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_content_list, tagOptions);
        mTagSpinner.setAdapter(spinnerArrayAdapter);
        mTagSpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mTagSpinner.setSelection(0);
    }
    private void setSortBySpinnerOptions() {
        ArrayList<String> sortByOptions = new ArrayList<>();
        sortByOptions.add("date  ↓");
        sortByOptions.add("date  ↑");
        sortByOptions.add("upvotes  ↓");
        sortByOptions.add("upvotes  ↑ ");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_content_list, sortByOptions);
        mSortBySpinner.setAdapter(spinnerArrayAdapter);
        mSortBySpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mSortBySpinner.setSelection(0);
    }
}
