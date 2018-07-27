package com.example.cameron.ethereumtest1.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.activities.ViewPublicationActivity;
import com.example.cameron.ethereumtest1.database.DBPublication;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;

public class PublicationsRecyclerViewAdapter extends RecyclerView.Adapter<PublicationsRecyclerViewAdapter.ViewHolder>{

    private CursorAdapter mCursorAdapter;
    private final Context mContext;

    public PublicationsRecyclerViewAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_publication, parent, false);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                final DBPublication pub = DatabaseHelper.convertCursorToDBPublication(cursor);
                ViewHolder holder = new ViewHolder(view);

                holder.mNameView.setText(pub.name);
                holder.mPostCountView.setText("articles: " + pub.numPublished);
                holder.mSupportersView.setText("supporters: " + pub.uniqueSupporters);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ViewPublicationActivity.class);
                    }
                });
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.mView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mPostCountView;
        public final TextView mSupportersView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mPostCountView = (TextView) view.findViewById(R.id.posts);
            mSupportersView = (TextView) view.findViewById(R.id.supporters);
        }
    }
}
