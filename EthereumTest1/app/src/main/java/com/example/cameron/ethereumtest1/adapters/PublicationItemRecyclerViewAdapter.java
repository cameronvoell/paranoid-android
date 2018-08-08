package com.example.cameron.ethereumtest1.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.activities.ViewContentActivity;
import com.example.cameron.ethereumtest1.database.DBPublication;
import com.example.cameron.ethereumtest1.database.DBPublicationContentItem;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;
import com.example.cameron.ethereumtest1.ethereum.EthereumConstants;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.fragments.PublicationContentListFragment.OnListFragmentInteractionListener;
import com.example.cameron.ethereumtest1.util.DataUtils;
import com.example.cameron.ethereumtest1.util.PrefUtils;

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
    private final Context mContext;
    private Spinner mTagSpinner;
    private CursorAdapter mCursorAdapter;

    public PublicationItemRecyclerViewAdapter(Context context, Cursor cursor) {
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
                final DBPublicationContentItem ci = DatabaseHelper.convertCursorToDBPublicationContentItem(cursor);
                final ViewHolder holder = new ViewHolder(view);

                //holder.mRevenueView.setText(DataUtils.formatAccountBalanceEther(pci.contentRevenue, 4));

                if (!ci.imageIPFS.equals("empty")) {
                    holder.mImageView.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(EthereumConstants.IPFS_GATEWAY_URL + ci.imageIPFS)
                            .into(holder.mImageView);
                } else {
                    holder.mImageView.setVisibility(View.GONE);
                }

                holder.mTitleView.setText(ci.title);
                String textFromHtml = Jsoup.parse(ci.primaryText).text();
                holder.mBodyView.setText(textFromHtml);
                holder.mDateAndAuthorView.setText("Published " + DataUtils.convertTimeStampToDateString(ci.publishedDate)
                        + " by " + ci.publishedByEthAddress);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptions options = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            Intent intent = new Intent(mContext, ViewContentActivity.class);
                            ArrayList<DBPublicationContentItem> contentItems = new ArrayList<>();
                            contentItems.add(ci);
                            intent.putParcelableArrayListExtra("content_items", contentItems);
                            if (!ci.imageIPFS.equals("empty")) {
                                options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, holder.mImageView, mContext.getString(R.string.picture_transition_name));
                                mContext.startActivity(intent, options.toBundle());
                            } else {
                                mContext.startActivity(intent);
                            }
                        }
                    }
                });
            }
        };

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_content_list, parent, false);
            mTagSpinner = (Spinner) view.findViewById(R.id.tag);
            setTagSpinnerOptions();
            return new ViewHolder(view);
        } else {
            View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position != 0) {
            mCursorAdapter.getCursor().moveToPosition(position - 1);
            mCursorAdapter.bindView(holder.mView, mContext, mCursorAdapter.getCursor());
        }
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    private void setTagSpinnerOptions() {
        ArrayList<String> tagOptions = new ArrayList<>();
        final ArrayList<DBPublication> pubs = new DatabaseHelper(mContext).getPublicationsToView();
        for (DBPublication pub: pubs) {
            tagOptions.add(pub.name);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_dropdown_content_list, tagOptions);
        mTagSpinner.setAdapter(spinnerArrayAdapter);
        int selection = PrefUtils.getSelectedPublication(mContext);
        mTagSpinner.setSelection(selection, false);
        mTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DBPublication pub = pubs.get(position);
                Intent i = new Intent();
                i.setAction("refresh-list");
                i.putExtra("whichPub", pub.publicationID);
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
                lbm.sendBroadcast(i);
                PrefUtils.saveSelectedPublication(mContext, pub.publicationID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public final TextView mBodyView;
            public final TextView mDateAndAuthorView;
            public final ImageView mImageView;
            public final TextView mRevenueView;

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
}
