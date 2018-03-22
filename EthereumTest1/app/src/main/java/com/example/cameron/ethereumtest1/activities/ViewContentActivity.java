package com.example.cameron.ethereumtest1.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.data.EthereumConstants;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.util.DataUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

import us.feras.mdv.MarkdownView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewContentActivity extends AppCompatActivity {

    private ContentItem mContentItem;
    private TextView mTitleTextView;
    private TextView mDateAndAuthorTextView;
    private WebView mBodyWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_content);

        Intent intent = getIntent();
        ArrayList<Parcelable> items = intent.getParcelableArrayListExtra("content_items");
        mContentItem = (ContentItem) items.get(0);

        mTitleTextView = (TextView) findViewById(R.id.contentTitle);
        mDateAndAuthorTextView = (TextView) findViewById(R.id.dateAndAuthor);
        mBodyWebView = (WebView) findViewById(R.id.contentBody);

        mTitleTextView.setText(mContentItem.title);
        String dateAndPublishedBy = "Published " + DataUtils.convertTimeStampToDateString(mContentItem.publishedDate)
                + " by " + mContentItem.publishedBy;
        mDateAndAuthorTextView.setText(dateAndPublishedBy);
        mBodyWebView.loadData(mContentItem.primaryText, "text/html; charset=UTF-8", null);

        ImageView imageView = (ImageView) findViewById(R.id.image_content_activity);
        Glide.with(getBaseContext())
                .load(EthereumConstants.IPFS_GATEWAY_URL + mContentItem.primaryImageUrl)
                .into(imageView);
    }


    public void close(View view) {
        onBackPressed();
    }
}
