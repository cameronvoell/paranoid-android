package com.example.cameron.ethereumtest1.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.commonsware.cwac.anddown.AndDown;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.database.DBUserContentItem;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;
import com.example.cameron.ethereumtest1.ethereum.EthereumConstants;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.util.PrefUtils;
import com.google.gson.Gson;

import org.jsoup.Jsoup;

import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_PUBLISH_USER_CONTENT;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_CONTENT_ITEM;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_DRAFT_PHOTO_URL;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_PASSWORD;

public class EditContentActivity extends AppCompatActivity implements View.OnTouchListener {

    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int DRAFT_BODY_MARKDOWN_REQUEST = 2;


    public static final String PARAM_DRAFT_CONTENT_BODY_MARKDOWN = "param.draft.content.body.markdown";
    ImageView mImageView;
    String mImageURL = null;

    TextView mTitleTextView;
    EditText mTitleEditText;
    Button mEditTitleButton;
    String mTitleText = null;

    TextView mBodyTextView;
    WebView mBodyWebView;
    String mBodyText = null;
    DBUserContentItem mDbUserContentItem;

    public DatabaseHelper mDatabaseHelper;

    // handler for received data from service
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            if (intent.getAction().equals(EthereumClientService.UI_UPDATE_DRAFT_PHOTO_URL)) {
                mImageURL = intent.getStringExtra(PARAM_DRAFT_PHOTO_URL);
                if (mImageURL != null) {
                    Glide.with(getBaseContext())
                            .load(EthereumConstants.IPFS_GATEWAY_URL + mImageURL)
                            .into(mImageView);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mDbUserContentItem = (DBUserContentItem)intent.getParcelableExtra("dbItem");

        setContentView(R.layout.activity_edit_content);
        mImageView = (ImageView) findViewById(R.id.image_content);

        mTitleTextView = (TextView) findViewById(R.id.contentTitle);
        mTitleEditText = (EditText) findViewById(R.id.editTitle);
        mEditTitleButton = (Button) findViewById(R.id.editTitleButton);

        mBodyTextView = (TextView) findViewById(R.id.contentBody);
        mBodyWebView = (WebView) findViewById(R.id.bodyWeb);

        if (mDbUserContentItem != null) {
            mImageURL = mDbUserContentItem.imageIPFS;
            String textFromHtml = Jsoup.parse(mDbUserContentItem.primaryText == null ? "" : mDbUserContentItem.primaryText).text();
            mBodyText = textFromHtml;
            mBodyTextView.setVisibility(View.GONE);
            mBodyWebView.setVisibility(View.VISIBLE);
            mBodyWebView.loadData(mDbUserContentItem.primaryText, "text/html; charset=UTF-8", null);
            mTitleText = mDbUserContentItem.title;
            mTitleTextView.setText(mDbUserContentItem.title);
        }

        mBodyWebView.setOnTouchListener(this);

        if (mImageURL != null) {
            Glide.with(getBaseContext())
                    .load(EthereumConstants.IPFS_GATEWAY_URL + mImageURL)
                    .into(mImageView);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(EthereumClientService.UI_UPDATE_DRAFT_PHOTO_URL);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);
        mDatabaseHelper = new DatabaseHelper(this);
    }

    private String mUri;

    public void uploadPhoto(View v) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = getRealPathFromURI(data.getData());
            startService(new Intent(EditContentActivity.this, EthereumClientService.class)
                    .putExtra(PARAM_DRAFT_PHOTO_URL, mUri)
                    .setAction(EthereumClientService.IPFS_FETCH_DRAFT_IMAGE_URL));
            Toast.makeText(getApplicationContext(), "Upload this photo " + mUri, Toast.LENGTH_SHORT).show();
        } else if (requestCode == DRAFT_BODY_MARKDOWN_REQUEST) {
            mBodyText = data.getStringExtra(PARAM_DRAFT_CONTENT_BODY_MARKDOWN);

            if (mBodyText != null) {
                mBodyTextView.setVisibility(View.GONE);
                mBodyWebView.setVisibility(View.VISIBLE);

                AndDown andDown = new AndDown();
                String html = andDown.markdownToHtml(mBodyText);
                html = "<style>div{font-size: 20px;font-family: \"Roboto\";font-weight: 400;}</style> <div>" + html + "</div>";
                mBodyWebView.loadData(html, "text/html; charset=UTF-8", null);
            }

        }
    }

    private String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;

    }

    public void showEditTitle(View view) {
        mTitleTextView.setVisibility(View.GONE);
        mTitleEditText.setVisibility(View.VISIBLE);
        mEditTitleButton.setVisibility(View.VISIBLE);
    }


    public void saveTitle(View view) {
        mTitleText = mTitleEditText.getText().toString();
        mTitleEditText.setVisibility(View.GONE);
        mEditTitleButton.setVisibility(View.GONE);

        mTitleTextView.setText(mTitleText);
        mTitleTextView.setVisibility(View.VISIBLE);
    }

    public void editBody(View view) {
        Intent intent = new Intent(getBaseContext(), EditContentBodyActivity.class);
        if (mBodyText != null) {
            intent.putExtra(PARAM_DRAFT_CONTENT_BODY_MARKDOWN, mBodyText);
        }
        startActivityForResult(intent, DRAFT_BODY_MARKDOWN_REQUEST);
    }

    public void saveDraft(View view) {
        String html = new AndDown().markdownToHtml(mBodyText == null ? "" : mBodyText);
        html = "<style>div{font-size: 20px;font-family: \"Roboto\";font-weight: 400;}</style> <div>" + html + "</div>";
        ContentItem contentItem = convertDialogInputToContentItem(mTitleTextView.getText().toString(), html, mUri);
        Gson gson = new Gson();
        String json = gson.toJson(contentItem);
        String address = PrefUtils.getSelectedAccountAddress(this);
        DBUserContentItem dbContentItem = new DBUserContentItem(address, mDbUserContentItem == null ?
                mDatabaseHelper.getNumContentItemsForUser(this, address) : mDbUserContentItem.userContentIndex, "not yet published", mImageURL,
                json, contentItem.title, contentItem.primaryText, System.currentTimeMillis(), false, true);
        mDatabaseHelper.saveUserContentItem(dbContentItem);
        finish();
    }

    public void publishPost(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_post_content_to_feed);
        dialog.setTitle("Publish \"" + mTitleText + "\"?");

        final TextView textView = (TextView) dialog.findViewById(R.id.dialogTitle);
        final EditText password = (EditText) dialog.findViewById(R.id.editPassword);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndDown andDown = new AndDown();
                String html = andDown.markdownToHtml(mBodyText);
                html = "<style>div{font-size: 20px;font-family: \"Roboto\";font-weight: 400;}</style> <div>" + html + "</div>";
                ContentItem contentItem = convertDialogInputToContentItem(mTitleTextView.getText().toString(), html, mImageURL);
                startService(new Intent(EditContentActivity.this, EthereumClientService.class)
                        .putExtra(PARAM_CONTENT_ITEM, contentItem)
                        .putExtra(PARAM_PASSWORD, password.getText().toString())
                        .setAction(ETH_PUBLISH_USER_CONTENT));
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private ContentItem convertDialogInputToContentItem(String title, String text, String imagePath) {
        String publishedBy = PrefUtils.getSelectedAccountAddress(this);
        long publishedDate = System.currentTimeMillis();
        String primaryHttpLink = "empty";
        String primaryImageUrl = imagePath;
        String primaryContentAddressedLink = "empty";
        String primaryText = text;
        ContentItem ci = new ContentItem(publishedBy, title,
                publishedDate, primaryText, primaryImageUrl, -1);
        return ci;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            editBody(null);
        }
        return true;
    }

    public void close(View view) {
        finish();
    }


}
